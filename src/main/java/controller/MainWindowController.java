package controller;


import Main.Main;
import controller.Messages.*;
import controller.State.*;
import customFeatures.LastModificationDate;


import customFeatures.ObjectDetection;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import model.Collections.Collection;
import model.Collections.CollectionManager;
import model.Collections.DirectoryCollection;
import model.Collections.VirtualDirectoryCollection;
import model.Config.Configuration;
import model.Loaders.DefaultImageLoader;
import net.semanticmetadata.lire.imageanalysis.features.global.*;
import org.apache.lucene.queryparser.classic.ParseException;
import view.CollectionViews.*;
import view.engine.GameEngine;
import view.engine.IGameLogic;
import view.engine.Window;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ExecutionException;

public class MainWindowController extends Observable implements Initializable, Observer {
    private Configuration config;
    private CollectionManager collectionManager;
    public ProgressBar progressBar;
    public GameEngine collectionWindow =null;
    private String currentlySelectedCollectionName= "";
    private DefaultImageLoader imageLoader;
    private DirectoryCollection currentlyLoadedDirectoryCollection;
    private GameEngine virtualCollectionWindow=null;
    private VirtualDirectoryCollection currentlyLoadedVirtualDirectoryCollection;
    private IState directoryCollectionDisplaying;
    private IState directoryCollectionLoading;
    private IState directoryCollectionProcessing;
    private IState notLoaded;
    private IState virtualDirectoryCollectionCreating;
    private IState virtualDirectoryCollectionDisplaying;
    private IState virtualDirectoryCollectionLoading;
    private IState currentState;
    private IState waitingForPathInput;
    private IState checkDirectory;
    private IState waitingForCollectionNameInput;
    private IState virtualCollectionWaitingForNameInput;
    private IState virtualDirectoryCollectionProcessing;
    private IState virtualDirectoryCollectionSavingToFolder;
    private IState virtualDirectoryCollectionWaitingForSavePathInput;
    private String lastPathInput="D:\\";
    private String lastSaveInput="D:\\";

    private static final String GRID_VIEW="Grid view";
    private static final String TIMEHISTOGRAM_VIEW="Time histogram";
    private static final String SPIRAL_VIEW="Spiral View";
    private static final String SPIRAL3D_VIEW="Spiral View 3D";
    private static final String TSNE_VIEW="t-SNE based View";
    private static final String DISTANCE_VIEW="Wave View";
    private static final String CIRCULARHIST3D_VIEW="Circular Time Histogram 3D";
    private static final String CIRCULARHIST2D_VIEW="Circular Time Histogram";
    private static final String SPIRALHIST3D_VIEW="Spiral Time Histogram";
    private static final String FLOATINGHIST_VIEW="Floating Histogram";



    @FXML
    private Menu menuOpen;
    @FXML
    private ListView collectionsListView;
    @FXML
    private ListView virtualCollectionsListView;
    @FXML
    private ListView featuresListView;

    @FXML
    private ListView detectedObjectListView;

    private HashMap<String,Class> features;

    @FXML
    private Button markImagesButton;
    @FXML
    private Button reorderButton;
    @FXML
    private Button resetCameraButton;
    @FXML
    private Button createNewVirtualCollectionButton;
    @FXML
    private Button addSelectedImagesToVirtualCollectionButton;
    @FXML
    private Button removeSelectedImagesFromVirtualCollectionButton;
    @FXML
    private Button reorderVirtualCollectionButton;
    @FXML
    private Button resetVirtualCollectionCameraButton;
    @FXML
    private Button saveSelectedImagesFromVirtualCollectionButton;
    @FXML
    private Button deleteCollectionButton;
    @FXML
    private Button deleteVirtualCollectionButton;
    @FXML
    private Button closeCollectionButton;
    @FXML
    private Button closeVirtualCollectionButton;
    @FXML
    private ComboBox<String> viewsList;


