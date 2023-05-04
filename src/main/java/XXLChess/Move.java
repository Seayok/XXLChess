package XXLChess;

public class Move {
  private Square startSquare;
  private Square endSquare;
  private int flag;
  private Piece sourcePiece;
  private Piece destPiece;

  public Move(Square startSquare, Square endSquare, int flag, Piece sourcePiece) {
    this.startSquare = startSquare;
    this.endSquare = endSquare;
    this.flag = flag;
    this.sourcePiece = sourcePiece;
  }

  public Piece getSourcePiece() {
    return sourcePiece;
  }

  public void setDestinationPiece(Piece destPiece) {
    this.destPiece = destPiece;
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
