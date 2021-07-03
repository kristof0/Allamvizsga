package model.Loaders;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.TreeSet;

public interface ILoader extends Serializable {
    int getNumberOfImagesPerTexture();
    int getNumberOfTextures();
    int getNumberOfImages();
    int getTextureId();
    float[] getTextCoordinate(int nthImage);
    void setTextCoordinate(float[] textCoords, int nthPos, int nthPic);
    int getTextTable(int nthImage);
    void loadTextures() throws IOException;
    void bindTextures();
    void processDirectory(String path,LinkedList<Class> features) throws IOException;
    int[] getOrder(int nthImage,Class feature) throws IOException;
    double[] getScores(int nthImage,Class feature) throws IOException;
    void clean();
    String getSavePath();
    void saveSelectedImagesToDirectory(String savePath, TreeSet<Integer> selectedImages) throws IOException;
     int getMaxNumberOfRows();
     int getNumberOfCols();
     LinkedList<Integer> getGroupsByDate();
}
