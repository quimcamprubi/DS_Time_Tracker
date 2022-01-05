package core;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/*
Class extending from the core.Activity abstract class. It has a list of Intervals, and it cannot
contain  Activities. A core.Task is a way of organizing continuous time units (intervals). Basically,
it will always be a "semi-leaf" in the tree structure, as it will only have one level of
children, composed of Intervals, but it will never have a deeper structure.
*/
public class Task extends Activity implements Observer {
  // ----- ATTRIBUTES -----
  private final ArrayList<Interval> intervals;
  private boolean active;

  //Logger implementation
  final Logger logger = LoggerFactory.getLogger(Task.class);
  final String firstrelease = "FITA1";
  final Marker first = MarkerFactory.getMarker(firstrelease);

  // ----- CONSTRUCTOR -----
  public Task(String name, ArrayList<String> tags, Project parent, int id) {
    super(name, tags, parent, id);
    logger.info(first, "Creating task {}", name);
    this.intervals = new ArrayList<Interval>();
    this.active = false;
    Clock.getInstance().addObserver(this);
  }

  // Secondary constructor used mainly for the JSON reloading of the tree.
  public Task(String name, ArrayList<String> tags, Project parent, Duration duration,
              LocalDateTime startTime, LocalDateTime endTime, int id) {
    super(name, tags, parent, duration, startTime, endTime, id);
    logger.info(first, "Creating parametrized task {}", name);
    logger.trace(first, "core.Task {} values: Parent -> {}, Duration -> {}, startTime -> {}, "
        + "endTime -> {}", name, parent.getName(), duration, startTime, endTime);
    this.intervals = new ArrayList<Interval>();
    Clock.getInstance().addObserver(this);
  }

  // ----- METHODS -----
  // Methods to start and stop intervals
  public void start() {
    if (!this.active) {
      this.intervals.add(new Interval(this));
      this.active = true;
    }
  }

  public void stop() {
    if (this.active) {
      if (this.intervals.size() == 0) {
        logger.error(first, "core.Task {} cannot be stopped because it has no intervals.", name);
        throw new IllegalArgumentException("An interval cannot be stopped if none has been created");
      }
      Interval lastInterval = this.intervals.get(this.intervals.size() - 1);
      lastInterval.endInterval();
      this.active = false;
    }
  }

  public void addInterval(LocalDateTime startTime, LocalDateTime endTime) {
    if (startTime == null || endTime == null) {
      throw new IllegalArgumentException("startTime and endTime cannot be null parameters.");
    }
    this.intervals.add(new Interval(this, startTime, endTime));
  }

  // Function used to update the core.Interval's parent's duration. After the core.Interval is updated with
  // a core.Clock update call, the core.Interval calls this function to propagate the duration from the
  // bottom to the top of the tree. At first, it updates its parent core.Task, but the core.Task then
  // propagates the information upwards to any type of core.Activity.
  @Override
  public void updateParentDuration() {
    Duration taskDuration = Duration.ZERO;
    for (Interval interval : this.intervals) {
      taskDuration = taskDuration.plus(interval.getDuration());
    }
    this.duration = taskDuration;
    logger.trace(first, "core.Task {} has been updated", name);

    // Invariant
    assert invariant();
    assert (this.parent != null);
    this.parent.updateParentDuration();
  }

  @Override
  public boolean isActive() {
    return this.active;
  }

  @Override
  public JSONObject toJson(int depth) {
    JSONObject returnedJsonObject = new JSONObject();
    // We add all the important information for each core.Activity
    returnedJsonObject.put("tags", this.tags);
    returnedJsonObject.put("name", this.name);
    returnedJsonObject.put("class", this.getClass().getSimpleName().toLowerCase());
    returnedJsonObject.put("id", this.Id);
    returnedJsonObject.put("active", this.active);
    // Since the timings can be null, we check before trying to parse them.
    if (this.startTime == null) {
      returnedJsonObject.put("initialDate", JSONObject.NULL);
      returnedJsonObject.put("finalDate", JSONObject.NULL);
      returnedJsonObject.put("duration", 0);
    } else {
      returnedJsonObject.put("initialDate", this.getParsedStartTime());
      returnedJsonObject.put("finalDate", this.getParsedEndTime());
      returnedJsonObject.put("duration", this.getDuration().toSecondsPart()); //TODO DURATION
    }
    returnedJsonObject.put("parent", this.parent.getName());
    JSONArray intervals = new JSONArray();
    for (Interval interval : this.intervals) {
      JSONObject obj2 = new JSONObject();
      obj2.put("id", interval.getId());
      obj2.put("active", interval.getActive());
      obj2.put("initialDate", interval.getParsedStartTime());
      obj2.put("finalDate", interval.getParsedEndTime());
      obj2.put("duration", interval.getDuration().toSecondsPart());
      intervals.put(obj2);
    }
    logger.trace(first, "Intervals of task {} stored", this.name);
    returnedJsonObject.put("intervals", intervals);
    return returnedJsonObject;
  }

  public ArrayList<Interval> getIntervals() {
    return this.intervals;
  }

  public void acceptVisitor(Visitor visitor) {
    if (visitor == null) {
      //logger.error(first, "core.Visitor is null");
      throw new IllegalArgumentException("core.Visitor parameter cannot be null.");
    }
    visitor.visitTask(this);

    // Invariant
    assert invariant();
  }


  @Override
  public void update(Observable o, Object arg) {
    Duration taskDuration = Duration.ZERO;
    for (Interval interval : this.intervals) {
      taskDuration = taskDuration.plus(interval.getDuration());
    }
    this.duration = taskDuration;
  }
}
