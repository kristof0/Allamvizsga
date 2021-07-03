package model.Collections;

import DB.MongoApi;
import controller.Messages.*;
import model.Loaders.DefaultImageLoader;
import model.Loaders.StandardLoader;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;

public class CollectionManager extends Observable implements Serializable,Observer {
    String processedCollectionsPath;

    public CollectionManager(String processedCollectionsPath) {
        this.processedCollectionsPath = processedCollectionsPath;
        collections=new TreeSet<>();
        new File(processedCollectionsPath).mkdirs();
    }
    public boolean directoryAlreadyProcessed(String path){
        Iterator<DirectoryCollection> it = collections.iterator();
        while (it.hasNext()) {
            DirectoryCollection temp=it.next();
            String ogPath=temp.getOriginalPath();
            if(ogPath.equals(path)){
                File file = new File(path);
                long lastModified=file.lastModified();
                if(lastModified==temp.getLastModified()){
                    return true;
                }
            }
        }
        return false;
    }
    public DirectoryCollection getCollectionByDirectory(String path){
        Iterator<DirectoryCollection> it = collections.iterator();
        while (it.hasNext()) {
            DirectoryCollection temp=it.next();
            String ogPath=temp.getOriginalPath();
            if(ogPath.equals(path)){
                File file = new File(path);
                long lastModified=file.lastModified();
                if(lastModified==temp.getLastModified()){
                    return temp;
                }
            }
        }
        return null;
    }
    public DirectoryCollection getCollectionByName(String collectionName){

        return collections.floor(new DirectoryCollection(collectionName));
        //TODO OPTIMALIZALNI
/*        Iterator<DirectoryCollection> it = collections.iterator();
        while (it.hasNext()) {
            DirectoryCollection temp=it.next();
            if(temp.getCollectionName().equals(collectionName)){
                return temp;
            }
        }
        return null;*/
    }
    public void processDirectory(model.Collections.Collection collection, String path) throws IOException {

        if(directoryAlreadyProcessed(path)){
            System.out.println("Collection already processed !");
            return;
        }
       DirectoryCollection dirCollection=new DirectoryCollection(
               collection,
               path,
               new DefaultImageLoader(new StandardLoader(processedCollectionsPath+"\\"+collection.getCollectionName()))
       );
        dirCollection.addObserver(this);
        System.out.println("Collection manager:processDirectory()");
        dirCollection.processImages();

    }
    public void loadCollection(String path) throws IOException {
        DirectoryCollection dirCollection= getCollectionByDirectory(path);

        if(null!=dirCollection){
            System.out.println("Load Collection ! !");
            dirCollection.addObserver(this);
            dirCollection.loadCollection();
        }
    }
    public HashSet<String> getCollectionNames(){
        HashSet<String> ret=new HashSet<>();
        Iterator<DirectoryCollection> it = collections.iterator();
        while (it.hasNext()) {
            ret.add(it.next().getCollectionName());
        }
        return ret;
    }
    public VirtualDirectoryCollection getVirtualDirectoryCollection(String collectionName,String virtualCollectionName){
        DirectoryCollection dirCollection=getCollectionByName(collectionName);
        if(null!=dirCollection){
            return dirCollection.getVirtualCollectionByName(virtualCollectionName);
        }
        else{
            return null;
        }
    }

    private final TreeSet<DirectoryCollection> collections;


    @Override
    public void update(Observable o, Object arg) {
        System.out.println("CollectionManager.update");
        if(o instanceof DirectoryCollection){
            if(arg instanceof FinishedCollectionProcessingMessage) {
                System.out.println("CollectionManager.update:FinishedCollectionProcessingMessage");
                collections.add((DirectoryCollection) o);
                setChanged();
                notifyObservers(new NewDirectoryCollectionCreated((DirectoryCollection)o));
                return;
            }
            if(arg instanceof FinishedProcessedCollectionLoading){
                System.out.println("CollectionManager.update:FinishedProcessedCollectionLoading");
                setChanged();
                notifyObservers(new CollectionLoadedMessage((DirectoryCollection)o));
                return;
            }
        }
        setChanged();
        notifyObservers(arg);
    }
    public void loadCollectionByName(String collectionName) throws IOException {
        DirectoryCollection dirCollection= getCollectionByName(collectionName);
        if(null!=dirCollection){
            System.out.println("Load Collection ! !");
            dirCollection.addObserver(this);
            dirCollection.loadCollection();
        }
    }
    public void reloadCollectionByName(String collectionName) throws IOException {
        DirectoryCollection dirCollection= getCollectionByName(collectionName);
        if(null!=dirCollection){
            System.out.println("Load Collection ! !");
            dirCollection.addObserver(this);
            ArrayList<String> lala=new ArrayList<>();
            lala.add("person");
            lala.add("laptop");
            lala.add("subidubi");


            dirCollection.reloadCustomCollection();
        }
    }
/*    public VirtualDirectoryCollection createVirtualDirectoryCollection(String collectionName,String virtualCollectionName,int[] neededImages) throws IOException {
        DirectoryCollection dirCollection= getCollectionByName(collectionName);
        if(null!=dirCollection){
            dirCollection.addObserver(this);
            return dirCollection.createVirtualCollection(virtualCollectionName,neededImages);
        }
        return null;
    }*/

    public VirtualDirectoryCollection createVirtualDirectoryCollection(String collectionName,String virtualCollectionName,TreeSet<Integer> neededImages) throws IOException {
        DirectoryCollection dirCollection= getCollectionByName(collectionName);
        if(null!=dirCollection){
            dirCollection.addObserver(this);
            return dirCollection.createVirtualCollection(virtualCollectionName,neededImages);
        }
        return null;
    }
    public void addImagesToVirtualDirectoryCollection(String directoryCollectionName ,String virtualDirectoryCollectionName ,TreeSet<Integer> neededImages ){
        DirectoryCollection dirCollection= getCollectionByName(directoryCollectionName);
        if(null!=dirCollection){
            dirCollection.addObserver(this);
            dirCollection.addImagesToVirtualDirectoryCollection(virtualDirectoryCollectionName,neededImages);
        }
    }
    public void removeImagesFromVirtualDirectoryCollection(String directoryCollectionName ,String virtualDirectoryCollectionName ,TreeSet<Integer> notNeededImages ){
        DirectoryCollection dirCollection= getCollectionByName(directoryCollectionName);
        if(null!=dirCollection){
            dirCollection.addObserver(this);
            dirCollection.removeImagesFromVirtualDirectoryCollection(virtualDirectoryCollectionName,notNeededImages);
        }
    }
    public void deleteDirectoryCollection(String collectioName){
        Iterator<DirectoryCollection> it = collections.iterator();
        while (it.hasNext()) {
            DirectoryCollection temp=it.next();
            if(temp.getCollectionName().equals(collectioName)){
                it.remove();
                setChanged();
                notifyObservers(new CollectionManagerChanged() );
                return;
            }
        }
    }
    public void deleteVirtualDirectoryCollection(String collectionName,String virtualCollectionName){
        DirectoryCollection dirCollection= getCollectionByName(collectionName);
        if(null!=dirCollection){
            dirCollection.addObserver(this);
            dirCollection.deleteVirtualCollection(virtualCollectionName);

            //dirCollection.createVirtualCollection(virtualCollectionName,neededImages);
        }

    }


/*    Object search(TreeSet treeset, Object key) {
        Object ceil  = treeset.ceiling(key); // least elt >= key
        Object floor = treeset.floor(key);   // highest elt <= key
        return ceil == floor? ceil : null;
    }*/
}
