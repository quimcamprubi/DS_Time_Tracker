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


    public Duration getDuration() { return this.duration; }

    public void endInterval() {
        //this.parent.addDuration(this.duration);
        //this.parent.setEndTime(this.endTime);
        Clock.getInstance().deleteObserver(this);

    }

    public LocalDateTime getEndTime(){return this.endTime;}
    public LocalDateTime getStartTime(){return this.startTime;}

    public void acceptVisitor(Visitor visitor) {
        visitor.visitInterval(this);
    }
    @Override
    public String toString() {
        return String.format("%-21s child of %-10s %-30s %-30s %-5d", this.getClass().getSimpleName(), this.parent.getName(),
                this.startTime, this.endTime, Utils.roundDuration(this.duration));
    }

    @Override
    public void update(Observable o, Object arg) {
        this.endTime = (LocalDateTime) arg;
        if(this.startTime == null){this.startTime = this.endTime;}
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
