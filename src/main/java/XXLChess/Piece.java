package XXLChess;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Arrays;
import java.util.List;
import processing.core.PApplet;
import processing.core.PImage;


public abstract class Piece extends GameObject {
  public static final int GRIDNUM = Board.GRIDNUM;
  public static final int GRIDSIZE = Board.GRIDSIZE;
  public static PImage BLACK_QUEEN_IMAGE;
  public static PImage WHITE_QUEEN_IMAGE;
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
  protected float displayX;
  protected float displayY;
  protected Square curSquare;
  protected boolean isMoved;
  protected double direction;
  protected String code;
  protected Piece pinPiece;
  protected boolean moving;
  protected double value;
  protected PImage sprite;
  protected CopyOnWriteArrayList<Move> validMoves = new CopyOnWriteArrayList<Move>();
  protected CopyOnWriteArrayList<Move> preLegalMoves = new CopyOnWriteArrayList<Move>();
  protected boolean isWhite;

  public Piece(float x, float y, String code, Square curSquare) {
    super(x, y);
    this.curSquare = curSquare;
    destX = x;
    destY = y;
    displayX = x;
    displayY = y;
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

    if (WHITE_QUEEN_IMAGE == null) {
      WHITE_QUEEN_IMAGE = app.loadImage("src/main/resources/XXLChess/wq.png");
      BLACK_QUEEN_IMAGE = app.loadImage("src/main/resources/XXLChess/bq.png");
    }
  }

  public void setPromotedSprite() {
    this.sprite = isWhite ? WHITE_QUEEN_IMAGE : BLACK_QUEEN_IMAGE;
    this.sprite.resize(GRIDSIZE, GRIDSIZE);
  }

  public Square getSquare() {
    return curSquare;
  }

  public void startMoving() {
    displayX = destX;
    displayY = destY;
    this.direction = Math.atan2(destY - cordY, destX - cordX);
    double distance = Math.sqrt(Math.pow(displayX - cordX, 2) + Math.pow(displayY - cordY, 2));
    double time = distance / Piece.movementSpeed;
    if (time >= movementTime * FPS - 1) {
      overrideSpeed = distance / ((movementTime * FPS) - 1);
    }

  }

  public void setPinPiece(Piece pin) {
    pinPiece = pin;
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
    int kingOccur = 0;
    int occur = 0;
    Piece prevPiece = this;
    CopyOnWriteArrayList<Piece>[] attackers = curBoard.getAttackers();
    Square[][] squares = curBoard.getSquareMat();
    for (int i = 1; i <= range; i++) {
      int changeX = i * dirX;
      int changeY = i * dirY;
      int resX = (int) destX / GRIDSIZE + changeX;
      int resY = (int) destY / GRIDSIZE + changeY;
      if (resX >= 0 && resX < GRIDNUM && (changeX != 0 || changeY != 0) && resY >= 0
          && resY < GRIDNUM) {
        Square s = squares[resX][resY];
        Piece destPiece = s.getPiece();
        if (destPiece == null) {
          if (!(this.code.contains("p") && dirX != 0) && occur - kingOccur == 0) {
            preLegalMoves.add(new Move(curSquare, s, Move.NORMAL, this, null));
            s.underControl(isWhite);
          }
        } else {
          if (kingOccur > 0) {
            break;
          }
          if (destPiece.isWhitePiece() != this.isWhite) {
            if (!(this.code.contains("p") && dirX == 0)) {
              if (destPiece.getCode().contains("k")) {
                if (occur == 0) {
                  attackers[this.isWhite ? 0 : 1].add(this);
                } else {
                  prevPiece.setPinPiece(this);
                }
                kingOccur++;
              }
              prevPiece = destPiece;
              occur++;
              if (occur <= 1) {
                preLegalMoves.add(new Move(curSquare, s, Move.CAPTURE, this, destPiece));
                s.underControl(isWhite);
              }
            } else {
              break;
            }
          } else {
            s.underControl(isWhite);
            break;
          }
          if (occur > 1) {
            break;
          }
        }
      }
    }
  }

