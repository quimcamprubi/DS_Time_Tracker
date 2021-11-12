import java.time.LocalDateTime;
import java.util.Timer;
import java.util.TimerTask;

// Clock implements an Observable which returns the current time every x seconds, where x is 2 in our case.
public class Clock extends java.util.Observable{
    // ----- ATTRIBUTES -----
    private final int tick; //Precision in seconds
    private final TimerTask timerTask;
    private final Timer timer;
    private static Clock uniqueInstance;

    // Since Clock is a Singleton, its constructor is private, and will only be called once (from getInstance()).
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
