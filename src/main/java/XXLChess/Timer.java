package XXLChess;

import processing.core.PApplet;

/**
 * Class representing the countdown timer.
 */
public class Timer {
  private int startTime;
  private int interval;

  public Timer(int interval) {
    this.interval = interval;
  }

  public void start(PApplet app) {
    startTime = app.millis();
  }

  /**
   * Function check if the timer is completed or not.
   *
   * @param app the main application.
   * @return true if the timer is completed, false otherwise.
   */
  public boolean complete(PApplet app) {
    int elapsed = app.millis() - startTime;
    if (elapsed > interval) {
      return true;
    }
    return false;
  }

}
