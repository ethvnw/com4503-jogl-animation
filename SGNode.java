import com.jogamp.opengl.GL3;
import gmaths.Mat4;

import java.util.ArrayList;

/**
 * SGNode class to handle a scene graph node in the scene.
 * @author Dr. Steve Maddock
 */
public class SGNode {
  protected String name;
  protected ArrayList<SGNode> children;
  protected Mat4 worldTransform;

  /**
   * Create a new scene graph node with the given name.
   * @param name The name of the node
   */
  public SGNode(String name) {
    children = new ArrayList<>();
    this.name = name;
    worldTransform = new Mat4(1);
  }

  /**
   * Add a child to the scene graph node.
   * @param child The child to add
   */
  public void addChild(SGNode child) {
    children.add(child);
  }

  /**
   * Update the scene graph node.
   */
  public void update() {
    update(worldTransform);
  }

  /**
   * Update the scene graph node with the given transformation matrix.
   * @param t The transformation matrix
   */
  protected void update(Mat4 t) {
    worldTransform = t;
    for (int i=0; i<children.size(); i++) {
      children.get(i).update(t);
    }
  }

  /**
   * Gets the indent string for the given indent level.
   * @param indent The indent level
   * @return The indent string
   */
  protected String getIndentString(int indent) {
    String s = ""+indent+" ";
    for (int i=0; i<indent; ++i) {
      s+="  ";
    }
    return s;
  }

  /**
   * Print the scene graph node.
   * @param indent The indent level
   * @param inFull Whether to print the world transform in full
   */
  public void print(int indent, boolean inFull) {
    System.out.println(getIndentString(indent)+"Name: "+name);
    if (inFull) {
      System.out.println("worldTransform");
      System.out.println(worldTransform);
    }
      for (SGNode child : children) {
          child.print(indent + 1, inFull);
      }
  }

  /**
   * Render all the nodes in the scene graph.
   * @param gl The GL3 object
   */
  public void draw(GL3 gl) {
      for (SGNode child : children) {
          child.draw(gl);
      }
  }
}
