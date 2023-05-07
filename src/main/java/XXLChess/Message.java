package XXLChess;

import processing.core.PApplet;

public class Message extends GameObject {
  private float size;
  private String text;

  public Message(float x, float y, float size) {
    super(x, y);
    text = "";
    this.size = size;
  }

  public float getSize() {
    return size;
  }

  public String getText() {
    return text;
  }

  public void checkWarning() {
    this.text = "Your king is\nin check!\n\n\nYou must defend\nyour king!";
    this.size = 13;
  }

  public void timeOver(Player curPlayer) {
    String status = curPlayer.isBot() ? "won" : "lost";
    this.text = "You " + status + "\non time!\n\n\nPress 'r' to restart\nthe game";
    this.size = 13;
  }

  public void checkMessage() {
    this.text = "Check!";
    this.size = 24;
  }

  public void checkMate(Player curPlayer) {
    String status = curPlayer.isBot() ? "won" : "lost";
    this.text = "You " + status + " by\ncheckmate!\n\n\nPress 'r' to restart\nthe game";
    this.size = 13;
  }

  public void resign() {
    this.text = "You resign!\n\n\nPress 'r' to restart\nthe game";
    this.size = 13;
  }

  public void hide() {
    this.text = "";
  }

  public void drawMessage() {
    this.text = "Stalemate - draw";
    this.size = 13;
  }

  @Override
  public void draw(PApplet app) {
    app.fill(255);
    app.textSize(size);
    app.text(this.text, cordX, cordY);
  }
}
