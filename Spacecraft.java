import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.util.FPSAnimator;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;

/**
 * Main class of the Spacecraft assignment
 * @author Dr. Steve Maddock
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 */
public class Spacecraft extends JFrame implements ActionListener, ChangeListener {
    private static final int WIDTH = 1920;
    private static final int HEIGHT = 1080;
    private static final Dimension DIMENSION = new Dimension(WIDTH, HEIGHT);
    private final FPSAnimator animator;
    private GLCanvas canvas;
    private Spacecraft_EventListener glEventListener;

    /**
     * Make the components to render the canvas and JPanels.
     * @param titleBarText Text to use in the title bar
     */
    public Spacecraft(String titleBarText) {
        super(titleBarText);

        this.setupCanvas();
        getContentPane().add(this.canvas, BorderLayout.CENTER);
        addWindowListener(new windowHandler());

        this.makeJPanelAndComponents();

        this.animator = new FPSAnimator(this.canvas, 60);
        this.animator.start();
    }

    /**
     * Helper method to make the components for the bottom bar of the application.
     */
    private void makeJPanelAndComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel dancingRobotPanel = new JPanel();
        JPanel movingRobotPanel = new JPanel();
        JPanel ceilingLightPanel = new JPanel();
        JPanel spotlightPanel = new JPanel();

        // Dropdowns for the state of each robot.
        JPanel dropdownPanel = new JPanel();
        dropdownPanel.setLayout(new BoxLayout(dropdownPanel, BoxLayout.X_AXIS));
        JPanel sliderPanel = new JPanel();
        sliderPanel.setLayout(new BoxLayout(sliderPanel, BoxLayout.X_AXIS));

        String[] dancingRobotOptions = {"Dance", "Use Distance Measure", "Stop"};
        JComboBox<String> comboBox = new JComboBox<>(dancingRobotOptions);
        comboBox.setName("Dancing Robot Options");
        comboBox.setSelectedItem("Use Distance Measure");
        comboBox.addActionListener(this);
        JLabel label = new JLabel("Dancing Robot Options");

        dancingRobotPanel.add(label);
        dancingRobotPanel.add(comboBox);

        String[] movingRobotOptions = {"Start Traversal", "Stop Traversal"};
        comboBox = new JComboBox<>(movingRobotOptions);
        comboBox.setName("Moving Robot Options");
        comboBox.addActionListener(this);
        label = new JLabel("Moving Robot Options");

        movingRobotPanel.add(label);
        movingRobotPanel.add(comboBox);

        // Ceiling light brightness control
        ceilingLightPanel.setLayout(new BoxLayout(ceilingLightPanel, BoxLayout.Y_AXIS));
        JSlider slider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
        slider.setName("Ceiling Light Brightness");
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.addChangeListener(this);
        label = new JLabel("Ceiling Light Brightness");

        ceilingLightPanel.add(label);
        ceilingLightPanel.add(slider);

        // Spotlight brightness control
        spotlightPanel.setLayout(new BoxLayout(spotlightPanel, BoxLayout.Y_AXIS));
        slider = new JSlider(JSlider.HORIZONTAL, 0, 10, 5);
        slider.setName("Spotlight Brightness");
        slider.setMajorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.addChangeListener(this);
        label = new JLabel("Spotlight Brightness");

        spotlightPanel.add(label);
        spotlightPanel.add(slider);

        dropdownPanel.add(dancingRobotPanel);
        dropdownPanel.add(movingRobotPanel);
        sliderPanel.add(ceilingLightPanel);
        sliderPanel.add(spotlightPanel);

