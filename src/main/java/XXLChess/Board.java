package XXLChess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
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
  private HashMap<Character, CreatePiece> createOperations;
  private CopyOnWriteArrayList<Piece> pieceList;
  private Square[][] squareMat;

  /**
   * Creates a new board with coordinates (0, 0).
   */
  public Board(String[][] levelArr, PApplet app) {
    super(0, 0);
    squareMat = new Square[GRIDNUM][GRIDNUM];
    king = new Piece[2];
    pieceList = new CopyOnWriteArrayList<>();
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
          pieceList.add(newPiece);
          squareMat[i][j].setPiece(newPiece);
          if (levelArr[j][i].charAt(1) == 'k') {
            king[levelArr[j][i].charAt(0) == 'w' ? 1 : 0] = newPiece;
          }
        }
      }
    }
    newMoveSet();
  }

  public int startClick(int x, int y) {
    if (squareMat[x / GRIDSIZE][y / GRIDSIZE].getPiece() == null) {
      return 0;
    }
    selSquare = squareMat[x / GRIDSIZE][y / GRIDSIZE];
    selPiece = selSquare.getPiece();
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
      if (target.getPiece() != null) {
        return startClick(x, y);
      }
      return 0;
    } else {
      makeMove(selPiece.getMoveFromSquare(target, true), true, true);
      return 2;
    }
  }

  public void makeMove(Move move, boolean display, boolean switchturn) {
    Piece movePiece = move.getSourcePiece();
    Square target = move.getEndSquare();

    if (move.getFlag() == Move.CASTLE) {
      makeMove(move.getSubMove(), display, false);
    }

    Square curSquare = movePiece.getSquare();
    // if (move.promote()) {
    // movePiece = new Queen((int) movePiece.getX() / GRIDSIZE, (int) movePiece.getY() / GRIDSIZE,
    // Character.toString(movePiece.getCode().charAt(0)) + "q", curSquare);
    // }
    pieceList.remove(target.getPiece());
    target.setPiece(movePiece);
    curSquare.setPiece(null);
    if (display) {
      movePiece.setDestination(target);
    } else {
      movePiece.setCurSquare(curSquare);
    }
    if (switchturn) {
      whiteTurn = !whiteTurn;
      resetSquares(curSquare, movePiece, target);
    }
    movePiece.setMoved(true);
  }

  public List<Move> getAllMoves(boolean isWhite) {
    List<Move> result = new ArrayList<>();
    for (Piece p : pieceList) {
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
    Square kingSquare = kingPiece.getSquare();
    kingSquares.add(new Move(kingPiece.getSquare(), kingSquare, Move.NORMAL, kingPiece, null));
    kingSquare.setPiece(null);
    for (Move m : kingSquares) {
      // s.setOnCapture(true);
      Square s = m.getEndSquare();
      Piece p = squareUnderAttack(whiteTurn, s);
      if (p == null) {
        Piece prevPiece = s.getPiece();
        s.setPiece(null);
        p = squareUnderAttack(whiteTurn, s);
        s.setPiece(prevPiece);
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
    for (Piece p : pieceList) {
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
    for (Piece p : pieceList) {
      res += p.getValue();
    }

    return res;
  }

  public double evaluateMove(Move move, int depth, double alpha, double beta) {
    Piece movePiece = move.getSourcePiece();
    double res = !movePiece.isWhitePiece() ? -Double.MAX_VALUE : Double.MAX_VALUE;
    final boolean prevIsMoved = movePiece.isMoved();
    final Piece destPiece = move.getDestinationPiece();
    int position = pieceList.indexOf(destPiece);

    makeMove(move, false, false);


    if (depth == 0) {
      res = evaluateBoard();
      // System.out.println(movePiece.getCode() + res);
    } else {
      for (Piece p : pieceList) {
        if (p.isWhitePiece() != movePiece.isWhitePiece()) {
          p.updatePreLegalMoves(this);
          if (depth > 1) {
            p.removeIllegalMove(this);
          }
          for (Move nextMove : p.getValidMoves()) {
            double eval = evaluateMove(nextMove, depth - 1, alpha, beta);
            if (movePiece.isWhitePiece()) {
              res = Math.min(res, eval);
              beta = Math.min(beta, eval);
              if (beta <= alpha) {
                break;
              }
            } else {
              res = Math.max(res, eval);
              alpha = Math.max(alpha, eval);
              if (beta <= alpha) {
                break;
              }
            }
          }
        }
      }
    }
    unmove(move, prevIsMoved);

    if (position != -1) {
      pieceList.add(position, destPiece);
    }
    System.out
        .println(movePiece.getCode() + move.getEndSquare().getX() + move.getEndSquare().getY());
    return res;
  }

  public void unmove(Move move, boolean isMoved) {
    Piece movePiece = move.getSourcePiece();
    Square startSquare = move.getStartSquare();
    Square endSquare = move.getEndSquare();
    if (move.getFlag() == Move.CASTLE) {
      unmove(move.getSubMove(), false);
    }
    // if (move.promote()) {
    // movePiece = new Pawn()
    // }
    endSquare.setPiece(move.getDestinationPiece());
    startSquare.setPiece(movePiece);
    movePiece.setCurSquare(startSquare);
    movePiece.setMoved(isMoved);
  }

  public void newMoveSet() {
    for (Piece p : pieceList) {
      if (p.isWhitePiece() == this.isWhiteTurn()) {
        p.updatePreLegalMoves(this);
        p.removeIllegalMove(this);
      }
    }
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
    for (Piece piece : pieceList) {
      piece.draw(app);
    }
  }
}
