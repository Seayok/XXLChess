package XXLChess;

import java.util.ArrayList;
import java.util.Collections;
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
  public static final int[] pieceSquareTables = {-20, -15, -10, -10, -10, -5, -5, -5, -5, -10, -10,
      -10, -15, -20, -10, -5, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, -5, -10, -10, 0, 0, 0, 5, 5, 5, 5, 5, 5,
      0, 0, 0, -10, -5, 0, 0, 0, 5, 5, 5, 5, 5, 5, 0, 0, 0, -5, 0, 0, 0, 5, 10, 10, 10, 10, 10, 10,
      5, 0, 0, 0, 0, 0, 5, 10, 10, 15, 15, 15, 15, 10, 10, 5, 0, 0, 0, 5, 10, 10, 15, 15, 20, 20,
      15, 15, 10, 10, 5, 0, 0, 5, 10, 10, 15, 15, 20, 20, 15, 15, 10, 10, 5, 0, 0, 0, 5, 10, 10, 15,
      15, 15, 15, 10, 10, 5, 0, 0, 0, 0, 0, 5, 10, 10, 10, 10, 10, 10, 5, 0, 0, 0, -5, 0, 0, 0, 5,
      5, 5, 5, 5, 5, 0, 0, 0, -5, -10, 0, 0, 0, 5, 5, 5, 5, 5, 5, 0, 0, 0, -10, -10, -5, 0, 0, 0, 0,
      0, 0, 0, 0, 0, 0, -5, -10, -20, -15, -10, -10, -10, -5, -5, -5, -5, -10, -10, -10, -15, -20};
  public static final int GRIDSIZE = 48;
  public static final int GRIDNUM = 14;
  public static double MAX_MOVEMENT_TIME;
  private boolean whiteTurn = true;
  private Piece[] king;
  private CopyOnWriteArrayList<Piece>[] attackers;
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
    attackers = new CopyOnWriteArrayList[2];
    attackers[0] = new CopyOnWriteArrayList<>();
    attackers[1] = new CopyOnWriteArrayList<>();
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

  public void setSpriteAndDisplay(PApplet app) {
    for (Piece piece : pieceList) {
      piece.setSprite(app);
    }
    draw(app);
  }

  public List<Piece> getPieceList() {
    return pieceList;
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
      makeMove(selPiece.getMoveFromSquare(target, true), true, false);
      return 2;
    }
  }

  public void makeMove(Move move, boolean display, boolean submove) {

    Piece movePiece = move.getSourcePiece();
    final Square target = move.getEndSquare();
    final Square start = move.getStartSquare();

    if (move.getFlag() == Move.CASTLE) {
      makeMove(move.getSubMove(), display, true);
    }

    if (move.isPromotion()) {
      pieceList.remove(movePiece);
      movePiece = new Queen(movePiece.getDesX(), movePiece.getDesY(),
          Character.toString(movePiece.getCode().charAt(0)) + "q", start);
      move.setPromotedPiece(movePiece);
      pieceList.add(movePiece);
    }
    if (move.getFlag() == Move.CAPTURE) {
      pieceList.remove(move.getDestinationPiece());
    }
    target.setPiece(movePiece);
    start.setPiece(null);
    movePiece.setDestination(target);
    if (display) {
      movePiece.startMoving();
      if (move.isPromotion()) {
        movePiece.setPromotedSprite();
      }
      resetSquares(start, movePiece, target);
    }
    movePiece.setMoved(true);
    if (!submove) {
      whiteTurn = !whiteTurn;
      newMoveSet();
    }
  }

  public List<Move> getAllMoves(boolean isWhite, boolean order) {
    List<Move> result = new ArrayList<>();
    for (Piece p : pieceList) {
      if (p.isWhitePiece() == isWhite) {
        result.addAll(p.getValidMoves());
      }
    }
    if (order) {
      for (Move move : result) {
        Piece sourcePiece = move.getSourcePiece();
        double moveScore = 0;
        if (move.getFlag() == Move.CAPTURE) {
          moveScore = 8 * Math.abs(move.getDestinationPiece().getValue())
              - Math.abs(sourcePiece.getValue());
        }
        if (move.isPromotion()) {
          moveScore += 9;
        }
        if (move.getEndSquare().isControl(!whiteTurn) && move.getFlag() != Move.CAPTURE) {
          moveScore -= Math.abs(sourcePiece.getValue());
        }
        move.setScore(moveScore);
      }
      Collections.sort(result,
          (a, b) -> (int) new Double(b.getScore()).compareTo(new Double(a.getScore())));
    }
    return result;
  }

  public boolean checkCheck() {
    boolean check = attackers[whiteTurn ? 1 : 0].size() > 0;
    king[whiteTurn ? 1 : 0].getSquare().setKingChecked(check);
    return check;
  }

  public boolean checkCheckMate() {
    return getAllMoves(whiteTurn, false).size() == 0;
  }

  public static boolean checkOnWay(Square start, Square end, Square check) {
    double firstDistance = distanceBetweenTwoSquares(start, end);
    double secondDistance = distanceBetweenTwoSquares(start, check);
    double thirdDistance = distanceBetweenTwoSquares(end, check);
    return firstDistance == secondDistance + thirdDistance;
  }

  public void displayCheckMatePiece() {
    List<Square> occupiedSquare = new ArrayList<>();
    Piece kingPiece = king[whiteTurn ? 1 : 0];
    List<Move> kingMoves = new ArrayList<>(kingPiece.getPreLegalMoves());
    Square kingSquare = kingPiece.getSquare();
    kingMoves.add(new Move(kingPiece.getSquare(), kingSquare, Move.NORMAL, kingPiece, null));
    kingSquare.setPiece(null);
    for (Move m : kingMoves) {
      Square s = m.getEndSquare();
      Piece p = squareUnderAttack(whiteTurn, s);
      if (p == null) {
        Piece prevPiece = s.getPiece();
        s.setPiece(null);
        newMoveSet();
        p = squareUnderAttack(whiteTurn, s);
        s.setPiece(prevPiece);
        newMoveSet();
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

  public CopyOnWriteArrayList<Piece>[] getAttackers() {
    return attackers;
  }

  public Piece getKing(boolean isWhite) {
    return king[isWhite ? 1 : 0];
  }

  public static double distanceBetweenTwoSquares(Square square1, Square square2) {
    return Math.sqrt(Math.pow(square1.getX() - square2.getX(), 2)
        + Math.pow(square1.getY() - square2.getY(), 2));
  }

  public Piece squareUnderAttack(boolean isWhite, Square target) {
    for (Piece p : pieceList) {
      if (p.isWhitePiece() != isWhite) {
        if (p.getMoveFromSquare(target, false) != null) {
          return p;
        }
      }
    }
    return null;
  }

  public double forceKingToEdge() {
    double eval = 0;
    Piece theirKing = king[whiteTurn ? 1 : 0];
    Piece ourKing = king[whiteTurn ? 0 : 1];
    double dstxToCenter =
        Math.max((theirKing.getDesX() / GRIDSIZE) - 6, 7 - (theirKing.getDesX() / GRIDSIZE));
    double dstyToCenter =
        Math.max((theirKing.getDesY() / GRIDSIZE) - 6, 7 - (theirKing.getDesX() / GRIDSIZE));
    double dstxToOpponent = Math.abs(ourKing.getDesX() - theirKing.getDesX()) / GRIDSIZE;
    double dstyToOpponent = Math.abs(ourKing.getDesY() - theirKing.getDesY()) / GRIDSIZE;
    eval += dstxToCenter + dstyToCenter;
    eval += (26 - dstxToOpponent - dstyToOpponent) / 260;
    return eval * (56 - pieceList.size()) / 600;
  }

  public double evaluateBoard() {
    double res = 0;
    for (Piece p : pieceList) {
      res += p.getValue();
      if (pieceList.size() > 10) {
        if (!p.getCode().contains("k")) {
          double pawnWeight = (p.getCode().contains("p") ? 3 : 1);
          res +=
              (pieceSquareTables[(int) p.getDesX() / GRIDSIZE + 14 * (int) (p.getDesY() / GRIDSIZE)]
                  * (p.isWhitePiece() ? 1 : -1) * pieceList.size() * pawnWeight) / 600;
        } else {
          if (p.isMoved()) {
            res += 0.5 * (p.isWhitePiece() ? 1 : -1);
          }
        }
      }
    }
    if (checkCheckMate()) {
      if (!checkCheck()) {
        return 0;
      } else {
        return Double.MAX_VALUE * (whiteTurn ? -1 : 1);
      }
    }
    res += forceKingToEdge() * (whiteTurn ? -1 : 1);
    return res;
  }

  public double evaluateCapture(Move move, double alpha, double beta, boolean maximize) {
    Piece movePiece = move.getSourcePiece();
    double res = maximize ? -Double.POSITIVE_INFINITY : Double.POSITIVE_INFINITY;
    final boolean prevIsMoved = movePiece.isMoved();
    final Piece destPiece = move.getDestinationPiece();
    int startPosition = pieceList.indexOf(movePiece);
    final int position = pieceList.indexOf(destPiece);

    makeMove(move, false, false);
    double evalCur = evaluateBoard();
    if (maximize) {
      res = Math.max(res, evalCur);
      alpha = Math.max(alpha, evalCur);
    } else {
      res = Math.min(res, evalCur);
      beta = Math.min(beta, evalCur);
    }


    if (checkCheckMate()) {
      if (!checkCheck()) {
        res = 0;
      } else {
        res = whiteTurn ? -Double.POSITIVE_INFINITY : Double.POSITIVE_INFINITY;
      }
    } else if (beta > alpha) {
      List<Move> moveList = getAllMoves(maximize, true);
      for (Move m : moveList) {
        if (m.getFlag() != Move.CAPTURE) {
          continue;
        }
        double eval = evaluateCapture(m, alpha, beta, !maximize);
        if (maximize) {
          res = Math.max(res, eval);
          alpha = Math.max(alpha, eval);
        } else {
          res = Math.min(res, eval);
          beta = Math.min(beta, eval);
        }
        if (beta <= alpha) {
          break;
        }
      }
    }

    unmove(move, prevIsMoved, false);
    if (move.isPromotion()) {
      pieceList.remove(move.getPromotedPiece());
      int offset = (move.getFlag() == Move.CAPTURE && position < startPosition) ? 1 : 0;
      pieceList.add(startPosition - offset, movePiece);
    }

    pieceList.add(position, destPiece);
    newMoveSet();
    return res;
  }

  public double evaluateMove(Move move, int depth, double alpha, double beta, boolean maximize) {
    Piece movePiece = move.getSourcePiece();
    double res = maximize ? -Double.POSITIVE_INFINITY : Double.POSITIVE_INFINITY;
    final boolean prevIsMoved = movePiece.isMoved();
    final Piece destPiece = move.getDestinationPiece();
    int startPosition = pieceList.indexOf(movePiece);
    int position = pieceList.indexOf(destPiece);

    makeMove(move, false, false);

    if (checkCheckMate()) {
      if (!checkCheck()) {
        res = 0;
      } else {
        res = whiteTurn ? -Double.POSITIVE_INFINITY : Double.POSITIVE_INFINITY;
      }
    } else if (depth == 0) {
      res = evaluateBoard();
      List<Move> moveList = getAllMoves(maximize, true);
      for (Move m : moveList) {
        if (m.getFlag() == Move.CAPTURE) {
          double eval = evaluateCapture(m, alpha, beta, !maximize);
          if (maximize) {
            res = Math.max(res, eval);
            alpha = Math.max(alpha, eval);
          } else {
            res = Math.min(res, eval);
            beta = Math.min(beta, eval);
          }
          if (beta <= alpha) {
            break;
          }
        }
      }
    } else {
      List<Move> moveList = getAllMoves(maximize, true);
      for (Move m : moveList) {
        double eval = evaluateMove(m, depth - 1, alpha, beta, !maximize);
        if (maximize) {
          res = Math.max(res, eval);
          alpha = Math.max(alpha, eval);
        } else {
          res = Math.min(res, eval);
          beta = Math.min(beta, eval);
        }
        if (beta <= alpha) {
          break;
        }
      }
    }

    unmove(move, prevIsMoved, false);

    if (move.isPromotion()) {
      pieceList.remove(move.getPromotedPiece());
      int offset = (move.getFlag() == Move.CAPTURE && position < startPosition) ? 1 : 0;
      pieceList.add(startPosition - offset, movePiece);
    }

    if (move.getFlag() == Move.CAPTURE) {
      pieceList.add(position, destPiece);
    }
    newMoveSet();
    return res;
  }

  public void unmove(Move move, boolean isMoved, boolean submove) {
    final Piece movePiece = move.getSourcePiece();
    final Square startSquare = move.getStartSquare();
    final Square endSquare = move.getEndSquare();
    if (move.getFlag() == Move.CASTLE) {
      unmove(move.getSubMove(), false, true);
    }
    if (!submove) {
      whiteTurn = !whiteTurn;
    }
    endSquare.setPiece(move.getDestinationPiece());
    startSquare.setPiece(movePiece);
    movePiece.setDestination(startSquare);
    movePiece.setMoved(isMoved);
  }

  public void newMoveSet() {
    for (int i = 0; i < GRIDNUM; i++) {
      for (int j = 0; j < GRIDNUM; j++) {
        squareMat[i][j].noControl();
      }
    }
    attackers[0].removeAll(attackers[0]);
    attackers[1].removeAll(attackers[1]);
    for (Piece p : pieceList) {
      p.setPinPiece(null);
    }
    for (Piece p : pieceList) {
      p.updatePreLegalMoves(this);
    }

    for (Piece p : pieceList) {
      p.updateLegalMove(this);
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
