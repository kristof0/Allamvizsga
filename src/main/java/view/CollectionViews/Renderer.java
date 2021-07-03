package view.CollectionViews;

import model.Loaders.IImageLoader;
import org.joml.Matrix4f;
import view.engine.GameItem;
import view.engine.Utils;
import view.engine.Window;
import view.engine.graph.Camera;
import view.engine.graph.ShaderProgram;
import view.engine.graph.Transformation;

import static org.lwjgl.opengl.ARBDirectStateAccess.glBindTextureUnit;
import static org.lwjgl.opengl.GL11.*;

public class Renderer {

    /**
     * Field of View in Radians
     */
    private static final float FOV = (float) Math.toRadians(60.0f);

    private static final float Z_NEAR = 0.01f;

    private static final float Z_FAR = 1000.f;

    private final Transformation transformation;

    private ShaderProgram shaderProgram;

    private final IImageLoader textureLoader;
    public Renderer(IImageLoader textureLoader)
    {
        transformation = new Transformation();
        this.textureLoader=textureLoader;
    }

    public void init(Window window) throws Exception {


        shaderProgram = new ShaderProgram();
        shaderProgram.createVertexShader(Utils.loadResource("/shaders/vertex.vs"));
        shaderProgram.createFragmentShader(Utils.loadResource("/shaders/fragment.fs"));
        shaderProgram.link();

        // Create uniforms for modelView and projection matrices and texture
        shaderProgram.createUniform("projectionMatrix");
        shaderProgram.createUniform("modelViewMatrix");

        shaderProgram.createUniform("layer");
        shaderProgram.createUniform("tex1");
        int texture_unit = 2;
        shaderProgram.setUniform("tex1",texture_unit);
        glBindTextureUnit(texture_unit, textureLoader.getTextureId());
        shaderProgram.createUniform("marked");
    }

    public void clear() {
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT| GL_STENCIL_BUFFER_BIT);
    }

    public void render(Window window, Camera camera, GameItem[] gameItems) {
        clear();

        if ( window.isResized() ) {
            glViewport(0, 0, window.getWidth(), window.getHeight());
            window.setResized(false);
        }

        shaderProgram.bind();

        // Update projection Matrix
        window.updateProjectionMatrix();
        Matrix4f projectionMatrix = window.getProjectionMatrix();
        shaderProgram.setUniform("projectionMatrix", projectionMatrix);

        // Update view Matrix
        Matrix4f viewMatrix = camera.getViewMatrix();

        // Render each gameItem

        for(GameItem gameItem : gameItems) {

            // Set model view matrix for this item
            //System.out.println(gameItem.getTextureAtlasPageIndex());

            shaderProgram.setUniform("layer",gameItem.getTextureAtlasPageIndex());
            Matrix4f modelViewMatrix = transformation.getModelViewMatrix(gameItem, viewMatrix);
            shaderProgram.setUniform("modelViewMatrix", modelViewMatrix);
            // Render the mes for this CollectionViews item
            shaderProgram.setUniform("marked",0);
            gameItem.getMesh().render();
            shaderProgram.setUniform("marked",1);
            gameItem.getMarkedMesh().render();


        }

        shaderProgram.unbind();
    }

    public void cleanup() {
        if (shaderProgram != null) {
            shaderProgram.cleanup();
        }
    }
}
