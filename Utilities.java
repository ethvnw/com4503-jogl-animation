import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

/**
 * Utilities class to handle utility functions in the scene.
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 */
public class Utilities {
    public static final double START_TIME = getSeconds();

    /**
     * Get the current time in seconds.
     * @return double
     */
    public static double getCurrentTime() {
        return getSeconds() - START_TIME;
    }

    /**
     * Helper function to get the current time in seconds.
     * @return double
     */
    private static double getSeconds() {
        return System.currentTimeMillis() / 1000.0;
    }

    /**
     * Create a model with the given parameters.
     * @param gl GL3
     * @param name name of the model
     * @param vertices vertices of the model
     * @param indices indices of the model
     * @param diffuse diffuse texture
     * @param specular specular texture
     * @param lights lights that illuminate the model
     * @param camera camera to view the model
     * @return Model
     */
    public static Model makeModel(GL3 gl, String name, float[] vertices, int[] indices,
                                  Texture diffuse, Texture specular, Light[] lights, Camera camera) {
        Mesh mesh = new Mesh(gl, vertices.clone(), indices.clone());
        Shader shader;

        Material material = new Material(
                new Vec3(0.5f, 0.5f, 0.5f),
                new Vec3(0.5f, 0.5f, 0.5f),
                Material.DEFAULT_SPECULAR,
                Material.DEFAULT_SHININESS);

        if (name.equals("light")) {
            shader = new Shader(gl, "assets/shaders/vs_light_01.txt",
                    "assets/shaders/fs_light_01.txt");
        } else {
            shader = getShader(gl, diffuse, specular);
        }

        Mat4 modelMatrix = Mat4.multiply(
                Mat4Transform.scale(1, 1, 1),
                Mat4Transform.translate(0,0.5f, 0));

        if (diffuse == null && specular == null) {
            return new Model(name, mesh, modelMatrix, shader, material, lights, camera);
        } else if (diffuse != null && specular == null) {
            return new Model(name, mesh, modelMatrix, shader, material, lights, camera, diffuse);
        }

        return new Model(name, mesh, modelMatrix, shader, material, lights, camera, diffuse, specular);
    }

    /**
     * Get the shader for the model. Fragment shader is based on the number of textures.
     * @param gl GL3
     * @param diffuse diffuse texture
     * @param specular specular texture
     * @return Shader
     */
    private static Shader getShader(GL3 gl, Texture diffuse, Texture specular) {
        String fragmentShader;

        if (diffuse == null && specular == null) {
            fragmentShader = "assets/shaders/fs_standard_m_0t.txt";
        } else if (diffuse != null && specular == null) {
            fragmentShader = "assets/shaders/fs_standard_m_1t.txt";
        } else {
            fragmentShader = "assets/shaders/fs_standard_m_2t.txt";
        }

        return new Shader(gl, "assets/shaders/vs_standard.txt", fragmentShader);
    }
}
