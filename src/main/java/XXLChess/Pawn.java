package XXLChess;

public class Pawn extends Piece {
  public Pawn(float x, float y, String code, Square curSquare) {
    super(x, y, code, curSquare);
    this.value = this.isWhite ? 1 : -1;
  }

  public void generateMove(Board curBoard) {
    int dir = isWhite ? -1 : 1;
    dir *= pawnDirection;
    int range = 1;
    int rowDest = (int) destY / GRIDSIZE;
    if ((rowDest == 1 || rowDest == GRIDNUM - 2) && !this.isMoved()) {
      range = 2;
    }
    straightMove(curBoard, 0, dir, range);
    straightMove(curBoard, dir, dir, 1);
    straightMove(curBoard, -dir, dir, 1);
    for (Move move : preLegalMoves) {
      int colorOffset = isWhite ? 1 : 0;
      int playerOffset = pawnDirection == 1 ? 1 : 0;
      if (move.getEndSquare().getY() / GRIDSIZE == 6 + (colorOffset ^ playerOffset)) {
        move.promotion();
      }
    }
  }

  public void promoteToQueen() {
    this.code = Character.toString(this.code.charAt(0)) + "q";
    this.value = (isWhite ? 1 : -1) * 9.5;
  }

  public void unpromote() {}
}
