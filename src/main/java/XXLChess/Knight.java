package XXLChess;

public class Knight extends Piece {
  public Knight(float x, float y, String code, Square curSquare) {
    super(x, y, code, curSquare);
  }

  @Override
  public void generateMove(Board curBoard) {
    setHorseMove(curBoard, HORSE_RANGE);
  }
}
