package XXLChess;


public class Move {
   /* FLags: 0 -> normal, 
   1 -> right castle, 
   2 -> left castle,
   3 -> pawn first move, 
   4 -> promotion
    */ 
    private int flag;
    private Square startSquare;
    private Square endSquare;
    
    public Move(Square startSquare, Square endSquare, int flag) {
        this.startSquare = startSquare;
        this.endSquare = endSquare;
        this.flag = flag;
    }

    public int getFlag() {
        return flag;
    }
}
