package XXLChess;
import processing.core.PApplet;
public abstract class GameObject {

  protected float x;

  protected float y;


  /**
   * Creates a new Game object.
   *
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   */
  public GameObject(float x, float y) {
    this.x = x;
    this.y = y;
  }

  public abstract void draw(PApplet app);

  /**
   * Gets the x-coordinate.
   * @return The x-coordinate.
   */
  public float getX() { return this.x; }

  /**
   * Returns the y-coordinate.
   * @return The y-coordinate.
   */
  public float getY() { return this.y; }
}
