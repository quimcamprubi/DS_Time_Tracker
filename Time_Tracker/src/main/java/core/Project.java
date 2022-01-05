package core;

import java.awt.image.ImageObserver;
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
Class extending from the core.Activity abstract class. It has a list of activities, and it cannot
contain Intervals. A core.Project is a way of organizing smaller time units. Basically, it will always
be a node in the tree which will not directly contain an interval. It must contain other Projects
 or Tasks, which in turn will contain Intervals.
*/
public class Project extends Activity implements Observer {
  // ----- ATTRIBUTES -----
  private final ArrayList<Activity> activities;

  //Logger implementation
  final Logger logger = LoggerFactory.getLogger(Project.class);
  final String firstrelease = "FITA1";
  final Marker first = MarkerFactory.getMarker(firstrelease);

  // ----- CONSTRUCTOR -----
  public Project(String name, ArrayList<String> tags, Project parent, int id) {
    super(name, tags, parent, id);
    logger.info(first, "Creating project {}", name);
    this.activities = new ArrayList<Activity>();
    Clock.getInstance().addObserver(this);
  }

  // Secondary constructor used mainly for the JSON reloading of the tree.
  public Project(String name, ArrayList<String> tags, Project parent, Duration duration,
                 LocalDateTime startTime, LocalDateTime endTime, int id) {
    super(name, tags, parent, duration, startTime, endTime, id);
    logger.info(first, "Creating parametrized project {}", name);
    logger.trace(first, "core.Project {} values: Duration -> {}, startTime -> {}, endTime -> {}", name,
        duration, startTime, endTime);
    this.activities = new ArrayList<Activity>();
    Clock.getInstance().addObserver(this);
  }

  // ----- METHODS -----
  // Function used to update the core.Interval's parent's duration. After the core.Interval is updated with
  // a core.Clock update call, the core.Interval calls this function to propagate the duration from the
  // bottom to the top of the tree. At first, it updates its parent core.Task, but the core.Task then
  // propagates the information upwards to any type of core.Activity.
  @Override
  public void updateParentDuration() {
    Duration taskDuration = Duration.ZERO;
    for (Activity activity : this.activities) {
      taskDuration = taskDuration.plus(activity.getDuration());
    }
    this.duration = taskDuration;
    logger.trace(first, "{} has received the update", name);
    // Invariant
    assert invariant();

    if (this.parent != null) {
      this.parent.updateParentDuration();
    }
  }

  @Override
  public boolean isActive() {
    return activities.stream().anyMatch(a -> a.isActive());
  }

  public void addChild(Activity child) {
    // Preconditions
    if (child == null) {
      logger.error(first, "Child of parent {} is null", name);
      throw new IllegalArgumentException("Child to add cannot be null.");
    }

    this.activities.add(child);
    logger.trace(first, "Succesfully added child {}", child.getName());
  }

  public void removeChild(Activity child) {
    // Preconditions
    if (child == null) {
      logger.error(first, "Trying to remove child of parent {} while it is null", name);
      throw new IllegalArgumentException("Child parameter for removal cannot null.");
    }
    logger.trace(first, "Removed child {}", child.getName());
    this.activities.remove(child);
  }

  public ArrayList<Activity> getActivities() {
    return this.activities;
  }

  public void acceptVisitor(Visitor visitor) {
    if (visitor == null) {
      logger.error(first, "core.Visitor is null");
      throw new IllegalArgumentException("core.Visitor parameter cannot be null.");
    }
    visitor.visitProject(this);

    // Invariant
    assert invariant();
  }

  @Override
  public JSONObject toJson(int depth) {
    JSONObject returnedJsonObject = new JSONObject();
    // We add all the important information for each core.Activity
    returnedJsonObject.put("tags", this.tags);
    returnedJsonObject.put("name", this.name);
    returnedJsonObject.put("class", this.getClass().getSimpleName().toLowerCase());
    returnedJsonObject.put("id", this.Id);
    returnedJsonObject.put("active", this.isActive());
    // Since the timings can be null, we check before trying to parse them.
    if (this.startTime == null) {
      returnedJsonObject.put("initialDate", JSONObject.NULL);
      returnedJsonObject.put("finalDate", JSONObject.NULL);
      returnedJsonObject.put("duration", 0);
    } else {
      returnedJsonObject.put("initialDate", this.getParsedStartTime());
      returnedJsonObject.put("finalDate", this.getParsedEndTime());
      returnedJsonObject.put("duration", this.getDuration().getSeconds());
    }
    JSONArray arr = new JSONArray();
    if (this.parent == null) {
      returnedJsonObject.put("parent", JSONObject.NULL);
    } else {
      returnedJsonObject.put("parent", this.parent.getName());
    }
    if (depth != 0) {
      for (Activity a : this.activities) {
        arr.put(a.toJson(depth-1));
      }
    }
    returnedJsonObject.put("activities", arr);
    return returnedJsonObject;
  }

  @Override
  public void update(Observable o, Object arg) {
    Duration taskDuration = Duration.ZERO;
    for (Activity activity : this.activities) {
      taskDuration = taskDuration.plus(activity.getDuration());
    }
    this.duration = taskDuration;
  }
}
