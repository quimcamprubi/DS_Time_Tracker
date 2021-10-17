import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Observable;

public class Interval implements java.util.Observer{
    // ----- ATTRIBUTES -----
    private LocalDateTime startTime; // Update double class to dateTime when CV is up
    private LocalDateTime endTime;
    private Duration duration;
    private Task parent;

    // ----- CONSTRUCTOR -----
    public Interval(Task parent){
        this.parent = parent;
        Clock.getInstance().addObserver(this);
    }

    public void endInterval() {
        this.duration = Duration.between(this.startTime, this.endTime);
        //this.parent.addDuration(this.duration);
        //this.parent.setEndTime(this.endTime);
        Clock.getInstance().deleteObserver(this);

    }

    public Duration getDuration(){return this.duration;}
    public LocalDateTime getEndTime(){return this.endTime;}
    public LocalDateTime getStartTime(){return this.startTime;}

    public void accept(Visitor visitor) {
        //TODO
    }

    @Override
    public void update(Observable o, Object arg) {
        this.endTime = (LocalDateTime) arg;
        if(this.startTime == null){this.startTime = this.endTime;}
        this.duration = Duration.between(this.startTime, this.endTime);
        //call print visitor

    }

}
