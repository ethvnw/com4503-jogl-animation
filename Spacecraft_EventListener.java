import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import gmaths.Mat4Transform;
import gmaths.Vec3;

import java.util.Arrays;
import java.util.List;

/**
 * GL Event Listener for the Spacecraft assignment
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 * With reference to Dr. Steve Maddock's code.
 */
public class Spacecraft_EventListener implements GLEventListener {
    private final Camera camera;
    private TextureLibrary textures;
    private Room room;
    private Globe globe;
    private Skybox skybox;
    private Light[] lights;
    private DancingRobot dancingRobot;
    private MovingRobot movingRobot;
    private boolean movingRobotTraversing = true;

    /**
     * Create a new GL Event Listener for the Spacecraft assignment.
     * @param camera camera to view the scene
     */
    public Spacecraft_EventListener(Camera camera) {
        this.camera = camera;
        this.camera.setPosition(new Vec3(0f,6f,35f));
        this.camera.setTarget(new Vec3(0, 5f, 0));
    }

    /**
     * Initialise the GL Auto Drawable.
     * @param drawable GL Auto Drawable
     */
    @Override
    public void init(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        System.out.println("Chosen GL Capabilities: " + drawable.getChosenGLCapabilities());
        gl.glClearColor(0.0f, 0.0f, 0.0f, 1.0f);
        gl.glClearDepth(1.0f);
        gl.glEnable(GL.GL_DEPTH_TEST);
        gl.glDepthFunc(GL.GL_LESS);
        gl.glFrontFace(GL.GL_CCW);
        gl.glEnable(GL.GL_CULL_FACE);
        gl.glCullFace(GL.GL_BACK);

        this.initialise(gl);
    }

    /**
     * Reshape the viewport.
     * @param drawable the triggering {@link GLAutoDrawable}
     * @param x lower left corner of the viewport rectangle in pixel units
     * @param y lower left corner of the viewport rectangle in pixel units
     * @param width width of the viewport rectangle in pixel units
     * @param height height of the viewport rectangle in pixel units
     */
    @Override
    public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
        GL3 gl = drawable.getGL().getGL3();
        gl.glViewport(x, y, width, height);

        float aspect = (float)width/(float)height;
        camera.setPerspectiveMatrix(Mat4Transform.perspective(45, aspect));
    }

    /**
     * Display the scene.
     * @param drawable the triggering {@link GLAutoDrawable}
     */
    @Override
    public void display(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        this.render(gl);
    }

    /**
     * Dispose of the objects in the scene.
     * @param drawable the triggering {@link GLAutoDrawable}
     */
    @Override
    public void dispose(GLAutoDrawable drawable) {
        GL3 gl = drawable.getGL().getGL3();
        this.room.dispose(gl);
        this.skybox.dispose(gl);
        for (Light light : this.lights) {
            light.dispose(gl);
        }
        textures.destroy(gl);
        this.globe.dispose(gl);
        this.movingRobot.dispose(gl);
        this.dancingRobot.dispose(gl);
    }

    /**
     * Helper method to initialise the scene.
     * @param gl GL3
     */
    private void initialise(GL3 gl) {
        this.textures = new TextureLibrary();

        this.skybox = new Skybox(gl, this.camera , this.textures);

        this.lights = new Light[3];
        this.lights[0] = new Light(gl);
        this.lights[0].setCamera(this.camera);
        this.lights[0].setPosition(new Vec3(0, 8, -5));

        this.lights[1] = new Light(gl);
        this.lights[1].setCamera(this.camera);
        this.lights[1].setPosition(new Vec3(0, 8, 5));

        this.lights[2] = new Spotlight(gl);
        this.lights[2].setCamera(this.camera);
        this.lights[2].setPosition(new Vec3(6.25f, 2, 2f));

        this.room = new Room(gl, this.camera, this.lights, this.textures);
        this.globe = new Globe(gl, this.camera, this.lights, this.textures);
        this.movingRobot = new MovingRobot(gl, this.camera, this.lights, this.textures);
        this.dancingRobot = new DancingRobot(gl, this.camera, this.lights, this.textures);
    }

    /**
     * JPanel event handler for the dropdown menu for the dancing robot.
     * @param state the state of the dancing robot
     */
    public void startStopRobot1Dance(DancingRobot.State state) {
        this.dancingRobot.setState(state);
    }

    /**
     * JPanel event handler for the dropdown menu for the moving robot.
     * @param start whether to start or stop the moving robot
     */
    public void startStopRobot2Traversal(boolean start) {
        this.movingRobotTraversing = start;
    }

    /**
     * JPanel event handler for the brightness slider for the ceiling light.
     * @param brightness the brightness of the ceiling light
     */
    public void setCeilingLightBrightness(int brightness) {
        float adjustedBrightness = (float) (brightness / 10.0);
        this.lights[0].setBrightness(adjustedBrightness);
        this.lights[1].setBrightness(adjustedBrightness);
    }

    /**
     * JPanel event handler for the brightness slider for the spotlight.
     * @param brightness the brightness of the spotlight
     */
    public void setSpotlightBrightness(int brightness) {
        float adjustedBrightness = (float) (brightness / 10.0);
        this.lights[2].setBrightness(adjustedBrightness);
    }

    /**
     * Helper method to render the scene.
     * @param gl GL3
     */
    private void render(GL3 gl) {
        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
        this.lights[0].render(gl);
        this.lights[1].render(gl);

        this.room.render(gl);

        this.globe.updateGlobeSpin();
        this.globe.render(gl);

        if (this.movingRobotTraversing) {
            this.movingRobot.updateSpotlightSpin();
            this.movingRobot.moveRobot();
        }
        this.movingRobot.render(gl);

        this.dancingRobot.animate(this.movingRobot.getPosition());
        this.dancingRobot.render(gl);

        this.skybox.render(gl);
    }

}
