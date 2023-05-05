package XXLChess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.HashMap;
import java.util.List;
import processing.core.PApplet;

interface CreatePiece {
  Piece makeNewPiece(int x, int y, String code, Square square);
}


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
  private HashMap<Square, Piece> boardMap;
  private HashMap<Character, CreatePiece> createOperations;
  private Square[][] squareMat;

  /**
   * Creates a new board with coordinates (0, 0).
   */
  public Board(String[][] levelArr, PApplet app) {
    super(0, 0);
    squareMat = new Square[GRIDNUM][GRIDNUM];
    king = new Piece[2];
    boardMap = new HashMap<>();
    createOperations = new HashMap<>();
    createOperations.put('p', (x, y, code, square) -> new Pawn(x, y, code, square));
    createOperations.put('n', (x, y, code, square) -> new Knight(x, y, code, square));
    createOperations.put('g', (x, y, code, square) -> new Guard(x, y, code, square));
    createOperations.put('e', (x, y, code, square) -> new Chancellor(x, y, code, square));
    createOperations.put('k', (x, y, code, square) -> new King(x, y, code, square));
    createOperations.put('q', (x, y, code, square) -> new Queen(x, y, code, square));
    createOperations.put('c', (x, y, code, square) -> new Camel(x, y, code, square));
    createOperations.put('a', (x, y, code, square) -> new Amazon(x, y, code, square));
    createOperations.put('h', (x, y, code, square) -> new Archbishop(x, y, code, square));
    createOperations.put('r', (x, y, code, square) -> new Rook(x, y, code, square));
    createOperations.put('b', (x, y, code, square) -> new Bishop(x, y, code, square));


    for (int i = 0; i < GRIDNUM; i++) {
      for (int j = 0; j < GRIDNUM; j++) {
        squareMat[i][j] = new Square(i, j, app);
        if (!levelArr[j][i].equals(" ")) {
          Piece newPiece = createOperations.get(levelArr[j][i].charAt(1)).makeNewPiece(i * GRIDSIZE,
              j * GRIDSIZE, levelArr[j][i], squareMat[i][j]);
          newPiece.setSprite(app);
          boardMap.put(squareMat[i][j], newPiece);
          if (levelArr[j][i].charAt(1) == 'k') {
            king[levelArr[j][i].charAt(0) == 'w' ? 1 : 0] = newPiece;
          }
        }
      }
    }
    newMoveSet();
  }

  public int startClick(int x, int y) {
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
    selSquare.onSelected();
    selPiece.displayMoveSet();
    return 1;
  }

  public int selectClick(int x, int y) {
    Square target = squareMat[x / GRIDSIZE][y / GRIDSIZE];
    if (!target.isOnPieceWay() && !target.isOnCaptured()) {
      Square kingSquare = king[whiteTurn ? 1 : 0].getSquare();
      if (kingSquare.isKingChecked() && selPiece.getMoveFromSquare(target, false) != null) {
        king[whiteTurn ? 1 : 0].getSquare().setWarning();
        return 3;
      }
      resetSquares(selSquare, null, null);
      if (boardMap.containsKey(target)) {
        return startClick(x, y);
      }
      return 0;
    } else {
      makeMove(selPiece, target);
      return 2;
    }
  }

  public void makeMove(Piece movePiece, Square target) {
    float destX = target.getX();
    float destY = target.getY();

    if (movePiece.getCode().toLowerCase().contains("k")
        && Math.abs(destX - movePiece.getDesX()) / 48 == 2) {
      int offset = (int) ((destX - movePiece.getDesX()) / Math.abs(destX - movePiece.getDesX()));
      int rookX = (offset == 1) ? 13 : 0;
      Square rookSquare = squareMat[rookX][(int) destY / 48];
      Piece rook = boardMap.get(rookSquare);
      Square newRookSquare = squareMat[(int) destX / 48 - offset][(int) destY / 48];
      boardMap.remove(rookSquare);
      rook.setDestination(newRookSquare);
      boardMap.compute(newRookSquare, (k, v) -> rook);
      rook.setMoved(true);
    }
    Square curSquare = movePiece.getSquare();
    boardMap.compute(target, (k, v) -> movePiece);
    boardMap.remove(movePiece.getSquare());
    movePiece.setDestination(target);
    movePiece.promotion();
    movePiece.setMoved(true);
    resetSquares(curSquare, movePiece, target);
    whiteTurn = !whiteTurn;
  }

  public List<Move> getAllMoves(boolean isWhite) {
    List<Move> result = new ArrayList<>();
    for (Piece p : boardMap.values()) {
      if (p.isWhitePiece() == isWhite) {
        result.addAll(p.getValidMoves());
      }
    }
    return result;
  }

  public boolean checkCheck() {
    Square kingSquare = king[whiteTurn ? 1 : 0].getSquare();
    boolean checked = squareUnderAttack(whiteTurn, kingSquare) == null;
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
    List<Move> kingSquares = new ArrayList<>(kingPiece.getPreLegalMoves());
    kingSquares
        .add(new Move(kingPiece.getSquare(), kingPiece.getSquare(), Move.NORMAL, kingPiece, null));
    for (Move m : kingSquares) {
      // s.setOnCapture(true);
      Square s = m.getEndSquare();
      Piece p = squareUnderAttack(whiteTurn, s);
      if (p == null) {
        Piece prevPiece = boardMap.get(s);
        boardMap.remove(s);
        p = squareUnderAttack(whiteTurn, s);
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

  public void resetSquares(Square curSquare, Piece piece, Square target) {
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
      curSquare.setPrevMove(true);
      target.setPrevMove(true);
    }
    curSquare.deselect();
    selPiece = null;
    selSquare = null;
  }

  public Piece squareUnderAttack(boolean isWhite, Square target) {
    for (Piece p : boardMap.values()) {
      if (p.isWhitePiece() != isWhite) {
        p.updatePreLegalMoves(this);
        if (p.getMoveFromSquare(target, false) != null) {
          return p;
        }
      }
    }
    return null;
  }

  public double evaluateBoard() {
    double res = 0;
    for (Piece p : boardMap.values()) {
      res += p.getValue();
    }
    return res;
  }

  public double evaluateMove(Move move, int depth) {
    Piece movePiece = move.getSourcePiece();
    double res = !movePiece.isWhitePiece() ? -Double.MAX_VALUE : Double.MAX_VALUE;
    final boolean prevIsMoved = movePiece.isMoved();
    final Square endSquare = move.getEndSquare();
    final Square startSquare = move.getStartSquare();

    movePiece.setMoved(true);
    boardMap.compute(endSquare, (k, v) -> movePiece);
    movePiece.setCurSquare(endSquare);
    boardMap.remove(startSquare);
    Collection<Piece> pieceList = new ArrayList<>(boardMap.values());


    if (depth == 0) {
      res = evaluateBoard();
    } else {
      for (Piece p : pieceList) {
        if (p.isWhitePiece() != movePiece.isWhitePiece()) {
          p.updatePreLegalMoves(this);
          if (depth > 1) {
            p.removeIllegalMove(this);
          }
          for (Move nextMove : p.getValidMoves()) {
            res = movePiece.isWhitePiece() ? Math.min(res, evaluateMove(nextMove, depth - 1))
                : Math.max(res, evaluateMove(nextMove, depth - 1));
          }
        }
      }
    }

    boardMap.put(startSquare, movePiece);
    movePiece.setCurSquare(startSquare);
    movePiece.setMoved(prevIsMoved);
    boardMap.compute(endSquare, (k, v) -> move.getDestinationPiece());

    return res;

  }

  public void newMoveSet() {
    Collection<Piece> pieceList = new ArrayList<>(boardMap.values());
    for (Piece p : pieceList) {
      if (p.isWhitePiece() == this.isWhiteTurn()) {
        p.updatePreLegalMoves(this);
        p.removeIllegalMove(this);
      }
    }
  }

  public HashMap<Square, Piece> getBoardMap() {
    return boardMap;
  }

  public boolean isWhiteTurn() {
    return whiteTurn;
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
