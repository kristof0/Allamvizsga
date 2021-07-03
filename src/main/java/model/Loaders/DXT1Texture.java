package model.Loaders;

import java.io.Serializable;

public class DXT1Texture implements Serializable {
    private int width;
    private int height;
    private byte[] texture;

    public DXT1Texture(int width, int height, byte[] texture) {
        this.width = width;
        this.height = height;
        this.texture = texture;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public byte[] getTexture() {
        return texture;
    }

    public void setTexture(byte[] texture) {
        this.texture = texture;
    }
}
