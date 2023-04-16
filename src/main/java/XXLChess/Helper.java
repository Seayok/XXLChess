package XXLChess;

import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import processing.data.JSONObject;

public class Helper {
  private JSONObject config;
  public void setConfig(JSONObject config) { this.config = config; }

  public void initTime(JSONObject config, Clock player1, Clock player2) {
    JSONObject timeConfig = config.getJSONObject("time_controls");
    JSONObject player1Time = timeConfig.getJSONObject("player");
    JSONObject player2Time = timeConfig.getJSONObject("cpu");
    player1.setConfig(player1Time.getInt("seconds"), player1Time.getInt("increment")); 
    player2.setConfig(player2Time.getInt("seconds"), player2Time.getInt("increment")); 
  }

  public String[][] loadBoard() {
    int gridSize = 14;
    String[][] arr = new String[gridSize][gridSize];

    try (Scanner scan = new Scanner(new File(config.getString("layout")))) {

      for (int i = 0; i < gridSize; i++) {
        String line = "";

        if (scan.hasNextLine()) {
          line = scan.nextLine();
        }

        for (int j = 0; j < gridSize; j++) {

          if (j > line.length() - 1) {
            arr[i][j] = "";
          } else {
            char lineChar = line.charAt(i);

            if (Character.isUpperCase(lineChar)) {
              arr[i][j] = Character.toString(line.charAt(j));
            } else {
              arr[i][j] = "w" + Character.toString(line.charAt(j));
            }
          }
        }
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return arr;
  }
}
