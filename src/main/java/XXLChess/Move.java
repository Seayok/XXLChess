package XXLChess;

/**
 * Class representing a move.
 */
public class Move {
  public static final int NORMAL = 0;
  public static final int CAPTURE = 1;
  public static final int CASTLE = 2;
  public static final int THREAT = 3;


  private Square startSquare;
  private Square endSquare;
  private boolean promotion;
  private int flag;
  private double score;
  private Piece promotedPiece;
  private Piece sourcePiece;
  private Piece destPiece;
  private Move subMove;

  /**
   * Contruct a new move.
   *
   * @param startSquare is the start square.
   * @param endSquare is the end square.
   * @param flag is the flag of the move: 0 is normal, 1 is capture, 2 is castle, 3 is threat.
   * @param sourcePiece is the moving piece.
   * @param destPiece is the piece of the destination.
   */
  public Move(Square startSquare, Square endSquare, int flag, Piece sourcePiece, Piece destPiece) {
    this.startSquare = startSquare;
    this.endSquare = endSquare;
    this.flag = flag;
    this.sourcePiece = sourcePiece;
    this.destPiece = destPiece;
    promotion = false;
  }

  public void setScore(double score) {
    this.score = score;
  }

  public void setPromotedPiece(Piece piece) {
    this.promotedPiece = piece;
  }

  public Piece getPromotedPiece() {
    return promotedPiece;
  }

  public boolean isPromotion() {
    return promotion;
  }

  public double getScore() {
    return score;
  }

  public Move getSubMove() {
    return subMove;
  }

  public void promotion() {
    this.promotion = true;
  }

  public void setSubMove(Move subMove) {
    this.subMove = subMove;
  }

  public Piece getSourcePiece() {
    return sourcePiece;
  }


  public Piece getDestinationPiece() {
    return destPiece;
  }

  public int getFlag() {
    return flag;
  }

  public Square getStartSquare() {
    return startSquare;
  }

  public Square getEndSquare() {
    return endSquare;
  }

}
