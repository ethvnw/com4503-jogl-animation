import com.jogamp.opengl.GL3;
import com.jogamp.opengl.util.texture.Texture;
import gmaths.Mat4;
import gmaths.Mat4Transform;
import gmaths.Vec3;

/**
 * Dancing Robot class to handle a robot that dances.
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 */
public class DancingRobot {
    private final SGNode root;
    private final Model[] spheres;
    private final Light[] lights;
    private final Camera camera;
    private final AnimationController danceController;
    private TransformNode topLegTransform, bottomLegTransform, bodyPartsTransform, armsTransform,
            antennaTransform;

    public enum State {
        DANCE, USE_DISTANCE, STOP
    };
    private State state = State.USE_DISTANCE;

    /**
     * Create a new dancing robot with the given camera, lights, and texture library.
     * @param gl The GL3 object
     * @param camera The camera object
     * @param lights The array of lights in the scene
     * @param textures The texture library
     */
    public DancingRobot(GL3 gl, Camera camera, Light[] lights, TextureLibrary textures) {
        this.root = new NameNode("root");
        this.lights = lights;
        this.camera = camera;

        textures.add(gl, "dancingLegDiffuse", "assets/textures/dancingRobotLegDiffuse.jpg");
        textures.add(gl, "dancingBodyDiffuse", "assets/textures/dancingRobotBodyDiffuse.jpg");
        textures.add(gl, "dancingBodySpecular", "assets/textures/dancingRobotBodySpecular.jpg");
        textures.add(gl, "dancingArmDiffuse", "assets/textures/dancingRobotArmDiffuse.jpg");
        textures.add(gl, "dancingHeadDiffuse", "assets/textures/dancingRobotHeadDiffuse.jpg");
        textures.add(gl, "dancingEyeDiffuse", "assets/textures/dancingRobotEyeDiffuse.jpg");
        textures.add(gl, "dancingEyeSpecular", "assets/textures/dancingRobotEyeSpecular.jpg");
        textures.add(gl, "dancingHairDiffuse", "assets/textures/dancingRobotHairDiffuse.jpg");

        this.spheres = new Model[] {
                this.createModel(gl, null, null),
                this.createModel(gl, textures.get("dancingLegDiffuse"), null),
                this.createModel(gl, textures.get("dancingBodyDiffuse"), textures.get("dancingBodySpecular")),
                this.createModel(gl, textures.get("dancingArmDiffuse"), null),
                this.createModel(gl, textures.get("dancingHeadDiffuse"), null),
                this.createModel(gl, textures.get("dancingEyeDiffuse"), textures.get("dancingEyeSpecular")),
                this.createModel(gl, textures.get("dancingHairDiffuse"), null)
        };

        float antennaHeight = 1f;
        float antennaWidth = 0.2f;
        float eyeScale = 0.4f;
        float headHeight = 1f;
        float headWidth = 2f;
        float headDepth = 1f;
        float bodyHeight = 2f;
        float bodyWidth = 1f;
        float armHeight = 2f;
        float armWidth = 0.5f;
        float legHeight = 1f;
        float legWidth = 0.5f;
        float baseHeight = 0.25f;
        float baseWidth = 2f;

        NameNode base = this.makeBase(this.spheres[0], baseHeight, baseWidth);
        NameNode bottomLeg = this.makeLeg(this.spheres[1], legHeight, legWidth, baseHeight, false);
        NameNode topLeg = this.makeLeg(this.spheres[1], legHeight, legWidth, baseHeight, true);
        NameNode body = this.makeBody(this.spheres[2], bodyHeight, bodyWidth);
        NameNode leftArm = this.makeArm(this.spheres[3], armHeight, armWidth, bodyHeight, bodyWidth, true);
        NameNode rightArm = this.makeArm(this.spheres[3], armHeight, armWidth, bodyHeight, bodyWidth, false);
        NameNode head = this.makeHead(this.spheres[4], headHeight, headWidth, headDepth, bodyHeight);
        NameNode eye = this.makeEye(this.spheres[5], eyeScale, bodyHeight, headHeight, headDepth);
        NameNode antenna1 = this.makeAntenna(this.spheres[6], antennaHeight, antennaWidth,
                bodyHeight, headHeight, 1);
        NameNode antenna2 = this.makeAntenna(this.spheres[6], antennaHeight, antennaWidth,
                bodyHeight, headHeight, 0);
        NameNode antenna3 = this.makeAntenna(this.spheres[6], antennaHeight, antennaWidth,
                bodyHeight, headHeight, -1);

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.translate(-4f, baseHeight/2, -9f));
        m = Mat4.multiply(m, Mat4Transform.rotateAroundY(-150));

