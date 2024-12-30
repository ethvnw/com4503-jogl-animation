/**
 * Vertex and index data for a two-triangle plane with repeating textures.
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 */
public final class TwoTrianglesRepeating {

  // ***************************************************
  /* THE DATA
   */
  // anticlockwise/counterclockwise ordering
  public static final float[] vertices = {      // position, colour, tex coords
          -0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 3.0f,  // top left
          -0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  0.0f, 0.0f,  // bottom left
          0.5f, 0.0f,  0.5f,  0.0f, 1.0f, 0.0f,  4.0f, 0.0f,  // bottom right
          0.5f, 0.0f, -0.5f,  0.0f, 1.0f, 0.0f,  4.0f, 3.0f   // top right
  };

  public static final int[] indices = {         // Note that we start from 0!
          0, 1, 2,
          0, 2, 3
  };

}
