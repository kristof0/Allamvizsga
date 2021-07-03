package model.Collections;

import controller.Messages.VirtualDirectoryCollectionChanged;
import model.Loaders.DefaultVirtualImageLoader;

import java.io.Serializable;
import java.util.TreeSet;

public class VirtualDirectoryCollection extends java.util.Observable implements Serializable {
    private final String collectionName;
    private final TreeSet<Integer> neededImages;
    private final DirectoryCollection parentCollection;
    private final DefaultVirtualImageLoader virtualImageLoader;

    public VirtualDirectoryCollection(String collectionName, int[] neededImages, DirectoryCollection parentCollection) {
        this.collectionName = collectionName;
        this.neededImages = new TreeSet<>();
        for(int i=0;i<neededImages.length;++i){
            this.neededImages.add(neededImages[i]);
        }
        this.parentCollection = parentCollection;
        this.virtualImageLoader=new DefaultVirtualImageLoader(parentCollection.getImageLoader());
    }
    public VirtualDirectoryCollection(String collectionName, TreeSet<Integer> neededImages, DirectoryCollection parentCollection) {
        this.collectionName = collectionName;
        this.neededImages = neededImages;
        //Arrays.sort( this.neededImages );
        this.parentCollection = parentCollection;
        this.virtualImageLoader=new DefaultVirtualImageLoader(parentCollection.getImageLoader());

    }

    public String getCollectionName() {
        return collectionName;
    }

    public TreeSet<Integer> getNeededImages() {
        return neededImages;
    }

    public DirectoryCollection getParentCollection() {
        return parentCollection;
    }

    public DefaultVirtualImageLoader getVirtualImageLoader() {
        return virtualImageLoader;
    }

    public void addImages(TreeSet<Integer> neededImages){
        this.neededImages.addAll(neededImages);
        this.virtualImageLoader.addImages(neededImages);
        setChanged();
        notifyObservers(new VirtualDirectoryCollectionChanged());
    }
    public void removeImages(TreeSet<Integer> notNeededImages){
        this.neededImages.removeAll(notNeededImages);
        this.virtualImageLoader.removeImages(notNeededImages);
        setChanged();
        notifyObservers(new VirtualDirectoryCollectionChanged());
    }
}
