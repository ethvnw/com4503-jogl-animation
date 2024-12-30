import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

/**
 * Room class to handle the room object in the scene.
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 * With reference to Dr. Steve Maddock's code.
 */
public class Room {
    private final Camera camera;
    private final Light[] lights;
    private final Model[] walls;

    /**
     * Create a room.
     * @param gl GL3
     * @param camera camera to view the room
     * @param lights lights that illuminate the room
     * @param textures library of textures to use
     */
    public Room(GL3 gl, Camera camera, Light[] lights, TextureLibrary textures) {
        this.camera = camera;
        this.lights = lights;
        this.walls = new Model[5];

        textures.add(gl, "nameWallDiffuse", "assets/textures/diffuse_ethan.jpg");
        textures.add(gl, "nameWallSpecular", "assets/textures/specular_ethan.jpg");
        textures.add(gl, "floor", "assets/textures/floor.jpg");
        textures.add(gl, "repeatWallDiffuse", "assets/textures/repeatWallDiffuse.jpg", true);
        textures.add(gl, "repeatWallSpecular", "assets/textures/repeatWallSpecular.jpg", true);
        textures.add(gl, "ceiling", "assets/textures/ceiling.jpg");
        textures.add(gl, "windowDiffuse", "assets/textures/windowDiffuse.jpg");
        textures.add(gl, "windowSpecular", "assets/textures/windowSpecular.jpg");

        float scale = 16f;

        walls[0] = makeWall(gl, "floor",
                new Vec3(0, 0, 0),
                new Vec3(0, 0, 0),
                new Vec3(scale, 1, scale * 2),
                textures.get("floor"),
                null);

        walls[1] = makeWall(gl, "backWall",
                new Vec3(0, scale / 4, -scale),
                new Vec3(90, 0, 0),
                new Vec3(scale, 1, scale / 2),
                textures.get("nameWallDiffuse"),
                textures.get("nameWallSpecular"));

        walls[2] = makeWall(gl, "window",
                new Vec3(-scale / 2, scale / 4, 0),
                new Vec3(90, 90, 0),
                new Vec3(scale * 2, 1, scale / 2),
                textures.get("windowDiffuse"),
                textures.get("windowSpecular"));

        walls[3] = makeWall(gl, "rightWall",
                new Vec3(scale / 2, scale / 4, 0),
                new Vec3(90, 90, 0),
                new Vec3(scale * 2, 1, scale / 2),
                textures.get("repeatWallDiffuse"),
                textures.get("repeatWallSpecular"));

        walls[4] = makeWall(gl, "ceiling",
                new Vec3(0, scale / 2, 0),
                new Vec3(180, 0, 0),
                new Vec3(scale, 1, scale * 2),
                textures.get("ceiling"),
                null);
    }


    /**
     * Create a wall model.
     * @param gl GL3
     * @param name name of the wall
     * @param position position of the wall
     * @param rotation rotation of the wall
     * @param scale scale of the wall
     * @param diffuse diffuse texture
     * @param specular specular texture
     * @return Model of the wall
     */
    private Model makeWall(GL3 gl, String name, Vec3 position, Vec3 rotation, Vec3 scale,
                           Texture diffuse, Texture specular) {

        Mat4 modelMatrix = new Mat4(1);
        modelMatrix = Mat4.multiply(Mat4Transform.scale(scale), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundX(rotation.x), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundY(rotation.y), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.rotateAroundZ(rotation.z), modelMatrix);
        modelMatrix = Mat4.multiply(Mat4Transform.translate(position.x, position.y, position.z), modelMatrix);

        // Change mesh used based on name
        Model model;
        switch (name) {
            case "window":
                model = Utilities.makeModel(gl, name, TwoTrianglesWindowCutout.vertices.clone(), TwoTrianglesWindowCutout.indices.clone(),
                        diffuse, specular, lights, camera);
                break;

                case "rightWall":
                    model = Utilities.makeModel(gl, name, TwoTrianglesRepeating.vertices.clone(),
                            TwoTrianglesRepeating.indices.clone(),
                            diffuse, specular, lights, camera);
                break;

                default:
                model = Utilities.makeModel(gl, name, TwoTriangles.vertices.clone(), TwoTriangles.indices.clone(),
                        diffuse, specular, lights, camera);
        }

        model.setModelMatrix(modelMatrix);
        return model;
    }

    /**
     * Render the room.
     * @param gl GL3
     */
    public void render(GL3 gl) {
        gl.glDisable(GL3.GL_CULL_FACE);
        for (Model wall : walls) {
            wall.render(gl);
        }
        gl.glEnable(GL3.GL_CULL_FACE);
    }

    /**
     * Dispose of the room.
     * @param gl GL3
     */
    public void dispose(GL3 gl) {
        for (Model wall : walls) {
            wall.dispose(gl);
        }
    }
}
