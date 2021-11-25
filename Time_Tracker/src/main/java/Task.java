import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/*
Class extending from the Activity abstract class. It has a list of Intervals, and it cannot
contain  Activities. A Task is a way of organizing continuous time units (intervals). Basically,
it will always be a "semi-leaf" in the tree structure, as it will only have one level of
children, composed of Intervals, but it will never have a deeper structure.
*/
public class Task extends Activity {
  // ----- ATTRIBUTES -----
  private final ArrayList<Interval> intervals;

  //Logger implementation
  final Logger logger = LoggerFactory.getLogger(Task.class);
  final String firstrelease = "FITA1";
  final Marker first = MarkerFactory.getMarker(firstrelease);

  // ----- CONSTRUCTOR -----
  public Task(String name, ArrayList<String> tags, Project parent) {
    super(name, tags, parent);
    logger.info(first, "Creating task {}", name);
    this.intervals = new ArrayList<Interval>();
  }

  // Secondary constructor used mainly for the JSON reloading of the tree.
  public Task(String name, ArrayList<String> tags, Project parent, Duration duration,
              LocalDateTime startTime, LocalDateTime endTime) {
    super(name, tags, parent, duration, startTime, endTime);
    logger.info(first, "Creating parametrized task {}", name);
    logger.trace(first, "Task {} values: Parent -> {}, Duration -> {}, startTime -> {}, "
        + "endTime -> {}", name, parent.getName(), duration, startTime, endTime);
    this.intervals = new ArrayList<Interval>();
  }

  // ----- METHODS -----
  // Methods to start and stop intervals
  public void start() {
    this.intervals.add(new Interval(this));
  }

  public void stop() {
    if (this.intervals.size() == 0) {
      logger.error(first, "Task {} cannot be stopped because it has no intervals.", name);
      throw new IllegalArgumentException("An interval cannot be stopped if none has been created");
    }
    Interval lastInterval = this.intervals.get(this.intervals.size() - 1);
    lastInterval.endInterval();
  }

  public void addInterval(LocalDateTime startTime, LocalDateTime endTime) {
    if (startTime == null || endTime == null) {
      throw new IllegalArgumentException("startTime and endTime cannot be null parameters.");
    }
    this.intervals.add(new Interval(this, startTime, endTime));
  }

  // Function used to update the Interval's parent's duration. After the Interval is updated with
  // a Clock update call, the Interval calls this function to propagate the duration from the
  // bottom to the top of the tree. At first, it updates its parent Task, but the Task then
  // propagates the information upwards to any type of Activity.
  @Override
  public void updateParentDuration() {
    Duration taskDuration = Duration.ZERO;
    for (Interval interval : this.intervals) {
      taskDuration = taskDuration.plus(interval.getDuration());
    }
    this.duration = taskDuration;
    logger.trace(first, "Task {} has been update", name);

    // Invariant
    assert invariant();
    assert (this.parent != null);
    this.parent.updateParentDuration();
  }

  public ArrayList<Interval> getIntervals() {
    return this.intervals;
  }

  public void acceptVisitor(Visitor visitor) {
    if (visitor == null) {
      //logger.error(first, "Visitor is null");
      throw new IllegalArgumentException("Visitor parameter cannot be null.");
    }
    visitor.visitTask(this);

    // Invariant
    assert invariant();
  }
}
