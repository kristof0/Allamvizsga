package controller.Messages;

import model.Collections.VirtualDirectoryCollection;

public class NewVirtualDirectoryCollectionCreated {
    private final VirtualDirectoryCollection virtualDirectoryCollection;

    public NewVirtualDirectoryCollectionCreated(VirtualDirectoryCollection virtualDirectoryCollection) {
        this.virtualDirectoryCollection = virtualDirectoryCollection;
    }

    public VirtualDirectoryCollection getVirtualDirectoryCollection() {
        return virtualDirectoryCollection;
    }
}
