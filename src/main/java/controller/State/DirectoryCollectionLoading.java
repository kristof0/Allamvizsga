package controller.State;

import controller.MainWindowController;
import model.Collections.Collection;
import model.Collections.DirectoryCollection;
import model.Collections.VirtualDirectoryCollection;

import java.io.IOException;

public class DirectoryCollectionLoading implements IState {
    MainWindowController mainWindow;

    public DirectoryCollectionLoading(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void createNewDirectoryCollection() {

    }

    @Override
    public void processDirectory() {

    }

    @Override
    public void loadDirectoryCollection(DirectoryCollection collection) {

    }

    @Override
    public void reloadDirectoryCollection(DirectoryCollection collection) throws IOException {

    }

    @Override
    public void createVirtualDirectoryCollection() {

    }

    @Override
    public void createVirtualDirectoryCollectionOfObjects() throws IOException {

    }

    @Override
    public void loadVirtualDirectoryCollection(String virtualCollectionName) {

    }

    @Override
    public void deleteImagesFormVirtualCollection() {

    }

    @Override
    public void reorderImages() {

    }

    @Override
    public void resetCamera() {

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
        System.out.println("DirectoryCollectionLoading:directoryCollectionLoadingFinished");
        mainWindow.displayDirectoryCollection(collection);
        mainWindow.setCurrentState(mainWindow.getDirectoryCollectionDisplaying());

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
        mainWindow.disableAll();
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

    }

    @Override
    public void deleteVirtualDirectoryCollection() {

    }

    @Override
    public void processDirectoryCollectionFailedNoImagesFound() {

    }
}
