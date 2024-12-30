/**
 * Vertex and index data for a two-triangle plane with a window cutout.
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 */
public final class TwoTrianglesWindowCutout {
    // ***************************************************
    /* THE DATA   */
    // anticlockwise/counterclockwise ordering
    public static final float[] vertices = {
            // X, Y, Z,                 NX, NY, NZ,              S, T
            // Outer rectangle vertices
            -0.5f, 0.0f,  -0.5f,      0.0f, 1.0f, 0.0f,      0.0f, 1.0f,  // top left       0
            0.0f, 0.0f, -0.5f,      0.0f, 1.0f, 0.0f,      0.5f, 1.0f,  // top middle      1
            0.5f,  0.0f,  -0.5f,      0.0f, 1.0f, 0.0f,      1.0f, 1.0f,  // top right      2
            -0.5f, 0.0f,   0.5f,      0.0f, 1.0f, 0.0f,      0.0f, 0.0f,  // bottom left    3
            0.0f, 0.0f,  0.5f,       0.0f, 1.0f, 0.0f,      0.5f, 0.0f,  // bottom middle   4
            0.5f,  0.0f,   0.5f,      0.0f, 1.0f, 0.0f,      1.0f, 0.0f,  // bottom right   5

            // Inner hexagonal window cutout
            -0.15f, 0.0f, -0.35f,     0.0f, 1.0f, 0.0f,      0.35f, 0.85f,  // top left     6
            -0.25f, 0.0f, 0.0f,       0.0f, 1.0f, 0.0f,      0.25f, 0.5f,  // middle left   7
            -0.15f, 0.0f, 0.35f,      0.0f, 1.0f, 0.0f,      0.35f, 0.15f,  // bottom left  8
            0.15f, 0.0f, 0.35f,       0.0f, 1.0f, 0.0f,      0.65f, 0.15f,  // bottom right 9
            0.25f, 0.0f, 0.0f,        0.0f, 1.0f, 0.0f,      0.75f, 0.5f,  // middle right  10
            0.15f, 0.0f, -0.35f,      0.0f, 1.0f, 0.0f,      0.65f, 0.85f,  // top right    11
    };

    public static final int[] indices = {
       6, 0, 1,
            0, 6, 7,
            0, 7, 3,
            3, 7, 8,
            3, 4, 8,
            4, 8, 9,
            4, 5, 9,
            5, 9, 10,
            1, 6, 11,
            1, 2, 11,
            2, 11, 10,
            2, 10, 5,
    };
}
