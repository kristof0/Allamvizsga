package model.Loaders;

import controller.Messages.FinishedProcessedCollectionLoading;
import controller.Messages.ImageLoderOrderChanged;
import controller.Messages.NeedShowedImageChange;
import customFeatures.LastModificationDate;
import org.apache.lucene.queryparser.classic.ParseException;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class DefaultImageLoader extends Observable implements Serializable,Observer,IImageLoader  {
    private final StandardLoader standardLoader;
    transient private int[] order;
    transient private double[] scores;

    private boolean [] showed;


    public DefaultImageLoader(StandardLoader standardLoader) {
        this.standardLoader = standardLoader;
        this.standardLoader.addObserver(this);
       // this.showed=new boolean[]
    }

    public LinkedList<Integer> getGroupsByDate(){ return standardLoader.getGroupsByDate();}

    public int getMaxNumberOfRows(){
        return standardLoader.getMaxNumberOfRows();
    }
    public int getNumberOfCols(){
        return standardLoader.maxColSize;
    }
    @Override
    public int[] getCurrentOrder() {
        //return standardLoader.getOrderByTime();
        return order;
    }

    public HashMap<Integer, Integer> getFloatingGroups(String format) throws IOException{
        return standardLoader.getFloatingGroups(format);
    }

    public double[] getFloatingScores() throws IOException {
        return standardLoader.getFloatingScores();
    }
    @Override
    public double[] getCurrentScores() {
        return scores;
    }

    @Override
    public int[] getOrderByTime() throws IOException {
        return standardLoader.getOrderByTime();
    }

    public void reorderImagesByTime(){
        try {
            order = standardLoader.getOrderByTime();
        }catch(Exception e){
            e.printStackTrace();}

        setChanged();
        notifyObservers(new ImageLoderOrderChanged());
    }

    @Override
    public double[][] getDistanceBasedPositions() {


        return standardLoader.getDistanceBasedPositions();
    }

    @Override
    public void reorderImages(int nthImage, Class feature) throws IOException {

        if(feature.getClass().equals(LastModificationDate.class)){
            //reorderImagesByTime();
            order = standardLoader.getOrderByTime();
            scores=new double[getNumberOfImages()];
            setChanged();
            notifyObservers(new ImageLoderOrderChanged());
        }else {

            scores=standardLoader.getScores(nthImage,feature);
            order = standardLoader.getOrder(nthImage, feature);

            setChanged();
            notifyObservers(new ImageLoderOrderChanged());
        }
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
        standardLoader.calculateDistanceMatrix(feature);
    }

    @Override
    public int getNumberOfImagesPerTexture() {
        return standardLoader.getNumberOfImagesPerTexture();
    }

    @Override
    public int getNumberOfTextures() {
        return standardLoader.getNumberOfTextures();
    }

    @Override
    public int getNumberOfImages() {
        return standardLoader.getNumberOfImages();
    }

    @Override
    public int getTextureId() {
        return standardLoader.getTextureId();
    }

    @Override
    public float[] getTextCoordinate(int nthImage) {
        return standardLoader.getTextCoordinate(nthImage);
    }

    @Override
    public void setTextCoordinate(float[] textCoords, int nthPos, int nthPic) {
        standardLoader.setTextCoordinate(textCoords,nthPos,nthPic);
    }

    @Override
    public int getTextTable(int nthImage) {
        return standardLoader.getTextTable(nthImage);
    }

    @Override
    public void loadTextures() throws IOException {
        order=new int[standardLoader.getNumberOfImages()];
        for (int u = 0; u < order.length; ++u) {
            order[u] = u;

        }
        scores=new double[standardLoader.getNumberOfImages()];
        for (int u = 0; u < scores.length; ++u) {
            scores[u] = 1;

        }

        standardLoader.addObserver(this);

        standardLoader.loadTextures();
    }
    public void reloadTextures() throws IOException {
        order=new int[standardLoader.getNumberOfImages()];
        for (int u = 0; u < order.length; ++u) {
            order[u] = u;

        }
        standardLoader.addObserver(this);

        standardLoader.reloadTextures();
    }


    @Override
    public void bindTextures() {
        standardLoader.bindTextures();
    }

    @Override
    public void processDirectory(String path, LinkedList<Class> features) throws IOException {
        standardLoader.addObserver(this);
        standardLoader.processDirectory(path,features);
    }

    @Override
    public int[] getOrder(int nthImage, Class feature) throws IOException {

        return standardLoader.getOrder(nthImage,feature);
    }

    @Override
    public double[] getScores(int nthImage, Class feature) throws IOException {
        return standardLoader.getScores(nthImage,feature);
    }


    @Override
    public void clean() {
        standardLoader.clean();
    }

    @Override
    public String getSavePath() {
        return standardLoader.getSavePath();
    }

    @Override
    public void saveSelectedImagesToDirectory(String savePath, TreeSet<Integer> selectedImages) throws IOException {
        standardLoader.saveSelectedImagesToDirectory(savePath,selectedImages);
    }

    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof FinishedProcessedCollectionLoading){
            this.showed=new boolean[standardLoader.getNumberOfImages()];
            for(int i=0;i<this.showed.length;++i){
                this.showed[i]=true;
            }
        }
        System.out.println("DEFAULTIMAGELAODE UPDATEEEEEE");
        setChanged();
        notifyObservers(arg);
    }

    public TreeSet<Integer> getImagesByLabel(HashSet<String> strings) throws IOException, ParseException {

         return standardLoader.getImagesByLabel(strings);
    }


}
