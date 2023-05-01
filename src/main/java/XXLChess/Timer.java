package XXLChess;

import processing.core.PApplet;

public class Timer {
  private int startTime;
  private int interval;

  public Timer(int interval) {
    this.interval = interval;
  }

  public void start(PApplet app) {
    startTime = app.millis();
  }

  public boolean complete(PApplet app) {
    int elapsed = app.millis() - startTime;
    if (elapsed > interval) {
      return true;
    }
    return false;
  }

}