        TransformNode fullRobotTranslateAndRotate = new TransformNode(
                "full robot translate and rotate", m);

        TransformNode moveBodyPartsUp = new TransformNode("move body parts up",
                Mat4Transform.translate(0, (baseHeight/2) + (3 * legHeight), 0));

        // Initialise the transform nodes for later animations
        this.topLegTransform = new TransformNode("top leg transform", new Mat4(1));
        this.bottomLegTransform = new TransformNode("bottom leg transform", new Mat4(1));
        this.armsTransform = new TransformNode("arms transform", new Mat4(1));
        this.antennaTransform = new TransformNode("antenna transform", new Mat4(1));
        this.bodyPartsTransform = new TransformNode("body parts transform", new Mat4(1));
        this.danceController = new AnimationController(10, true);

        this.root.addChild(fullRobotTranslateAndRotate);
            fullRobotTranslateAndRotate.addChild(base);
                base.addChild(bottomLegTransform);
                    bottomLegTransform.addChild(bottomLeg);
                        bottomLeg.addChild(this.topLegTransform);
                            this.topLegTransform.addChild(topLeg);
                                topLeg.addChild(moveBodyPartsUp);
                                    moveBodyPartsUp.addChild(this.bodyPartsTransform);
                                        this.bodyPartsTransform.addChild(body);
                                        body.addChild(this.armsTransform);
                                            this.armsTransform.addChild(leftArm);
                                            this.armsTransform.addChild(rightArm);
                                        body.addChild(head);
                                            head.addChild(eye);
                                            head.addChild(this.antennaTransform);
                                                this.antennaTransform.addChild(antenna1);
                                                this.antennaTransform.addChild(antenna2);
                                                this.antennaTransform.addChild(antenna3);

