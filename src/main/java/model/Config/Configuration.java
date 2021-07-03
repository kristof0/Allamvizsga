package model.Config;

import controller.Messages.*;
import model.Collections.CollectionManager;

import java.io.*;
import java.util.Observable;
import java.util.Observer;

public class Configuration implements Observer {
    public Configuration(){

    }
   public void load(){
        try{
            loadConfig();
            collectionManager.addObserver(this);
        }
        catch(Exception ex){
            collectionManager=new CollectionManager(defaultProcessedCollectionsPath);

            try {
                saveConfig();
                collectionManager.addObserver(this);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
    private void loadConfig() throws IOException, ClassNotFoundException {
        FileInputStream fi = new FileInputStream(new File(defaultConfigSavePath));
        ObjectInputStream oi = new ObjectInputStream(fi);
        collectionManager = (CollectionManager) oi.readObject();
        oi.close();
        fi.close();
    }
    private void saveConfig() throws IOException {
        System.out.println("Configuratiot.saveConfig()");
        File cfg=new File(defaultConfigSavePath);
        //cfg.deleteOnExit();
        //cfg.createNewFile();
        FileOutputStream f = new FileOutputStream(cfg);
        ObjectOutputStream o = new ObjectOutputStream(f);
        o.writeObject( collectionManager );
        o.close();
        f.close();
    }
    @Override
    public void update(Observable o, Object arg) {
        if(o==collectionManager){
            //System.out.println();
            if(arg instanceof NewDirectoryCollectionCreated) {
                try {
                    saveConfig();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            if(arg instanceof NewVirtualDirectoryCollectionCreated){
                try {
                    saveConfig();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            if(arg instanceof VirtualDirectoryCollectionChanged){
                try {
                    saveConfig();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            if(arg instanceof DirectoryCollectionChanged){
                try {
                    saveConfig();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
            if(arg instanceof CollectionManagerChanged){
                try {
                    saveConfig();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return;
            }
        }
    }
    public CollectionManager getCollectionManager() {
        return collectionManager;
    }

    private final static String defaultConfigSavePath="config.cfg";
    private final static String defaultProcessedCollectionsPath="Collections";
    private CollectionManager collectionManager;
}
