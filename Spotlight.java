import com.jogamp.opengl.GL3;
import gmaths.Mat4;
import gmaths.Vec3;

/**
 * Spotlight class to handle a spotlight in the scene.
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 */
public class Spotlight extends Light {
    private final float cutoff;
    private final float outerCutoff;
    private Vec3 direction;

    /**
     * Create a new spotlight.
     * @param gl GL3
     */
    public Spotlight(GL3 gl) {
        super(gl);
        super.getMaterial().setAmbient(0.0f, 0.0f, 0.0f);
        super.getMaterial().setDiffuse(0.5f, 0.5f, 0.5f);
        super.getMaterial().setSpecular(0.5f, 0.5f, 0.5f);

        this.cutoff = 12.5f;
        this.outerCutoff = 17.5f;
        this.direction = new Vec3(0, 0f, -1f);
    }

    /**
     * Get the direction of the spotlight.
     * @return The direction of the spotlight
     */
    public Vec3 getDirection() {
        return direction;
    }

    /**
     * Get the cutoff angle of the spotlight.
     * @return The cutoff angle of the spotlight
     */
    public float getCutoff() {
        return cutoff;
    }

    /**
     * Get the outer cutoff angle of the spotlight.
     * @return The outer cutoff angle of the spotlight
     */
    public float getOuterCutoff() {
        return outerCutoff;
    }

    /**
     * Set the direction of the spotlight.
     * @param direction The direction of the spotlight
     */
    public void setDirection(Vec3 direction) {
        this.direction = direction;
    }

    /**
     * Set the brightness of the light.
     * @param brightness The new brightness of the light
     */
    @Override
    public void setBrightness(float brightness) {
        super.getMaterial().setDiffuse(brightness, brightness, brightness);
        super.getMaterial().setSpecular(brightness, brightness, brightness);
    }

    /**
     * Set the direction and position of the spotlight.
     * @param m The matrix to set the direction and position from
     */
    public void setDirectionAndPosition(Mat4 m) {
        float[] xyz = m.getColumn(3);
        Vec3 position = new Vec3(xyz[0], xyz[1], xyz[2]);
        super.setPosition(position);

        float yRotationRadians = (float) Math.atan2(m.get(0, 2), m.get(2, 2));
        float x = (float) Math.sin(yRotationRadians);
        float z = (float) Math.cos(yRotationRadians);

        this.setDirection(new Vec3(x, 0, z));
    }
}