  public abstract void generateMove(Board curBoard);

  public void updatePreLegalMoves(Board curBoard) {
    preLegalMoves.removeAll(preLegalMoves);
    validMoves.removeAll(validMoves);
    generateMove(curBoard);
  }

  public double getValue() {
    return value;
  }

  public void updateLegalMove(Board curBoard) {
    CopyOnWriteArrayList<Piece> attackers = curBoard.getAttackers()[isWhite ? 1 : 0];
    Square kingSquare = curBoard.getKing(isWhite).getSquare();
    // Move king out of danger
    if (this.code.contains("k")) {
      for (Move move : preLegalMoves) {
        if (!move.getEndSquare().isControl(!isWhite)) {
          validMoves.add(move);
        }
        if (move.getFlag() == Move.CASTLE
            && (move.getSubMove().getSourcePiece().getPinPiece() != null || attackers.size() > 0)) {
          validMoves.remove(move);
        }
      }
      return;
    }
    if (attackers.size() > 1) {
      return;
    } else if (attackers.size() == 1) {
      Piece attacker = attackers.get(0);
      Square attackerSquare = attacker.getSquare();
      // Capture the attacking piece
      if (attackerSquare.isControl(isWhite)) {
        Move move = getMoveFromSquare(attackerSquare, false);
        if (move != null) {
          validMoves.add(move);
        }
      }

      // Block the attack
      if (!attacker.code.contains("p|n|c|g|k")) {
        for (Move move : preLegalMoves) {
          if (Board.checkOnWay(kingSquare, attackerSquare, move.getEndSquare())) {
            validMoves.add(move);
          }
        }
      }
    } else {
      validMoves.addAll(preLegalMoves);
    }

    if (pinPiece != null) {
      validMoves.removeIf(
          move -> !Board.checkOnWay(kingSquare, pinPiece.getSquare(), move.getEndSquare()));
    }

  }

  public Piece getPinPiece() {
    return pinPiece;
  }


  protected void setHorseMove(Board curBoard, int range) {
    CopyOnWriteArrayList<Piece>[] attackers = curBoard.getAttackers();
    Square[][] squares = curBoard.getSquareMat();
    List<Integer> changeX = Arrays.asList(1, range);
    List<Integer> changeY = Arrays.asList(range, 1);
    for (int i = 0; i < DIRECTION_NUMBER; i++) {
      for (int j = 0; j < changeX.size(); j++) {
        int resX = (int) destX / GRIDSIZE + X_DIAGONAL_DIRECTION[i] * changeX.get(j);
        int resY = (int) destY / GRIDSIZE + Y_DIAGONAL_DIRECTION[i] * changeY.get(j);
        if (resX >= 0 && resX < GRIDNUM && resY >= 0 && resY < GRIDNUM) {
          Square s = squares[resX][resY];
          Piece destPiece = s.getPiece();
          if (destPiece == null) {
            preLegalMoves.add(new Move(curSquare, s, Move.NORMAL, this, null));
          } else if (destPiece.isWhitePiece() != this.isWhite) {
            if (destPiece.getCode().contains("k")) {
              attackers[isWhite ? 0 : 1].add(this);
            }
            preLegalMoves.add(new Move(curSquare, s, Move.CAPTURE, this, s.getPiece()));
          }
          s.underControl(isWhite);
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
    float distX = cordX - displayX;
    float distY = cordY - displayY;
    double distance = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
    if (distance > movementSpeed) {
      cordX += Math.cos(direction) * movementSpeed;
      cordY += Math.sin(direction) * movementSpeed;
    } else {
      cordX = this.destX;
      cordY = this.destY;
      overrideSpeed = 0;
      moving = false;
    }
  }

  public boolean isWhitePiece() {
    return isWhite;
  }

  public void setDestination(Square target) {
    this.curSquare = target;
    this.destX = target.getX();
    this.destY = target.getY();
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
    app.image(this.sprite, cordX, cordY);
  }
}
