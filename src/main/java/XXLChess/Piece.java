package XXLChess;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Arrays;
import java.util.List;
import processing.core.PApplet;
import processing.core.PImage;


public abstract class Piece extends GameObject {
  public static final int GRIDNUM = Board.GRIDNUM;
  public static final int GRIDSIZE = Board.GRIDSIZE;
  public static final int FPS = 60;
  public static final int DIRECTION_NUMBER = 4;
  protected static final int HORSE_RANGE = 2;
  protected static final int CAMEL_RANGE = 3;
  public static final int[] Y_STRAIGHT_DIRECTION = {-1, 0, 1, 0};
  public static final int[] X_STRAIGHT_DIRECTION = {0, 1, 0, -1};
  public static final int[] Y_DIAGONAL_DIRECTION = {-1, -1, 1, 1};
  public static final int[] X_DIAGONAL_DIRECTION = {1, -1, -1, 1};
  private static double movementSpeed;
  protected static int pawnDirection;
  protected static int movementTime;

  private double overrideSpeed;
  protected float destX;
  protected float destY;
  protected Square curSquare;
  protected boolean isMoved;
  protected double direction;
  protected String code;
  protected double value;
  protected PImage sprite;
  protected CopyOnWriteArrayList<Move> validMoves = new CopyOnWriteArrayList<Move>();
  protected CopyOnWriteArrayList<Move> preLegalMoves = new CopyOnWriteArrayList<Move>();
  protected boolean isWhite;

  public Piece(int x, int y, String code, Square curSquare) {
    super(x, y);
    this.curSquare = curSquare;
    destX = x;
    destY = y;
    direction = 0;
    this.code = code;
    if (code.charAt(0) == 'w') {
      this.isWhite = true;
    } else {
      this.isWhite = false;
    }
    this.overrideSpeed = 0;
    isMoved = false;
  }

  public static void setMoveStat(double movementSpeed, int movementTime, int pawnDirection) {
    Piece.pawnDirection = pawnDirection;
    Piece.movementSpeed = movementSpeed;
    Piece.movementTime = movementTime;
  }

  /**
   * Sets the object's sprite.
   *
   * @param sprite The new sprite to use.
   */
  public void setSprite(PApplet app) {
    String dir = "src/main/resources/XXLChess/" + code + ".png";
    this.sprite = app.loadImage(dir);
    this.sprite.resize(GRIDSIZE, GRIDSIZE);
  }

  public Square getSquare() {
    return curSquare;
  }

  public void setCurSquare(Square curSquare) {
    this.curSquare = curSquare;
  }

  public List<Move> getValidMoves() {
    return validMoves;
  }

  public List<Move> getPreLegalMoves() {
    return preLegalMoves;
  }

  public String getCode() {
    return code;
  }

  public void setMoved(boolean moved) {
    this.isMoved = moved;
  }

  public boolean isMoved() {
    return isMoved;
  }

  public void displayMoveSet() {
    for (Move m : validMoves) {
      Square s = m.getEndSquare();
      if (m.getFlag() == Move.CAPTURE) {
        s.setOnCapture(true);
      } else {
        s.setOnPieceWay(true);
      }
    }
  }


  protected void straightMove(Board curBoard, int dirX, int dirY, int range) {
    Square[][] squares = curBoard.getSquareMat();
    for (int i = 1; i <= range; i++) {
      int changeX = i * dirX;
      int changeY = i * dirY;
      int resX = (int) destX / GRIDSIZE + changeX;
      int resY = (int) destY / GRIDSIZE + changeY;
      if (resX >= 0 && resX < GRIDNUM && (changeX != 0 || changeY != 0) && resY >= 0
          && resY < GRIDNUM) {
        Square s = squares[resX][resY];
        if (s.getPiece() == null) {
          if (!(this.code.toLowerCase().contains("p") && dirX != 0)) {
            preLegalMoves.add(new Move(curSquare, s, Move.NORMAL, this, null));
          }
        } else {
          Piece destPiece = s.getPiece();
          if (destPiece.isWhitePiece() != this.isWhite) {
            if (!(this.code.toLowerCase().contains("p") && dirX == 0)) {
              preLegalMoves.add(new Move(curSquare, s, Move.CAPTURE, this, destPiece));
            }
          }
          break;
        }
      }
    }
  }

  public abstract void generateMove(Board curBoard);

