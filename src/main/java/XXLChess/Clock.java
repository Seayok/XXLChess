package XXLChess;

import processing.core.PApplet;

public class Clock extends GameObject{
    private Timer timer;
    private boolean counting = false;
    private int countdown;
    private int increment;
    
    public Clock(float x, float y) {
        super(x, y);
        this.timer = new Timer(1000);
    }

    public void setConfig(int countdown, int increment) {
        this.increment = increment;
        this.countdown = countdown; 
    }

    public void start(PApplet app) {
        timer.start(app);
        counting = true;
    } 

    public void stop() {
        counting = false;
        countdown += increment;
    }

    public void tick(PApplet app) {
       if(counting) {
            if(timer.complete(app)){
                countdown -= 1;
                timer.start(app);
            }
       } 
    }
    
    @Override
    public void draw(PApplet app) {
        tick(app);
        int min = countdown / 60;
        int sec = countdown % 60;
        String s = String.format("%02d:%02d", min, sec);
        app.fill(255); 
        app.textSize(24);
        app.text(s, this.x, this.y);
    }
}