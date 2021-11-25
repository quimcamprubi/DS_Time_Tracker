import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/*
Class which represents the minimum unit of time in our project. It stores a continuous amount of
time. Basically, each time a Task starts, a new Interval is created and started, and each time a
Task stops, the current Interval is stopped, and not used again.
*/
public class Interval implements java.util.Observer {
  // ----- ATTRIBUTES -----
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Duration duration;
  private final Task parent;
  final Logger logger = LoggerFactory.getLogger(Interval.class);
  String firstrelease = "FITA1";
  Marker first = MarkerFactory.getMarker(firstrelease);

  // ----- CONSTRUCTOR -----
  public Interval(Task parent) {
    this.parent = parent;
    Clock.getInstance().addObserver(this);
    logger.debug(first, "A new interval with parent {} has been created", parent.getName());
  }

  // Secondary constructor used to build an Interval when we already know its timings (such as
  // when reloading from a JSON file).
  public Interval(Task parent, LocalDateTime startTime, LocalDateTime endTime) {
    logger.debug(first, "A new interval with parent {} has been created", parent.getName());
    this.parent = parent;
    this.startTime = startTime;
    this.endTime = endTime;
    this.duration = Duration.between(this.startTime, this.endTime);
    logger.trace(first, "Interval values: Parent -> {}, startTime -> {}, endTime -> {}, Duration "
        + "->  {}", parent.getName(), startTime, endTime, duration);
  }

  // ----- METHODS -----
  // Getters
  public Duration getDuration() {
    return this.duration;
  }

  public LocalDateTime getEndTime() {
    return this.endTime;
  }

  public Task getParent() {
    return this.parent;
  }

  public String getParsedStartTime() {
    return this.startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  public String getParsedEndTime() {
    return this.endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  // When the interval is finished, we delete it from the Observable's (Clock) Observers list,
  // since it will no longer have to be updated.
  public void endInterval() {
    Clock.getInstance().deleteObserver(this);
  }

  // acceptVisitor function as part of the Visitor implementation. Necessary for our 3 Visitors.
  public void acceptVisitor(Visitor visitor) {
    visitor.visitInterval(this);
  }

  // Function used to format the Interval into a String. This is used when the information is
  // printed on the console.
  @Override
  public String toString() {
    logger.trace(first, "Starting string parsing of interval with parent {}", parent.getName());
    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String typeClass = this.getClass().getSimpleName() + ":";
    String startTime = this.startTime == null ? "null" : this.startTime.format(timeFormat);
    String endTime = this.endTime == null ? "null" : this.endTime.format(timeFormat);
    return String.format("%-31s %-30s %-30s %-5d", typeClass, startTime, endTime,
        Utils.roundDuration(this.duration));
  }

  // The update method is called by the Observable (Clock). In this function, we update the
  // current  duration of the Interval, as well as the end time (and the start time, but only the
  // first time).  Then, we propagate the infromation upwards. updateParentDuration and
  // updateParentInformation are used to calculate the duration and the start and end times of
  // the Activities above.
  @Override
  public void update(Observable o, Object arg) {
    this.endTime = (LocalDateTime) arg;
    if (this.startTime == null) {
      this.startTime = this.endTime.minus(2, ChronoUnit.SECONDS);
    }
    this.duration = Duration.between(this.startTime, this.endTime);
    this.parent.updateParentDuration();
    this.parent.updateParentInformation(this.startTime, this.endTime);
    logger.trace(first, "Update has been received from clock object, update time for interval "
            + "with  parent {}: Duration -> {}, startTime -> {}, endTime -> {}", parent.getName(),
        duration, startTime, endTime);
    // Call print visitor
    PrintTree.getInstance().print(this);
  }
}
