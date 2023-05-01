package XXLChess;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import processing.core.PApplet;

/**
 * Represents a board.
 */
public class Board extends GameObject {
  public static final int GRIDSIZE = 48;
  public static final int GRIDNUM = 14;
  public static double MAX_MOVEMENT_TIME;
  private boolean whiteTurn = true;
  private Piece[] king;
  private Piece selPiece;
  private Square selSquare;
  private ConcurrentHashMap<Square, Piece> boardMap;
  private Square[][] squareMat;

  /**
   * Creates a new board with coordinates (0, 0).
   */
  public Board(String[][] levelArr, PApplet app) {
    super(0, 0);
    squareMat = new Square[GRIDNUM][GRIDNUM];
    king = new Piece[2];
    boardMap = new ConcurrentHashMap<>();
    for (int i = 0; i < GRIDNUM; i++) {
      for (int j = 0; j < GRIDNUM; j++) {
        squareMat[i][j] = new Square(i, j, app);
        if (!levelArr[j][i].equals(" ")) {
          Piece newPiece = new Piece(i * GRIDSIZE, j * GRIDSIZE, levelArr[j][i], squareMat[i][j]);
          if (levelArr[j][i].equals("K")) {
            king[0] = newPiece;
          }
          if (levelArr[j][i].equals("wk")) {
            king[1] = newPiece;
          }
          newPiece.setSprite(app);
          boardMap.put(squareMat[i][j], newPiece);
        }
      }
    }
  }

  public int startClick(int x, int y) {
    // overflow check
    if (!boardMap.containsKey(squareMat[x / GRIDSIZE][y / GRIDSIZE])) {
      return 0;
    }
    selSquare = squareMat[x / GRIDSIZE][y / GRIDSIZE];
    selPiece = boardMap.get(selSquare);
    if (selPiece.isWhitePiece() != whiteTurn) {
      selSquare = null;
      selPiece = null;
      return 0;
    }
    // System.out.println(selPiece.getCode());
    selSquare.onSelected();
    selPiece.updateValidMove(this);
    selPiece.removeIllegalMove(this);
    selPiece.displayMoveSet();
    return 1;
  }

  public int selectClick(int x, int y, App app) {
    Square target = squareMat[x / GRIDSIZE][y / GRIDSIZE];
    if (!target.isOnPieceWay() && !target.isOnCaptured()) {
      Square kingSquare = king[whiteTurn ? 1 : 0].getSquare();
      if (kingSquare.isKingChecked() && selPiece.checkPreLegalMove(target)) {
        king[whiteTurn ? 1 : 0].getSquare().setWarning();
        return 3;
      }
      resetSquares(null);
      if (boardMap.containsKey(target)) {
        return startClick(x, y);
      }
      return 0;
    } else {
      // Castle
      float destX = target.getX();
      float destY = target.getY();
      if (selPiece.getCode().toLowerCase().contains("k")
          && Math.abs(destX - selPiece.getDesX()) / 48 == 2) {
        int offset = (int) ((destX - selPiece.getDesX()) / Math.abs(destX - selPiece.getDesX()));
        int rookX = (offset == 1) ? 13 : 0;
        Square rookSquare = squareMat[rookX][(int) destY / 48];
        Piece rook = boardMap.get(rookSquare);
        Square newRookSquare = squareMat[(int) destX / 48 - offset][(int) destY / 48];
        boardMap.remove(rookSquare);
        rook.setDestination(newRookSquare);
        boardMap.compute(newRookSquare, (k, v) -> rook);
        rook.moved();
      }

      boardMap.compute(target, (k, v) -> selPiece);
      boardMap.remove(selSquare);
      selPiece.setDestination(target);
      selPiece.promotion(app);
      selPiece.moved();
      resetSquares(target);
      whiteTurn = !whiteTurn;
      return 2;
    }
  }

  public List<Square> getAllMoves(boolean isWhite) {
    List<Square> result = new ArrayList<>();
    List<Piece> allPieces = new ArrayList<>(boardMap.values());
    for (Piece p : allPieces) {
      if (p.isWhitePiece() == isWhite) {
        p.updateValidMove(this);
        p.removeIllegalMove(this);
        result.addAll(p.getValidMove());
        result.addAll(p.getValidCapture());
      }
    }
    return result;
  }

  public boolean checkCheck() {
    Square kingSquare = king[whiteTurn ? 1 : 0].getSquare();
    boolean checked = squareUnderAttack(whiteTurn, kingSquare, true) == null;
    kingSquare.setKingChecked(!checked);
    if (!checked) {
      return true;
    } else {
      return false;
    }
  }

  public boolean checkCheckMate() {
    return getAllMoves(whiteTurn).size() == 0;
  }

  public void displayCheckMatePiece() {
    List<Square> occupiedSquare = new ArrayList<>();
    Piece kingPiece = king[whiteTurn ? 1 : 0];
    List<Square> kingSquares = new ArrayList<>(kingPiece.getPreLegalMove());
    kingSquares.add(kingPiece.getSquare());
    for (Square s : kingSquares) {
      // s.setOnCapture(true);
      Piece p = squareUnderAttack(whiteTurn, s, false);
      if (p == null) {
        Piece prevPiece = boardMap.get(s);
        boardMap.remove(s);
        p = squareUnderAttack(whiteTurn, s, true);
        boardMap.compute(s, (k, v) -> prevPiece);
      }
      System.out.print(s.getX() / 48 + " " + s.getY() / 48 + " ");
      System.out.println(p.getCode());
      occupiedSquare.add(p.getSquare());
    }
    for (Square s : occupiedSquare) {
      s.setOnCapture(true);
    }
  }

  public void resetSquares(Square target) {
    for (int i = 0; i < GRIDNUM; i++) {
      for (int j = 0; j < GRIDNUM; j++) {
        Square square = squareMat[i][j];
        square.setOnPieceWay(false);
        square.setOnCapture(false);
        if (target != null) {
          square.setPrevMove(false);
          square.unWarning();
          square.setKingChecked(false);
        }
      }
    }
    if (target != null) {
      selSquare.setPrevMove(true);
      target.setPrevMove(true);
    }
    selSquare.deselect();
    selPiece = null;
    selSquare = null;
  }

  public Piece squareUnderAttack(boolean isWhite, Square target, boolean moved) {
    for (Piece p : boardMap.values()) {
      if (p.isWhitePiece() != isWhite) {
        p.updateValidMove(this);
        for (Square s : p.getPreLegalMove()) {
          if (s == target) {
            return p;
          }
        }
      }
    }
    return null;
  }

  public void newMoveSet() {
    for (Piece p : boardMap.values()) {
      p.newMoveSet();
    }
  }

  public ConcurrentHashMap<Square, Piece> getBoardMap() {
    return boardMap;
  }

  public boolean isWhiteTurn() {
    return whiteTurn;
  }

  public Piece getKing() {
    return king[whiteTurn ? 1 : 0];
  }

  public Square[][] getSquareMat() {
    return squareMat;
  }

  public int getSize() {
    return GRIDSIZE * GRIDNUM;
  }

  public void draw(PApplet app) {
    for (int i = 0; i < GRIDNUM; i++) {
      for (int j = 0; j < GRIDNUM; j++) {
        squareMat[i][j].draw(app);
      }
    }
    for (Piece piece : boardMap.values()) {
      piece.draw(app);
    }
  }
}
