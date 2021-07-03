package view.engine;

import java.util.TreeSet;

public interface IGameLogic {

    void init(Window window) throws Exception;

    void input(Window window, MouseInput mouseInput);

    void update(float interval, MouseInput mouseInput, Window window);

    void render(Window window);

    void cleanup();
    void reorderImages();
    void randomReorder();
    void resetCam();
    int[] getSelectedImages();
    TreeSet<Integer> getSelectedImagesAsSet();
    void setMarkedImages(TreeSet<Integer> set);
    int getCurrentlyMarkedImage();
    void recalculateShowed();

}