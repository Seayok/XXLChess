package XXLChess;

import processing.core.PApplet;

/**
 * Represents a square on the board.
 */
public class Square extends GameObject {
  public static final int SQUARESIZE = 48;
  public static final int[] LIGHT_BROWN = {240, 217, 181};
  public static final int[] LIGHT_GREEN = {105, 138, 76};
  public static final int[] LIGHT_BlUE = {196, 224, 232};
  public static final int[] LIGHT_RED = {255, 164, 102};

  private boolean selected;
  private boolean onPieceWay;
  private boolean onCaptureway;
  private Pieces piece;
  private int[] color;
  /**
   * Creates a new square with coordinates and colors
   */
  public Square(int x, int y, String piece, PApplet app) {
    super(x * SQUARESIZE, y * SQUARESIZE);
    selected = false;
    color = new int[3];
    setColor();
    setPiece(piece, app);
  }

  public void setColor() {
    if (selected){
      color = LIGHT_GREEN;
    } else if(onPieceWay) {
      color = LIGHT_BlUE;
    } else if(onCaptureway) {
      color = LIGHT_RED;
    } else {
      color = LIGHT_BROWN;
    }
    return;
  }

  
  public boolean isOnPieceWay() {
    return onPieceWay;
  }

  public void setPiece(Pieces piece) {
    this.piece = piece;
    this.piece.setDestination(x, y);
  }

  public void movePiece(Square target){
    target.setPiece(piece);
    this.piece = null;
  }

  public void setOnPieceWay(boolean onPieceWay) {
    this.onPieceWay = onPieceWay;
    setColor();
  }

  public void deselect() {
    selected = false;
    setColor();
  }

  public void setOnCapture(boolean onCapture) {
    onCaptureway = onCapture;
    setColor();
  }

  public void setPiece(String piece, PApplet app) {
    if (!piece.equals("")) {
      this.piece = new Pieces(x, y, piece);
      this.piece.setSprite(app);
    } else {
      this.piece = null;
    }
  }

  public Pieces getPiece(){
    return this.piece;
  }

  public void onSelected() {
    this.selected = true;
    setColor();
    if(piece != null){
      piece.onSelected();
    }
  } 

  public boolean isOnCaptured() {
    return onCaptureway;
  }
  
  public void unsetSelected() {
    this.selected = false;
  }

  public void draw(PApplet app) {
    // Draw square
    app.stroke(color[0], color[1], color[2]);
    app.fill(color[0], color[1], color[2]);
    app.rect(this.x, this.y, SQUARESIZE, SQUARESIZE);
    if((x + y)/48 % 2 == 1){
      app.stroke(60, 10, 10, 100);
      app.fill(60, 10, 0, 100);
      app.rect(this.x, this.y, SQUARESIZE, SQUARESIZE);
    }
  }
}
