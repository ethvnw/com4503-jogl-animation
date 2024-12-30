import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.glsl.ShaderCode;
import com.jogamp.opengl.util.glsl.ShaderProgram;
import gmaths.Vec3;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Shader class to handle the shader for each object in the scene.
 * @author Dr. Steve Maddock
 */
public class Shader {

  private static final boolean DISPLAY_SHADERS = false;

  private int ID;
  private String vertexShaderSource;
  private String fragmentShaderSource;

  /**
   * Create a new shader with the given GL3 object and vertex and fragment shader paths.
   * @param gl The GL3 object
   * @param vertexPath The path to the vertex shader
   * @param fragmentPath The path to the fragment shader
   */
  public Shader(GL3 gl, String vertexPath, String fragmentPath) {
    try {
      vertexShaderSource = Files.readString(Paths.get(vertexPath), Charset.defaultCharset());
      fragmentShaderSource = Files.readString(Paths.get(fragmentPath), Charset.defaultCharset());
    }
    catch (IOException e) {
      e.printStackTrace();
    }
    if (DISPLAY_SHADERS) display();
    ID = compileAndLink(gl);
  }

  /**
   * Get the ID of the shader.
   * @return The ID of the shader
   */
  public int getID() {
    return ID;
  }

  /**
   * Use the shader.
   * @param gl The GL3 object
   */
  public void use(GL3 gl) {
    gl.glUseProgram(ID);
  }

  /**
   * Set an integer in the shader.
   * @param gl The GL3 object
   * @param name The name of the integer
   * @param value The value of the integer
   */
  public void setInt(GL3 gl, String name, int value) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform1i(location, value);
  }

  /**
   * Set a float in the shader.
   * @param gl The GL3 object
   * @param name The name of the float
   * @param value The value of the float
   */
  public void setFloat(GL3 gl, String name, float value) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform1f(location, value);
  }

  /**
   * Set a float array in the shader.
   * @param gl The GL3 object
   * @param name The name of the float array
   * @param f1 The first value of the float array
   * @param f2 The second value of the float array
   */
  public void setFloat(GL3 gl, String name, float f1, float f2) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform2f(location, f1, f2);
  }

  /**
   * Set a float array in the shader.
   * @param gl The GL3 object
   * @param name The name of the float array
   * @param f1 The first value of the float array
   * @param f2 The second value of the float array
   * @param f3 The third value of the float array
   */
  public void setFloat(GL3 gl, String name, float f1, float f2, float f3) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform3f(location, f1, f2, f3);
  }

  /**
   * Set a float array in the shader.
   * @param gl The GL3 object
   * @param name The name of the float array
   * @param f1 The first value of the float array
   * @param f2 The second value of the float array
   * @param f3 The third value of the float array
   * @param f4 The fourth value of the float array
   */
  public void setFloat(GL3 gl, String name, float f1, float f2, float f3, float f4) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform4f(location, f1, f2, f3, f4);
  }

  /**
   * Set a float array in the shader.
   * @param gl The GL3 object
   * @param name The name of the float array
   * @param f The float array
   */
  public void setFloatArray(GL3 gl, String name, float[] f) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniformMatrix4fv(location, 1, false, f, 0);
  }

  /**
   * Set a Vec3 in the shader.
   * @param gl The GL3 object
   * @param name The name of the Vec3
   * @param v The Vec3
   */
  public void setVec3(GL3 gl, String name, Vec3 v) {
    int location = gl.glGetUniformLocation(ID, name);
    gl.glUniform3f(location, v.x, v.y, v.z);
  }

  /**
   * Display the paths of the vertex and fragment shaders.
   */
  private void display() {
    System.out.println("***Vertex shader***");
    System.out.println(vertexShaderSource);
    System.out.println("\n***Fragment shader***");
    System.out.println(fragmentShaderSource);
  }

  /**
   * Compiles the vertex and fragment shaders.
   * @param gl The GL3 object
   * @return The ID of the shader
   */
  private int compileAndLink(GL3 gl) {
    gl.glBindVertexArray(1);  // hack to stop link error, since a VAO needs to be bound for shader validation
    String[][] sources = new String[1][1];

    sources[0] = new String[]{ vertexShaderSource };
    ShaderCode vertexShaderCode = new ShaderCode(GL3.GL_VERTEX_SHADER, sources.length, sources);
    boolean compiled = vertexShaderCode.compile(gl, System.err);
    if (!compiled)
      System.err.println("[error] Unable to compile vertex shader: " + sources);

    sources[0] = new String[]{ fragmentShaderSource };
    ShaderCode fragmentShaderCode = new ShaderCode(GL3.GL_FRAGMENT_SHADER, sources.length, sources);
    compiled = fragmentShaderCode.compile(gl, System.err);
    if (!compiled)
      System.err.println("[error] Unable to compile fragment shader: " + sources);

    ShaderProgram program = new ShaderProgram();
    program.init(gl);
    program.add(vertexShaderCode);
    program.add(fragmentShaderCode);
    program.link(gl, System.out);

    if (!program.validateProgram(gl, System.out)) {
      System.err.println("[error] Unable to link program");
      this.display();
    }
    return program.program();
  }
}
