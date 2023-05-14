package XXLChess;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

public class TestCpuMateInOne {
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

  public void setup() {
    app = new App();
    player1 = new Player();
    player2 = new Player();
    configPath = "test3/config.json";
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


  // Let Cpu perform the checkmate
  @Test
  public void testCheckMate() {
    setup();
    player2.setDepth(0);
    game.mouseClicked(10, 10);
    game.mouseClicked(58, 10);
    game.processMoveState(2);
    player2.guessMove(board);
    assertTrue(player2.isCalculating());
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    int moveState = player2.guessMove(board);
    assertEquals(2, moveState);
    assertTrue(board.checkCheck());
    assertTrue(board.checkCheckMate());
    game.processMoveState(moveState);
  }
}
