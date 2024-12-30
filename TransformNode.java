import gmaths.Mat4;

/**
 * TransformNode class to handle a transform node in the scene graph.
 * @author Dr. Steve Maddock
 */
public class TransformNode extends SGNode {

  private Mat4 transform;

  /**
   * Create a new transform node.
   * @param name The name of the node
   * @param t The transformation matrix to use
   */
  public TransformNode(String name, Mat4 t) {
    super(name);
    transform = new Mat4(t);
  }

  /**
   * Get the transformation matrix of the node.
   * @param m The transformation matrix
   */
  public void setTransform(Mat4 m) {
    transform = new Mat4(m);
  }
  
  protected void update(Mat4 t) {
    worldTransform = t;
    t = Mat4.multiply(worldTransform, transform);
      for (SGNode child : children) {
          child.update(t);
      }
  }

  /**
   * Print the children of the node.
   * @param indent The indent level
   * @param inFull Whether to print the world transform in full
   */
  public void print(int indent, boolean inFull) {
    System.out.println(getIndentString(indent)+"Name: "+name);
    if (inFull) {
      System.out.println("worldTransform");
      System.out.println(worldTransform);
      System.out.println("transform node:");
      System.out.println(transform);
    }
      for (SGNode child : children) {
          child.print(indent + 1, inFull);
      }
  }
}
