package XXLChess;

import processing.core.PApplet;

/**
 * Interface for objects that changing overtime.
 */
public interface LiveObject {
  public abstract void tick(PApplet app);
}
