package view.engine;

import org.joml.Vector2d;
import org.joml.Vector2f;

import static org.lwjgl.glfw.GLFW.*;

public class MouseInput {

    private final Vector2d previousPos;

    private final Vector2d currentPos;

    private final Vector2f displVec;
    private final Vector2f scrollVec;
    private final Vector2d currentScrollPos;
    private final Vector2d previousScrollPos;
    private boolean inWindow = false;

    private boolean leftButtonPressed = false;

    private boolean rightButtonPressed = false;
    private boolean leftButtonJustRelesed=false;
    private final boolean scrolled=false;

    public MouseInput() {
        previousPos = new Vector2d(-1, -1);
        currentPos = new Vector2d(0, 0);
        displVec = new Vector2f();
        scrollVec=new Vector2f();
        previousScrollPos= new Vector2d(-1, -1);
        currentScrollPos=new Vector2d(0, 0);
    }

    public void init(Window window) {
        glfwSetCursorPosCallback(window.getWindowHandle(), (windowHandle, xpos, ypos) -> {
            currentPos.x = xpos;
            currentPos.y = ypos;
        });
        glfwSetCursorEnterCallback(window.getWindowHandle(), (windowHandle, entered) -> {
            inWindow = entered;
        });
        glfwSetMouseButtonCallback(window.getWindowHandle(), (windowHandle, button, action, mode) -> {
            leftButtonJustRelesed=(button==GLFW_MOUSE_BUTTON_1&&action==GLFW_RELEASE)&&(leftButtonPressed);
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS;
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS;

        });
        glfwSetScrollCallback(window.getWindowHandle(), ( windowHandle,xoffset,yoffset)->{
            System.out.println("SCROLL");
            currentScrollPos.x=xoffset;
            currentScrollPos.y=yoffset;
            System.out.println("offset:"+currentScrollPos);
        });
    }

    public Vector2f getDisplVec() {
        return displVec;
    }

    public Vector2d getCurrentPos() {
        return currentPos;
    }

    public Vector2f getScrollVec() {
        return scrollVec;
    }

    public Vector2d getCurrentScrollPos() {
        return currentScrollPos;
    }

    public void input(Window window) {
        displVec.x = 0;
        displVec.y = 0;
        scrollVec.x=0;
        scrollVec.y=0;
        if (previousPos.x > 0 && previousPos.y > 0 && inWindow) {
            double deltax = currentPos.x - previousPos.x;
            double deltay = currentPos.y - previousPos.y;
            boolean rotateX = deltax != 0;
            boolean rotateY = deltay != 0;
            if (rotateX) {
                displVec.y = (float) deltax;
            }
            if (rotateY) {
                displVec.x = (float) deltay;
            }

            if(previousScrollPos.y==currentScrollPos.y){
                scrollVec.x=0f;
                scrollVec.y=0f;
            }
            else{
                scrollVec.x=(float)currentScrollPos.x;
                scrollVec.y=(float)currentScrollPos.y;
            }
        }

        previousPos.x = currentPos.x;
        previousPos.y = currentPos.y;
        previousScrollPos.x=currentScrollPos.x;
        previousScrollPos.y=currentScrollPos.y;
    }

    public boolean isLeftButtonPressed() {
        return leftButtonPressed;
    }

    public boolean isRightButtonPressed() {
        return rightButtonPressed;
    }

    public boolean isLeftButtonJustRelesed(){return leftButtonJustRelesed;}

    public void setLeftButtonJustRelesed(boolean q){leftButtonJustRelesed=q;}
    public void setLeftButtonPressed(boolean q){leftButtonPressed=q;}
}