        mainPanel.add(dropdownPanel, BorderLayout.NORTH);
        mainPanel.add(sliderPanel, BorderLayout.SOUTH);
        this.add(mainPanel, BorderLayout.SOUTH);
    }

    /**
     * Set up the canvas.
     */
    private void setupCanvas() {
        GLCapabilities glCapabilities = new GLCapabilities(GLProfile.get(GLProfile.GL3));
        this.canvas = new GLCanvas(glCapabilities);
        Camera camera = new Camera(Camera.DEFAULT_POSITION, Camera.DEFAULT_TARGET, Camera.DEFAULT_UP);

        this.glEventListener = new Spacecraft_EventListener(camera);
        this.canvas.addGLEventListener(this.glEventListener);
        this.canvas.addKeyListener(new MyKeyboardInput(camera));
        this.canvas.addMouseMotionListener(new MyMouseInput(camera));
    }

    /**
     * Dropdown (combobox) event handler.
     * @param e the event to be processed
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() instanceof JComboBox) {
            JComboBox<?> sourceComboBox = (JComboBox<?>) e.getSource();
            if (sourceComboBox.getName().equalsIgnoreCase("Dancing Robot Options")) {
                String selectedItem = (String) sourceComboBox.getSelectedItem();
                if (selectedItem != null) {
                    switch (selectedItem) {
                        case "Dance":
                            this.glEventListener.startStopRobot1Dance(DancingRobot.State.DANCE);
                            break;
                        case "Use Distance Measure":
                            this.glEventListener.startStopRobot1Dance(DancingRobot.State.USE_DISTANCE);
                            break;
                        case "Stop":
                            this.glEventListener.startStopRobot1Dance(DancingRobot.State.STOP);
                            break;
                    }
                }
            } else {
                String selectedItem = (String) sourceComboBox.getSelectedItem();
                if (selectedItem != null) {
                    switch (selectedItem) {
                        case "Start Traversal":
                            this.glEventListener.startStopRobot2Traversal(true);
                            break;
                        case "Stop Traversal":
                            this.glEventListener.startStopRobot2Traversal(false);
                            break;
                    }
                }
            }
        }
    }

    /**
     * Brightness slider event handler.
     * @param e  a ChangeEvent object
     */
    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        String name = source.getName();
        int brightness = source.getValue();

        if (name.equalsIgnoreCase("Ceiling Light Brightness")) {
            this.glEventListener.setCeilingLightBrightness(brightness);
        } else if (name.equalsIgnoreCase("Spotlight Brightness")) {
            this.glEventListener.setSpotlightBrightness(brightness);
        }
    }

    /**
     * Handles closing the program.
     */
    private class windowHandler extends WindowAdapter {
        @Override
        public void windowClosing(WindowEvent e) {
            animator.stop();
            remove(canvas);
            dispose();
            System.exit(0);
        }
    }

    /**
     * Run the program.
     * @param args keyboard arguments
     */
    public static void main(String[] args) {
        Spacecraft frame = new Spacecraft("Spacecraft Assignment");
        frame.getContentPane().setPreferredSize(DIMENSION);
        frame.pack();
        frame.setVisible(true);
        frame.canvas.requestFocusInWindow();
    }
}

/**
 * Keyboard input class.
 * @author Dr. Steve Maddock
 */
class MyKeyboardInput extends KeyAdapter {
    private final Camera camera;

    /**
     * Create keyboard input handler with given camera.
     * @param camera The camera to use
     */
    public MyKeyboardInput(Camera camera) {
        this.camera = camera;
    }

    /**
     * Keyboard key pressed event handler.
     * @param e the event to be processed
     */
    public void keyPressed(KeyEvent e) {
        Camera.Movement m = Camera.Movement.NO_MOVEMENT;
        switch (e.getKeyCode()) {
            case KeyEvent.VK_LEFT:  m = Camera.Movement.LEFT;  break;
            case KeyEvent.VK_RIGHT: m = Camera.Movement.RIGHT; break;
            case KeyEvent.VK_UP:    m = Camera.Movement.UP;    break;
            case KeyEvent.VK_DOWN:  m = Camera.Movement.DOWN;  break;
            case KeyEvent.VK_A:  m = Camera.Movement.FORWARD;  break;
            case KeyEvent.VK_Z:  m = Camera.Movement.BACK;  break;
        }
        camera.keyboardInput(m);
    }
}

/**
 * Mouse input class.
 * @author Dr. Steve Maddock
 */
class MyMouseInput extends MouseMotionAdapter {
    private Point lastpoint;
    private final Camera camera;

    /**
     * Create a mouse input handler with the given camera.
     * @param camera The camera to use
     */
    public MyMouseInput(Camera camera) {
        this.camera = camera;
    }

    /**
     * mouse is used to control camera position
     *
     * @param e  instance of MouseEvent
     */
    public void mouseDragged(MouseEvent e) {
        Point ms = e.getPoint();
        float sensitivity = 0.001f;
        float dx=(float) (ms.x-lastpoint.x)*sensitivity;
        float dy=(float) (ms.y-lastpoint.y)*sensitivity;
        //System.out.println("dy,dy: "+dx+","+dy);
        if (e.getModifiersEx()==MouseEvent.BUTTON1_DOWN_MASK)
            camera.updateYawPitch(dx, -dy);
        lastpoint = ms;
    }

    /**
     * mouse is used to control camera position
     *
     * @param e  instance of MouseEvent
     */
    public void mouseMoved(MouseEvent e) {
        lastpoint = e.getPoint();
    }
}
