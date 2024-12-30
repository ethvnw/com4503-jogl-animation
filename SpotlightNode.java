import gmaths.Mat4;

/**
 * SpotlightNode class to handle a spotlight node in the scene graph.
 * @author Ethan Watts (eawatts1@sheffield.ac.uk)
 * With reference to Dr. Steve Maddock's code.
 *
 */
public class SpotlightNode extends SGNode {

  private final Spotlight spotlight;

  /**
   * Create a new spotlight node.
   * @param name The name of the node
   * @param s The spotlight to use
   */
  public SpotlightNode(String name, Spotlight s) {
    super(name);
    spotlight = s;
  }

  /**
   * Update the spotlight node with the given transformation matrix.
   * @param t The transformation matrix to use
   */
  @Override
  protected void update(Mat4 t) {
    super.update(t);
    spotlight.setDirectionAndPosition(worldTransform);
  }
}
