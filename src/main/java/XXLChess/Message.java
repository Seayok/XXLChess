package XXLChess;

import processing.core.PApplet;

/**
 * Class representing a message box.
 */
public class Message extends GameObject {
  private float size;
  private String text;

  /**
   * Constructs a new message box.
   *
   * @param x is the x position of the message.
   * @param y is the y position of the message.
   * @param size is the size of the message.
   */
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

  /**
   * Display timeover message for a certain player.
   *
   * @param curPlayer is the player whose clock is over.
   */
  public void timeOver(Player curPlayer) {
    String status = curPlayer.isBot() ? "won" : "lost";
    this.text = "You " + status + "\non time!\n\n\nPress 'r' to restart\nthe game";
    this.size = 13;
  }

  public void checkMessage() {
    this.text = "Check!";
    this.size = 24;
  }

  /**
   * Display message after being checkmate.
   *
   * @param curPlayer is the player being checkmate.
   */
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
