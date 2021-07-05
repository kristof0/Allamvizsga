package model.Collections;


import DB.MongoApi;
import controller.Messages.DirectoryCollectionChanged;
import controller.Messages.NewVirtualDirectoryCollectionCreated;
import model.Loaders.DefaultImageLoader;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class DirectoryCollection extends Observable implements Observer,Comparable<DirectoryCollection>,Serializable {
//
    private final String collectionName;
    protected LinkedList<Class> features;
    protected List<String> detectedObjects;
    protected transient MongoApi DB;
    protected String DbName;
    public LinkedList<Class> getFeatures() {return features;}


    public List<String> getDetectedObjects() {
        this.DB=new MongoApi(this.DbName);
        //HashSet<String> hashSet=
        try {
            detectedObjects= MongoApi.getDetectedObjects(DbName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return detectedObjects;
        }
    public String getCollectionName() {
        return collectionName;
    }

    ////
    private String originalPath;
    private long lastModified;
    private DefaultImageLoader imageLoader;
    //TODO MAS ADATSZERKEZETRE MEGOLDANI
    private LinkedList<VirtualDirectoryCollection> virtualCollectionList;
    public DirectoryCollection(String teszt){
        this.collectionName=teszt;
    }
    public DirectoryCollection(model.Collections.Collection collection, String originalPath, DefaultImageLoader imageLoader) {

        this.collectionName = collection.getCollectionName();
        this.features = collection.getFeatures();
        this.originalPath = originalPath;
        File file = new File(originalPath);
        this.lastModified = file.lastModified();
        this.imageLoader = imageLoader;
        virtualCollectionList=new LinkedList<>();
        imageLoader.addObserver(this);
        if(collection.getDbName()!=""){
            this.DB=new MongoApi(collection.getDbName());
            this.DbName=collection.getDbName();
        }
        //this.detectedObjects=collection.getDbName();
        //TODO NE FELEJDS A LAST MODIFIEDOT
    }

    public void processImages() throws IOException {
        File file = new File(originalPath);
        lastModified=file.lastModified();
        imageLoader.processDirectory(originalPath,features);
    }

    public void reloadCustomCollection() throws IOException {
        imageLoader.addObserver(this);
        imageLoader.reloadTextures();

    }
    public void loadCollection() throws IOException {
        imageLoader.addObserver(this);
        imageLoader.loadTextures();

    }
    public String getOriginalPath() {
        return originalPath;
    }
    public long getLastModified() {
        return lastModified;
    }
    public DefaultImageLoader getImageLoader() {
        return imageLoader;
    }

    public VirtualDirectoryCollection createVirtualCollection(String virtualCollectionName,TreeSet<Integer> neededImages) throws IOException {
        //TODO HA MAR LETEZIK ILYEN NEVU VIRT COLLEKCIO
        VirtualDirectoryCollection temp=new VirtualDirectoryCollection(virtualCollectionName,neededImages,this);
        virtualCollectionList.add(temp);
        temp.addObserver(this);
        temp.addImages(neededImages);
        setChanged();
        notifyObservers(new NewVirtualDirectoryCollectionCreated(temp));
        return temp;
    }
    public HashSet<String> getVirtualCollectionNames(){
        HashSet<String> ret=new HashSet<>();
        Iterator<VirtualDirectoryCollection> it = virtualCollectionList.iterator();
        while (it.hasNext()) {
            ret.add(it.next().getCollectionName());
        }
        return ret;
    }
    public VirtualDirectoryCollection getVirtualCollectionByName(String collectionName){
        //TODO OPTIMALIZED DATA STRUCTURE
        Iterator<VirtualDirectoryCollection> it = virtualCollectionList.iterator();
        while (it.hasNext()) {
            VirtualDirectoryCollection temp=it.next();
            if(temp.getCollectionName().equals(collectionName)){
                return temp;
            }
        }
        return null;
    }
    public void addImagesToVirtualDirectoryCollection(String virtualDirectoryCollectionName ,TreeSet<Integer> neededImages ){
        VirtualDirectoryCollection temp=getVirtualCollectionByName(virtualDirectoryCollectionName);
        temp.addObserver(this);
        temp.addImages(neededImages);
    }
    public void removeImagesFromVirtualDirectoryCollection(String virtualDirectoryCollectionName ,TreeSet<Integer> notNeededImages ){
        VirtualDirectoryCollection temp=getVirtualCollectionByName(virtualDirectoryCollectionName);
        temp.addObserver(this);
        temp.removeImages(notNeededImages);
    }
    public void deleteVirtualCollection(String virtualDirectoryCollectionName){
        Iterator<VirtualDirectoryCollection> it = virtualCollectionList.iterator();
        while (it.hasNext()) {
            VirtualDirectoryCollection temp=it.next();
            if(temp.getCollectionName().equals(virtualDirectoryCollectionName)){
                it.remove();
                setChanged();
                notifyObservers(new DirectoryCollectionChanged());
                return;
            }
        }

    }
    @Override
    public int compareTo(DirectoryCollection o) {
        return collectionName.compareTo(o.collectionName);
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("DIRECTORYCOLLECTION UPDATE:"+arg.getClass());
        setChanged();
        notifyObservers(arg);
    }


    public HashSet<String> getIdentifiersForLabels(ArrayList<String> selectedObjectsArr) {
        return this.DB.getIdentifiersForLabelsFromDb(selectedObjectsArr);
    }
}

