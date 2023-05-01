package XXLChess;

import processing.core.PApplet;
import processing.data.JSONObject;

public class Player {
  private boolean isWhite;
  private boolean isBot;
  private boolean isLose;
  private Clock clock;

  public void setupSide(boolean isWhite, boolean isBot) {
    this.isWhite = isWhite;
    this.isBot = isBot;
  }

  public void lose() {
    this.isLose = true;
  }

  public boolean isBot() {
    return isBot;
  }

  public boolean isLose() {
    return isLose;
  }

  public boolean isWhite() {
    return isWhite;
  }

  public void setUpClock(JSONObject playertime) {
    float clockY = isBot ? 56 : 634;
    float clockX = 690;
    clock = new Clock(clockX, clockY);
    clock.setConfig(playertime.getInt("seconds"), playertime.getInt("increment"));
  }

  public Clock getClock() {
    return clock;
  }

  public void endTurn() {
    this.clock.stop(true);
  }

  public void startTurn(PApplet app) {
    this.clock.start(app);
  }
}
