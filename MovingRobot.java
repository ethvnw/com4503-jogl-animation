import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

/**
 * MovingRobot class to handle the moving robot object in the scene.
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 */
public class MovingRobot {
    private final SGNode root;
    private Model cube, sphere1, sphere2, sphere3;
    private TransformNode rotateSpotlightHousing, fullRobotTranslate, fullRobotRotate;
    private Vec3 position;
    private Vec3 direction;

    /**
     * Create a new moving robot object with the given camera, lights, and texture library.
     * @param gl The GL3 object
     * @param camera The camera object
     * @param lights The array of lights in the scene
     * @param textures The texture library
     */
    public MovingRobot(GL3 gl, Camera camera, Light[] lights, TextureLibrary textures) {
        this.root = new NameNode("root");

        textures.add(gl, "bodyDiffuse", "assets/textures/movingRobotBodyDiffuse.jpg");
        textures.add(gl, "bodySpecular", "assets/textures/movingRobotBodySpecular.jpg");
        textures.add(gl, "eyeDiffuse", "assets/textures/movingRobotEyeDiffuse.jpg");
        textures.add(gl, "eyeSpecular", "assets/textures/movingRobotEyeSpecular.jpg");
        textures.add(gl, "housingDiffuse", "assets/textures/movingRobotHousingDiffuse.jpg");
        textures.add(gl, "housingSpecular", "assets/textures/movingRobotHousingSpecular.jpg");
        textures.add(gl, "bulb", "assets/textures/movingRobotBulb.jpg");

        this.cube = Utilities.makeModel(gl, "cube", Cube.vertices.clone(), Cube.indices.clone(),
                textures.get("bodyDiffuse"), textures.get("bodySpecular"), lights, camera);
        this.sphere1 = Utilities.makeModel(gl, "sphere", Sphere.vertices.clone(), Sphere.indices.clone(),
                textures.get("eyeDiffuse"), textures.get("eyeSpecular"), lights, camera);
        this.sphere2 = Utilities.makeModel(gl, "sphere", Sphere.vertices.clone(), Sphere.indices.clone(),
                textures.get("housingDiffuse"), textures.get("housingSpecular"), lights, camera);
        this.sphere3 = Utilities.makeModel(gl, "light", Sphere.vertices.clone(), Sphere.indices.clone(),
                null, null, lights, camera);

        float bodyHeight = 1f;
        float bodyWidth = 1f;
        float bodyDepth = 2f;
        float eyeRadius = 0.2f;
        float spotlightStemHeight = 0.9f;
        float spotlightStemWidth = 0.1f;
        float spotlightHousingRadius = 1f;
        float spotLightHousingDepth = 0.3f;
        float spotlightBulbRadius = 0.4f;

        Vec3 initialPosition = new Vec3(6.25f, 0.05f, 5.5f);
        this.fullRobotTranslate = new TransformNode("full robot translate",
                Mat4Transform.translate(initialPosition));
        this.position = initialPosition;
        this.direction = new Vec3(0, 0, 0);

        // Initialise the rotation transform for later animation.
        this.fullRobotRotate = new TransformNode("full robot rotate",
                Mat4Transform.rotateAroundY(0));

        NameNode body = this.makeBody(this.cube, bodyHeight, bodyWidth, bodyDepth);
        NameNode eye1 = this.makeEye(this.sphere1, bodyHeight, bodyDepth, eyeRadius, true);
        NameNode eye2 = this.makeEye(this.sphere1, bodyHeight, bodyDepth, eyeRadius ,false);
        NameNode spotlightStem = this.makeSpotlightStem(this.sphere2, bodyHeight,
                spotlightStemHeight, spotlightStemWidth);
        NameNode spotlightHousing = this.makeSpotlightHousing(this.sphere2,
                spotlightHousingRadius, spotLightHousingDepth);
        NameNode spotlightBulb = this.makeSpotlightBulb(gl, this.sphere3, spotlightHousingRadius,
                spotLightHousingDepth, spotlightBulbRadius);
        SpotlightNode spotlight = new SpotlightNode("spotlight", (Spotlight) lights[2]);

        TransformNode translateSpotlight = new TransformNode("translate spotlight",
                Mat4Transform.translate(0, spotlightStemHeight + bodyHeight, 0));
        this.rotateSpotlightHousing = new TransformNode("rotate spotlight housing", new Mat4(1));

        this.root.addChild(fullRobotTranslate);
            fullRobotTranslate.addChild(fullRobotRotate);
                fullRobotRotate.addChild(body);
                    body.addChild(eye1);
                    body.addChild(eye2);
                    body.addChild(spotlightStem);
                        spotlightStem.addChild(translateSpotlight);
                            translateSpotlight.addChild(rotateSpotlightHousing);
                                rotateSpotlightHousing.addChild(spotlightHousing);
                                    spotlightHousing.addChild(spotlightBulb);
                                    spotlightBulb.addChild(spotlight);

        this.root.update();
    }

