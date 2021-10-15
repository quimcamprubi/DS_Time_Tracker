import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Observable;

public class Interval implements java.util.Observer{
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
        this.endTime = this.startTime + this.duration;
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

    @Override
    public void update(Observable o, Object arg) {

    }

}
