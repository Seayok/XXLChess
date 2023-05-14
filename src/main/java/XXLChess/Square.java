package XXLChess;

import processing.core.PApplet;

/**
 * Represents a square on the board.
 */
public class Square extends GameObject {
  public static final int SQUARESIZE = 48;
  public static final int[] LIGHT_BROWN = {240, 217, 181};
  public static final int[] DARK_GREEN = {105, 138, 76};
  public static final int[] LIGHT_GREEN = {171, 162, 59};
  public static final int[] LIGHT_BlUE = {196, 224, 232};
  public static final int[] DARK_RED = {215, 0, 0};
  public static final int[] PURPLE = {159, 43, 104};
  public static final int[] LIGHT_RED = {255, 164, 102};
  public static final int FLASH_DURATION = 30; // in frames

  private boolean selected;
  private int numFlashes;
  private int curCoolDown;
  private Piece curPiece;
  private boolean prevMove;
  private boolean special;
  private boolean onPieceWay;
  private boolean kingChecked;
  private boolean onCaptureway;
  private boolean[] underControl; // 1 white //2 black
  private int[] color;

  /**
   * Creates a new square with coordinates and colors.
   *
   * @param x the x position.
   * @param y the y position.
   * @param app the main application.
   */
  public Square(int x, int y, PApplet app) {
    super(x * SQUARESIZE, y * SQUARESIZE);
    color = new int[3];
    underControl = new boolean[2];
  }

  public void setPiece(Piece piece) {
    this.curPiece = piece;
  }

  public Piece getPiece() {
    return curPiece;
  }

  public boolean isControl(boolean isWhite) {
    return underControl[isWhite ? 1 : 0];
  }

  public void underControl(boolean isWhite) {
    underControl[isWhite ? 1 : 0] = true;
  }

  public void setSpecial(boolean special) {
    this.special = special;
  }

  public void noControl() {
    underControl[0] = false;
    underControl[1] = false;
  }

  /**
   * Change color according the flags of the square.
   */
  public void setColor() {
    if (selected) {
      color = DARK_GREEN;
    } else if (kingChecked) {
      color = DARK_RED;
    } else if (special) {
      color = PURPLE;
    } else if (onPieceWay) {
      color = LIGHT_BlUE;
    } else if (onCaptureway) {
      color = LIGHT_RED;
    } else if (prevMove) {
      color = LIGHT_GREEN;
    } else {
      color = LIGHT_BROWN;
    }
    return;
  }

  public void setKingChecked(boolean check) {
    this.kingChecked = check;
  }

  public boolean isKingChecked() {
    return kingChecked;
  }

  public void setPrevMove(boolean prevMove) {
    this.prevMove = prevMove;
  }

  public boolean isOnPieceWay() {
    return onPieceWay;
  }

  public void setOnPieceWay(boolean onPieceWay) {
    this.onPieceWay = onPieceWay;
  }

  public void deselect() {
    selected = false;
  }

  public void setWarning() {
    this.numFlashes = 7;
    this.curCoolDown = 0;
  }

  public void setOnCapture(boolean onCapture) {
    onCaptureway = onCapture;
  }

  public void unWarning() {
    this.numFlashes = 0;
    this.curCoolDown = 0;
  }

  public void onSelected() {
    this.selected = true;
  }

  public boolean isOnCaptured() {
    return onCaptureway;
  }

  public int[] getColor() {
    return color;
  }

  /**
   * Function to display flashing if the player made a legal move not protect the king while king is
   * in check.
   */
  public void tick() {
    if (numFlashes > 0) {
      if (curCoolDown == 0) {
        if (numFlashes != 1) {
          curCoolDown = FLASH_DURATION;
        }
        numFlashes--;
      } else {
        curCoolDown--;
        this.setKingChecked(numFlashes % 2 == 1);
      }
    }
    setColor();
  }

  @Override
  public void draw(PApplet app) {
    // Draw square
    tick();
    app.noStroke();
    app.fill(color[0], color[1], color[2]);
    app.rect(cordX, cordY, SQUARESIZE, SQUARESIZE);
    if ((cordX + cordY) / 48 % 2 == 1) {
      app.noStroke();
      app.fill(60, 10, 0, 100);
      app.rect(cordX, cordY, SQUARESIZE, SQUARESIZE);
    }
  }
}
