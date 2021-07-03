package view.CollectionViews;

import org.joml.Matrix4f;
import org.joml.Vector2d;
import org.joml.Vector3f;
import org.joml.Vector4f;
import view.engine.Window;
import view.engine.graph.Camera;

public class MouseDateTimeViewSelectionDetector {
    private final Matrix4f invProjectionMatrix;

    private final Matrix4f invViewMatrix;

    private final Vector3f mouseDir;

    private final Vector4f tmpVec;

    private final int lastpos=-1;
    private final boolean change=false;
    private int lastOriginalPos;
    public MouseDateTimeViewSelectionDetector() {

        invProjectionMatrix = new Matrix4f();
        invViewMatrix = new Matrix4f();
        mouseDir = new Vector3f();
        tmpVec = new Vector4f();
    }



    public Vector3f selectGameItem(
            Window window,
            Vector2d mousePos,
            Camera camera,
            Vector3f picturesPos
    ) {
        // Transform mouse coordinates into normalized spaze [-1, 1]
        int wdwWidth = window.getWidth();
        int wdwHeight = window.getHeight();

        float x = (float)(2 * mousePos.x) / (float)wdwWidth - 1.0f;
        float y = 1.0f - (float)(2 * mousePos.y) / (float)wdwHeight;
        float z = -1.0f;

        invProjectionMatrix.set(window.getProjectionMatrix());
        invProjectionMatrix.invert();

        tmpVec.set(x, y, z, 1.0f);
        tmpVec.mul(invProjectionMatrix);
        tmpVec.z = -1.0f;
        tmpVec.w = 0.0f;

        Matrix4f viewMatrix = camera.getViewMatrix();
        invViewMatrix.set(viewMatrix);
        invViewMatrix.invert();
        tmpVec.mul(invViewMatrix);
        mouseDir.set(tmpVec.x, tmpVec.y, tmpVec.z);
        //TODO VIGYAZAT ELORE OPTIMALIZALT KOD,ELORE UGY VETTEM,HOGY A KEPEK AZ OX TENGELLYEL PARHUZAMOSAN VANNAK ES HOGY ORIGOKOZEPPONTU
        //TODO VALAMINT hogy 1 es -1 kozott vannak az x es az y
        float t=(picturesPos.z-camera.getPosition().z)/mouseDir.z;
        Vector3f clickCord=new Vector3f(
                camera.getPosition().x+mouseDir.x*t,
                camera.getPosition().y+mouseDir.y*t,
                camera.getPosition().z+mouseDir.z*t
        );
        return clickCord;
///
/*     //clicked outside of the pictures frame
        if(clickCord.x>1||clickCord.x<-1||clickCord.y>1||clickCord.y<-1){
            return false;
        }
        //transform from (-1,1) to (0,2)
        float transformedX=clickCord.x+1;
        //transform from (-1,1) to (-2,0)
        float transformedY=clickCord.y-1;

        int tx=(int)(transformedX/pictureSize);
        int ty=(int)(transformedY/pictureSize);

        int pictureIndex=(abs(ty)*numbeOfImagesPerRow)+tx;


        if(pictureIndex>numberOfImages-1){
            return false;
        }
        int originalIndex=pictureIndex;
        pictureIndex=invOrder[pictureIndex];
        boolean q=false;
        if(window.isKeyPressed(GLFW_KEY_LEFT_CONTROL)||window.isKeyPressed(GLFW_KEY_RIGHT_CONTROL)) {
            if(lastpos!=pictureIndex){
                //if(!marked[pictureIndex]) {

                marked[pictureIndex] =  !marked[pictureIndex];
                q=true;
                change=true;
                //System.out.println("HOSSZ KATT\n");
                // }
            }
            else{
                if(mouseInput.isLeftButtonJustRelesed()){
                    if(change==false) {
                        marked[pictureIndex] = !marked[pictureIndex];
                        q = true;
                        //System.out.println("EGYKATT");
                    }
                    else{
                        change=false;
                    }
                }
            }

        }
        else {
            if(lastpos!=-1&&window.isKeyPressed(GLFW_KEY_LEFT_SHIFT)||window.isKeyPressed(GLFW_KEY_RIGHT_SHIFT)){
                int last=lastOriginalPos;
                if(last>originalIndex){
                    int k=last;
                    last=originalIndex;
                    originalIndex=k;
                }

               // marked[last]=true;
              //  marked[pictureIndex]=true;
                //marked[pictureIndex-1]=true;
                for(;last+1<=originalIndex+1;++last){
                    marked[invOrder[last]] = true;
                }
                q=true;
            }
            else{
                   // System.out.println("EGYEB\n");
                    for (int i = 0; i < marked.length; ++i) {
                        marked[i] = false;
                    }
                    marked[pictureIndex] = true;
                    q = true;
                }
        }
        lastpos=pictureIndex;
        lastOriginalPos=originalIndex;
        mouseInput.setLeftButtonJustRelesed(false);
       return q;*/
    }

}
