import java.time.Duration;
import java.time.LocalDateTime;

public class Interval implements Observer{
    // ----- ATTRIBUTES -----
    private LocalDateTime startTime; // Update double class to dateTime when CV is up
    private LocalDateTime endTime;
    private LocalDateTime currentTime;
    private Duration duration;

    // ----- CONSTRUCTOR -----
    public Interval(){
        this.startTime = this.currentTime;
    }

    public void endInterval() {
        this.endTime = this.currentTime;
        this.duration = Duration.between(startTime, endTime);
    }

    public void accept(Visitor visitor) {
        //TODO
    }

    public LocalDateTime getCurrentTime() {
        return this.currentTime;
    }

    public void setCurrentTime(LocalDateTime currentTime) {
        this.currentTime = currentTime;
    }

    public void update(){
        //TODO
    }
}
