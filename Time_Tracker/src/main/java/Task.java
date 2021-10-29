import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Objects;
/*
Class extending from the Activity abstract class. It has a list of Intervals, and it cannot contain Activities. A Task
is a way of organizing continuous time units (intervals). Basically, it will always be a "semi-leaf" in the tree structure, as it
will only have one level of children, composed of Intervals, but it will never have a deeper structure.
*/
public class Task extends Activity {
    // ----- ATTRIBUTES -----
    private ArrayList<Interval> intervals;

    // ----- CONSTRUCTOR -----
    public Task(String name, ArrayList<String> tags, Activity parent) {
        super(name, tags, parent);
        this.intervals = new ArrayList<Interval>();
    }

    // Secondary constructor used mainly for the JSON reloading of the tree.
    public Task(String name, ArrayList<String> tags, Activity parent, Duration duration,  LocalDateTime startTime, LocalDateTime endTime) {
        super(name, tags, parent, duration, startTime, endTime);
        this.intervals = new ArrayList<Interval>();
    }

    // ----- METHODS -----
    // Methods to start and stop intervals
    public void start(){ this.intervals.add(new Interval(this)); }
    public void stop(){
        Interval lastInterval = this.intervals.get(this.intervals.size() - 1);
        lastInterval.endInterval();
    }

    public void addInterval(LocalDateTime startTime, LocalDateTime endTime){
        this.intervals.add(new Interval(this, startTime, endTime));
    }

    // Function used to update the Interval's parent's duration. After the Interval is updated with a Clock update call,
    // the Interval calls this function to propagate the duration from the bottom to the top of the tree. At first, it updates
    // its parent Task, but the Task then propagates the information upwards to any type of Activity.
    @Override
    public void updateParentDuration(){
        Duration taskDuration = Duration.ZERO;
        for (Interval interval : this.intervals) {
            taskDuration = taskDuration.plus(interval.getDuration());
        }
        this.setDuration(taskDuration);
        if (this.getParent() != null) this.getParent().updateParentDuration();
    }

    public ArrayList<Interval> getIntervals(){return this.intervals;}
    public void acceptVisitor(Visitor visitor) {
        visitor.visitTask(this);
    }

    @Override
    public void addChild(Activity activity){};
}
