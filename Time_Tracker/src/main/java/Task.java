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
    @Override
    public Duration computeComponentDuration(){
        Duration duration = Duration.ZERO;
        for (Interval interval : this.intervals){
            duration = duration.plus(interval.getDuration());
        }
        return duration;
    }

    public void start(){this.intervals.add(new Interval(this));}

    public void stop(){
        this.intervals.get(this.intervals.size() - 1).endInterval();

    }

    public void startInterval() {
        //TODO
    }

    public void addDuration(Duration duration) {
        //TODO
    }

    public void setEndTime(LocalDateTime endTime) {
        //TODO
    }

    public void acceptVisitor(Visitor v) {
        //TODO;
    }
}
