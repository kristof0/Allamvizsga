package view.CollectionViews;

import com.drew.metadata.Metadata;
import customFeatures.LastModificationDate;
import model.Loaders.IImageLoader;
import org.joml.Vector2f;
import org.joml.Vector3f;
import org.nd4j.shade.guava.primitives.Doubles;
import view.engine.*;
import view.engine.graph.Camera;
import view.engine.graph.Mesh;

import java.io.IOException;
import java.util.*;

import static java.lang.Math.*;
import static org.lwjgl.glfw.GLFW.*;

public class FloatingHistogramView implements IGameLogic {

    private static final float MOUSE_SENSITIVITY = 0.2f;

    private final Vector3f cameraInc;

    private final Renderer renderer;

    private final Camera camera;

    private GameItem[] gameItems;

    private static final float CAMERA_POS_STEP = 0.05f;
    private static final float MOUSE_DRAG_STEP=0.01f;
    private static final float MOUSE_SCROLL_STEP=2;

    private  int[] order;
    private  double[] scores;
    private  double scoresMin;
    private  double scoresMax;

    private boolean[] marked;
    private int[] invOrder;
    private boolean [] showed;
    private int numberOfImages;
    private int numbeOfImagesPerTexture;
    private int numbeOfImagesPerRow;
    private float pictureSize;//
    private HashMap<Integer,Integer> groupsByTime;

    private Metadata metadata;

    //private float keptav=-0.1f;
    private final Vector3f picturesPos;
    private final MouseDateTimeViewSelectionDetector selectDetector;
//HUD


    //THE SPEED IS KEY :)


    private final IImageLoader textureLoader;

