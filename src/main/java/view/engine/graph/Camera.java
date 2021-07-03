package view.engine.graph;

import org.joml.Matrix4f;
import org.joml.Vector3f;

public class Camera {

    private final Vector3f position;
    
    private final Vector3f rotation;
    private final float maxX;
    private final float maxY;
    private final float maxZ;
    private final float minX;
    private final float minY;
    private final float minZ;

    private final float maxRotX;
    private final float maxRotY;
    private final float maxRotZ;
    private final float minRotX;
    private final float minRotY;
    private final float minRotZ;

    private final Matrix4f viewMatrix;
    public Camera(float minX,float maxX,float minY,float maxY,float minZ,float maxZ,
                  float minRotX,float maxRotX,float minRotY,float maxRotY,float minRotZ,float maxRotZ) {
        position = new Vector3f(0, 0, 0);
        rotation = new Vector3f(0, 0, 0);
        this.viewMatrix = new Matrix4f();

        if(maxX<minX){
            float temp=maxX;
            maxX=minX;
            minX=temp;
        }
        if(maxY<minY){
            float temp=maxY;
            maxY=minY;
            minY=temp;
        }
        if(maxZ<minZ){
            float temp=maxZ;
            maxZ=minZ;
            minZ=temp;
        }
        this.maxX=maxX;
        this.maxY=maxY;
        this.maxZ=maxZ;
        this.minX=minX;
        this.minY=minY;
        this.minZ=minZ;

        if(maxRotX<minRotX){
            float temp=maxRotX;
            maxRotX=minRotX;
            minRotX=temp;
        }
        if(maxRotY<minRotY){
            float temp=maxRotY;
            maxRotY=minRotY;
            minRotY=temp;
        }
        if(maxRotZ<minRotZ){
            float temp=maxRotZ;
            maxRotZ=minRotZ;
            minRotZ=temp;
        }
        this.maxRotX=maxRotX;
        this.maxRotY=maxRotY;
        this.maxRotZ=maxRotZ;
        this.minRotX=minRotX;
        this.minRotY=minRotY;
        this.minRotZ=minRotZ;

    }
    
    public Camera(Vector3f position, Vector3f rotation,
                  float minX,float maxX,float minY,float maxY,float minZ,float maxZ,
                  float minRotX,float maxRotX,float minRotY,float maxRotY,float minRotZ,float maxRotZ) {
        this.position = position;
        this.rotation = rotation;
        this.viewMatrix = new Matrix4f();

        if(maxX<minX){
            float temp=maxX;
            maxX=minX;
            minX=temp;
        }
        if(maxY<minY){
            float temp=maxY;
            maxY=minY;
            minY=temp;
        }
        if(maxZ<minZ){
            float temp=maxZ;
            maxZ=minZ;
            minZ=temp;
        }
        this.maxX=maxX;
        this.maxY=maxY;
        this.maxZ=maxZ;
        this.minX=minX;
        this.minY=minY;
        this.minZ=minZ;

        if(maxRotX<minRotX){
            float temp=maxRotX;
            maxRotX=minRotX;
            minRotX=temp;
        }
        if(maxRotY<minRotY){
            float temp=maxRotY;
            maxRotY=minRotY;
            minRotY=temp;
        }
        if(maxRotZ<minRotZ){
            float temp=maxRotZ;
            maxRotZ=minRotZ;
            minRotZ=temp;
        }
        this.maxRotX=maxRotX;
        this.maxRotY=maxRotY;
        this.maxRotZ=maxRotZ;
        this.minRotX=minRotX;
        this.minRotY=minRotY;
        this.minRotZ=minRotZ;

        updateViewMatrix();
    }

    public Vector3f getPosition() {
        return position;
    }

    public float getPositionX() {
        return position.x;
    }
    public float getPositionY() { return position.y; }
    public float getPositionZ() { return position.z; }
    public void setPosition(float x, float y, float z) {
        setPositionX(x);
        setPositionY(y);
        setPositionZ(z);
    }
    public void setPositionX(float x) {
        position.x = x;
        if(x>maxX){
            position.x=maxX;
        }
        else
            if(x<minX){
                position.x=minX;
            }

    }
    public void setPositionY(float y) {
        position.y = y;
        if(y>maxY){
            position.y=maxY;
        }
        else
        if(y<minY){
            position.y=minY;
        }
    }
    public void setPositionZ(float z) {

        position.z = z;
        if(z>maxZ){
            position.z=maxZ;
        }
        else
        if(z<minZ){
            position.z=minZ;
        }
    }
    public void movePosition(float offsetX, float offsetY, float offsetZ) {
        movePositionX(offsetX);
        movePositionY(offsetY);
        movePositionZ(offsetZ);
    }
    public void movePositionX(float offsetX){
        if ( offsetX != 0) {
            setPositionX(position.x +(float)Math.sin(Math.toRadians(rotation.y - 90)) * -1.0f * offsetX);
            setPositionZ(position.z + (float)Math.cos(Math.toRadians(rotation.y - 90)) * offsetX);
        }
    }
    public void movePositionY(float offsetY){
        if ( offsetY != 0) {
            setPositionY(position.y + offsetY);
        }
    }
    public void movePositionZ(float offsetZ){
        if ( offsetZ != 0 ) {
            setPositionX(position.x + (float)Math.sin(Math.toRadians(rotation.y)) * -1.0f * offsetZ);
            setPositionZ(position.z + (float)Math.cos(Math.toRadians(rotation.y)) * offsetZ);
        }
    }
    public Vector3f getRotation() {
        return rotation;
    }
    
    public void setRotation(float x, float y, float z) {
        setRotationX(x);
        setRotationY(y);
        setRotationZ(z);
    }
    public void setRotationX(float x) {
        if(x>maxRotX){
            rotation.x=maxRotX;
        }
        else
            if(x<minRotX){
                rotation.x=minRotX;
            }
            else{
                rotation.x = x;
            }
    }
    public void setRotationY(float y) {
        if(y>maxRotY){
            rotation.y=maxRotY;
        }
        else
        if(y<minRotY){
            rotation.y=minRotY;
        }
        else{
            rotation.y = y;
        }

    }
    public void setRotationZ(float z) {
        if(z>maxRotZ){
            rotation.z=maxRotZ;
        }
        else
        if(z<minRotZ){
            rotation.z=minRotZ;
        }
        else{
            rotation.z = z;
        }
    }
    public void moveRotation(float offsetX, float offsetY, float offsetZ) {
        setRotationX(rotation.x + offsetX);
        setRotationY(rotation.y + offsetY);
        setRotationZ(rotation.z + offsetZ);
    }
    public Matrix4f getViewMatrix() {
        return viewMatrix;
    }
    public void updateViewMatrix() {
        viewMatrix.identity();
        // First do the rotation so camera rotates over its position
        viewMatrix.rotate((float)Math.toRadians(this.rotation.x), new Vector3f(1, 0, 0))
                .rotate((float)Math.toRadians(this.rotation.y), new Vector3f(0, 1, 0));
        // Then do the translation
        viewMatrix.translate(-this.position.x, -this.position.y, -this.position.z);

    }

}