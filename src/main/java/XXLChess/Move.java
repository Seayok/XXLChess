package XXLChess;

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