    //latest
    private int showedImageNumber;
    private int nonShowedImageNumber;
    private int[] reverseHelper;
    int[] offset;
    public FloatingHistogramView(IImageLoader textureLoader) {
        showed=new boolean[textureLoader.getNumberOfImages()];
        try {
            order=textureLoader.getOrderByTime();
        } catch (IOException e) {
            e.printStackTrace();
        }

        for(int i=0;i<showed.length;++i){
            showed[i]=true;
        }
        renderer = new Renderer(textureLoader);
        selectDetector = new MouseDateTimeViewSelectionDetector();
        picturesPos=new Vector3f(0f,0f,-0.5f);
        camera = new Camera(new Vector3f(0,0,1.8f),new Vector3f(0,0,0),-1,1,-1,1,picturesPos.z+0.05f,1.8f,
                -80f,80f,-80f,80f,0.0f,0f
        );

        cameraInc = new Vector3f(0, 0, 0);
        this.textureLoader=textureLoader;
        try {
            this.groupsByTime=this.textureLoader.getFloatingGroups("HH:mm");
            scores=this.textureLoader.getFloatingScores();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scoresMin= Doubles.min(scores);
        scoresMax= Doubles.max(scores);


    }
    public FloatingHistogramView(IImageLoader textureLoader, int[] showedImages) {
        showed=new boolean[textureLoader.getNumberOfImages()];
        for(int i=0;i<showedImages.length;++i){
            showed[showedImages[i]]=true;
        }
        renderer = new Renderer(textureLoader);
        selectDetector = new MouseDateTimeViewSelectionDetector();
        picturesPos=new Vector3f(0f,0f,-0.1f);
        camera = new Camera(new Vector3f(0,0,1.8f),new Vector3f(0,0,0),-1,1,-1,1,picturesPos.z+0.1f,1.8f,
                -80f,80f,-80f,80f,0.0f,0f
        );

        cameraInc = new Vector3f(0, 0, 0);
        this.textureLoader=textureLoader;

    }

    public FloatingHistogramView(IImageLoader textureLoader, TreeSet<Integer> showedImages) {


        showed=new boolean[textureLoader.getNumberOfImages()];

        Iterator<Integer> it = showedImages.iterator();
        while (it.hasNext()) {
            showed[it.next()]=true;
        }
        renderer = new Renderer(textureLoader);
        selectDetector = new MouseDateTimeViewSelectionDetector();
        picturesPos=new Vector3f(0f,0f,-0.1f);
        camera = new Camera(new Vector3f(0,0,2f),new Vector3f(0,0,0),-1,1,-1,1,picturesPos.z+0.1f,1.8f,
                -80f,80f,-80f,80f,0.0f,0f
        );

        cameraInc = new Vector3f(0, 0, 0);
        this.textureLoader=textureLoader;
        try {
            this.groupsByTime=this.textureLoader.getFloatingGroups("HH:mm");
            scores=this.textureLoader.getFloatingScores();
        } catch (IOException e) {
            e.printStackTrace();
        }
        scoresMin= Doubles.min(scores);
        scoresMax= Doubles.max(scores);
    }
///
final void  SetPosition(float[] positions,int nthPic,int nthPos,int column,double score){
    //float x=((oszlop%numberOfImagesPerRow)*pictureSize-pictureSize;//-1

    float x=(column)*pictureSize-1;//-1
    // double n=2*((value-min)/(max-min))-1;
    float y= (float) (2*((score-scoresMin)/(scoresMax-scoresMin))-1);
    //System.out.println("x="+x+" y="+y+" col="+column);
    int j=12*(nthPic%numbeOfImagesPerTexture);

    positions[j]=x;            // +    *
    positions[j+1]=y;
    positions[j+2]=0;          // *    *

    positions[j+3]=x;           // *    *
    positions[j+4]=y-pictureSize;
    positions[j+5]=0;           // +    *

    positions[j+6]=x+pictureSize;     // *    *
    positions[j+7]=y-pictureSize;
    positions[j+8]=0;           // *    +

    positions[j+9]=x+pictureSize;     // *    +
    positions[j+10]=y;
    positions[j+11]=0;          // *    *




}
    final void setIndices(int[] indices,int nthPic){
        int l=(nthPic%numbeOfImagesPerTexture)*6;
        int p=(nthPic%numbeOfImagesPerTexture)*4;
        indices[l]=p;
        indices[l+1]=p+1;
        indices[l+2]=p+3;
        indices[l+3]=p+3;
        indices[l+4]=p+1;
        indices[l+5]=p+2;
    }

    @Override
    public void init(Window window) throws Exception {
        //HUD

        //
        textureLoader.bindTextures();
        renderer.init(window);

        numberOfImages=textureLoader.getNumberOfImages();
        gameItems = new GameItem[textureLoader.getNumberOfTextures()];
        order=new int[numberOfImages];
        try {
            this.groupsByTime=this.textureLoader.getFloatingGroups("HH:mm");
        } catch (IOException e) {
            e.printStackTrace();
        }
        //order=textureLoader.getCurrentOrder();
        order=textureLoader.getOrderByTime();
        //for(int i=0;i<9;++i){order[i]=i;}
        /*for(int i=0;i<order.length;++i){
            System.out.println(i+"  "+order[i]);
        }*/
        recalculateShowed();

    }
    @Override
    public void reorderImages(){

        int k=0;
        int i=0;
        //TODO NAGYON NEM OPTIMALIS
        int[] tempInv=new int[order.length];
        offset=new int[order.length];
        //int[] resultOrder=new int[2];
        for(int u=0;u<order.length;++u){
            tempInv[order[u]] = u;
        }
        int minus=0;
        for(int u=0;u<tempInv.length;++u){
            if(!showed[tempInv[u]]){
                --minus;
            }
            offset[tempInv[u]]=minus;
        }
//////

        ///
        float x=-1.0f;
        float y=1.0f;
        float z=0.5f;

        float[] positions = new float[numbeOfImagesPerTexture*3*4];
        float[] textCoords=new float[numbeOfImagesPerTexture*2*4];
        int[] indices = new int[numbeOfImagesPerTexture*6];

        float[] markedPositions = new float[numbeOfImagesPerTexture*3*4];
        float[] markedTextCoords=new float[numbeOfImagesPerTexture*2*4];
        int[] markedIndices = new int[numbeOfImagesPerTexture*6];
        int textureAtlasIndex=0;

        long startTime = System.currentTimeMillis();
        //System.out.println("numbeOfImagesPerRow:"+numbeOfImagesPerRow);

        int nonMarkedIndex=0;
        int markedIndex=0;
//----------
        // this.groupsByTime=this.textureLoader.getGroupsByDate();


        HashMap<Integer,Integer> tempgroupsByTime= new HashMap<>(this.groupsByTime);
        int eredetiMeret=tempgroupsByTime.size();

        i=0;
        k=0;

        int q = 0;
        for(;i<order.length;++i){



            if ((i != 0) && (((i) % numbeOfImagesPerTexture) == 0)) {
                System.out.println("SIMAAAAAAAAAAAAAAAAAAAAAAAAA");
                //TODO BIZTOS ? VIZSGALD FELUL
                if (gameItems[textureAtlasIndex] != null) {
                    gameItems[textureAtlasIndex].getMesh().cleanUp();
                    gameItems[textureAtlasIndex].getMarkedMesh().cleanUp();
                }
                ///

                gameItems[textureAtlasIndex] = new GameItem(new Mesh(positions, nonMarkedIndex*12 ,
                        textCoords,nonMarkedIndex*8,
                        indices,nonMarkedIndex*6
                ),new Mesh(markedPositions, markedIndex*12 ,
                        markedTextCoords,markedIndex*8,
                        markedIndices,markedIndex*6
                ), textureAtlasIndex);
                gameItems[textureAtlasIndex].setPosition(picturesPos.x, picturesPos.y, picturesPos.z);
                ++textureAtlasIndex;
                nonMarkedIndex=0;
                markedIndex=0;

            }

            //if(!groupsByTime.isEmpty()){q=groupsByTime.getLast();}

            if(showed[i] ) {
                /*if(q==0 && !groupsByTime.isEmpty()){ // && !groupsByTime.isEmpty()
                    q=tempgroupsByTime.getLast();
                    tempgroupsByTime.removeLast();
                }*/
                invOrder[order[i]+offset[i]]=k;
                reverseHelper[k]=i-k;
                int c=order[i];
                int col=-1;
                int row=0;
               /* while(c>-1){
                    col++;
                    c-=groupsByTime.get(col);
                }
                row=groupsByTime.get(col)+(c);*/
                if (marked[k]) {




                    //hanyadik oszlop, magasagga
                    //SetPosition(markedPositions, markedIndex,order[i], eredetiMeret-tempgroupsByTime.size(),q--);//markedindex
                    SetPosition(markedPositions, markedIndex,order[i], groupsByTime.get(i),scores[i]);//markedindex
                    //System.out.println("q="+q);
                    textureLoader.setTextCoordinate(markedTextCoords, i, markedIndex);
                    setIndices(markedIndices, markedIndex);
                    ++markedIndex;//markedIndex

                } else {


                    //System.out.println(order[i]+"   "+ groupsByTime.get(i)+"   "+ scores[i]);

                    SetPosition(positions,nonMarkedIndex,order[i], groupsByTime.get(i),scores[i]);
                    textureLoader.setTextCoordinate(textCoords, i,nonMarkedIndex);
                    //System.out.println("q="+q);
                    setIndices(indices, nonMarkedIndex);
                    ++nonMarkedIndex;
                }
                ++k;
            }
        }



        System.out.println("LLLLLLLLLLLLLLLLLLLLLLLLLLLLLLL");
        System.out.println("numbeOfImagesPerTexture:"+numbeOfImagesPerTexture);
        System.out.println("i:"+i);
        System.out.println("order.length:"+order.length);
        if(i!=0){
            System.out.println("PPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPPP");
            //TODO BIZTOS ? VIZSGALD FELUL
            if(gameItems[textureAtlasIndex]!=null) {
                gameItems[textureAtlasIndex].getMesh().cleanUp();
                gameItems[textureAtlasIndex].getMarkedMesh().cleanUp();
            }
            ///
            // System.out.println("SUBIDUBIII");
            gameItems[textureAtlasIndex]= new GameItem(new Mesh(positions, nonMarkedIndex*12 ,
                    textCoords,nonMarkedIndex*8,
                    indices,nonMarkedIndex*6
            ),new Mesh(markedPositions, markedIndex*12 ,
                    markedTextCoords,markedIndex*8,
                    markedIndices,markedIndex*6
            ), textureAtlasIndex);
            gameItems[textureAtlasIndex].setPosition(picturesPos.x,picturesPos.y,picturesPos.z);
        }
        long estimatedTime = System.currentTimeMillis() - startTime;
        System.out.println(estimatedTime);
    }

    @Override
    public void randomReorder() {
        try {
            order=textureLoader.getOrderByTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //for(int i=0;i<9;++i){order[i]=i;}
        reorderImages();
    }

    @Override
    public void resetCam() {
        camera.setPosition( 0,0,1.8f);
        camera.setRotation(0,0,0);
        camera.updateViewMatrix();
    }

    @Override
    public int[] getSelectedImages()
    {
        int temp=0;
        for(int i=0;i<marked.length;++i){
            if(marked[i]){
                ++temp;
            }
        }
        int[] result=new int[temp];

        for(int i=0,j=0;i<marked.length;++i){
            if(marked[i]){
                result[j]=reverseHelper[i]+i;
                ++j;
            }
        }

        return result;

    }

    @Override
    public TreeSet<Integer> getSelectedImagesAsSet() {
        TreeSet<Integer> result=new TreeSet<>();
        for(int i=0;i<marked.length;++i){
            if(marked[i]){
                result.add(reverseHelper[i]+i);
            }
        }
        return result;
    }

    @Override
    public void setMarkedImages(TreeSet<Integer> set) {
        set.forEach((id)->{
            marked[id]=true;
        });

        reorderImages();
        camera.updateViewMatrix();
    }

    @Override
    public int getCurrentlyMarkedImage() {
        for(int i=0,j=0;i<marked.length;++i){
            if(marked[i]){

                return reverseHelper[i]+i;
            }
        }
        return -1;
    }

    @Override
    public void input(Window window, MouseInput mouseInput) {
        cameraInc.set(0, 0, 0);
        if (window.isKeyPressed(GLFW_KEY_W)) {
            cameraInc.z = -0.2f;
        } else if (window.isKeyPressed(GLFW_KEY_S)) {
            cameraInc.z = 0.2f;
        }
        if (window.isKeyPressed(GLFW_KEY_A)) {
            cameraInc.x = -0.2f;
        } else if (window.isKeyPressed(GLFW_KEY_D)) {
            cameraInc.x = 0.2f;
        }
        if (window.isKeyPressed(GLFW_KEY_Z)) {
            cameraInc.y = -0.2f;
        } else if (window.isKeyPressed(GLFW_KEY_X)) {
            cameraInc.y = 0.2f;
        }
    }
    private Vector3f lastClickCord=null;
    private Vector3f originalClickedCord=null;
    private Vector3f clickCord;
    private boolean teszt=false;
    private int lastpos=-1;
    private boolean change=false;
    private int lastOriginalPos;
    private boolean wasDragged=false;
    @Override
    public void update(float interval, MouseInput mouseInput, Window window) {


        // Update camera position
        boolean needUpdate=false;

        Vector2f scroll=mouseInput.getScrollVec();
        cameraInc.z+=scroll.y*MOUSE_SCROLL_STEP;
        if(mouseInput.isLeftButtonPressed()||mouseInput.isLeftButtonJustRelesed()){
/*            if(this.selectDetector.selectGameItem(window, mouseInput.getCurrentPos(),
                    camera,picturesPos,marked,pictureSize,numbeOfImagesPerRow,invOrder.length,invOrder,mouseInput)){
                reorderImages();
            }*/
            clickCord=selectDetector.selectGameItem(window, mouseInput.getCurrentPos(), camera,picturesPos);

            //clicked outside of the pictures frame
            if(clickCord.x>1||clickCord.x<-1||clickCord.y>1||clickCord.y<-1){
                return ;
            }

            //transform from (-1,1) to (0,2)
            float transformedX=clickCord.x+1;
            //transform from (-1,1) to (-2,0)
            //float transformedY=clickCord.y-1;
            //(-1,1) to (0,2)
            float transformedY=clickCord.y+1;

            //System.out.println("trx="+transformedX+" try="+transformedY);
            int tx=(int)(transformedX/pictureSize);
            int ty=((int)(transformedY/pictureSize));
            //System.out.println("tx="+tx+" ty="+ty);


            int indexHelper=0;
            boolean in=false;
            int pictureIndex=0;
            int originalIndex=0;
            pictureIndex=invOrder[pictureIndex];
            boolean q=false;

            if(window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)||window.isKeyPressed(GLFW_KEY_RIGHT_CONTROL)) {
                if(lastpos!=pictureIndex){
                    //if(!marked[pictureIndex]) {

                    marked[pictureIndex] =  !marked[pictureIndex];
                    q=true;
                    change=true;
                    //System.out.println("HOSSZ KATT\n");
                    // }
                }
                else{
                    if(mouseInput.isLeftButtonJustRelesed()){
                        if(change==false) {
                            marked[pictureIndex] = !marked[pictureIndex];
                            q = true;
                            //System.out.println("EGYKATT");
                        }
                        else{
                            change=false;
                        }
                    }
                }

            }
            else {
                if(lastpos!=-1&&window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)||window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)){
                    int last=lastOriginalPos;
                    if(last>originalIndex){
                        int k=last;
                        last=originalIndex;
                        originalIndex=k;
                    }

                    // marked[last]=true;
                    //  marked[pictureIndex]=true;
                    //marked[pictureIndex-1]=true;
                    for(;last+1<=originalIndex+1;++last){
                        marked[invOrder[last]] = true;
                    }
                    q=true;
                }
                else{
                    if(mouseInput.isLeftButtonPressed()){
                        if (lastClickCord != null) {
                            lastClickCord.x -= clickCord.x;
                            lastClickCord.y -= clickCord.y;
                            lastClickCord.z -= clickCord.z;
                            if(lastClickCord.x!=0||lastClickCord.y!=0||lastClickCord.z!=0){
                                wasDragged=true;
                            }
                            camera.movePosition(originalClickedCord.x - clickCord.x, originalClickedCord.y - clickCord.y, 0);
                            needUpdate = true;
                            teszt = true;
                        } else {
                            originalClickedCord = new Vector3f();
                            originalClickedCord.x = clickCord.x;
                            originalClickedCord.y = clickCord.y;
                            originalClickedCord.z = clickCord.z;

                        }

                        lastClickCord=clickCord;
                    }
                    else{
                        // System.out.println("EGYEB\n");
                        if(!wasDragged) {
                            for (int i = 0; i < marked.length; ++i) {
                                marked[i] = false;
                            }
                            marked[pictureIndex] = true;
                            q = true;
                        }
                        wasDragged=false;
                        lastClickCord=null;
                    }

                }
            }


            lastpos=pictureIndex;
            lastOriginalPos=originalIndex;
            mouseInput.setLeftButtonJustRelesed(false);
            if(q){
                reorderImages();
            }
        }


