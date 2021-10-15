import java.time.LocalDateTime;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;

public class Clock extends java.util.Observable{

    private Long tick; //Precision in seconds
    private TimerTask timerTask;
    private Timer timer;

    public void Clock() {
        this.tick = 2000L;
        this.timer = new Timer("Timer");
        this.timerTask = new TimerTask() {
            @Override
            public void run() {
                notifyObservers(LocalDateTime.now());
            }
        };
        timer.schedule(this.timerTask, this.tick);
    }

    //TODO Singleton implementation
}