    /**
     * Make a spotlight bulb with the given parameters.
     * @param gl The GL3 object
     * @param sphere The sphere model
     * @param spotlightHousingRadius The radius of the spotlight housing
     * @param spotlightHousingDepth The depth of the spotlight housing
     * @param spotlightBulbRadius The radius of the spotlight bulb
     * @return The spotlight bulb node
     */
    private NameNode makeSpotlightBulb(GL3 gl, Model sphere, float spotlightHousingRadius,
                                       float spotlightHousingDepth, float spotlightBulbRadius) {
        NameNode spotlightBulb = new NameNode("spotlight bulb");

        TransformNode spotlightBulbTranslate = new TransformNode("spotlight bulb translate",
                Mat4Transform.translate(0, spotlightHousingRadius*0.4f, spotlightHousingDepth/2));

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(spotlightBulbRadius, spotlightBulbRadius,
                spotlightBulbRadius/2));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

        TransformNode scale = new TransformNode("spotlight bulb scale", m);
        ModelNode model = new ModelNode("Sphere(spotlight bulb)", sphere);

        spotlightBulb.addChild(spotlightBulbTranslate);
        spotlightBulbTranslate.addChild(scale);
        scale.addChild(model);

        return spotlightBulb;
    }

    /**
     * Make a spotlight housing with the given parameters.
     * @param sphere The sphere model
     * @param spotlightHousingRadius The radius of the spotlight housing
     * @param spotLightHousingDepth The depth of the spotlight housing
     * @return The spotlight housing node
     */
    private NameNode makeSpotlightHousing(Model sphere, float spotlightHousingRadius,
                                          float spotLightHousingDepth) {
        NameNode spotlightHousing = new NameNode("spotlight housing");

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(spotlightHousingRadius, spotlightHousingRadius,
                spotLightHousingDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

        TransformNode scale = new TransformNode("spotlight housing scale", m);
        ModelNode model = new ModelNode("Sphere(spotlight housing)", sphere);

        spotlightHousing.addChild(scale);
        scale.addChild(model);

        return spotlightHousing;
    }

    /**
     * Make a spotlight stem with the given parameters.
     * @param sphere The sphere model
     * @param bodyHeight The height of the body
     * @param spotlightStemHeight The height of the spotlight stem
     * @param spotlightStemWidth The width of the spotlight stem
     * @return The spotlight stem node
     */
    private NameNode makeSpotlightStem(Model sphere, float bodyHeight, float spotlightStemHeight,
                                      float spotlightStemWidth) {
        NameNode spotlightStem = new NameNode("spotlight stem");

        TransformNode spotlightStemTranslate = new TransformNode("spotlight stem translate",
                Mat4Transform.translate(0, bodyHeight, 0));

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(spotlightStemWidth, spotlightStemHeight, spotlightStemWidth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

        TransformNode scale = new TransformNode("spotlight stem scale", m);
        ModelNode model = new ModelNode("Sphere(spotlight stem)", sphere);

        spotlightStem.addChild(spotlightStemTranslate);
        spotlightStemTranslate.addChild(scale);
        scale.addChild(model);

        return spotlightStem;
    }

    /**
     * Make an eye with the given parameters.
     * @param sphere The sphere model
     * @param bodyHeight The height of the body
     * @param bodyDepth The depth of the body
     * @param eyeRadius The radius of the eye
     * @param left Whether the eye is the left eye
     * @return The eye node
     */
    private NameNode makeEye(Model sphere, float bodyHeight,
                             float bodyDepth, float eyeRadius, boolean left) {
        NameNode eye;
        TransformNode eyeTranslate;
        if (left) {
            eye = new NameNode("left eye");
            eyeTranslate = new TransformNode("left eye translate",
                    Mat4Transform.translate(0.3f, bodyHeight/2, bodyDepth/2));
        } else {
            eye = new NameNode("right eye");
            eyeTranslate = new TransformNode("right eye translate",
                    Mat4Transform.translate(-0.3f, bodyHeight/2, bodyDepth/2));
        }

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.rotateAroundX(180));
        m = Mat4.multiply(m, Mat4Transform.scale(eyeRadius, eyeRadius, eyeRadius));

        TransformNode scale = new TransformNode("eye scale", m);
        ModelNode model = new ModelNode("Sphere(eye)", sphere);

        eye.addChild(eyeTranslate);
        eyeTranslate.addChild(scale);
        scale.addChild(model);

        return eye;
    }

    /**
     * Make a body with the given parameters.
     * @param cube The cube model
     * @param bodyHeight The height of the body
     * @param bodyWidth The width of the body
     * @param bodyDepth The depth of the body
     * @return The body node
     */
    private NameNode makeBody(Model cube, float bodyHeight, float bodyWidth, float bodyDepth) {
        NameNode body = new NameNode("body");

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(bodyWidth, bodyHeight, bodyDepth));
        m = Mat4.multiply(m, Mat4Transform.translate(0, 0.5f, 0));

        TransformNode transform = new TransformNode("body transform", m);
        ModelNode model = new ModelNode("Cube(body)", cube);

        body.addChild(transform);
        transform.addChild(model);

        return body;
    }

    /**
     * Render the robot object.
     * @param gl The GL3 object
     */
    public void render(GL3 gl) {
        this.root.draw(gl);
    }

    /**
     * Get the position of the robot.
     * @return The position of the robot
     */
    public Vec3 getPosition() {
        return this.position;
    }

    /**
     * Update the robot's spotlight spin based on the current time.
     */
    public void updateSpotlightSpin() {
        double spinAngle = 60 * Utilities.getCurrentTime();
        Mat4 rotate = Mat4Transform.rotateAroundY((float) spinAngle);
        rotate = Mat4.multiply(rotate, Mat4Transform.rotateAroundX(25));

        this.rotateSpotlightHousing.setTransform(rotate);
        this.rotateSpotlightHousing.update();
    }

    /**
     * Move the robot based on the current direction.
     */
    public void moveRobot() {
        if (this.direction.x > -90 && this.position.z > 10.25) {
            rotate();
        } else if (this.direction.x <= -90 && this.direction.x > -180 && this.position.x < -3.9) {
            rotate();
        } else if (this.direction.x <= -180 && this.direction.x >= -270 && this.position.z < -12) {
            rotate();
        } else if (this.direction.x <= -270 && this.position.x > 4.1) {
            rotate();
        } else {
            if (this.direction.z > 0) {
                reverseRotateZ();
            }
        }
        moveForward();
    }

    /**
     * Helper method to move the robot forward based on the current direction.
     */
    private void moveForward() {
        float speed = 0.05f;
        if (this.direction.x >-90) {
            this.position.z += speed;
        } else if (this.direction.x <= -90 && this.direction.x > -180) {
            this.position.x -= speed;
        } else if (this.direction.x < -90 && this.direction.x >= -180) {
            this.position.z -= speed;
        } else {
            this.position.x += speed;
        }

        this.fullRobotTranslate.setTransform(Mat4Transform.translate(this.position));
        this.fullRobotTranslate.update();
    }

    /**
     * Helper method to rotate the robot based on the current direction.
     */
    private void rotate() {
        float speed = 2;
        if (this.direction.x <= -360) {
            this.direction.x = 0;
        } else {
            this.direction.x -= speed;
        }

        Mat4 rotate = Mat4Transform.rotateAroundY(this.direction.x);

        this.direction.z += 0.5f;
        rotate = Mat4.multiply(rotate, Mat4Transform.rotateAroundZ(this.direction.z));

        this.fullRobotRotate.setTransform(rotate);
        this.fullRobotRotate.update();
    }

    /**
     * Helper method to rotate the robot on the z-axis in the reverse direction.
     */
    private void reverseRotateZ() {
        this.direction.z -= 1f;
        Mat4 rotate = Mat4Transform.rotateAroundY(this.direction.x);
        rotate = Mat4.multiply(rotate, Mat4Transform.rotateAroundZ(this.direction.z));

        this.fullRobotRotate.setTransform(rotate);
        this.fullRobotRotate.update();
    }

    /**
     * Dispose of the robot object.
     * @param gl The GL3 object
     */
    public void dispose(GL3 gl) {
        this.sphere1.dispose(gl);
        this.sphere2.dispose(gl);
        this.sphere3.dispose(gl);
        this.sphere3.dispose(gl);
        this.cube.dispose(gl);
    }
}