    public String getCurrentView(){
        return this.viewsList.getValue();
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {

        System.out.println("Inicializalas...");
        //State initialization
        directoryCollectionDisplaying=new DirectoryCollectionDisplaying(this);
        directoryCollectionLoading=new DirectoryCollectionLoading(this);
        directoryCollectionProcessing=new DirectoryCollectionProcessing(this);
        notLoaded=new NotLoaded(this);
        virtualDirectoryCollectionCreating=new VirtualDirectoryCollectionCreating(this);
        virtualDirectoryCollectionDisplaying=new VirtualDirectoryCollectionDisplaying(this);
        virtualDirectoryCollectionLoading=new VirtualDirectoryCollectionLoading(this);
        waitingForPathInput=new WaitingForPathInput(this);
        checkDirectory=new CheckDirectory(this);
        waitingForCollectionNameInput=new WaitingForCollectionNameInput(this);
        virtualCollectionWaitingForNameInput=new VirtualCollectionWaitingForNameInput(this);
        virtualDirectoryCollectionProcessing=new VirtualDirectoryCollectionProcessing(this);
        virtualDirectoryCollectionSavingToFolder=new VirtualDirectoryCollectionSavingToFolder(this);
        virtualDirectoryCollectionWaitingForSavePathInput= new VirtualDirectoryCollectionWaitingForSavePathInput(this);
        setCurrentState(notLoaded);

        fillViewsList();
        config=new Configuration();
        config.load();
        collectionManager=config.getCollectionManager();


        detectedObjectListView.getSelectionModel().setSelectionMode(
                SelectionMode.MULTIPLE
        );

        HashSet<String> names=collectionManager.getCollectionNames();

        ObservableList<String> items = FXCollections.observableArrayList (
                names);
        collectionsListView.setItems(items);

        deleteCollectionButton.disableProperty().bind(Bindings.isEmpty(collectionsListView.getSelectionModel().getSelectedItems()));
        deleteVirtualCollectionButton.disableProperty().bind(Bindings.isEmpty(virtualCollectionsListView.getSelectionModel().getSelectedItems()));


      //  Bindings.isEmpty(virtualCollectionsListView.getItems()).addListener(
               // (obs, wasEmpty, isNowEmpty) -> deleteVirtualCollectionButton.setDisable(isNowEmpty));

        features=new HashMap<String, Class>();
        features.put(CEDD.class.getSimpleName(),CEDD.class);
        features.put(JCD.class.getSimpleName(),JCD.class);
        features.put(PHOG.class.getSimpleName(),PHOG.class);
        features.put(ACCID.class.getSimpleName(),ACCID.class);
        features.put(AutoColorCorrelogram.class.getSimpleName(),AutoColorCorrelogram.class);
        features.put(BinaryPatternsPyramid.class.getSimpleName(),BinaryPatternsPyramid.class);
        features.put(COMO.class.getSimpleName(),COMO.class);
        features.put(ColorLayout.class.getSimpleName(),ColorLayout.class);
        features.put(EdgeHistogram.class.getSimpleName(),EdgeHistogram.class);
        features.put(FCTH.class.getSimpleName(),FCTH.class);
        features.put(FuzzyColorHistogram.class.getSimpleName(),FuzzyColorHistogram.class);
        features.put(FuzzyOpponentHistogram.class.getSimpleName(),FuzzyOpponentHistogram.class);
        features.put(Gabor.class.getSimpleName(),Gabor.class);
       // features.put(GenericGlobalDoubleFeature.class.getSimpleName(),GenericGlobalDoubleFeature.class);
        features.put(JpegCoefficientHistogram.class.getSimpleName(),JpegCoefficientHistogram.class);
        features.put(LocalBinaryPatterns.class.getSimpleName(),LocalBinaryPatterns.class);
        features.put(LuminanceLayout.class.getSimpleName(),LuminanceLayout.class);
        features.put(OpponentHistogram.class.getSimpleName(),OpponentHistogram.class);
        features.put(RotationInvariantLocalBinaryPatterns.class.getSimpleName(),RotationInvariantLocalBinaryPatterns.class);
        features.put(ScalableColor.class.getSimpleName(),ScalableColor.class);
        features.put(SimpleColorHistogram.class.getSimpleName(),SimpleColorHistogram.class);
        features.put(Tamura.class.getSimpleName(),Tamura.class);
        features.put(AutoColorCorrelogram.class.getSimpleName(),AutoColorCorrelogram.class);
        features.put(AutoColorCorrelogram.class.getSimpleName(),AutoColorCorrelogram.class);
        features.put(LastModificationDate.class.getSimpleName(), LastModificationDate.class);
        features.put(ObjectDetection.class.getSimpleName(),ObjectDetection.class);


    }
    @FXML
    public void collectionsListViewOnClick(MouseEvent mouseEvent) throws IOException {
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
            if(mouseEvent.getClickCount() == 2){
                String collectionName=(String)collectionsListView.getSelectionModel().getSelectedItem();
                if(null!=collectionName) {

                    System.out.println("SELECTED:"+collectionName+"     ");
                    DirectoryCollection directoryCollection=collectionManager.getCollectionByName(collectionName);
                    updateDetectedObjectsListView(directoryCollection);


                    currentState.loadDirectoryCollection(directoryCollection);
                }
            }
        }
    }
    @FXML
    public void virtulCollectionsListViewOnClick(MouseEvent mouseEvent) throws IOException{
        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
            if(mouseEvent.getClickCount() == 2){
                String collectionName=(String)virtualCollectionsListView.getSelectionModel().getSelectedItem();
                if(null!=collectionName) {
                    System.out.println("Virtual collection SELECTED:"+collectionName);
                    currentState.loadVirtualDirectoryCollection(collectionName);
                }
            }
        }
    }
    public void updateFeatureListView(DirectoryCollection collection){
        ObservableList<String> items = FXCollections.observableArrayList ();
        for(Class it:collection.getFeatures()){
            for(Map.Entry<String,Class> entry : features.entrySet()) {
                if (entry.getValue() == it) {
                    items.add(entry.getKey());
                    break;
                }
            }
        }
        if(items.contains(ObjectDetection.class.getSimpleName())){items.remove(ObjectDetection.class.getSimpleName());}
        featuresListView.setItems(items);
        featuresListView.getSelectionModel().select(0);
    }
    public void updateDetectedObjectsListView(DirectoryCollection collection){
        if(!collection.getFeatures().contains(ObjectDetection.class)){return;}
        ObservableList<String> items = FXCollections.observableArrayList ();
        items.addAll(collection.getDetectedObjects());

        detectedObjectListView.setItems(items);

    }

    public void menuItemOpenOnClick() throws IOException {
        currentState.createNewDirectoryCollection();
    }
    public void fillViewsList(){
        ObservableList<String> tempList=viewsList.getItems();
        tempList.addAll(GRID_VIEW,FLOATINGHIST_VIEW,TIMEHISTOGRAM_VIEW,SPIRALHIST3D_VIEW,SPIRAL_VIEW,SPIRAL3D_VIEW,TSNE_VIEW,DISTANCE_VIEW,CIRCULARHIST3D_VIEW,CIRCULARHIST2D_VIEW);

        viewsList.setValue(GRID_VIEW);
    }

    public void reorderButtonOnClickListener(){
        currentState.reorderImages();
    }

    public ArrayList<String> getSelectedLabels(){
        ObservableList<String> selectedItems=detectedObjectListView.getSelectionModel().getSelectedItems();
        ArrayList<String> temp=new ArrayList<>();
        temp.addAll(selectedItems);
        return temp;
    }


    public void reorderVirtualCollectionButtonOnClickListener(){
        currentState.reorderVirtualCollection();
        //currentState.reorderImages();
    }
    public void resetCameraOnClick(){
        currentState.resetCamera();
    }
    public void resetVirtualCollectionCameraOnClick(){
        currentState.resetVirtualCollectionCamera();
    }
    public void markImagesButtonOnClickListener() throws IOException {
        ObservableList<String> selectedObjects=detectedObjectListView.getSelectionModel().getSelectedItems();
        ArrayList<String> selectedObjectsArr=new ArrayList<>(selectedObjects);

        TreeSet<Integer> temp2=new TreeSet<>();
        HashSet<String> temptree=currentlyLoadedDirectoryCollection.getIdentifiersForLabels(selectedObjectsArr);
        try {
            temp2=imageLoader.getImagesByLabel(temptree);

        } catch (ParseException e) {
            e.printStackTrace();
        }

        collectionWindow.setMarkedImages(temp2);


        //currentState.createVirtualDirectoryCollectionOfObjects();

    }
    public void createNewVirtualCollectionButtonOnClick() throws IOException {
        System.out.println("Virtual gomb megnyomva");

        //currentlyLoadedDirectoryCollection.getImageLoader().saveSelectedImagesToDirectory("teszt",new int[]{0,1,2,31,59});
        currentState.createVirtualDirectoryCollection();
    }
    public void addSelectedImagesToVirtualCollectionButtonOnClick(){
        System.out.println("addSelectedImagesToVirtualCollectionButtonOnClick");
        currentState.addSelectedImagesToVirtualCollection();
    }
    public void removeSelectedImagesFromVirtualCollectionButtonOnClick(){
        System.out.println("removeSelectedImagesFromVirtualCollectionButtonOnClick");
        //currentState.addSelectedImagesToVirtualCollection();
        currentState.removeSelectedImagesFromVirtualCollection();
    }
    public void saveSelectedImagesFromVirtualCollectionButtonOnClick() throws IOException {
        System.out.println("saveSelectedImagesFromVirtualCollection");
        currentState.saveSelectedImagesFromVirtualCollection();
    }
    public void deleteCollectionButtonOnClick(){
        currentState.deleteDirectoryCollection();
    }
    public void deleteVirtualCollectionButtonOnClick(){
        currentState.deleteVirtualDirectoryCollection();
    }
    public void closeCollectionButtonOnClick(){
        printCurrentState();
        currentState.closeDirectoryCollection();

    }
    public void closeVirtualCollectionButtonOnClick(){
        currentState.closeVirtualCollection();
    }
    @Override
    public void update(Observable o, Object arg) {
        if(arg instanceof ProgressMessage){
            System.out.println("Progess:"+((ProgressMessage) arg).getProgress());
            progressBar.setProgress(((ProgressMessage) arg).getProgress());
            return;
        }
        if(arg instanceof NewDirectoryCollectionCreated){
            Platform.runLater(
                    () ->{
                        try {
                            directoryCollectionProcessingCompleted(((NewDirectoryCollectionCreated) arg).getCollection());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );

            return;
        }
        if(arg instanceof CollectionLoadedMessage){
            System.out.println("CollectionLoadedMessage");

            Platform.runLater(
                    () ->{
                        try {
                            directoryCollectionLoaded(((CollectionLoadedMessage)arg).getCollection());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
            );
            return;
        }
        if(arg instanceof VirtualDirectoryCollectionSavingToFolderCompleted){
            System.out.println("VirtualDirectoryCollectionSavingToFolderCompleted");
            Platform.runLater(
                    () ->{
                        currentState.selectedVirtualImagesSaved();
                    }
            );
            return;
        }
        if(arg instanceof NoImagesFound){
            Platform.runLater(
                    () ->{
                        System.out.println("TRRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                        printCurrentState();
                        System.out.println("TRRAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
                        currentState.processDirectoryCollectionFailedNoImagesFound();
                    }
            );
            return;
        }
    }

    public void getPathInput() throws IOException {
/*        System.out.println("MainWindiw:getPathInput()");
        JFileChooser f = new JFileChooser();
        f.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        f.showSaveDialog(null);*/
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Some Directories");
        try {
            directoryChooser.setInitialDirectory(new File(lastPathInput));
        }
        catch(Exception ex){

        }

        File dir=directoryChooser.showDialog(Main.mainStage);
        if(dir!=null) {
            lastPathInput=dir.getPath();
            currentState.directoryPathInput(lastPathInput);
        }
        else
        {
            currentState.directoryPathInputFailed();
        }
    }
    public void checkIfAlreadyProcessed(String selectedDir) throws IOException {
        DirectoryCollection result=collectionManager.getCollectionByDirectory(selectedDir);
        if(null!=result){
            currentState.directoryAlreadyProcessed(result);
        }
        else {
            currentState.processDirectory();
        }
    }
    public boolean directoryAlreadyProcessed(String selectedDir){
        return collectionManager.directoryAlreadyProcessed(selectedDir);
    }
    public void getDirectoryCollectionInput() throws IOException {
        Collection collection =  new NewCollectionNameInput().display(collectionManager.getCollectionNames(),features);
        if(collection.getFeatures().contains(ObjectDetection.class)){collection.setDbName(collection.getCollectionName());}
        if (collection == null){
            currentState.directoryNameInputFailed();
            return;
        }
        else{
            if(collection.getCollectionName()==null||collection.getFeatures()==null||collection.getFeatures().size()==0){
                currentState.directoryNameInputFailed();
                return;
            }
            currentState.processDirectoryCollection(collection);
            return;
        }

    }
    public void addNewDirectoryCollectionToUi(DirectoryCollection collection){
        collectionsListView.getItems().add(collection.getCollectionName());
        updateFeatureListView(collection);
        updateDetectedObjectsListView(collection);
    }
    public void directoryCollectionProcessingCompleted(DirectoryCollection directoryCollection) throws IOException {
        currentState.directoryCollectionProcessingFinished(directoryCollection);
    }
    public void processDirectoryCollection(Collection collection,String path) throws IOException {
        collectionManager.addObserver(this);
        collectionManager.processDirectory(collection,path);
    }
    public void loadDirectoryCollection(DirectoryCollection collection) throws IOException {
        System.out.println("loadDirectoryCollection!!!!!!!!!!!!!");
        collectionManager.addObserver(this);
        collectionManager.loadCollectionByName(collection.getCollectionName());


        //collection.loadCollection();
    }
    public void reloadDirectoryCollection(DirectoryCollection collection) throws IOException{
        System.out.println("reloadDirectoryCollection");
        collectionManager.addObserver(this);
        collectionManager.reloadCollectionByName(collection.getCollectionName());
    }

    public void directoryCollectionLoaded(DirectoryCollection collection) throws IOException {
        //this.currentlyLoadedDirectoryCollection=collection;
        currentState.directoryCollectionLoadingFinished(collection);
    }
    public void displayDirectoryCollection(DirectoryCollection collection){
            System.out.println("displayDirectoryCollection");
            //closeWindow();
            System.out.println("displayDirectoryCollection2");
            boolean vSync = true;
            imageLoader=collection.getImageLoader();
            currentlySelectedCollectionName=collection.getCollectionName();
            if(null!=this.currentlyLoadedDirectoryCollection&&this.currentlyLoadedDirectoryCollection!=collection){
                //TODO FELSZABADITAS MEGOLDASA
                //this.currentlyLoadedDirectoryCollection.unloadCollection();
            }
            this.currentlyLoadedDirectoryCollection=collection;
            updateFeatureListView(this.currentlyLoadedDirectoryCollection);
            updateDetectedObjectsListView(this.currentlyLoadedDirectoryCollection);

     ////
        IGameLogic gameLogic=null ;
       // String currView=viewsList.getValue();
        if(this.getCurrentView()==SPIRAL_VIEW){gameLogic = new SpiralView(imageLoader);}
        if(this.getCurrentView()==SPIRAL3D_VIEW){gameLogic = new SpiralView3D(imageLoader);}
        if(this.getCurrentView()==GRID_VIEW) {gameLogic=new GridView(imageLoader);}
        if(this.getCurrentView()==TIMEHISTOGRAM_VIEW) {gameLogic=new DateTimeHistogram(imageLoader);}
        if(this.getCurrentView()==TSNE_VIEW) {gameLogic=new tSNEBasedView(imageLoader);}
        if(this.getCurrentView()==DISTANCE_VIEW) {gameLogic=new WaveView(imageLoader);}
        if(this.getCurrentView()==CIRCULARHIST3D_VIEW) {gameLogic=new CircularDateHistogram3D(imageLoader);}
        if(this.getCurrentView()==CIRCULARHIST2D_VIEW) {gameLogic=new CircularDateHistogram2D(imageLoader);}
        if(this.getCurrentView()==SPIRALHIST3D_VIEW) {gameLogic=new SpiralDateHistogram3D(imageLoader);}
        if(this.getCurrentView()==FLOATINGHIST_VIEW) {gameLogic=new FloatingHistogramView(imageLoader);}


            view.engine.Window.WindowOptions opts = new Window.WindowOptions();
            opts.cullFace = true;
            opts.showFps = true;
            opts.compatibleProfile = true;
            opts.antialiasing = true;

            try {
                collectionWindow = new GameEngine(collection.getCollectionName(), vSync, opts, gameLogic);
                //Communicatiob between GameEngine and the ImadeLoader
                imageLoader.addObserver(collectionWindow);
                this.addObserver(collectionWindow);
                collectionWindow.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
            //collectionManager.deleteObserver(this);
    }
    public void updateVirtualCollectionListView(){
        if(null!=currentlyLoadedDirectoryCollection){
            HashSet<String> names=currentlyLoadedDirectoryCollection.getVirtualCollectionNames();
            ObservableList<String> items = FXCollections.observableArrayList (
                    names);
            virtualCollectionsListView.setItems(items);
        }
        else{
            virtualCollectionsListView.getItems().clear();
        }
    }
    public void clearVirtualCollectionListView(){
        virtualCollectionsListView.getItems().clear();
    }
    public void updateDirectoryCollectionListView(){
        HashSet<String> names=collectionManager.getCollectionNames();

        ObservableList<String> items = FXCollections.observableArrayList (
                names);
        collectionsListView.setItems(items);
    }
    public void clearFeatureListView(){
        featuresListView.getItems().clear();
    }
    public void clearDetectedObjectsListView(){
        detectedObjectListView.getItems().clear();
    }
    public void closeWindow(){
        if(collectionWindow !=null) {
            collectionWindow.stop();
            this.deleteObserver(collectionWindow);
            collectionWindow.join();
        }
    }
    public void reorderImages(){
        String feature= (String)featuresListView.getSelectionModel().getSelectedItem();
        if(feature==null){
            return;
        }

        if(feature.equals("LastModificationDate")){
            imageLoader.reorderImagesByTime();

        }
        else{

            int currentlyMarkedImage= collectionWindow.getCurrentlyMarkedImage();
            if(-1!=currentlyMarkedImage ) {
                try {
                    imageLoader.reorderImages(currentlyMarkedImage, features.get(feature));
                    if(this.getCurrentView()==TSNE_VIEW){
                        imageLoader.calculateDistanceMatrix(features.get(feature));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public void resetCamera(){
        collectionWindow.resetCamera();
        //TODO MESSAGE
        //setChanged();
        //notifyObservers(new NeedResetCameraMessage());
    }
    public void getVirtualDirectoryCollectionInput() throws IOException {
        String collectionName =  new NewNameInput().display(this.currentlyLoadedDirectoryCollection.getVirtualCollectionNames());
        if (collectionName == null){

            currentState.virtualDirectoryNameInputFailed();
            return;
        }
        else{
            if(collectionName==null){
                currentState.virtualDirectoryNameInputFailed();
                return;
            }
            currentState.virtualDirectoryNameInput(collectionName);
            return;
        }
    }

    public void getVirtualDirectoryCollectionInput2() throws IOException {
        //String collectionName =  new NewNameInput().display(this.currentlyLoadedDirectoryCollection.getVirtualCollectionNames());
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        String collectionName="_"+timestamp.getTime();
        if (collectionName == null){

            currentState.virtualDirectoryNameInputFailed();
            return;
        }
        else{
            if(collectionName==null){
                currentState.virtualDirectoryNameInputFailed();
                return;
            }



            currentState.virtualDirectoryNameInput2(collectionName);
            return;
        }
    }
    public void createVirtualDirectoryCollection(String collectionName) throws IOException {
        collectionManager.addObserver(this);
        currentState.virtualDirectoryCollectionProcessed(collectionManager.createVirtualDirectoryCollection(
                this.currentlyLoadedDirectoryCollection.getCollectionName(),
                collectionName, collectionWindow.getSelectedImagesAsSet()
        ));
        HashSet<String> names=currentlyLoadedDirectoryCollection.getVirtualCollectionNames();

        ObservableList<String> items = FXCollections.observableArrayList (
                names);
        virtualCollectionsListView.setItems(items);
    }
    public void createVirtualDirectoryCollectionOfObjects(String collectionName) throws IOException, ParseException {
        collectionManager.addObserver(this);
        ObservableList<String> selectedObjects=detectedObjectListView.getSelectionModel().getSelectedItems();
        ArrayList<String> selectedObjectsArr=new ArrayList<>(selectedObjects);

        //imageLoader.getIdentifiersForLabels(selectedObjectsArr);

        currentState.virtualDirectoryCollectionProcessed(collectionManager.createVirtualDirectoryCollection(
                this.currentlyLoadedDirectoryCollection.getCollectionName(),
                collectionName,
                imageLoader.getImagesByLabel(currentlyLoadedDirectoryCollection.getIdentifiersForLabels(selectedObjectsArr))
        ));

        HashSet<String> names=currentlyLoadedDirectoryCollection.getVirtualCollectionNames();
        ObservableList<String> items = FXCollections.observableArrayList (
                names);
        virtualCollectionsListView.setItems(items);
    }
    public void loadVirtualDirectoryCollection(VirtualDirectoryCollection virtualDirectoryCollection) throws IOException {
        currentlyLoadedVirtualDirectoryCollection=virtualDirectoryCollection;
        currentState.virtualDirectoryCollectionLoaded(virtualDirectoryCollection);
    }
    public void loadVirtualDirectoryCollection(String virtualDirectoryCollectionName) throws IOException {
        //TODO ELLENORIZN HOGY SIKERES VOLTE A BETOTLES
        //System.out.println();
        currentlyLoadedVirtualDirectoryCollection=collectionManager.getVirtualDirectoryCollection(
                this.currentlyLoadedDirectoryCollection.getCollectionName(),
                virtualDirectoryCollectionName
        );
        currentState.virtualDirectoryCollectionLoaded(currentlyLoadedVirtualDirectoryCollection);
    }
    public void displayVirtualCollection(VirtualDirectoryCollection virtualDirectoryCollection) throws IOException {
        closeVirtualCollectionWindow();
        boolean vSync = true;

        virtualDirectoryCollection.getVirtualImageLoader().loadTextures();
        //IGameLogic gameLogic = new SpiralView(virtualDirectoryCollection.getVirtualImageLoader(),virtualDirectoryCollection.getNeededImages());
        IGameLogic gameLogic=null ;

        if(this.getCurrentView()==SPIRAL_VIEW){gameLogic = new SpiralView(virtualDirectoryCollection.getVirtualImageLoader(),virtualDirectoryCollection.getNeededImages());}
        if(this.getCurrentView()==GRID_VIEW) {gameLogic=new GridView(virtualDirectoryCollection.getVirtualImageLoader(),virtualDirectoryCollection.getNeededImages());}
        if(this.getCurrentView()==TIMEHISTOGRAM_VIEW) {gameLogic=new DateTimeHistogram(virtualDirectoryCollection.getVirtualImageLoader(),virtualDirectoryCollection.getNeededImages());}
        if(this.getCurrentView()==TSNE_VIEW) {gameLogic=new tSNEBasedView(virtualDirectoryCollection.getVirtualImageLoader(),virtualDirectoryCollection.getNeededImages());}
        if(this.getCurrentView()==DISTANCE_VIEW) {gameLogic=new WaveView(virtualDirectoryCollection.getVirtualImageLoader(),virtualDirectoryCollection.getNeededImages());}
        if(this.getCurrentView()==CIRCULARHIST3D_VIEW) {gameLogic=new CircularDateHistogram3D(virtualDirectoryCollection.getVirtualImageLoader(),virtualDirectoryCollection.getNeededImages());}
        if(this.getCurrentView()==CIRCULARHIST2D_VIEW) {gameLogic=new CircularDateHistogram2D(virtualDirectoryCollection.getVirtualImageLoader(),virtualDirectoryCollection.getNeededImages());}
        if(this.getCurrentView()==SPIRALHIST3D_VIEW) {gameLogic=new SpiralDateHistogram3D(virtualDirectoryCollection.getVirtualImageLoader(),virtualDirectoryCollection.getNeededImages());}
        if(this.getCurrentView()==FLOATINGHIST_VIEW) {gameLogic=new FloatingHistogramView(virtualDirectoryCollection.getVirtualImageLoader(),virtualDirectoryCollection.getNeededImages());}


        view.engine.Window.WindowOptions opts = new Window.WindowOptions();
        opts.cullFace = true;
        opts.showFps = true;
        opts.compatibleProfile = true;
        opts.antialiasing = true;

        try {
            long result=0;
            try {
                result=collectionWindow.copyContext().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            System.out.println("Context Result:"+result);

            virtualCollectionWindow = new GameEngine(virtualDirectoryCollection.getCollectionName(), vSync, opts,gameLogic,result);
            //Communicatiob between GameEngine and the ImadeLoader
            virtualDirectoryCollection.getVirtualImageLoader().addObserver(virtualCollectionWindow);
            this.addObserver(virtualCollectionWindow);
            virtualCollectionWindow.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
        //collectionManager.deleteObserver(this);



    }
    public void closeVirtualCollectionWindow(){

        if(virtualCollectionWindow!=null) {
            //glfwDestroyWindow(virtualCollectionWindow.window.getWindowHandle());
            virtualCollectionWindow.stop();
            this.deleteObserver(virtualCollectionWindow);
            virtualCollectionWindow.join();
            //we need to close the virtual window, but we can onyl close on the opengl main thread
            collectionWindow.destroyWindow(virtualCollectionWindow.window.getWindowHandle());
            //glfwDestroyWindow(virtualCollectionWindow.window.getWindowHandle());


        }
    }
    public void addSelectedImagesToVirtualCollection(){
        TreeSet<Integer> temp=collectionWindow.getSelectedImagesAsSet();
        collectionManager.addImagesToVirtualDirectoryCollection(
                this.currentlyLoadedDirectoryCollection.getCollectionName(),
                this.currentlyLoadedVirtualDirectoryCollection.getCollectionName(),
                temp
        );
        //TODO MESSAGE
       //setChanged();
        //notifyObservers(new AddImagesMessage(temp));

    }
    public void removeSelectedImagesFromVirtualCollection(){
        TreeSet<Integer> temp=virtualCollectionWindow.getSelectedImagesAsSet();
        collectionManager.removeImagesFromVirtualDirectoryCollection(
                this.currentlyLoadedDirectoryCollection.getCollectionName(),
                this.currentlyLoadedVirtualDirectoryCollection.getCollectionName(),
                temp
        );
        //TODO MESSAGE
        //setChanged();
        //notifyObservers(new RemoveImagesMessage(temp));

    }
    public void reorderVirtualCollection(){
        String feature= (String)featuresListView.getSelectionModel().getSelectedItem();
        if(feature==null){
            return;
        }

        int currentlyMarkedImage= virtualCollectionWindow.getCurrentlyMarkedImage();
        if(-1!=currentlyMarkedImage) {
            try {
                currentlyLoadedVirtualDirectoryCollection.getVirtualImageLoader().reorderImages(currentlyMarkedImage, features.get(feature));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    public void saveSelectedImagesFromVirtualCollection() throws IOException {
        currentState.saveSelectedImagesFromVirtualCollection();
    }
    public void savePathInput() throws IOException {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Some Directories");
        try {
            directoryChooser.setInitialDirectory(new File(lastSaveInput));
        }
        catch(Exception Ex){

        }
        File dir=directoryChooser.showDialog(Main.mainStage);
        if(dir!=null) {
            lastSaveInput=dir.getPath();
            currentState.saveImagesPathInput(lastSaveInput);
        }
        else {
            currentState.saveImagesPathInputFailed();
        }
    }
    public void virtualDirectoryCollectionSaveSelectedImages(String savePath) throws IOException {
        currentlyLoadedDirectoryCollection.getImageLoader().saveSelectedImagesToDirectory(savePath,virtualCollectionWindow.getSelectedImagesAsSet());
    }

    public void resetVirtualCollectionCamera(){
        virtualCollectionWindow.resetCamera();
    }


    public IState getDirectoryCollectionDisplaying() {
        return directoryCollectionDisplaying;
    }

    public IState getDirectoryCollectionLoading() {
        return directoryCollectionLoading;
    }

    public IState getDirectoryCollectionProcessing() {
        return directoryCollectionProcessing;
    }

    public IState getNotLoaded() {
        return notLoaded;
    }

    public IState getVirtualDirectoryCollectionCreating() {
        return virtualDirectoryCollectionCreating;
    }

    public IState getVirtualDirectoryCollectionDisplaying() {
        return virtualDirectoryCollectionDisplaying;
    }

    public IState getVirtualDirectoryCollectionLoading() {
        return virtualDirectoryCollectionLoading;
    }

    public IState getCurrentState() {
        return currentState;
    }

    public IState getWaitingForPathInput() {
        return waitingForPathInput;
    }

    public IState getCheckDirectory() {
        return checkDirectory;
    }

    public IState getWaitingForCollectionNameInput() {
        return waitingForCollectionNameInput;
    }

    public IState getVirtualCollectionWaitingForNameInput() {
        return virtualCollectionWaitingForNameInput;
    }

    public IState getVirtualDirectoryCollectionProcessing() {
        return virtualDirectoryCollectionProcessing;
    }

    public Configuration getConfig() {
        return config;
    }

    public IState getVirtualDirectoryCollectionSavingToFolder() {
        return virtualDirectoryCollectionSavingToFolder;
    }

    public IState getVirtualDirectoryCollectionWaitingForSavePathInput() {
        return virtualDirectoryCollectionWaitingForSavePathInput;
    }

    public String getLastPathInput() {
        return lastPathInput;
    }

    public void setCurrentState(IState currentState) {
        this.currentState = currentState;
        this.currentState.uiSetup();
        printCurrentState();
    }

    public void enableVirtCollObjButton(){markImagesButton.setDisable(false);}
    public void disableVirtCollObjButton(){markImagesButton.setDisable(true);}

    public void enableDetectedObjectsListView(){
        detectedObjectListView.setDisable(false);
        enableVirtCollObjButton();
    }
    public void disableDetectedObjectsListView(){
        detectedObjectListView.setDisable(true);
        disableVirtCollObjButton();
    }
    public void enableCollectionsListView(){
        collectionsListView.setDisable(false);
    }
    public void disableCollectionsListView(){
        collectionsListView.setDisable(true);
    }
    public void enableVirtualCollectionsListView(){
        virtualCollectionsListView.setDisable(false);
    }
    public void disableVirtualCollectionsListView(){
        virtualCollectionsListView.setDisable(true);
    }
    public void enableFeaturesListView(){
        featuresListView.setDisable(false);
    }
    public void disableFeaturesListView(){
        featuresListView.setDisable(true);
    }
    public void enableDirectoryOpen(){
        menuOpen.setDisable(false);
    }
    public void disableDirectoryOpen(){
        menuOpen.setDisable(true);
    }
    public void enableReorderCollectionButton(){
        reorderButton.setDisable(false);
    }
    public void disableReorderCollectionButton(){
        reorderButton.setDisable(true);
    }
    public void enableResetCameraButton(){
        resetCameraButton.setDisable(false);
    }
    public void disableResetCameraButton(){
        resetCameraButton.setDisable(true);
    }
    public void enableCreateNewVirtualCollectionButton(){
        createNewVirtualCollectionButton.setDisable(false);
    }
    public void disableCreateNewVirtualCollectionButton(){
        createNewVirtualCollectionButton.setDisable(true);
    }
    public void enableAddSelectedImagesToVirtualCollectionButton(){ addSelectedImagesToVirtualCollectionButton.setDisable(false); }
    public void disableAddSelectedImagesToVirtualCollectionButton(){ addSelectedImagesToVirtualCollectionButton.setDisable(true); }
    public void enableRemoveSelectedImagesFromVirtualCollectionButton(){
        removeSelectedImagesFromVirtualCollectionButton.setDisable(false); }
    public void disableRemoveSelectedImagesFromVirtualCollectionButton(){
        removeSelectedImagesFromVirtualCollectionButton.setDisable(true);
    }
    public void enableReorderVirtualCollectionButton(){
        reorderVirtualCollectionButton.setDisable(false);
    }
    public void disableReorderVirtualCollectionButton(){
        reorderVirtualCollectionButton.setDisable(true);
    }

    public void enableResetVirtualCollectionCameraButton(){
        resetVirtualCollectionCameraButton.setDisable(false);
    }
    public void disableResetVirtualCollectionCameraButton(){
        resetVirtualCollectionCameraButton.setDisable(true);
    }
    public void enableSaveSelectedImagesFromVirtualCollectionButton(){
        saveSelectedImagesFromVirtualCollectionButton.setDisable(false);
    }
    public void disableSaveSelectedImagesFromVirtualCollectionButton(){
        saveSelectedImagesFromVirtualCollectionButton.setDisable(true);
    }
    public void enableCloseCollectionButton(){
        closeCollectionButton.setDisable(false);
    }
    public void disableCloseCollectionButton(){
        closeCollectionButton.setDisable(true);
    }
    public void enableCloseVirtualCollectionButton(){
        closeVirtualCollectionButton.setDisable(false);
    }
    public void disableCloseVirtualCollectionButton(){
        closeVirtualCollectionButton.setDisable(true);
    }
    public void deleteVirtualCollectionIfVirtualCollectionDisplaying(){
        String virtualCollectionName=(String)virtualCollectionsListView.getSelectionModel().getSelectedItem();
        if(null!=virtualCollectionName){
            if (confirmDelete("Are you sure you want to delete the virtual collection named:" + virtualCollectionName+" ?") == ButtonType.OK) {
                if (currentlyLoadedVirtualDirectoryCollection.getCollectionName() == virtualCollectionName) {
                    setCurrentState(getDirectoryCollectionDisplaying());
                    closeVirtualCollectionWindow();

                    this.currentlyLoadedDirectoryCollection.deleteVirtualCollection(virtualCollectionName);
                    updateVirtualCollectionListView();
                } else {
                    collectionManager.deleteVirtualDirectoryCollection(
                            this.currentlyLoadedDirectoryCollection.getCollectionName(), virtualCollectionName
                    );
                    updateVirtualCollectionListView();
                }
            }
        }
        //virtualCollectionsListView.getSelectionModel().getSelectedItems()
    }
    public void deleteVirtualCollectionIfDirectoryCollectionDisplaying(){
        String virtualCollectionName=(String)virtualCollectionsListView.getSelectionModel().getSelectedItem();
        if(null!=virtualCollectionName){
            if (confirmDelete("Are you sure you want to delete the virtual collection named:" + virtualCollectionName+" ?") == ButtonType.OK) {
                collectionManager.deleteVirtualDirectoryCollection(
                        this.currentlyLoadedDirectoryCollection.getCollectionName(), virtualCollectionName
                );
                updateVirtualCollectionListView();
            }
        }
    }
    public void deleteDirectoryCollectionWhenVirtualCollectionDisplaying(){
        String directoryCollectionName=(String)collectionsListView.getSelectionModel().getSelectedItem();
        if(null!=directoryCollectionName){
            if (confirmDelete("Are you sure you want to delete the collection named:" + directoryCollectionName+" ?") == ButtonType.OK) {
                if (currentlyLoadedDirectoryCollection.getCollectionName() == directoryCollectionName) {
                    setCurrentState(getNotLoaded());
                    closeVirtualCollectionWindow();
                    closeWindow();
                    collectionManager.deleteDirectoryCollection(directoryCollectionName);
                    updateDirectoryCollectionListView();
                } else {
                    collectionManager.deleteDirectoryCollection(directoryCollectionName);
                    updateDirectoryCollectionListView();
                }
            }
        }
    }
    public void deleteDirectoryCollectionWhenDirectoryCollectionDisplaying(){
        String directoryCollectionName=(String)collectionsListView.getSelectionModel().getSelectedItem();
        if(null!=directoryCollectionName){
            if (confirmDelete("Are you sure you want to delete the collection named:" + directoryCollectionName+" ?") == ButtonType.OK) {
                if (currentlyLoadedDirectoryCollection.getCollectionName() == directoryCollectionName) {
                    setCurrentState(getNotLoaded());
                    closeWindow();
                    collectionManager.deleteDirectoryCollection(directoryCollectionName);
                    updateDirectoryCollectionListView();
                } else {
                    collectionManager.deleteDirectoryCollection(directoryCollectionName);
                    updateDirectoryCollectionListView();
                }
            }
        }
    }
    public void deleteDirectoryCollectionWhenNotLoaded(){
        String directoryCollectionName=(String)collectionsListView.getSelectionModel().getSelectedItem();
        if(null!=directoryCollectionName) {
            if (confirmDelete("Are you sure you want to delete the collection named:" + directoryCollectionName+" ?") == ButtonType.OK) {
                collectionManager.deleteDirectoryCollection(directoryCollectionName);
                updateDirectoryCollectionListView();
            } else {
                // ... user chose CANCEL or closed the dialog
            }
        }

    }
    private ButtonType confirmDelete(String data){
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete confirm");
        alert.setHeaderText("");
        alert.setContentText(data);
        return alert.showAndWait().get();
    }
    public void closeDirectoryCollectionWhenDirectoryCollectionDisplaying(){
        closeWindow();
        clearFeatureListView();
        clearDetectedObjectsListView();
        clearVirtualCollectionListView();
        setCurrentState(getNotLoaded());
    }
    public void closeDirectoryCollectionWhenVirtualCollectionDisplaying(){
        closeVirtualCollectionWindow();
        closeWindow();
        clearFeatureListView();
        clearDetectedObjectsListView();
        clearVirtualCollectionListView();
        setCurrentState(getNotLoaded());
    }
    public void closeVirtualDirectoryCollection(){
        setCurrentState(getDirectoryCollectionDisplaying());
        closeVirtualCollectionWindow();
    }
/*    public void disableVirtualCollectionDisplayingRelatedButtons(){
        disableAddSelectedImagesToVirtualCollectionButton();
        disableRemoveSelectedImagesFromVirtualCollectionButton();
        disableReorderVirtualCollectionButton();
        disableResetVirtualCollectionCameraButton();
        disableSaveSelectedImagesFromVirtualCollectionButton();

    }
    public void disableDirectoryDisplayingRelatedButtons(){
        disableCreateNewVirtualCollectionButton();
        disableReorderCollectionButton();
        disableResetCameraButton();
    }*/
    public void disableAll(){
        disableDirectoryOpen();
        disableResetCameraButton();
        disableResetVirtualCollectionCameraButton();

        disableReorderCollectionButton();
        disableReorderVirtualCollectionButton();

        disableCreateNewVirtualCollectionButton();
        disableSaveSelectedImagesFromVirtualCollectionButton();
        disableRemoveSelectedImagesFromVirtualCollectionButton();
        disableAddSelectedImagesToVirtualCollectionButton();
        disableVirtualCollectionsListView();
        disableFeaturesListView();
        disableDetectedObjectsListView();
        disableCollectionsListView();
        disableCloseVirtualCollectionButton();
        disableCloseCollectionButton();
    }
    public void printCurrentState(){System.out.println("Current State:"+this.currentState.getClass().getSimpleName());}
    public void shutdown() {
        System.out.println("ITT JARTAM");
        closeWindow();
        closeVirtualCollectionWindow();
    }
}
