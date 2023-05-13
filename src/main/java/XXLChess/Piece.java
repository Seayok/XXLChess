package XXLChess;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import processing.core.PApplet;
import processing.core.PImage;

/**
 * Abtract class representing a piece.
 */
public abstract class Piece extends GameObject {
  public static final int GRIDNUM = Board.GRIDNUM;
  public static final int GRIDSIZE = Board.GRIDSIZE;
  public static final int FPS = 60;
  public static final int DIRECTION_NUMBER = 4;
  public static final int HORSE_RANGE = 2;
  public static final int CAMEL_RANGE = 3;
  public static final int[] Y_STRAIGHT_DIRECTION = {-1, 0, 1, 0};
  public static final int[] X_STRAIGHT_DIRECTION = {0, 1, 0, -1};
  public static final int[] Y_DIAGONAL_DIRECTION = {-1, -1, 1, 1};
  public static final int[] X_DIAGONAL_DIRECTION = {1, -1, -1, 1};

  protected static PImage blackQueenImage;
  protected static PImage whiteQueenImage;
  protected static int pawnDirection;
  protected static int movementTime;

  private static double movementSpeed;

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


  /**
   * Constructs a new piece.
   *
   * @param x the x coordinate of the piece.
   * @param y the y coordinate of the piece.
   * @param code the unique identifier for each piece.
   * @param curSquare the square which the piece belongs to initially.
   */
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

  /**
   * Setup the movement stats.
   *
   * @param movementSpeed the default movement speed.
   * @param movementTime the maximum movement time.
   * @param pawnDirection the direction of pawn for each side.
   */
  public static void setMoveStat(double movementSpeed, int movementTime, int pawnDirection) {
    Piece.pawnDirection = pawnDirection;
    Piece.movementSpeed = movementSpeed;
    Piece.movementTime = movementTime;
  }

  /**
   * Sets the object's sprite.
   *
   * @param app is the main application.
   */
  public void setSprite(PApplet app) {
    String dir = "src/main/resources/XXLChess/" + code + ".png";
    this.sprite = app.loadImage(dir);
    this.sprite.resize(GRIDSIZE, GRIDSIZE);

    if (whiteQueenImage == null) {
      whiteQueenImage = app.loadImage("src/main/resources/XXLChess/wq.png");
      blackQueenImage = app.loadImage("src/main/resources/XXLChess/bq.png");
    }
  }

  /**
   * Set the destination for the piece to move to.
   */
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


  /**
   * When clicking on the piece, the valid moveset will be displayed on the board with suitable
   * color.
   */
  public void displayMoveSet() {
    for (Move m : validMoves) {
      Square s = m.getEndSquare();
      if (m.getFlag() == Move.CASTLE || m.isPromotion()
          || (code.contains("p") && Math.abs(s.getY() - destY) > GRIDSIZE)) {
        s.setSpecial(true);
      }
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
    CopyOnWriteArrayList<Piece> attackers = curBoard.getAttackers(!isWhite);
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
                  attackers.add(this);
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

  protected void setHorseMove(Board curBoard, int range) {
    CopyOnWriteArrayList<Piece> attackers = curBoard.getAttackers(!isWhite);
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
              attackers.add(this);
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

  public abstract void generateMove(Board curBoard);

  /**
   * Get the prelegal move of a piece.
   *
   * @param curBoard the board which all pieces are on.
   */
  public void updatePreLegalMoves(Board curBoard) {
    preLegalMoves.removeAll(preLegalMoves);
    validMoves.removeAll(validMoves);
    generateMove(curBoard);
  }


  /**
   * From the prelegal moves, filter out the illegal moves.
   *
   * @param curBoard the board which all pieces are on.
   */
  public void updateLegalMove(Board curBoard) {
    CopyOnWriteArrayList<Piece> attackers = curBoard.getAttackers(isWhite);
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

  /**
   * Set the destination square of that the piece is going to move to.
   *
   * @param target the destination square.
   */
  public void setDestination(Square target) {
    this.curSquare = target;
    this.destX = target.getX();
    this.destY = target.getY();
  }


  /**
   * Get the move that have the particular destination square.
   *
   * @param s the destination square.
   * @param legal the flag indicating whether the list of move the function taking from is legal or
   *        prelegal.
   * @return the move that have the particular destination square, or null if no such move.
   */
  public Move getMoveFromSquare(Square s, boolean legal) {
    CopyOnWriteArrayList<Move> searchList = legal ? validMoves : preLegalMoves;
    for (Move m : searchList) {
      if (m.getEndSquare() == s) {
        return m;
      }
    }
    return null;
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

  public boolean isWhitePiece() {
    return isWhite;
  }

  public Piece getPinPiece() {
    return pinPiece;
  }

  public float getDesX() {
    return destX;
  }

  public float getDesY() {
    return destY;
  }

  public void setPromotedSprite() {
    this.sprite = isWhite ? whiteQueenImage : blackQueenImage;
    this.sprite.resize(GRIDSIZE, GRIDSIZE);
  }

  public Square getSquare() {
    return curSquare;
  }

  public double getValue() {
    return value;
  }

  /**
   * Draws the object to the screen.
   *
   * @param app The window to draw onto.
   */
  public void draw(PApplet app) {
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
    app.image(this.sprite, cordX, cordY);
  }
}
