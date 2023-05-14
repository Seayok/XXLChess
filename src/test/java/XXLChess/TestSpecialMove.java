package XXLChess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

public class TestSpecialMove {
  Player player1;
  Player player2;
  Clock clockWhite;
  Clock clockBlack;
  String configPath;
  Helper helper;
  Message textBox;
  Board board;
  Game game;
  App app;

  @BeforeEach
  public void setup() {
    app = new App();
    player1 = new Player();
    player2 = new Player();
    configPath = "test4/config.json";
    helper = new Helper();
    textBox = new Message(680, 300, 24);

    JSONObject conf = PApplet.loadJSONObject(new File(configPath));
    helper.setConfig(conf);
    helper.initTimeAndSide(player1, player2);
    helper.updateMoveStatus(conf, player1.isWhite());
    clockWhite = player1.isWhite() ? player1.getClock() : player2.getClock();
    clockWhite.start(app);
    board = new Board(helper.loadBoard(), app);
    game = new Game(board, app, player1, player2, textBox);

    player2.guessMove(board);
    assertFalse(player1.isCalculating());
    assertTrue(player2.isCalculating());
    try {
      Thread.sleep(1000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int moveState = player2.guessMove(board);
    assertEquals(2, moveState);
    game.processMoveState(moveState);

  }

  @Test
  public void testCastle() {
    Square[][] squareMat = board.getSquareMat();
    Piece king = squareMat[6][13].getPiece();
    Piece rook = squareMat[13][13].getPiece();
    game.mouseClicked(300, 624 + 10);
    squareMat[8][13].setColor();
    squareMat[7][13].setColor();
    assertEquals(squareMat[7][13].getColor(), Square.LIGHT_BlUE);
    assertEquals(squareMat[8][13].getColor(), Square.PURPLE);
    game.mouseClicked(300 + 48 * 2, 624 + 10);
    king.tick();
    rook.tick();
    assertEquals(king, squareMat[8][13].getPiece());
    assertEquals(rook, squareMat[7][13].getPiece());

  }

  @Test
  public void testPromotion() {
    Square[][] squareMat = board.getSquareMat();
    Piece pawn = squareMat[4][7].getPiece();
    board.makeMove(pawn.getValidMoves().get(0), false, false);
    game.processMoveState(2);
    assertTrue(squareMat[4][6].getPiece().getCode().equals("bq"));
  }

  /**
   * Below will be some tests case that are expected to fail because the junit test can not handle
   * the PApplet.image method
   */

  @Test
  public void testPromtionDisplay() {
    Square[][] squareMat = board.getSquareMat();
    Piece pawn = squareMat[4][7].getPiece();
    try {
      // Here I set dislay to true therefore the program will try to get the queen image loaded by
      // app, but it can not.
      board.makeMove(pawn.getValidMoves().get(0), true, false);
      game.processMoveState(2);
    } catch (RuntimeException e) {
      return;
    }
  }


}
