package model.Loaders;

import org.apache.lucene.document.Document;

import java.awt.image.BufferedImage;

public class ProcessedImage {
    public BufferedImage image;
    public Document document;

    public ProcessedImage(BufferedImage image, Document document) {
        this.image = image;
        this.document = document;
    }
}
