package XXLChess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

public class TestBlackPlayerCheckMate {
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
    configPath = "test2/config.json";
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

  }

  @Test
  public void testSide() {
    assertTrue(player2.isWhite());
    assertFalse(player1.isWhite());
    assertEquals(-1, Piece.pawnDirection);
  }

  // Perform a checkmate
  @Test
  public void testCheckMate() {
    player2.guessMove(board);
    Square[][] squares = board.getSquareMat();

    assertTrue(player2.isCalculating());
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }

    int moveState = player2.guessMove(board);
    assertEquals(2, moveState);
    game.processMoveState(moveState);
    assertFalse(board.isWhiteTurn());

    game.mouseClicked(120, 168);
    squares[2][3].setColor();
    assertTrue(squares[2][3].getColor() == Square.DARK_GREEN);
    game.mouseClicked(120, 10);
    squares[2][0].setColor();
    squares[10][0].setColor();
    assertTrue(squares[2][0].getColor() == Square.LIGHT_RED);
    assertTrue(squares[10][0].getColor() == Square.LIGHT_RED);

  }
}
