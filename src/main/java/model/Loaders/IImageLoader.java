package model.Loaders;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.TreeSet;

public interface IImageLoader extends ILoader {
    double[] getFloatingScores() throws IOException;
    HashMap<Integer,Integer> getFloatingGroups(String format) throws IOException;
    int[] getCurrentOrder();
    double[] getCurrentScores();
    double[][] getDistanceBasedPositions();
    int[] getOrderByTime() throws IOException;
    void reorderImages(int nthImage,Class feature)throws IOException;
    void addImages(TreeSet<Integer> imageList);
    void removeImages(TreeSet<Integer> imageList);
    boolean[] getCurrentShowedImages();
    void calculateDistanceMatrix(Class feature);


}