        this.root.update();
    }

    /**
     * Create a model with the given diffuse and specular textures.
     * @param gl The GL3 object
     * @param diffuse The diffuse texture
     * @param specular The specular texture
     * @return The model
     */
    public Model createModel(GL3 gl, Texture diffuse, Texture specular) {
        return Utilities.makeModel(gl, "sphere", Sphere.vertices.clone(), Sphere.indices.clone(),
                diffuse, specular, this.lights, this.camera);
    }

    /**
     * Make an antenna with the given parameters.
     * @param sphere The sphere model
     * @param height The height of the antenna
     * @param width The width of the antenna
     * @param bodyHeight The height of the body
     * @param headHeight The height of the head
     * @param modifier The modifier for the rotation
     * @return The antenna node
     */
    public NameNode makeAntenna(Model sphere, float height, float width, float bodyHeight,
                                float headHeight, float modifier)  {
        NameNode antenna = new NameNode("antenna");

        TransformNode antennaTranslate = new TransformNode("antenna translate",
                Mat4Transform.translate(0, headHeight/2 + bodyHeight, 0));

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.rotateAroundZ(modifier * 45));
        m = Mat4.multiply(m, Mat4Transform.scale(width, height, width));

        TransformNode scale = new TransformNode("antenna scale", m);
        ModelNode model = new ModelNode("Sphere(antenna)", sphere);

        antenna.addChild(antennaTranslate);
        antennaTranslate.addChild(scale);
        scale.addChild(model);

        return antenna;
    }

    /**
     * Make an eye with the given parameters.
     * @param sphere The sphere model
     * @param size The size of the eye
     * @param bodyHeight The height of the body
     * @param headHeight The height of the head
     * @param headDepth The depth of the head
     * @return The eye node
     */
    public NameNode makeEye(Model sphere, float size, float bodyHeight, float headHeight,
                            float headDepth) {
        NameNode eye = new NameNode("eye");

        TransformNode eyeTranslate = new TransformNode("eye translate",
                Mat4Transform.translate(0, headHeight/2 + bodyHeight/2, -headDepth*0.4f));

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(size, size*0.7f, size));

        TransformNode scale = new TransformNode("eye scale", m);
        ModelNode model = new ModelNode("Sphere(eye)", sphere);

        eye.addChild(eyeTranslate);
        eyeTranslate.addChild(scale);
        scale.addChild(model);

        return eye;
    }

    /**
     * Make a head with the given parameters.
     * @param sphere The sphere model
     * @param height The height of the head
     * @param width The width of the head
     * @param depth The depth of the head
     * @param bodyHeight The height of the body
     * @return The head node
     */
    public NameNode makeHead(Model sphere, float height, float width, float depth,
                             float bodyHeight) {
        NameNode head = new NameNode("head");

        TransformNode headTranslate = new TransformNode("head translate",
                Mat4Transform.translate(0, bodyHeight/2 + height/2, 0));

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(width, height, depth));

        TransformNode scale = new TransformNode("head scale", m);
        ModelNode model = new ModelNode("Sphere(head)", sphere);

        head.addChild(headTranslate);
        headTranslate.addChild(scale);
        scale.addChild(model);

        return head;
    }

    /**
     * Make an arm with the given parameters.
     * @param sphere The sphere model
     * @param height The height of the arm
     * @param width The width of the arm
     * @param bodyHeight The height of the body
     * @param bodyWidth The width of the body
     * @param left Whether the arm is the left arm
     * @return The arm node
     */
    public NameNode makeArm(Model sphere, float height, float width, float bodyHeight,
                            float bodyWidth, boolean left) {
        NameNode arm;
        TransformNode armTranslate;
        Mat4 rotateOut;
        if (left) {
            arm = new NameNode("left arm");
            armTranslate = new TransformNode("left arm translate",
                    Mat4Transform.translate(bodyWidth*1.3f, bodyHeight/4, 0));
            rotateOut = Mat4Transform.rotateAroundZ(90);
        } else {
            arm = new NameNode("right arm");
            armTranslate = new TransformNode("right arm translate",
                    Mat4Transform.translate(-bodyWidth*1.3f, bodyHeight/4, 0));
            rotateOut = Mat4Transform.rotateAroundZ(-90);
        }

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, rotateOut);
        m = Mat4.multiply(m, Mat4Transform.scale(width, height, width));

        TransformNode scale = new TransformNode("arm scale", m);
        ModelNode model = new ModelNode("Sphere(arm)", sphere);

        arm.addChild(armTranslate);
        armTranslate.addChild(scale);
        scale.addChild(model);

        return arm;
    }

    /**
     * Make a body with the given parameters.
     * @param sphere The sphere model
     * @param height The height of the body
     * @param width The width of the body
     * @return The body node
     */
    public NameNode makeBody(Model sphere, float height, float width) {
        NameNode body = new NameNode("body");

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(width, height, width));

        TransformNode scale = new TransformNode("body scale", m);
        ModelNode model = new ModelNode("Sphere(body)", sphere);

        body.addChild(scale);
        scale.addChild(model);

        return body;
    }

    /**
     * Make a leg with the given parameters.
     * @param sphere The sphere model
     * @param height The height of the leg
     * @param width The width of the leg
     * @param baseHeight The height of the base
     * @param top Whether the leg is the top leg
     * @return The leg node
     */
    public NameNode makeLeg(Model sphere, float height, float width, float baseHeight,
                            boolean top) {
        NameNode leg;
        TransformNode legTranslate;
        if (top) {
            leg = new NameNode("top leg");
            legTranslate = new TransformNode("top leg translate",
                    Mat4Transform.translate(0, (baseHeight/2) + (height*1.5f), 0));
        } else {
            leg = new NameNode("bottom leg");
            legTranslate = new TransformNode("bottom leg translate",
                    Mat4Transform.translate(0, baseHeight/2 + height/2, 0));
        }

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(width, height, width));

        TransformNode scale = new TransformNode("leg scale", m);
        ModelNode model = new ModelNode("Sphere(leg)", sphere);

        leg.addChild(legTranslate);
        legTranslate.addChild(scale);
        scale.addChild(model);

        return leg;
    }

    /**
     * Make a base with the given parameters.
     * @param sphere The sphere model
     * @param height The height of the base
     * @param width The width of the base
     * @return The base node
     */
    public NameNode makeBase(Model sphere, float height, float width) {
        NameNode base = new NameNode("base");

        Mat4 m = new Mat4(1);
        m = Mat4.multiply(m, Mat4Transform.scale(width, height, width));

        TransformNode scale = new TransformNode("base scale", m);
        ModelNode model = new ModelNode("Sphere(base)", sphere);

        base.addChild(scale);
        scale.addChild(model);

        return base;
    }

    /**
     * Get the state of the robot. (DANCE, USE_DISTANCE, STOP)
     * @param state The state of the robot
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * Animate the robot based on the position of the moving robot.
     * @param movingRobotPosition The position of the moving robot
     */
    public void animate(Vec3 movingRobotPosition) {
        if (this.state == State.STOP) {
            this.danceController.pause();
        } else if (this.state == State.DANCE) {
            this.danceController.resume();
            performAnimation();
        } else {
            if (movingRobotPosition.x > (-3 + 3) || movingRobotPosition.z > (-6 + 4)) {
                this.danceController.pause();
            } else {
                this.danceController.resume();
                performAnimation();
            }
        }
    }

    /**
     * Perform the animation of the robot.
     */
    public void performAnimation() {
        this.danceController.update();
        double progress = danceController.getProgress();

        // Phase 1: Descend and Rotate
        if (progress <= 0.2) {
            double descendProgress = progress / 0.2;
            float downwardMotion = (float) (-1 * descendProgress);
            float spin = (float) (360 * descendProgress);

            springAnimation(downwardMotion, spin);

            // Phase 2: Ascend Quickly with Fast Spin
        } else if (progress <= 0.4) {
            double ascendProgress = (progress - 0.2) / 0.2;
            float upwardMotion = (float) (1.7 * ascendProgress);
            float spin = (float) (720.0 * ascendProgress);

            springAnimation(upwardMotion, spin);

            // Phase 3: Descend to Normal Position with Spin
        } else if (progress <= 0.6) {
            double finalDescendProgress = (progress - 0.4) / 0.2;
            float descendToNormal = (float) (1.7 * (1 - finalDescendProgress));
            float spin = (float) (720.0 * finalDescendProgress);

            springAnimation(descendToNormal, spin);

            // Phase 4: Dance
        } else if (progress <= 0.8) {
            double danceProgress = (progress - 0.6) / 0.2;
            float bottomLegRotation = (float) (30 * danceProgress);
            float topLegRotation = (float) (10 * danceProgress);
            float armZRotation = (float) (45 * danceProgress);
            float armXRotation = (float) (360 * danceProgress);
            float antennaRotation = (float) (1080 * danceProgress);

            danceAnimation(bottomLegRotation, topLegRotation, armZRotation, armXRotation, antennaRotation);

        } else {
            double resetProgress = (progress - 0.8) / 0.2;
            float bottomLegRotation = (float) (30 * (1 - resetProgress));
            float topLegRotation = (float) (10 * (1 - resetProgress));
            float armZRotation = (float) (45 * (1 - resetProgress));
            float armXRotation = (float) (360 * (1 - resetProgress));
            float antennaRotation = (float) (1080 * (1 - resetProgress));

            danceAnimation(bottomLegRotation, topLegRotation, armZRotation, armXRotation, antennaRotation);
        }
    }

    /**
     * Handle the spring animation of the robot.
     * @param translation The translation of the spring
     * @param rotation The rotation of the spring
     */
    private void springAnimation(float translation, float rotation) {
        Mat4 currentTransform = new Mat4(1);
        currentTransform = Mat4.multiply(currentTransform, Mat4Transform.translate(0, translation, 0));
        currentTransform = Mat4.multiply(currentTransform, Mat4Transform.rotateAroundY(rotation));

        this.bodyPartsTransform.setTransform(currentTransform);
        this.bodyPartsTransform.update();
    }

    /**
     * Handle the dance animation of the robot.
     * @param bottomLegRotation The rotation of the bottom leg
     * @param topLegRotation The rotation of the top leg
     * @param armZRotation The rotation of the arms around the z-axis
     * @param armXRotation The rotation of the arms around the x-axis
     * @param antennaRotation The rotation of the antenna
     */
    private void danceAnimation(float bottomLegRotation, float topLegRotation, float armZRotation,
                                float armXRotation, float antennaRotation) {
        this.bottomLegTransform.setTransform(Mat4Transform.rotateAroundZ(bottomLegRotation));
        this.bottomLegTransform.update();

        this.topLegTransform.setTransform(Mat4Transform.rotateAroundZ(topLegRotation));
        this.topLegTransform.update();

        Mat4 armTransform = Mat4Transform.rotateAroundZ(armZRotation);
        armTransform = Mat4.multiply(armTransform, Mat4Transform.rotateAroundX(armXRotation));
        this.armsTransform.setTransform(armTransform);
        this.armsTransform.update();
        this.antennaTransform.setTransform(Mat4Transform.rotateAroundY(antennaRotation));
        this.antennaTransform.update();
    }

    /**
     * Render the robot.
     * @param gl The GL3 object
     */
    public void render(GL3 gl) {
        this.root.draw(gl);
    }

    /**
     * Dispose of the robot.
     * @param gl The GL3 object
     */
    public void dispose(GL3 gl) {
      for (Model sphere : this.spheres) {
                sphere.dispose(gl);
      }
    }
}
