import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.TextureData;
import com.jogamp.opengl.util.texture.TextureIO;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TextureLibrary class to handle the textures in the scene.
 * @author Dr. Steve Maddock
 */
public class TextureLibrary {

  private Map<String, Texture> textures;

  /**
   * Create a new texture library.
   */
  public TextureLibrary() {
    textures = new HashMap<>();
  }

    /**
     * Add a texture to the library.
     * @param gl GL3
     * @param name The name of the texture
     * @param filename The filename of the texture
     */
  public void add(GL3 gl, String name, String filename) {
    add(gl, name, filename, false);
  }

    /**
     * Add a texture to the library.
     * @author Ethan Watts (eawatts1@sheffield.ac.uk)
     * @param gl GL3
     * @param name The name of the texture
     * @param filename The filename of the texture
     * @param repeating Whether the texture should repeat
     */
  public void add(GL3 gl, String name, String filename, boolean repeating) {
    Texture texture = loadTexture(gl, filename, repeating);
    textures.put(name, texture);
  }

    /**
     * Get a texture from the library.
     * @param name The name of the texture
     * @return The texture
     */
  public Texture get(String name) {
    return textures.get(name);
  }

    /**
     * Load a texture from the given filename.
     * Modified by Ethan Watts (eawatts1@sheffield.ac.uk)
     * @param gl GL3
     * @param filename The filename of the texture
     * @param repeating Whether the texture should repeat
     * @return The texture
     */
  private static Texture loadTexture(GL3 gl, String filename, boolean repeating) {
    Texture t = null;
    try {
      File f = new File(filename);
      t = TextureIO.newTexture(f, true);
      t.bind(gl);
      t.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_S, repeating ? GL3.GL_REPEAT : GL3.GL_CLAMP_TO_EDGE);
      t.setTexParameteri(gl, GL3.GL_TEXTURE_WRAP_T, repeating ? GL3.GL_REPEAT : GL3.GL_CLAMP_TO_EDGE);
      t.setTexParameteri(gl, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR_MIPMAP_LINEAR);
      t.setTexParameteri(gl, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
      gl.glGenerateMipmap(GL3.GL_TEXTURE_2D);
    } catch (Exception e) {
      System.out.println("Error loading texture " + filename);
    }
    return t;
  }

    /**
     * Load a skybox from the given faces.
     * @author Ethan Watts (eawatts1@sheffield.ac.uk)
     * @param gl GL3
     * @param faces The faces of the skybox
     * @return The texture ID
     */
  public static int loadSkybox(GL3 gl, List<String> faces) {
    int[] textureID = new int[1];
    gl.glGenTextures(1, textureID, 0);
    gl.glBindTexture(GL3.GL_TEXTURE_CUBE_MAP, textureID[0]);

    for (int i = 0; i < faces.size(); i++) {
      try {
        File file = new File(faces.get(i));
        TextureData data = TextureIO.newTextureData(gl.getGLProfile(), file, false, null);
        if (data != null) {
          gl.glTexImage2D(GL3.GL_TEXTURE_CUBE_MAP_POSITIVE_X + i,
                  0, data.getInternalFormat(), data.getWidth(), data.getHeight(),
                  0, data.getPixelFormat(), data.getPixelType(), data.getBuffer());
        } else {
          System.err.println("Cubemap texture failed to load at path: " + faces.get(i));
        }
      } catch (Exception e) {
        System.err.println("Cubemap texture failed to load at path: " + faces.get(i));
        e.printStackTrace();
      }
    }

    gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_S, GL3.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_T, GL3.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_WRAP_R, GL3.GL_CLAMP_TO_EDGE);
    gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MAG_FILTER, GL3.GL_LINEAR);
    gl.glTexParameteri(GL3.GL_TEXTURE_CUBE_MAP, GL3.GL_TEXTURE_MIN_FILTER, GL3.GL_LINEAR);

    return textureID[0];
  }

    /**
     * Destroy all textures in the library.
     * @param gl3 GL3
     */
  public void destroy(GL3 gl3) {
    for (Texture texture : textures.values()) {
      texture.destroy(gl3);
    }
  }
}
