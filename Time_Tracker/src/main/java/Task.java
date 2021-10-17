import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class Task extends Component{
    // ----- ATTRIBUTES -----
    private ArrayList<Interval> intervals;

    // ----- CONSTRUCTOR -----
    public Task(String name, ArrayList<String> tags, Component parent) {
        super(name, tags, parent);
        this.intervals = new ArrayList<Interval>();
    }

    // ----- METHODS -----
    public void start(){this.intervals.add(new Interval(this));}

    public void stop(){
        Interval lastInterval = this.intervals.get(this.intervals.size() - 1);
        lastInterval.endInterval();
    }

    @Override
    public void updateParentDuration(){
        Duration taskDuration = Duration.ZERO;
        for (Interval interval : this.intervals) {
            taskDuration = taskDuration.plus(interval.getDuration());
        }
        this.setDuration(taskDuration);
        if (this.getParent() != null) this.getParent().updateParentDuration();
    }



    @Override
    public void addChild(Component component){};


    public void acceptVisitor(Visitor visitor) {
        visitor.visitTask(this);
        for(Interval interval : this.intervals){
            interval.acceptVisitor(visitor);
        }
    }
}
