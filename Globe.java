import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;

/**
 * Globe class to handle the globe object in the scene.
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 */
public class Globe {
    private final SGNode root;
    private final Model cube, sphere1, sphere2;
    private TransformNode globeSphereSpin;

    /**
     * Create a new globe object with the given camera, lights, and texture library.
     * @param gl The GL3 object
     * @param camera The camera object
     * @param lights The array of lights in the scene
     * @param textures The texture library
     */
    public Globe(GL3 gl, Camera camera, Light[] lights, TextureLibrary textures) {
        this.root = new NameNode("root");

        textures.add(gl, "baseDiffuse", "assets/textures/baseDiffuse.jpg");
        textures.add(gl, "baseSpecular", "assets/textures/baseSpecular.jpg");
        textures.add(gl, "axisDiffuse", "assets/textures/axisDiffuse.jpg");
        textures.add(gl, "axisSpecular", "assets/textures/axisSpecular.jpg");
        textures.add(gl, "globeDiffuse", "assets/textures/globeDiffuse.jpg");
        textures.add(gl, "globeSpecular", "assets/textures/globeSpecular.jpg");

        this.cube = Utilities.makeModel(gl, "cube", Cube.vertices.clone(), Cube.indices.clone(),
                textures.get("baseDiffuse"), textures.get("baseSpecular"), lights, camera);
        this.sphere1 = Utilities.makeModel(gl, "sphere", Sphere.vertices.clone(), Sphere.indices.clone(),
                textures.get("axisDiffuse"), textures.get("axisSpecular"), lights, camera);
        this.sphere2 = Utilities.makeModel(gl, "sphere", Sphere.vertices.clone(), Sphere.indices.clone(),
                textures.get("globeDiffuse"), textures.get("globeSpecular"), lights, camera);

        float baseHeight = 0.5f;
        float baseWidth = 1f;
        float axisHeight = 2f;
        float axisWidth = 0.1f;
        float globeRadius = 1.5f;

        TransformNode fullGlobeTranslate = new TransformNode("full globe translate",
                Mat4Transform.translate(4f, baseHeight/2, 9.5f));

        NameNode base = this.makeBase(this.cube, baseHeight, baseWidth);
        NameNode axis = this.makeAxis(this.sphere1, baseHeight, axisHeight, axisWidth);
        NameNode globe = this.makeGlobe(this.sphere2, axisHeight, globeRadius);

        this.root.addChild(fullGlobeTranslate);
            fullGlobeTranslate.addChild(base);
                base.addChild(axis);
                    axis.addChild(globe);

        this.root.update();
    }

    /**
     * Make a globe with the given parameters.
     * @param sphere The sphere model
     * @param axisHeight The height of the axis
     * @param radius The radius of the globe
     * @return The globe node
     */
    private NameNode makeGlobe(Model sphere, float axisHeight, float radius) {
        NameNode globe = new NameNode("globe");

        TransformNode globeTranslate = new TransformNode("globe translate",
                Mat4Transform.translate(0, axisHeight*0.4f, 0));

        this.globeSphereSpin = new TransformNode("globe spin", Mat4Transform.rotateAroundY(0));

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(radius, radius, radius));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

        TransformNode scale = new TransformNode("globe scale", m);
        ModelNode model = new ModelNode("Sphere(globe)", sphere);

        globe.addChild(globeTranslate);
        globeTranslate.addChild(this.globeSphereSpin);
        this.globeSphereSpin.addChild(scale);
        scale.addChild(model);

        return globe;

    }

    /**
     * Make an axis with the given parameters.
     * @param sphere The sphere model
     * @param baseHeight The height of the base
     * @param height The height of the axis
     * @param width The width of the axis
     * @return The axis node
     */
    private NameNode makeAxis(Model sphere, float baseHeight, float height, float width) {
        NameNode axis = new NameNode("axis");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(0, baseHeight, 0));
        m = Mat4.multiply(m, Mat4Transform.scale(width, height, width));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

        TransformNode transform = new TransformNode("axis transform", m);
        ModelNode model = new ModelNode("Sphere(axis)", sphere);

        axis.addChild(transform);
        transform.addChild(model);

        return axis;
    }

    /**
     * Make a base with the given parameters.
     * @param cube The cube model
     * @param height The height of the base
     * @param width The width of the base
     * @return The base node
     */
    private NameNode makeBase(Model cube, float height, float width) {
        NameNode base = new NameNode("base");
        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(width, height, width));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

        TransformNode transform = new TransformNode("base transform", m);
        ModelNode model = new ModelNode("Cube(base)", cube);

        base.addChild(transform);
        transform.addChild(model);
        return base;
    }

    /**
     * Update the globe's spin based on the current time.
     */
    public void updateGlobeSpin() {
        double spinAngle = 30 * Utilities.getCurrentTime();
        this.globeSphereSpin.setTransform(Mat4Transform.rotateAroundY((float) spinAngle));
        this.globeSphereSpin.update();
    }

    /**
     * Render the globe object.
     * @param gl The GL3 object
     */
    public void render(GL3 gl) {
        root.draw(gl);
    }

    /**
     * Dispose of the globe object.
     * @param gl The GL3 object
     */
    public void dispose(GL3 gl) {
        cube.dispose(gl);
        sphere1.dispose(gl);
        sphere2.dispose(gl);
    }
}
