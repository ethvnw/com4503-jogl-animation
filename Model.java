import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;

/**
 * Model class to handle a model object in the scene.
 * @author Dr. Steve Maddock
 */
public class Model {
    private String name;
    private Mesh mesh;
    private Mat4 modelMatrix;
    private Shader shader;
    private Material material;
    private Camera camera;
    private Light[] lights;
    private Texture diffuse;
    private Texture specular;

    /**
     * Create a new empty model object.
     */
    public Model() {
        name = null;
        mesh = null;
        modelMatrix = null;
        material = null;
        camera = null;
        lights = null;
        shader = null;
    }

    /**
     * Create a new model object with the given parameters.
     * @param name The name of the model
     * @param mesh The mesh of the model
     * @param modelMatrix The model matrix of the model
     * @param shader The shader of the model
     * @param material The material of the model
     * @param lights The lights in the scene
     * @param camera The camera in the scene
     * @param diffuse The diffuse texture of the model
     * @param specular The specular texture of the model
     */
    public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
                               Camera camera, Texture diffuse, Texture specular) {
        this.name = name;
        this.mesh = mesh;
        this.modelMatrix = modelMatrix;
        this.shader = shader;
        this.material = material;
        this.lights = lights;
        this.camera = camera;
        this.diffuse = diffuse;
        this.specular = specular;
    }

    /**
     * Create a new model object without a specular texture.
     * @param name The name of the model
     * @param mesh The mesh of the model
     * @param modelMatrix The model matrix of the model
     * @param shader The shader of the model
     * @param material The material of the model
     * @param lights The lights in the scene
     * @param camera The camera in the scene
     * @param diffuse The diffuse texture of the model
     */
    public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
                               Camera camera, Texture diffuse) {
        this(name, mesh, modelMatrix, shader, material, lights, camera, diffuse, null);
    }

    /**
     * Create a new model object without a diffuse or specular texture.
     * @param name The name of the model
     * @param mesh The mesh of the model
     * @param modelMatrix The model matrix of the model
     * @param shader The shader of the model
     * @param material The material of the model
     * @param lights The lights in the scene
     * @param camera The camera in the scene
     */
    public Model(String name, Mesh mesh, Mat4 modelMatrix, Shader shader, Material material, Light[] lights,
                               Camera camera) {
        this(name, mesh, modelMatrix, shader, material, lights, camera, null, null);
    }

    /**
     * Set the name of the model.
     * @param s The new name of the model
     */
    public void setName(String s) {
        this.name = s;
    }

    /**
     * Set the mesh of the model.
     * @param m The new mesh of the model
     */
    public void setMesh(Mesh m) {
        this.mesh = m;
    }

    /**
     * Set the model matrix of the model.
     * @param m The new model matrix of the model
     */
    public void setModelMatrix(Mat4 m) {
        modelMatrix = m;
    }

    /**
     * Set the material of the model.
     * @param material The new material of the model
     */
    public void setMaterial(Material material) {
        this.material = material;
    }

    /**
     * Set the shader of the model.
     * @param shader The new shader of the model
     */
    public void setShader(Shader shader) {
        this.shader = shader;
    }

    /**
     * Set the camera of the model.
     * @param camera The new camera of the model
     */
    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    /**
     * Set the lights of the model.
     * @param lights The new lights of the model
     */
    public void setLights(Light[] lights) {
        this.lights = lights;
    }

    /**
     * Set the diffuse texture of the model.
     * @param t The new diffuse texture of the model
     */
    public void setDiffuse(Texture t) {
        this.diffuse = t;
    }

    /**
     * Set the specular texture of the model.
     * @param t The new specular texture of the model
     */
    public void setSpecular(Texture t) {
        this.specular = t;
    }

    /**
     * Render the name of the model to the console.
     * @param gl The GL3 object
     */
    public void renderName(GL3 gl) {
        System.out.println("Name = " + name);
    }

    /**
     * Render the model.
     * @param gl The GL3 object
     */
    public void render(GL3 gl) {
        render(gl, modelMatrix);
    }

    /**
     * Render the model with the given model matrix.
     * @param gl The GL3 object
     * @param modelMatrix The model matrix to render the model with
     */
    public void render(GL3 gl, Mat4 modelMatrix) {
        if (mesh_null()) {
            System.out.println("Error: null in model render");
            return;
        }

        Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), modelMatrix));
        shader.use(gl);
        shader.setFloatArray(gl, "model", modelMatrix.toFloatArrayForGLSL());
        shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());

        shader.setVec3(gl, "viewPos", camera.getPosition());

        shader.setInt(gl,"numLights", lights.length);

        // Created by Ethan Watts (eawatts1@sheffield.ac.uk)
        // Handles the new spotlight shader uniform variables
        for (int i=0; i<lights.length; i++) {
            shader.setVec3(gl, "lights["+i+"].position", lights[i].getPosition());
            shader.setVec3(gl, "lights["+i+"].ambient", lights[i].getMaterial().getAmbient());
            shader.setVec3(gl, "lights["+i+"].diffuse", lights[i].getMaterial().getDiffuse());
            shader.setVec3(gl, "lights["+i+"].specular", lights[i].getMaterial().getSpecular());

            if (lights[i] instanceof Spotlight) {
                Spotlight spotlight = (Spotlight) lights[i];
                shader.setVec3(gl, "lights["+i+"].direction", spotlight.getDirection());
                shader.setFloat(gl, "lights["+i+"].cutOff", (float) Math.cos(Math.toRadians(spotlight.getCutoff())));
                shader.setFloat(gl, "lights["+i+"].outerCutOff", (float) Math.cos(Math.toRadians(spotlight.getOuterCutoff())));
                shader.setFloat(gl, "lights["+i+"].constant", 1.0f);
                shader.setFloat(gl, "lights["+i+"].linear", 0.07f);
                shader.setFloat(gl, "lights["+i+"].quadratic", 0.017f);
            }

            else { // point light
                shader.setFloat(gl, "lights["+i+"].constant", 1.0f);
                shader.setFloat(gl, "lights["+i+"].linear", 0.014f);
                shader.setFloat(gl, "lights["+i+"].quadratic", 0.0007f);

            }
        }

        shader.setVec3(gl, "material.ambient", material.getAmbient());
        shader.setVec3(gl, "material.diffuse", material.getDiffuse());
        shader.setVec3(gl, "material.specular", material.getSpecular());
        shader.setFloat(gl, "material.shininess", material.getShininess());

        if (diffuse!=null) {
            shader.setInt(gl, "first_texture", 0);  // be careful to match these with GL_TEXTURE0 and GL_TEXTURE1
            gl.glActiveTexture(GL.GL_TEXTURE0);
            diffuse.bind(gl);
        }
        if (specular!=null) {
            shader.setInt(gl, "second_texture", 1);
            gl.glActiveTexture(GL.GL_TEXTURE1);
            specular.bind(gl);
        }

        // then render the mesh
        mesh.render(gl);
    }

    /**
     * Helper methods to determine if the mesh is null.
     * @return True if the mesh is null, false otherwise
     */
    private boolean mesh_null() {
        return (mesh==null);
    }

    /**
     * Dispose of the model object.
     * @param gl The GL3 object
     */
    public void dispose(GL3 gl) {
        mesh.dispose(gl);  // only need to dispose of mesh
    }
}
