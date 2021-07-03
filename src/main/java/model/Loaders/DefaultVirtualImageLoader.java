package model.Loaders;

import controller.Messages.ImageLoderOrderChanged;
import controller.Messages.NeedShowedImageChange;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL30;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class DefaultVirtualImageLoader extends Observable implements Serializable,Observer,IImageLoader {
    DefaultImageLoader parentLoader;
    transient int [] order;
    transient double [] scores;
    transient double [][] distances;
    private final boolean [] showed;

    public DefaultVirtualImageLoader(DefaultImageLoader parentLoader) {
        this.parentLoader = parentLoader;
        this.showed=new boolean[parentLoader.getNumberOfImages()];
        for(int i=0;i<this.showed.length;++i){
            this.showed[i]=false;
        }
    }
    public LinkedList<Integer> getGroupsByDate(){ return parentLoader.getGroupsByDate();}
    public int getMaxNumberOfRows(){
        return parentLoader.getMaxNumberOfRows();
    }
    public int getNumberOfCols(){
        return parentLoader.getNumberOfCols();
    }
    @Override
    public int getNumberOfImagesPerTexture()
    {
       return parentLoader.getNumberOfImagesPerTexture();
    }

    @Override
    public int getNumberOfTextures() {
        return parentLoader.getNumberOfTextures();
    }

    @Override
    public int getNumberOfImages()
    {
        return parentLoader.getNumberOfImages();
    }

    @Override
    public int getTextureId() {

        return parentLoader.getTextureId();
    }

    @Override
    public float[] getTextCoordinate(int nthImage) {

        return parentLoader.getTextCoordinate(nthImage);
    }

    @Override
    public void setTextCoordinate(float[] textCoords, int nthPos, int nthPic) {
        parentLoader.setTextCoordinate(textCoords,nthPos,nthPic);
    }

    @Override
    public int getTextTable(int nthImage) {
        return parentLoader.getTextTable(nthImage);

    }

    @Override
    public void loadTextures()  {
        order=new int[parentLoader.getNumberOfImages()];
        for (int u = 0; u < order.length; ++u) {
            order[u] = u;

        }
        scores=new double[parentLoader.getNumberOfImages()];
        for (int u = 0; u < scores.length; ++u) {
            scores[u] = u;

        }

    }
    public HashMap<Integer, Integer> getFloatingGroups(String format) throws IOException{
        return parentLoader.getFloatingGroups(format);
    }

    public double[] getFloatingScores() throws IOException {
        return parentLoader.getFloatingScores();
    }

    @Override
    public void bindTextures() {

       // parentLoader.bindTextures();

        GL11.glBindTexture(GL30.GL_TEXTURE_2D_ARRAY,   parentLoader.getTextureId());
    }

    @Override
    public void processDirectory(String path, LinkedList<Class> features) throws IOException {

    }

    @Override
    public int[] getOrder(int nthImage, Class feature) throws IOException {
        //return parentLoader.getOrder(nthImage,feature);
        return null;
    }

    @Override
    public double[] getScores(int nthImage, Class feature) throws IOException {
        return parentLoader.getScores(nthImage,feature);
    }

    @Override
    public void clean() {

    }

    @Override
    public int[] getCurrentOrder() {
        //return parentLoader.getCurrentOrder();
        return order;
    }

    @Override
    public double[] getCurrentScores() {
        return scores;
    }

    @Override
    public double[][] getDistanceBasedPositions() {
        return parentLoader.getDistanceBasedPositions();
    }

    @Override
    public int[] getOrderByTime() {
        reorderImagesByTime();
        return order;
    }

    public void reorderImagesByTime(){
        try {
            order = parentLoader.getOrderByTime();
        }catch(Exception e){
            e.printStackTrace();}

        setChanged();
        notifyObservers(new ImageLoderOrderChanged());
    }
    @Override
    public void reorderImages(int nthImage, Class feature) throws IOException {
       //
        if(feature.equals("LastModificationDate")){
            order =parentLoader.getOrderByTime();
            setChanged();
            notifyObservers(new ImageLoderOrderChanged());
        }else{
            order =parentLoader.getOrder(nthImage,feature);
            setChanged();
            notifyObservers(new ImageLoderOrderChanged());}
    }



    @Override
    public void addImages(TreeSet<Integer> imageList) {
        Iterator<Integer> it = imageList.iterator();
        while (it.hasNext()) {
            showed[it.next()]=true;
        }
        setChanged();
        notifyObservers( new NeedShowedImageChange());
    }

    @Override
    public void removeImages(TreeSet<Integer> imageList) {
        Iterator<Integer> it = imageList.iterator();
        while (it.hasNext()) {
            showed[it.next()]=false;
        }
        setChanged();
        notifyObservers( new NeedShowedImageChange());
    }

    @Override
    public boolean[] getCurrentShowedImages() {
        return showed;
    }

    @Override
    public void calculateDistanceMatrix(Class feature) {
        parentLoader.calculateDistanceMatrix(feature);
    }


    @Override
    public String getSavePath() {

        return parentLoader.getSavePath();
    }

    @Override
    public void saveSelectedImagesToDirectory(String savePath, TreeSet<Integer> selectedImages) {

    }

    @Override
    public void update(Observable o, Object arg) {

    }
}
