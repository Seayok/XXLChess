package XXLChess;

public class Pawn extends Piece {
  public Pawn(int x, int y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = this.isWhite ? 1 : -1;
  }

  public void generateMove(Board curBoard) {
    int dir = 1;
    if (code.equals("wp")) {
      dir = -1;
    }
    dir *= pawnDirection;
    int range = 1;
    int rowDest = (int) destY / GRIDSIZE;
    if ((rowDest == 1 || rowDest == GRIDNUM - 2) && !this.isMoved()) {
      range = 2;
    }
    straightMove(curBoard, 0, dir, range);
    straightMove(curBoard, dir, dir, 1);
    straightMove(curBoard, -dir, dir, 1);

  }
}
