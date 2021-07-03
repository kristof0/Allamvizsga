package controller.State;

import controller.MainWindowController;
import model.Collections.Collection;
import model.Collections.DirectoryCollection;
import model.Collections.VirtualDirectoryCollection;

import java.io.IOException;

public class VirtualDirectoryCollectionSavingToFolder implements IState
{
    MainWindowController mainWindow;

    public VirtualDirectoryCollectionSavingToFolder(MainWindowController mainWindow) {
        this.mainWindow = mainWindow;
    }

    @Override
    public void createNewDirectoryCollection() throws IOException {

    }

    @Override
    public void processDirectory() throws IOException {

    }

    @Override
    public void loadDirectoryCollection(DirectoryCollection collection) throws IOException {

    }

    @Override
    public void reloadDirectoryCollection(DirectoryCollection collection) throws IOException {

    }

    @Override
    public void createVirtualDirectoryCollection() throws IOException {

    }

    @Override
    public void createVirtualDirectoryCollectionOfObjects() throws IOException {

    }

    @Override
    public void loadVirtualDirectoryCollection(String virtualCollectionName) throws IOException {

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
    public void directoryPathInput(String path) throws IOException {

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
    public void directoryAlreadyProcessed(DirectoryCollection collection) throws IOException {

    }

    @Override
    public void processDirectoryCollection(Collection collection) throws IOException {

    }

    @Override
    public void directoryCollectionProcessingFinished(DirectoryCollection collection) throws IOException {

    }

    @Override
    public void directoryCollectionLoadingFinished(DirectoryCollection collection) throws IOException {

    }

    @Override
    public void virtualDirectoryNameInput(String name) throws IOException {

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
    public void virtualDirectoryCollectionProcessed(VirtualDirectoryCollection virtualDirectoryCollection) throws IOException {

    }

    @Override
    public void virtualDirectoryCollectionLoaded(VirtualDirectoryCollection virtualDirectoryCollection) throws IOException {

    }

    @Override
    public void addSelectedImagesToVirtualCollection() {

    }

    @Override
    public void removeSelectedImagesFromVirtualCollection() {

    }

    @Override
    public void saveSelectedImagesFromVirtualCollection() throws IOException {

    }

    @Override
    public void reorderVirtualCollection() {

    }

    @Override
    public void resetVirtualCollectionCamera() {

    }

    @Override
    public void uiSetup() {
        mainWindow.disableDirectoryOpen();
        mainWindow.disableCollectionsListView();
        mainWindow.disableCreateNewVirtualCollectionButton();
        mainWindow.disableVirtualCollectionsListView();
        mainWindow.disableSaveSelectedImagesFromVirtualCollectionButton();
        mainWindow.disableRemoveSelectedImagesFromVirtualCollectionButton();
        mainWindow.disableAddSelectedImagesToVirtualCollectionButton();

        mainWindow.enableResetCameraButton();
        mainWindow.enableReorderCollectionButton();
        mainWindow.enableFeaturesListView();
        mainWindow.enableDetectedObjectsListView();

        mainWindow.enableResetVirtualCollectionCameraButton();
        mainWindow.enableReorderVirtualCollectionButton();

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
        mainWindow.setCurrentState(mainWindow.getVirtualDirectoryCollectionDisplaying());
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
