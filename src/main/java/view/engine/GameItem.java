package view.engine;

import org.joml.Vector3f;
import view.engine.graph.Mesh;

public class GameItem {

    public void setMesh(Mesh mesh) {
        this.mesh = mesh;
    }

    private  Mesh mesh;
    private final Mesh markedMesh;
    private final Vector3f position;
    
    private float scale;

    private final Vector3f rotation;
    private final int textureAtlasPageIndex;
    public GameItem(Mesh mesh,Mesh markedMesh,int textureAtlasPageIndex) {
        this.textureAtlasPageIndex=textureAtlasPageIndex;
        this.mesh = mesh;
        this.markedMesh=markedMesh;
        position = new Vector3f(0, 0, 0);
        scale = 1;
        rotation = new Vector3f(0, 0, 0);
    }

    public Vector3f getPosition() {
        return position;
    }
    public int getTextureAtlasPageIndex(){ return textureAtlasPageIndex; }
    public void setPosition(float x, float y, float z) {
        this.position.x = x;
        this.position.y = y;
        this.position.z = z;
    }

    public float getScale() {
        return scale;
    }

    public void setScale(float scale) {
        this.scale = scale;
    }

    public Vector3f getRotation() {
        return rotation;
    }

    public void setRotation(float x, float y, float z) {
        this.rotation.x = x;
        this.rotation.y = y;
        this.rotation.z = z;
    }
    
    public Mesh getMesh() {
        return mesh;
    }
    public Mesh getMarkedMesh() {
        return markedMesh;
    }
}