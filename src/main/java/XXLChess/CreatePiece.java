package XXLChess;

/**
 * Functional interface for creating new pieces.
 */
public interface CreatePiece {
  public Piece makeNewPiece(int x, int y, String code, Square square);
}
