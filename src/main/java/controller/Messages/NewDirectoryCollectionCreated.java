package controller.Messages;

import model.Collections.DirectoryCollection;

public class NewDirectoryCollectionCreated {
    DirectoryCollection collection;

    public NewDirectoryCollectionCreated(DirectoryCollection collection) {
        this.collection = collection;
    }

    public DirectoryCollection getCollection() {
        return collection;
    }
}
