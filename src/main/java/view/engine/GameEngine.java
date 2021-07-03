package view.engine;


import controller.Messages.ImageLoderOrderChanged;
import controller.Messages.NeedResetCameraMessage;
import controller.Messages.NeedShowedImageChange;
import view.engine.Commands.ICommand;

import java.util.HashSet;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;

import static org.lwjgl.glfw.GLFW.glfwDestroyWindow;

public class GameEngine extends Observable implements Runnable, Observer  {


    public static final int TARGET_FPS = 75;

    public static final int TARGET_UPS = 30;

    public Window window;

    private final Thread gameLoopThread;

    private final Timer timer;

    private final IGameLogic gameLogic;

    private final MouseInput mouseInput;

    private double lastFps;

    private int fps;

    private final String windowTitle;
    //TODO SUFNI MEGOLDAS,JAVITSD MAJD KI
    private FutureTask<Long> copyContextFuture;
    private final ConcurrentLinkedQueue<ICommand> messageQueue=new ConcurrentLinkedQueue<>();
    private final CountDownLatch isFullyStarted=new CountDownLatch(1);
    public GameEngine(String windowTitle, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception {
        this(windowTitle, 0, 0, vSync, opts, gameLogic);
    }
    public GameEngine(String windowTitle, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic,long other) throws Exception {
        this(windowTitle, 0, 0, vSync, opts, gameLogic,other);
    }

    public GameEngine(String windowTitle, int width, int height, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic) throws Exception {
        this.windowTitle = windowTitle;
        gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
        window = new Window(windowTitle, width, height, vSync, opts);
        mouseInput = new MouseInput();
        this.gameLogic = gameLogic;
        timer = new Timer();
    }
///
public GameEngine(String windowTitle, int width, int height, boolean vSync, Window.WindowOptions opts, IGameLogic gameLogic,long other) throws Exception {
    this.windowTitle = windowTitle;
    gameLoopThread = new Thread(this, "GAME_LOOP_THREAD");
    window = new Window(windowTitle, width, height, vSync, opts,other);
    mouseInput = new MouseInput();
    this.gameLogic = gameLogic;
    timer = new Timer();
}
    //
    public void start() {
        String osName = System.getProperty("os.name");
        if ( osName.contains("Mac") ) {
            gameLoopThread.run();
        } else {
            gameLoopThread.start();
        }
        try {
            isFullyStarted.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        try {
            init();
            isFullyStarted.countDown();
            gameLoop();
        } catch (Exception excp) {
            excp.printStackTrace();
        } finally {
            cleanup();
        }

    }

    protected void init() throws Exception {
        window.init();
        timer.init();
        mouseInput.init(window);
        gameLogic.init(window);
        lastFps = timer.getTime();
        fps = 0;
    }

    protected void gameLoop() {
        float elapsedTime;
        float accumulator = 0f;
        float interval = 1f / TARGET_UPS;


        while (!Thread.interrupted()&& !window.windowShouldClose()) {

            elapsedTime = timer.getElapsedTime();
            accumulator += elapsedTime;

            input();

            while (accumulator >= interval) {
                update(interval);
                accumulator -= interval;
            }

            render();

            if ( !window.isvSync() ) {
                try {
                    sync();
                } catch (InterruptedException e) {
                    cleanup();
                    gameLoopThread.interrupt();
                }

            }
        }

        cleanup();
    }

    protected void cleanup() {
        gameLogic.cleanup();
    }

    private void sync() throws InterruptedException {
        float loopSlot = 1f / TARGET_FPS;
        double endTime = timer.getLastLoopTime() + loopSlot;
        while (timer.getTime() < endTime) {
                Thread.sleep(1);
        }
    }

    protected void input() {
        mouseInput.input(window);
        gameLogic.input(window, mouseInput);
    }

    protected void update(float interval) {
        gameLogic.update(interval, mouseInput, window);
    }
    public void  setMarkedImages(TreeSet<Integer> set ){
        messageQueue.add(() -> gameLogic.setMarkedImages(set));
        //gameLogic.setMarkedImages(set);

        //gameLogic.update(0,mouseInput,window);

        //resetCamera();

    }
    protected void render() {

        if ( window.getWindowOptions().showFps && timer.getLastLoopTime() - lastFps > 1 ) {
            lastFps = timer.getLastLoopTime();
            window.setWindowTitle(windowTitle + " - " + fps + " FPS");
            fps = 0;
        }
        fps++;
        if(!messageQueue.isEmpty()){
            messageQueue.poll().execute();
        }
        gameLogic.render(window);
        window.update();
    }

    @Override
    public synchronized void update(Observable o, Object arg) {
        if(arg instanceof NeedResetCameraMessage){
            messageQueue.add(() -> gameLogic.resetCam());
            return;

        }
        if(arg instanceof ImageLoderOrderChanged){
            messageQueue.add(() -> gameLogic.randomReorder());
            return;
        }
        if(arg instanceof NeedShowedImageChange){
            System.out.println("**************************");
            System.out.println("NeedShowedImageChange:");
            System.out.println("**************************");
            messageQueue.add(() -> gameLogic.recalculateShowed());
            return;
        }
    }
    public void reorderImages(){
        messageQueue.add(() -> gameLogic.randomReorder());
    }
    public void resetCamera(){
        messageQueue.add(() -> gameLogic.resetCam());
    }
    public void stop(){
        gameLoopThread.interrupt();

    }
    public int getCurrentlyMarkedImage(){return gameLogic.getCurrentlyMarkedImage();}
    public int[] getSelectedImages(){return gameLogic.getSelectedImages();}
    public TreeSet<Integer> getSelectedImagesAsSet(){return gameLogic.getSelectedImagesAsSet();}
    public void join(){
        try {
            gameLoopThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    public FutureTask<Long> copyContext(){
        copyContextFuture=new FutureTask<Long>(new Callable<Long>() {
            public Long call() {
                return new Long(window.copyContext());
            }});
        messageQueue.add(() -> copyContextFuture.run());
        return copyContextFuture;
    }
    public void destroyWindow(long temp){
        messageQueue.add(() -> glfwDestroyWindow(temp));
    }

}
