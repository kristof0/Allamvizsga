package controller.State;

import controller.MainWindowController;
import model.Collections.Collection;
import model.Collections.DirectoryCollection;
import model.Collections.VirtualDirectoryCollection;

import java.io.IOException;

public class VirtualDirectoryCollectionDisplaying implements IState {
    MainWindowController mainWindow;

    public VirtualDirectoryCollectionDisplaying(MainWindowController mainWindow) {
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
    public void reloadDirectoryCollection(DirectoryCollection collection) throws IOException {

    }

    @Override
    public void createVirtualDirectoryCollection() throws IOException {
        System.out.println("VirtualDirectoryCollectionDisplaying:createVirtualDirectoryCollection");
        mainWindow.setCurrentState(mainWindow.getVirtualCollectionWaitingForNameInput());
        mainWindow.getVirtualDirectoryCollectionInput();
    }

    @Override
    public void createVirtualDirectoryCollectionOfObjects() throws IOException {
        System.out.println("VirtualDirectoryCollectionDisplaying:createVirtualDirectoryCollection");
        mainWindow.setCurrentState(mainWindow.getVirtualCollectionWaitingForNameInput());
        mainWindow.getVirtualDirectoryCollectionInput2();
    }

    @Override
    public void loadVirtualDirectoryCollection(String virtualCollectionName) throws IOException {
        System.out.println("VirtualDirectoryCollectionDisplaying:loadVirtualDirectoryCollection");
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
        mainWindow.closeDirectoryCollectionWhenVirtualCollectionDisplaying();
    }

    @Override
    public void closeVirtualCollection() {
        mainWindow.closeVirtualDirectoryCollection();
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
        mainWindow.addSelectedImagesToVirtualCollection();
    }

    @Override
    public void removeSelectedImagesFromVirtualCollection() {
        mainWindow.removeSelectedImagesFromVirtualCollection();
    }

    @Override
    public void saveSelectedImagesFromVirtualCollection() throws IOException {
        mainWindow.setCurrentState(mainWindow.getVirtualDirectoryCollectionWaitingForSavePathInput());
        mainWindow.savePathInput();
    }

    @Override
    public void reorderVirtualCollection() {
        System.out.println("VirtualDirectoryCollectionDisplaying:reorderVirtualCollection");
        mainWindow.reorderVirtualCollection();
    }

    @Override
    public void resetVirtualCollectionCamera() {
        mainWindow.resetVirtualCollectionCamera();
    }

    @Override
    public void uiSetup() {

        mainWindow.enableDirectoryOpen();
        mainWindow.enableCollectionsListView();
        mainWindow.enableResetCameraButton();
        mainWindow.enableReorderCollectionButton();
        mainWindow.enableCreateNewVirtualCollectionButton();
        mainWindow.enableFeaturesListView();
        mainWindow.enableDetectedObjectsListView();
        mainWindow.enableVirtualCollectionsListView();
        mainWindow.enableCloseCollectionButton();
        mainWindow.enableCloseVirtualCollectionButton();

        mainWindow.enableResetVirtualCollectionCameraButton();
        mainWindow.enableReorderVirtualCollectionButton();
        mainWindow.enableSaveSelectedImagesFromVirtualCollectionButton();
        mainWindow.enableRemoveSelectedImagesFromVirtualCollectionButton();
        mainWindow.enableAddSelectedImagesToVirtualCollectionButton();
    }

    @Override
    public void collectionWindowClose() {

    }

    @Override
    public void virtualCollectionWindowClosed() {

    }

    @Override
    public void saveSelectedVirtualImages() throws IOException {
        mainWindow.savePathInput();
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
        mainWindow.deleteDirectoryCollectionWhenVirtualCollectionDisplaying();
    }

    @Override
    public void deleteVirtualDirectoryCollection() {
        System.out.println("VirtualDirectoryCollectionDisplaying:deleteVirtualDirectoryCollection");
        mainWindow.deleteVirtualCollectionIfVirtualCollectionDisplaying();
    }

    @Override
    public void processDirectoryCollectionFailedNoImagesFound() {

    }
}
