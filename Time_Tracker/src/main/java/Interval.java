import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    // ----- METHODS -----
    // Getters
    public Duration getDuration() { return this.duration; }
    public LocalDateTime getEndTime(){return this.endTime;}
    public LocalDateTime getStartTime(){return this.startTime;}

    public void endInterval() {
        //this.parent.addDuration(this.duration);
        //this.parent.setEndTime(this.endTime);
        Clock.getInstance().deleteObserver(this);

    }

    public void acceptVisitor(Visitor visitor) {
        visitor.visitInterval(this);
    }

    @Override
    public String toString() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        String typeClass = this.getClass().getSimpleName() + ":";
        String parentName = this.parent == null ? "null": this.parent.getName();
        String startTime = this.startTime == null ? "null": this.startTime.format(timeFormat);
        String endTime = this.endTime == null ? "null": this.endTime.format(timeFormat);

        return String.format("%-31s %-30s %-30s %-5d", typeClass, startTime, endTime, Utils.roundDuration(this.duration));
    }

    // The update method is called by the Observable (Clock). In this function, we update the current duration of the Interval,
    // as well as the end time (and the start time, but only the first time).  Then, we propagate the infromation upwards.
    // updateParentDuration and updateParentInformation are used to calculate the duration and the start and end times of the Components above.
    @Override
    public void update(Observable o, Object arg) {
        this.endTime = (LocalDateTime) arg;
        if(this.startTime == null) { this.startTime = this.endTime; }
        this.duration = Duration.between(this.startTime, this.endTime);
        this.parent.updateParentDuration();
        this.parent.updateParentInformation(this.startTime, this.endTime);
        //call print visitor
        Component root = this.parent;
        while(root.getParent() != null){
            root = root.getParent();
        }
        PrintTree.getInstance(null).print(root);
    }
}
