import com.jogamp.common.nio.Buffers;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

/**
 * Light class to handle a light object in the scene.
 * @author Dr. Steve Maddock
 */
public class Light {
  
  private Material material;
  private Vec3 position;
  private Mat4 model;
  Shader shader;
  Camera camera;

  /**
   * Create a new light object with the given GL3 object.
   * @param gl The GL3 object
   */
  public Light(GL3 gl) {
    material = new Material();
    material.setAmbient(0.4f, 0.4f, 0.4f);
    material.setDiffuse(0.7f, 0.7f, 0.7f);
    material.setSpecular(0.7f, 0.7f, 0.7f);
    position = new Vec3(3f,2f,1f);
    model = new Mat4(1);
    
    fillBuffers(gl);
    shader = new Shader(gl, "assets/shaders/vs_light_01.txt", "assets/shaders/fs_light_01.txt");
  }

  /**
   * Set the position of the light.
   * @param v The new position of the light
   */
  public void setPosition(Vec3 v) {
    position.x = v.x;
    position.y = v.y;
    position.z = v.z;
  }

  /**
   * Set the position of the light.
   * @param x The x-coordinate of the new position
   * @param y The y-coordinate of the new position
   * @param z The z-coordinate of the new position
   */
  public void setPosition(float x, float y, float z) {
    position.x = x;
    position.y = y;
    position.z = z;
  }

  /**
   * Set the brightness of the light.
   * @author Ethan Watts (eawatts1@sheffield.ac.uk)
   * @param brightness The new brightness of the light
   */
  public void setBrightness(float brightness) {
    material.setAmbient(brightness, brightness, brightness);
    material.setDiffuse(brightness, brightness, brightness);
  }

  /**
   * Get the position of the light.
   * @return The position of the light
   */
  public Vec3 getPosition() {
    return position;
  }

  /**
   * Set the material of the light.
   * @param m The new material of the light
   */
  public void setMaterial(Material m) {
    material = m;
  }

  /**
   * Get the material of the light.
   * @return The material of the light
   */
  public Material getMaterial() {
    return material;
  }

  /**
   * Set the camera of the light.
   * @param camera The new camera of the light
   */
  public void setCamera(Camera camera) {
    this.camera = camera;
  }

  /**
   * Render the light object.
   * @param gl The GL3 object
   */
  public void render(GL3 gl) { //, Mat4 perspective, Mat4 view) {
    Mat4 model = new Mat4(1);
    model = Mat4.multiply(Mat4Transform.scale(0.3f,0.3f,0.3f), model);
    model = Mat4.multiply(Mat4Transform.translate(position), model);
    
    Mat4 mvpMatrix = Mat4.multiply(camera.getPerspectiveMatrix(), Mat4.multiply(camera.getViewMatrix(), model));
    
    shader.use(gl);
    shader.setFloatArray(gl, "mvpMatrix", mvpMatrix.toFloatArrayForGLSL());
  
    gl.glBindVertexArray(vertexArrayId[0]);
    
    gl.glDrawElements(GL.GL_TRIANGLES, indices.length, GL.GL_UNSIGNED_INT, 0);
    gl.glBindVertexArray(0);
  }

  /**
   * Dispose of the light object.
   * @param gl The GL3 object
   */
  public void dispose(GL3 gl) {
    gl.glDeleteBuffers(1, vertexBufferId, 0);
    gl.glDeleteVertexArrays(1, vertexArrayId, 0);
    gl.glDeleteBuffers(1, elementBufferId, 0);
  }

    // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  
    protected float[] vertices = new float[] {  // x,y,z
      -0.5f, -0.5f, -0.5f,  // 0
      -0.5f, -0.5f,  0.5f,  // 1
      -0.5f,  0.5f, -0.5f,  // 2
      -0.5f,  0.5f,  0.5f,  // 3
       0.5f, -0.5f, -0.5f,  // 4
       0.5f, -0.5f,  0.5f,  // 5
       0.5f,  0.5f, -0.5f,  // 6
       0.5f,  0.5f,  0.5f   // 7
     };

    protected int[] indices =  new int[] {
      0,1,3, // x -ve
      3,2,0, // x -ve
      4,6,7, // x +ve
      7,5,4, // x +ve
      1,5,7, // z +ve
      7,3,1, // z +ve
      6,4,0, // z -ve
      0,2,6, // z -ve
      0,4,5, // y -ve
      5,1,0, // y -ve
      2,3,7, // y +ve
      7,6,2  // y +ve
    };

  private int vertexStride = 3;
  private int vertexXYZFloats = 3;
  
  // ***************************************************
  /* THE LIGHT BUFFERS
   */

  private int[] vertexBufferId = new int[1];
  private int[] vertexArrayId = new int[1];
  private int[] elementBufferId = new int[1];

  /**
   * Fill the buffers for rendering the light object model
   * @param gl The GL3 object
   */
  protected void fillBuffers(GL3 gl) {
    gl.glGenVertexArrays(1, vertexArrayId, 0);
    gl.glBindVertexArray(vertexArrayId[0]);
    gl.glGenBuffers(1, vertexBufferId, 0);
    gl.glBindBuffer(GL.GL_ARRAY_BUFFER, vertexBufferId[0]);
    FloatBuffer fb = Buffers.newDirectFloatBuffer(vertices);
    
    gl.glBufferData(GL.GL_ARRAY_BUFFER, Float.BYTES * vertices.length, fb, GL.GL_STATIC_DRAW);
    
    int stride = vertexStride;
    int numXYZFloats = vertexXYZFloats;
    int offset = 0;
    gl.glVertexAttribPointer(0, numXYZFloats, GL.GL_FLOAT, false, stride*Float.BYTES, offset);
    gl.glEnableVertexAttribArray(0);
     
    gl.glGenBuffers(1, elementBufferId, 0);
    IntBuffer ib = Buffers.newDirectIntBuffer(indices);
    gl.glBindBuffer(GL.GL_ELEMENT_ARRAY_BUFFER, elementBufferId[0]);
    gl.glBufferData(GL.GL_ELEMENT_ARRAY_BUFFER, Integer.BYTES * indices.length, ib, GL.GL_STATIC_DRAW);
  }
}
