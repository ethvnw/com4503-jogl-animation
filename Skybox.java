import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;

import java.nio.FloatBuffer;
import java.util.Arrays;
import java.util.List;

/**
 * Skybox class to handle a skybox in the scene
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 * With reference to Joey's tutorials.
 */
public class Skybox {
    private final Camera camera;
    private final Shader shader;
    private final int textureId;
    private final Texture animatedTexture;
    private final int[] vertexArrayId = new int[1];
    private final int[] vertexBufferId = new int[1];
    float[] skyboxVertices = {
            -1.0f,  1.0f, -1.0f,
            -1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,

            -1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f, -1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,

            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,

            -1.0f, -1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f, -1.0f,  1.0f,
            -1.0f, -1.0f,  1.0f,

            -1.0f,  1.0f, -1.0f,
            1.0f,  1.0f, -1.0f,
            1.0f,  1.0f,  1.0f,
            1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f,  1.0f,
            -1.0f,  1.0f, -1.0f,

            -1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f, -1.0f,
            1.0f, -1.0f, -1.0f,
            -1.0f, -1.0f,  1.0f,
            1.0f, -1.0f,  1.0f
    };

    /**
     * Create a skybox with the given parameters.
     * @param gl The GL3 object
     * @param camera The camera to use
     * @param textures The texture library to use
     */
    public Skybox(GL3 gl, Camera camera, TextureLibrary textures) {
        this.camera = camera;
        this.shader = new Shader(gl, "assets/shaders/vs_skybox.txt",
                "assets/shaders/fs_skybox.txt");

        List<String> skyboxFaces = Arrays.asList(
                "assets/textures/skybox/right.png",
                "assets/textures/skybox/left.png",
                "assets/textures/skybox/top.png",
                "assets/textures/skybox/bottom.png",
                "assets/textures/skybox/front.png",
                "assets/textures/skybox/back.png"
        );

        this.textureId = TextureLibrary.loadSkybox(gl, skyboxFaces);
        textures.add(gl, "animatedTexture", "assets/textures/skybox/animated.png");
        this.animatedTexture = textures.get("animatedTexture");

        this.fillBuffers(gl);
    }

    /**
     * Render the skybox.
     * @param gl The GL3 object
     */
    public void render(GL3 gl) {
        gl.glDepthFunc(GL.GL_LEQUAL);
        this.shader.use(gl);

        // Remove the translation components
        Mat4 view = camera.getViewMatrix();
        view.set(0, 3, 0.0f);
        view.set(1, 3, 0.0f);
        view.set(2, 3, 0.0f);

        Mat4 perspective = camera.getPerspectiveMatrix();

        this.shader.setFloatArray(gl, "view", view.toFloatArrayForGLSL());
        this.shader.setFloatArray(gl, "projection", perspective.toFloatArrayForGLSL());

        gl.glBindVertexArray(vertexArrayId[0]);

        this.shader.setInt(gl, "skybox", 0);
        gl.glActiveTexture(GL.GL_TEXTURE0);
        gl.glBindTexture(GL.GL_TEXTURE_CUBE_MAP, textureId);

        this.shader.setInt(gl, "animatedTexture", 1);
        gl.glActiveTexture(GL.GL_TEXTURE1);
        animatedTexture.bind(gl);

        float offsetX = (float) (Math.sin(Utilities.getCurrentTime() * 0.1) * 0.1);
        float offsetY = (float) (Math.sin(Utilities.getCurrentTime() * 0.1) * 0.1);
        this.shader.setFloat(gl, "offset", offsetX, offsetY);

        gl.glDrawArrays(GL.GL_TRIANGLES, 0, 36);
        gl.glBindVertexArray(0);
        gl.glDepthFunc(GL.GL_LESS);
    }

    /**
     * Dispose of the skybox
     * @param gl The GL3 object
     */
    public void dispose(GL3 gl) {
        gl.glDeleteBuffers(1, vertexBufferId, 0);
        gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    }

    /**
     * Fill the buffers for the skybox shader.
     * @param gl The GL3 object
     */
    private void fillBuffers(GL3 gl) {
        gl.glGenVertexArrays(1, vertexArrayId, 0);
        gl.glBindVertexArray(vertexArrayId[0]);

        gl.glGenBuffers(1, vertexBufferId, 0);
        gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);

        FloatBuffer fb = Buffers.newDirectFloatBuffer(skyboxVertices);
        gl.glBufferData(GL.GL_ARRAY_BUFFER, (long) skyboxVertices.length * Float.BYTES, fb,
                GL.GL_STATIC_DRAW);
        gl.glEnableVertexAttribArray(0);
        gl.glVertexAttribPointer(0, 3, GL.GL_FLOAT, false,
                3 * Float.BYTES, 0);
    }
}
