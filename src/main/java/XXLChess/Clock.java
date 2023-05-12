package XXLChess;

import processing.core.PApplet;

/**
 * This class represents a clock for each player.
 */
public class Clock extends GameObject implements LiveObject {
  private Timer timer;
  private boolean counting = false;
  private int countdown;
  private int increment;

  public Clock(float x, float y) {
    super(x, y);
    this.timer = new Timer(1000);
  }

  public void setConfig(int countdown, int increment) {
    this.increment = increment;
    this.countdown = countdown;
  }

  public void start(PApplet app) {
    timer.start(app);
    counting = true;
  }

  /**
   * Stop the clock.
   *
   * @param temporary is the flag indicate whether the clock will be stopped temporarily or
   *        permanently.
   */
  public void stop(boolean temporary) {
    if (temporary) {
      countdown += increment;
    }
    counting = false;
  }

  @Override
  public void tick(PApplet applet) {
    App app = (App) applet;
    if (counting) {
      if (timer.complete(app)) {
        countdown -= 1;
        if (countdown == 0) {
          app.updateGameStatus(4);
        }
        timer.start(app);
      }
    }
  }

  public int getCountDown() {
    return countdown;
  }


  @Override
  public void draw(PApplet app) {
    tick(app);
    int min = countdown / 60;
    int sec = countdown % 60;
    String s = String.format("%02d:%02d", min, sec);
    app.fill(255);
    app.textSize(24);
    app.text(s, cordX, cordY);
  }
}
