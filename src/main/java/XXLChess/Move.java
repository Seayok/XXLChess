package XXLChess;

public class Move {
  public static final int NORMAL = 0;
  public static final int CAPTURE = 1;
  public static final int CASTLE = 2;

  private Square startSquare;
  private Square endSquare;
  private int flag;
  private Piece sourcePiece;
  private Piece destPiece;

  public Move(Square startSquare, Square endSquare, int flag, Piece sourcePiece, Piece destPiece) {
    this.startSquare = startSquare;
    this.endSquare = endSquare;
    this.flag = flag;
    this.sourcePiece = sourcePiece;
    this.destPiece = destPiece;
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
