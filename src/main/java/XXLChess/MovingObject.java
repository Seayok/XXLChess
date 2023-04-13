package XXLChess;

public abstract class MovingObject extends GameObject {
  /* Update object posistion */
  public MovingObject(int x, int y) { super(x, y); }
  public abstract void tick();
  
}