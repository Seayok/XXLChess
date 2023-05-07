package XXLChess;

public class Camel extends Piece {
  public Camel(float x, float y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = 2 * (this.isWhite ? 1 : -1);
  }

  @Override
  public void generateMove(Board curBoard) {
    setHorseMove(curBoard, CAMEL_RANGE);
  }
}
