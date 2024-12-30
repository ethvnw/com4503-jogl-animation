/**
 * Animation controller class for managing the progress of animations.
 * I declare that this code is my own work.
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 */

public class AnimationController {
    private double previousTime;
    private double elapsedTime;
    private final double duration;
    private final boolean looping;
    private boolean active; // whether animation has finished or been stopped
    private boolean paused; // temporarily stop the animation

    /**
     * Create a new animation controller with the given duration and looping status.
     * @param duration The duration of the animation in seconds
     * @param isLooping Whether the animation should loop
     */
    public AnimationController(double duration, boolean isLooping) {
        this.duration = duration;
        this.looping = isLooping;
        this.active = true;
        this.paused = false;
        this.previousTime = Utilities.getCurrentTime();
        this.elapsedTime = 0;
    }

    /**
     * Update the animation controller's progress based on the time since the last update.
     */
    public void update() {
        if (!this.active || this.paused) return;

        double currentTime = Utilities.getCurrentTime();
        double deltaTime = currentTime - this.previousTime;
        this.previousTime = currentTime;

        this.elapsedTime += deltaTime;
        if (this.elapsedTime > this.duration) {
            if (this.looping) {
                this.elapsedTime %= this.duration;
            } else {
                this.active = false; // Animation is complete
            }
        }
    }

    /**
     * Get the progress of the animation as a value between 0 and 1. (percentage complete)
     * @return The progress of the animation
     */
    public double getProgress() {
        return Math.min(this.elapsedTime / this.duration, 1.0);
    }

    /**
     * Reset the animation controller to its initial state.
     */
    public void reset() {
        this.elapsedTime = 0;
        this.active = true;
        this.paused = false;
        this.previousTime = Utilities.getCurrentTime();
    }

    /**
     * Pause the animation controller.
     */
    public void pause() {
        this.paused = true;
    }

    /**
     * Resume the animation controller.
     */
    public void resume() {
        if (this.paused) {
            this.paused = false;
            this.previousTime = Utilities.getCurrentTime(); // Prevent time jump
        }
    }

    /**
     * Set the active status of the animation controller.
     * @param active Whether the animation controller should be active
     */
    public void setActive(boolean active) {
        this.active = active;
        if (this.active) {
            this.reset(); // Reset on reactivation
        }
    }

    /**
     * Get the active status of the animation controller.
     * @return Whether the animation controller is active
     */
    public boolean isActive() {
        return this.active;
    }

    /**
     * Get the paused status of the animation controller.
     * @return Whether the animation controller is paused
     */
    public boolean isPaused() {
        return this.paused;
    }
}
