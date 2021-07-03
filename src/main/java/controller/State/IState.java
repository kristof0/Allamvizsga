package controller.State;

import model.Collections.Collection;
import model.Collections.DirectoryCollection;
import model.Collections.VirtualDirectoryCollection;

import java.io.IOException;

public interface IState {

    void createNewDirectoryCollection() throws IOException;
    void processDirectory() throws IOException;
    void loadDirectoryCollection(DirectoryCollection collection) throws IOException;
    void reloadDirectoryCollection(DirectoryCollection collection) throws IOException;
    void createVirtualDirectoryCollection() throws IOException;
    void createVirtualDirectoryCollectionOfObjects() throws IOException;
    void loadVirtualDirectoryCollection(String virtualCollectionName) throws IOException;
    void deleteImagesFormVirtualCollection();
    void reorderImages();
    void resetCamera();
    void selectFeature();
    void selectOneImage();
    void selectSeveralImages();
    void closeDirectoryCollection();
    void closeVirtualCollection();

    void directoryPathInput(String path) throws IOException;
    void directoryPathInputFailed();

    void directoryNameInput(String name);
    void directoryNameInputFailed();

    void directoryAlreadyProcessed(DirectoryCollection collection) throws IOException;
    void processDirectoryCollection(Collection collection) throws IOException;

    void directoryCollectionProcessingFinished(DirectoryCollection collection) throws IOException;
    void directoryCollectionLoadingFinished(DirectoryCollection collection) throws IOException;


    void virtualDirectoryNameInput(String name) throws IOException;
    void virtualDirectoryNameInput2(String name) throws IOException;
    void virtualDirectoryNameInputFailed();

    void processVirtualDirectoryCollection();
    void virtualDirectoryCollectionProcessed(VirtualDirectoryCollection virtualDirectoryCollection) throws IOException;

    void virtualDirectoryCollectionLoaded(VirtualDirectoryCollection virtualDirectoryCollection) throws IOException;

    void addSelectedImagesToVirtualCollection();
    void removeSelectedImagesFromVirtualCollection();
    void saveSelectedImagesFromVirtualCollection() throws IOException;

    void reorderVirtualCollection();
    void resetVirtualCollectionCamera();

    void uiSetup();

    void collectionWindowClose();
    void virtualCollectionWindowClosed();
    void saveSelectedVirtualImages() throws IOException;
    void selectedVirtualImagesSaved();
    void saveImagesPathInput(String savePath) throws IOException;
    void saveImagesPathInputFailed();

    void deleteDirectoryCollection();
    void deleteVirtualDirectoryCollection();
    void processDirectoryCollectionFailedNoImagesFound();
}
