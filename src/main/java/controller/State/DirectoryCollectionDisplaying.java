package controller.State;

import controller.MainWindowController;
import model.Collections.Collection;
import model.Collections.DirectoryCollection;
import model.Collections.VirtualDirectoryCollection;

import java.io.IOException;

public class DirectoryCollectionDisplaying implements IState {
    MainWindowController mainWindow;

    public DirectoryCollectionDisplaying(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void createNewDirectoryCollection() throws IOException {
        mainWindow.closeWindow();
        mainWindow.setCurrentState(mainWindow.getWaitingForPathInput());
        mainWindow.getPathInput();
    }

    @Override
    public void processDirectory() {

    }

    @Override
    public void loadDirectoryCollection(DirectoryCollection collection) throws IOException {
        mainWindow.closeWindow();
        mainWindow.setCurrentState(mainWindow.getDirectoryCollectionLoading());
        mainWindow.loadDirectoryCollection(collection);
    }

    @Override
    public void reloadDirectoryCollection(DirectoryCollection collection) throws IOException{
        mainWindow.closeWindow();
        mainWindow.setCurrentState(mainWindow.getDirectoryCollectionLoading());
        mainWindow.reloadDirectoryCollection(collection);
    }



    @Override
    public void createVirtualDirectoryCollection() throws IOException {
        System.out.println("DirectoryCollectionDisplaying:createVirtualDirectoryCollection");
        mainWindow.setCurrentState(mainWindow.getVirtualCollectionWaitingForNameInput());
        mainWindow.getVirtualDirectoryCollectionInput();
    }

    @Override
    public void createVirtualDirectoryCollectionOfObjects() throws IOException {
        System.out.println("DirectoryCollectionDisplaying:createVirtualDirectoryCollection");
        mainWindow.setCurrentState(mainWindow.getVirtualCollectionWaitingForNameInput());
        mainWindow.getVirtualDirectoryCollectionInput2();
    }

    @Override
    public void loadVirtualDirectoryCollection(String virtualCollectionName) throws IOException {
        System.out.println("DirectoryCollectionDisplaying:loadVirtualDirectoryCollection");
        mainWindow.setCurrentState(mainWindow.getVirtualDirectoryCollectionLoading());
        mainWindow.loadVirtualDirectoryCollection(virtualCollectionName);
    }

    @Override
    public void deleteImagesFormVirtualCollection() {

    }

    @Override
    public void reorderImages() {
        mainWindow.reorderImages();
    }

    @Override
    public void resetCamera() {
        mainWindow.resetCamera();
    }

    @Override
    public void selectFeature() {

    }

    @Override
    public void selectOneImage() {

    }

    @Override
    public void selectSeveralImages() {

    }

    @Override
    public void closeDirectoryCollection() {
        mainWindow.closeDirectoryCollectionWhenDirectoryCollectionDisplaying();
    }

    @Override
    public void closeVirtualCollection() {

    }

    @Override
    public void directoryPathInput(String path) {

    }

    @Override
    public void directoryPathInputFailed() {

    }

    @Override
    public void directoryNameInput(String name) {

    }

    @Override
    public void directoryNameInputFailed() {

    }

    @Override
    public void directoryAlreadyProcessed(DirectoryCollection collection) {

    }

    @Override
    public void processDirectoryCollection(Collection collection) {

    }

    @Override
    public void directoryCollectionProcessingFinished(DirectoryCollection collection) {

    }

    @Override
    public void directoryCollectionLoadingFinished(DirectoryCollection collection) throws IOException {

    }

    @Override
    public void virtualDirectoryNameInput(String name) {

    }

    @Override
    public void virtualDirectoryNameInput2(String name) throws IOException {

    }

    @Override
    public void virtualDirectoryNameInputFailed() {

    }

    @Override
    public void processVirtualDirectoryCollection() {

    }

    @Override
    public void virtualDirectoryCollectionProcessed(VirtualDirectoryCollection virtualDirectoryCollection) {

    }

    @Override
    public void virtualDirectoryCollectionLoaded(VirtualDirectoryCollection virtualDirectoryCollection) {

    }

    @Override
    public void addSelectedImagesToVirtualCollection() {

    }

    @Override
    public void removeSelectedImagesFromVirtualCollection() {

    }

    @Override
    public void saveSelectedImagesFromVirtualCollection() {

    }

    @Override
    public void reorderVirtualCollection() {

    }

    @Override
    public void resetVirtualCollectionCamera() {

    }

    @Override
    public void uiSetup() {
        mainWindow.updateVirtualCollectionListView();
        mainWindow.enableDirectoryOpen();
        mainWindow.enableCollectionsListView();
        mainWindow.enableResetCameraButton();
        mainWindow.enableReorderCollectionButton();
        mainWindow.enableCreateNewVirtualCollectionButton();
        mainWindow.enableFeaturesListView();
        mainWindow.enableDetectedObjectsListView();
        mainWindow.enableVirtualCollectionsListView();
        mainWindow.enableCloseCollectionButton();

        mainWindow.disableResetVirtualCollectionCameraButton();
        mainWindow.disableReorderVirtualCollectionButton();
        mainWindow.disableSaveSelectedImagesFromVirtualCollectionButton();
        mainWindow.disableRemoveSelectedImagesFromVirtualCollectionButton();
        mainWindow.disableAddSelectedImagesToVirtualCollectionButton();
        mainWindow.disableCloseVirtualCollectionButton();


    }

    @Override
    public void collectionWindowClose() {

    }

    @Override
    public void virtualCollectionWindowClosed() {

    }

    @Override
    public void saveSelectedVirtualImages() {

    }

    @Override
    public void selectedVirtualImagesSaved() {

    }

    @Override
    public void saveImagesPathInput(String savePath) {

    }

    @Override
    public void saveImagesPathInputFailed() {

    }

    @Override
    public void deleteDirectoryCollection() {
        mainWindow.deleteDirectoryCollectionWhenDirectoryCollectionDisplaying();
    }

    @Override
    public void deleteVirtualDirectoryCollection() {
        mainWindow.deleteVirtualCollectionIfDirectoryCollectionDisplaying();
    }

    @Override
    public void processDirectoryCollectionFailedNoImagesFound() {

    }
}