        if(cameraInc.x!=0||cameraInc.y!=0||cameraInc.z!=0) {
            camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP);
            needUpdate=true;
        }
        // Update camera based on mouse

        if (mouseInput.isRightButtonPressed()) {

            Vector2f rotVec = mouseInput.getDisplVec();
            if(rotVec.x!=0||rotVec.y!=0) {
                camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0);
                needUpdate=true;
            }
        }
        if(needUpdate) {
            camera.updateViewMatrix();
        }

    }

    @Override
    public void render(Window window)
    {
        renderer.render(window, camera, gameItems);

    }

    @Override
    public void cleanup() {
        renderer.cleanup();
        if(null!=gameItems) {
            for (GameItem gameItem : gameItems) {
                gameItem.getMesh().cleanUp();
                gameItem.getMarkedMesh().cleanUp();

            }
        }
        textureLoader.clean();

    }
    public int getMaxRowNumber(HashMap<Integer,Integer> map){
        int[] rows=new int[map.size()];
        Map.Entry<Integer, Integer> entryWithMaxValue = null;
        for (Map.Entry<Integer, Integer>  currentEntry : map.entrySet()) {
                rows[currentEntry.getValue()]++;

        }

        return Arrays.stream(rows).max().getAsInt();
    }
    @Override
    public void recalculateShowed(){

        showed=textureLoader.getCurrentShowedImages();
        showedImageNumber=0;
        nonShowedImageNumber=0;

        for(int j=0;j<showed.length;++j){
            if(true==showed[j]){
                ++showedImageNumber;
            }
            else
            {
                ++nonShowedImageNumber;
            }
            // showHelper[j]=nonshowedImages;
        }

///
        try {
            this.groupsByTime=this.textureLoader.getFloatingGroups("HH:mm");
            scores=this.textureLoader.getFloatingScores();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //how many columns do we have
        numbeOfImagesPerRow=max(Utils.getMaxInMapBasedOnValue(groupsByTime),getMaxRowNumber(groupsByTime));

        scoresMin= Doubles.min(scores);
        scoresMax= Doubles.max(scores);

        //numbeOfImagesPerRow=(int)ceil(sqrt(showedImageNumber));
        pictureSize=2.0f/( numbeOfImagesPerRow);
        System.out.println("Numofimagesperrow "+numbeOfImagesPerRow);
        System.out.println("picturesize "+pictureSize);
        numbeOfImagesPerTexture=textureLoader.getNumberOfImagesPerTexture();
        ///
        invOrder=new int[showedImageNumber];
        marked=new boolean[showedImageNumber];
        reverseHelper=new int[showedImageNumber];
        order=new int[numberOfImages];
        try {
            order=textureLoader.getOrderByTime();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //for(int i=0;i<9;++i){order[i]=i;}


        reorderImages();
    }

}

/////



///