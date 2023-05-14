package XXLChess;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import org.junit.jupiter.api.Test;
import processing.core.PApplet;
import processing.data.JSONObject;

public class TestSpeedAndCheck {
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
    configPath = "test6/config.json";
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
    player2.setupSide(player2.isWhite(), false);

  }


  // Let the pieces move the long distance to check the speed. Also perform the check to check the
  // TextBox textMethod working correctly together with the flashing.
  @Test
  public void testMovementSpeedAndCheck() {
    setup();
    Square[][] squares = board.getSquareMat();
    game.mouseClicked(10, 660);
    game.mouseClicked(620, 58);
    game.processMoveState(2);
    squares[13][0].tick();
    squares[0][13].tick();
    squares[12][1].getPiece().tick();
    squares[1][13].getPiece().tick();

    assertTrue(squares[0][13].getColor() == Square.LIGHT_GREEN);
    assertTrue(squares[13][0].getColor() == Square.DARK_RED);
    assertTrue(textBox.getText().equals("Check!"));
    assertTrue(squares[12][1].getPiece().getOverrideSpeed() > 6);

    game.mouseClicked(650, 58);
    game.mouseClicked(650, 58 + 48);
    assertTrue(
        textBox.getText().equals("Your king is\nin check!\n\n\nYou must defend\nyour king!"));
    squares[13][0].tick();
    squares[13][0].tick();
    game.mouseClicked(650, 10);
    game.mouseClicked(620, 58);
  }
}
