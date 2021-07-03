package controller.Messages;

import model.Collections.DirectoryCollection;

public class CollectionLoadedMessage {
    DirectoryCollection collection;

    public CollectionLoadedMessage(DirectoryCollection collection) {
        this.collection = collection;
    }

    public DirectoryCollection getCollection() {
        return collection;
    }
}
