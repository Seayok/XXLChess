package XXLChess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import processing.core.PApplet;

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
  private HashMap<Character, CreatePiece> createOperations;
  private CopyOnWriteArrayList<Piece> pieceList;
  private Square[][] squareMat;

  /**
   * Constructor for creating a new board.
   *
   * @param levelArr is a matrix containing the code of piece in a matrix extracted from the config
   *        file.
   * @param app is the applicaiton we are running on.
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

  /**
   * Set image for all pieces and draw them.
   *
   * @param app is our main application.
   */
  public void setSpriteAndDisplay(PApplet app) {
    for (Piece piece : pieceList) {
      piece.setSprite(app);
    }
    draw(app);
  }

  /**
   * Process information of the clicks when there is no selected pieces.
   *
   * @param x is the x coordinate of the click.
   * @param y is the y coordinate of the click.
   * @return 1 when successully selected a piece, 0 otherwise.
   */
  public int startClick(int x, int y) {
    if (squareMat[x / GRIDSIZE][y / GRIDSIZE].getPiece() == null) {
      return 0;
    }
    Square selSquare = squareMat[x / GRIDSIZE][y / GRIDSIZE];
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

  /**
   * Process information of the clicks when there is a selected pieces.
   *
   * @param x is the x coordinate of the click.
   * @param y is the y coordinate of the click.
   * @return 0 when reseting the selected piece, 2 if succesfully move a piece, 3 if make a legal
   *         move that not protect the king when it is in check
   */
  public int selectClick(int x, int y) {
    Square target = squareMat[x / GRIDSIZE][y / GRIDSIZE];
    if (!target.isOnPieceWay() && !target.isOnCaptured()) {
      Square kingSquare = king[whiteTurn ? 1 : 0].getSquare();
      if (kingSquare.isKingChecked() && selPiece.getMoveFromSquare(target, false) != null) {
        king[whiteTurn ? 1 : 0].getSquare().setWarning();
        return 3;
      }
      resetSquares(null);
      if (target.getPiece() != null) {
        return startClick(x, y);
      }
      return 0;
    } else {
      makeMove(selPiece.getMoveFromSquare(target, true), true, false);
      return 2;
    }
  }

  /**
   * Make a move.
   *
   * @param move is the move to execute.
   * @param display is the flag indicating whether the change to the board will display or not(not
   *        display is for AI computing moves).
   * @param submove indicates whether the move has the submove to be executed first.
   */
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
      resetSquares(move);
    }
    movePiece.setMoved(true);
    if (!submove) {
      whiteTurn = !whiteTurn;
      newMoveSet();
    }
  }

  /**
   * Get the all legal moves from one side.
   *
   * @param isWhite indicates whether the move is from white side or black side.
   * @param order indicates whether we have the move in order of the move score(for AI alpha beta
   *        pruning purpose).
   * @return a list of all legal moves satisfying the parameters.
   */
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
      result.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
    }
    return result;
  }

  /**
   * Check if the current king of one side is being check.
   *
   * @return true if the current king is being check or false otherwise.
   */
  public boolean checkCheck() {
    boolean check = attackers[whiteTurn ? 1 : 0].size() > 0;
    king[whiteTurn ? 1 : 0].getSquare().setKingChecked(check);
    return check;
  }

  public boolean checkCheckMate() {
    return getAllMoves(whiteTurn, false).size() == 0;
  }

  /**
   * Check if one square is on the same line with other squares.
   *
   * @param start is the starting square.
   * @param end is the ending square.
   * @param check is the square being checked if it is on the same line with start square and end
   *        square.
   * @return true if 3 squares in on the same line, false otherwise.
   */
  public static boolean checkOnWay(Square start, Square end, Square check) {
    double firstDistance = distanceBetweenTwoSquares(start, end);
    double secondDistance = distanceBetweenTwoSquares(start, check);
    double thirdDistance = distanceBetweenTwoSquares(end, check);
    return firstDistance == secondDistance + thirdDistance;
  }

  /**
   * After a checkmate, this function is called to display all the pieces contributed to the
   * checkmate.
   */
  public void displayCheckMatePiece() {
    List<Square> occupiedSquare = new ArrayList<>();
    Piece kingPiece = king[whiteTurn ? 1 : 0];
    List<Move> kingMoves = new ArrayList<>(kingPiece.getPreLegalMoves());
    Square kingSquare = kingPiece.getSquare();
    kingMoves.add(new Move(kingPiece.getSquare(), kingSquare, Move.NORMAL, kingPiece, null));
    kingSquare.setPiece(null);
    for (Move m : kingMoves) {
      Square s = m.getEndSquare();
      Piece p = pieceAttackSquare(whiteTurn, s);
      if (p == null) {
        final Piece prevPiece = s.getPiece();
        s.setPiece(null);
        newMoveSet();
        p = pieceAttackSquare(whiteTurn, s);
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

  /**
   * Function to reset the color of the square and all the atributes related to the current state of
   * the board.
   *
   * @param move is the move made previously to set the color of previous move(green).
   */
  public void resetSquares(Move move) {
    Square target = move.getEndSquare();
    Square prevSquare = move.getStartSquare();
    for (int i = 0; i < GRIDNUM; i++) {
      for (int j = 0; j < GRIDNUM; j++) {
        Square square = squareMat[i][j];
        square.setOnPieceWay(false);
        square.setOnCapture(false);
        square.setSpecial(false);
        if (target != null) {
          square.setPrevMove(false);
          square.unWarning();
          square.setKingChecked(false);
        }
      }
    }
    if (target != null) {
      prevSquare.setPrevMove(true);
      target.setPrevMove(true);
    }
    prevSquare.deselect();
    selPiece = null;
  }

  /**
   * Get the piece which attacks a particular square.
   *
   * @param isWhite is the side of piece attacking.
   * @param target is the square we wish to get the piece attacking this square.
   * @return the piece which attacks this square, or null if otherwise.
   */
  public Piece pieceAttackSquare(boolean isWhite, Square target) {
    for (Piece p : pieceList) {
      if (p.isWhitePiece() != isWhite) {
        if (p.getMoveFromSquare(target, false) != null) {
          return p;
        }
      }
    }
    return null;
  }

  /**
   * Function to return values for the evaluate function that encourage AI to make the opponent king
   * to edge for easier checkmate.
   *
   * @return values for the evaluate function.
   */
  public double forceKingToEdge() {
    double eval = 0;
    Piece theirKing = king[whiteTurn ? 1 : 0];
    Piece ourKing = king[whiteTurn ? 0 : 1];
    double dstxToCenter =
        Math.max((theirKing.getDesX() / GRIDSIZE) - 6, 7 - (theirKing.getDesX() / GRIDSIZE));
    double dstyToCenter =
        Math.max((theirKing.getDesY() / GRIDSIZE) - 6, 7 - (theirKing.getDesY() / GRIDSIZE));
    double dstxToOpponent = Math.abs(ourKing.getDesX() - theirKing.getDesX()) / GRIDSIZE;
    double dstyToOpponent = Math.abs(ourKing.getDesY() - theirKing.getDesY()) / GRIDSIZE;
    eval += dstxToCenter + dstyToCenter;
    eval += (26 - dstxToOpponent - dstyToOpponent) / 260;
    return eval * (56 - pieceList.size()) / 600;
  }

  /**
   * Evaluates the current state of the board by various factors: position of pieces, position of
   * kings, pieces value.
   *
   * @return the evaluted score of the current state of the board, negative if black has an
   *         advantage, positive if white has an advantage, 0 if black and white are equal.
   */
  public double evaluateBoard() {
    double res = 0;
    for (Piece p : pieceList) {
      res += p.getValue();
      if (pieceList.size() > 10) {
        if (!p.getCode().contains("k")) {
          double pawnWeight = (p.getCode().contains("p") ? 4 : 1);
          double offset =
              (pieceSquareTables[(int) p.getDesX() / GRIDSIZE + 14 * (int) (p.getDesY() / GRIDSIZE)]
                  * (p.isWhitePiece() ? 1 : -1) * pieceList.size() * pawnWeight) / 1000;
          res += offset;
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

  /**
   * Function called after evaluateMove to go to the end of the capture chain to get the best
   * optimized values out of if. This function has no depth since we will go to the end of the
   * capture chain.
   *
   * @param move is the move to make.
   * @param alpha is the maximun values founded so far.
   * @param beta is the minimun values founded so far.
   * @param maximize is the flag indicating whether we are maximizing the values of move or
   *        not(white need to maximize while black need to minimize).
   * @return value of the move.
   */
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

  /**
   * An evaluation function that evaluates a move by using minimax algorithm combining with alpha
   * beta prunning.
   *
   * @param move is the move to make.
   * @param depth is the number of moves afterwards we want to consider to evaluate the current
   *        move.
   * @param alpha is the maximun values founded so far.
   * @param beta is the minimun values founded so far.
   * @param maximize is the flag indicating whether we are maximizing the values of move or
   *        not(white need to maximize while black need to minimize).
   * @return value of the move.
   */
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

  /**
   * Reset the state before a move is executed.
   *
   * @param move is the move had been executed.
   * @param isMoved is whether the moving piece is previously moved or not.
   * @param submove is the flag indicating whether the move has the submove or not.
   */
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

  /**
   * Updates the moveset of all pieces and reset state of squares.
   */
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

  public boolean isWhiteTurn() {
    return whiteTurn;
  }

  public Square[][] getSquareMat() {
    return squareMat;
  }

  public int getSize() {
    return GRIDSIZE * GRIDNUM;
  }

  public List<Piece> getPieceList() {
    return pieceList;
  }

  @Override
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
