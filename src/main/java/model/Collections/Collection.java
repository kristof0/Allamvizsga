package model.Collections;

import net.semanticmetadata.lire.imageanalysis.features.GlobalFeature;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.Observable;
import java.util.Observer;

public class Collection extends Observable implements Observer,Comparable<Collection>,Serializable {
    private final String collectionName;
    private String dbName;
    protected LinkedList<Class> features;
    public LinkedList<Class> getFeatures() {
        return features;
    }
    public Collection(String collectionName, LinkedList<Class> features) {
        this.collectionName = collectionName;
        this.features = features;
        this.dbName="";

    }

    public void setDbName(String dbName) {
        this.dbName = "Collections_"+dbName;
    }

    public String getDbName() {
        return dbName;
    }

    public String getCollectionName() {
        return collectionName;
    }

    @Override
    public void update(Observable o, Object arg) {
        setChanged();
        notifyObservers(arg);
    }

    @Override
    public int compareTo(Collection o) {
        return collectionName.compareTo(o.collectionName);
    }
}
