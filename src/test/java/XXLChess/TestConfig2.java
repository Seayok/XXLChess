package XXLChess;


import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

public class TestConfig2 {
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
    clockBlack = player1.isWhite() ? player2.getClock() : player1.getClock();
    clockWhite.start(app);
    clockBlack.stop(false);
    board = new Board(helper.loadBoard(), app);
    game = new Game(board, app, player1, player2, clockBlack, clockWhite, textBox);

  }

  @Test
  public void testMessage() {
    assertEquals(24, textBox.getSize());
    assertEquals("", textBox.getText());
  }

  @Test
  public void testTimer() {
    assertEquals(180, clockWhite.getCountDown());
    assertEquals(180, clockBlack.getCountDown());
    clockWhite.stop(true);
    assertEquals(182, clockWhite.getCountDown());
  }

  @Test
  public void testBoard() {
    assertEquals(56, board.getPieceList().size());
    assertEquals(672, board.getSize());
  }
}
