package XXLChess;

public abstract class GameObject {

  protected int x;

  protected int y;

  /**
   * Creates a new Game object.
   *
   * @param x The x-coordinate.
   * @param y The y-coordinate.
   */
  public GameObject(int x, int y) {
    this.x = x;
    this.y = y;
  }


  /**
   * Gets the x-coordinate.
   * @return The x-coordinate.
   */
  public int getX() { return this.x; }

  /**
   * Returns the y-coordinate.
   * @return The y-coordinate.
   */
  public int getY() { return this.y; }
}
