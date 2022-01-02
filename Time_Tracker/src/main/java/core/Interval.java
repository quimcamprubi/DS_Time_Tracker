package core;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Observable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;
import core.IDgenerator;

/*
Class which represents the minimum unit of time in our project. It stores a continuous amount of
time. Basically, each time a core.Task starts, a new core.Interval is created and started, and each time a
core.Task stops, the current core.Interval is stopped, and not used again.
*/
public class Interval implements java.util.Observer {
  // ----- ATTRIBUTES -----
  private LocalDateTime startTime;
  private LocalDateTime endTime;
  private Duration duration;
  private final Task parent;
  final Logger logger = LoggerFactory.getLogger(Interval.class);
  final String firstrelease = "FITA1";
  final Marker first = MarkerFactory.getMarker(firstrelease);
  private int id;
  private boolean active = false;

  // ----- CONSTRUCTOR -----
  public Interval(Task parent) {
    this.parent = parent;
    Clock.getInstance().addObserver(this);
    this.id = IDgenerator.getInstance().getId();
    this.active = true;
    logger.debug(first, "A new interval with parent {} has been created", parent.getName());
  }

  // Secondary constructor used to build an core.Interval when we already know its timings (such as
  // when reloading from a JSON file).
  public Interval(Task parent, LocalDateTime startTime, LocalDateTime endTime) {
    logger.debug(first, "A new interval with parent {} has been created", parent.getName());
    this.parent = parent;
    this.startTime = startTime;
    this.endTime = endTime;
    this.duration = Duration.between(this.startTime, this.endTime);
    logger.trace(first, "core.Interval values: Parent -> {}, startTime -> {}, endTime -> {}, Duration "
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

  public int getId() {
    return this.id;
  }

  public boolean getActive() {
    return this.active;
  }

  // When the interval is finished, we delete it from the Observable's (core.Clock) Observers list,
  // since it will no longer have to be updated.
  public void endInterval() {
    Clock.getInstance().deleteObserver(this);
    this.active = false;
  }

  // acceptVisitor function as part of the core.Visitor implementation. Necessary for our 3 Visitors.
  public void acceptVisitor(Visitor visitor) {
    visitor.visitInterval(this);
  }

  // Function used to format the core.Interval into a String. This is used when the information is
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

  // The update method is called by the Observable (core.Clock). In this function, we update the
  // current  duration of the core.Interval, as well as the end time (and the start time, but only the
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
