import com.jogamp.opengl.GL3;

/**
 * ModelNode class to handle a model node in the scene.
 * @author Dr. Steve Maddock
 */
public class ModelNode extends SGNode {
  protected Model model;

  /**
   * Create a new model node with the given name and model.
   * @param name The name of the model node
   * @param m The model of the model node
   */
  public ModelNode(String name, Model m) {
    super(name);
    model = m; 
  }

  /**
   * Render the model node.
   * @param gl The GL3 object
   */
  public void draw(GL3 gl) {
    model.render(gl, worldTransform);
      for (SGNode child : children) {
          child.draw(gl);
      }
  }
}
