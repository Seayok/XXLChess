package XXLChess;

import processing.core.PApplet;

/**
 * Represents all visible objects in the game.
 */
public abstract class GameObject {

  protected float cordX;
  protected float cordY;

  /**
   * Creates a new Game object.
   *
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   */
  public GameObject(float x, float y) {
    this.cordX = x;
    this.cordY = y;
  }

  public abstract void draw(PApplet app);

  /**
   * Gets the x-coordinate.
   *
   * @return The x-coordinate.
   */
  public float getX() {
    return this.cordX;
  }

  /**
   * Returns the y-coordinate.
   *
   * @return The y-coordinate.
   */
  public float getY() {
    return this.cordY;
  }
}