  public void updatePreLegalMoves(Board curBoard) {
    preLegalMoves.removeAll(preLegalMoves);
    validMoves.removeAll(validMoves);
    generateMove(curBoard);
    validMoves.addAll(preLegalMoves);
  }

  public double getValue() {
    return value;
  }


  public void removeIllegalMove(Board curBoard) {
    for (Move m : preLegalMoves) {
      double moveScore =
          curBoard.evaluateMove(m, 1, -Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
      // Magic numbers
      if (moveScore * (this.isWhite ? -1 : 1) > 900) {
        validMoves.remove(m);
      }
    }
  }

  protected void setHorseMove(Board curBoard, int range) {
    Square[][] squares = curBoard.getSquareMat();
    List<Integer> changeX = Arrays.asList(1, range);
    List<Integer> changeY = Arrays.asList(range, 1);
    for (int i = 0; i < DIRECTION_NUMBER; i++) {
      for (int j = 0; j < changeX.size(); j++) {
        int resX = (int) destX / GRIDSIZE + X_DIAGONAL_DIRECTION[i] * changeX.get(j);
        int resY = (int) destY / GRIDSIZE + Y_DIAGONAL_DIRECTION[i] * changeY.get(j);
        if (resX >= 0 && resX < GRIDNUM && resY >= 0 && resY < GRIDNUM) {
          Square s = squares[resX][resY];
          if (s.getPiece() == null) {
            preLegalMoves.add(new Move(curSquare, s, Move.NORMAL, this, null));
          } else if (s.getPiece().isWhitePiece() != this.isWhite) {
            preLegalMoves.add(new Move(curSquare, s, Move.CAPTURE, this, s.getPiece()));
          }
        }
      }
    }
  }

  protected void setRookMove(Board curBoard) {
    for (int i = 0; i < DIRECTION_NUMBER; i++) {
      int dirX = X_STRAIGHT_DIRECTION[i];
      int dirY = Y_STRAIGHT_DIRECTION[i];
      straightMove(curBoard, dirX, dirY, GRIDNUM);
    }
  }

  protected void setBishopMove(Board curBoard) {
    for (int i = 0; i < DIRECTION_NUMBER; i++) {
      int dirX = X_DIAGONAL_DIRECTION[i];
      int dirY = Y_DIAGONAL_DIRECTION[i];
      straightMove(curBoard, dirX, dirY, GRIDNUM);
    }
  }

  protected void setKingMove(Board curBoard) {
    for (int i = 0; i < DIRECTION_NUMBER; i++) {
      int dirX = X_DIAGONAL_DIRECTION[i];
      int dirY = Y_DIAGONAL_DIRECTION[i];
      straightMove(curBoard, dirX, dirY, 1);
      dirX = X_STRAIGHT_DIRECTION[i];
      dirY = Y_STRAIGHT_DIRECTION[i];
      straightMove(curBoard, dirX, dirY, 1);
    }
  }

  public void tick() {
    double movementSpeed = Piece.movementSpeed;
    if (overrideSpeed > 0) {
      movementSpeed = overrideSpeed;
    }
    float distX = this.x - destX;
    float distY = this.y - destY;
    double distance = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
    if (distance > movementSpeed) {
      this.x += Math.cos(direction) * movementSpeed;
      this.y += Math.sin(direction) * movementSpeed;
    } else {
      this.x = this.destX;
      this.y = this.destY;
      overrideSpeed = 0;
    }
  }

  public boolean isWhitePiece() {
    return isWhite;
  }

  public void setDestination(Square target) {
    this.curSquare = target;
    this.destX = target.getX();
    this.destY = target.getY();
    this.direction = Math.atan2(destY - this.y, destX - this.x);
    double distance = Math.sqrt(Math.pow(destX - this.x, 2) + Math.pow(destY - this.y, 2));
    double time = distance / movementSpeed;
    if (time >= movementTime * FPS) {
      overrideSpeed = distance / ((movementTime * FPS) - 1);
    }
  }

  public float getDesX() {
    return destX;
  }

  public float getDesY() {
    return destY;
  }

  public Move getMoveFromSquare(Square s, boolean legal) {
    CopyOnWriteArrayList<Move> searchList = legal ? validMoves : preLegalMoves;
    for (Move m : searchList) {
      if (m.getEndSquare() == s) {
        return m;
      }
    }
    return null;
  }


  /**
   * Draws the object to the screen.
   *
   * @param app The window to draw onto.
   */
  public void draw(PApplet app) {
    tick();
    app.image(this.sprite, this.x, this.y);
  }
}
