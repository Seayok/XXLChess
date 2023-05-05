package XXLChess;

public class Knight extends Piece {
  public Knight(int x, int y, String code, Square curSquare) {
    super(x, y, code, curSquare);
  }

  @Override
  public void generateMove(Board curBoard) {
    setHorseMove(curBoard, HORSE_RANGE);
  }
}
