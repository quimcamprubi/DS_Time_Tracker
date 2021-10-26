import java.time.LocalDateTime;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class Clock extends java.util.Observable{

    private int tick; //Precision in seconds
    private TimerTask timerTask;
    private Timer timer;
    private static Clock uniqueInstance;

    private Clock() {
        this.timer = new Timer("Timer");
        this.tick = 2000;
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                timeScheduler();
            }
        };
        timer.scheduleAtFixedRate(this.timerTask, 0,this.tick);
    }

    // Singleton implementation
    public static Clock getInstance() {
        if (uniqueInstance == null) {uniqueInstance = new Clock();}
        return uniqueInstance;
    }

    // Function that notifies the Clock's observers with the current time.
    private void timeScheduler(){
        setChanged();
        notifyObservers(LocalDateTime.now());
    }
}
