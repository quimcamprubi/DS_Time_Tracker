import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

/*
Class extending from the Activity abstract class. It has a list of activities, and it cannot contain Intervals. A Project
is a way of organizing smaller time units. Basically, it will always be a node in the tree which will not directly contain
an interval. It must contain other Projects or Tasks, which in turn will contain Intervals.
*/
public class Project extends Activity {
  // ----- ATTRIBUTES -----
  private final ArrayList<Activity> activities;


  // ----- CONSTRUCTOR -----
  public Project(String name, ArrayList<String> tags, Project parent) {
    super(name, tags, parent);
    this.activities = new ArrayList<Activity>();
  }

  // Secondary constructor used mainly for the JSON reloading of the tree.
  public Project(String name, ArrayList<String> tags, Project parent, Duration duration, LocalDateTime startTime, LocalDateTime endTime) {
    super(name, tags, parent, duration, startTime, endTime);
    this.activities = new ArrayList<Activity>();
  }

  // ----- METHODS -----
  // Function used to update the Interval's parent's duration. After the Interval is updated with a Clock update call,
  // the Interval calls this function to propagate the duration from the bottom to the top of the tree. At first, it updates
  // its parent Task, but the Task then propagates the information upwards to any type of Activity.
  @Override
  public void updateParentDuration() {
    Duration taskDuration = Duration.ZERO;
    for (Activity activity : this.activities) {
      taskDuration = taskDuration.plus(activity.getDuration());
    }
    this.duration = taskDuration;
    if (this.parent != null) this.parent.updateParentDuration();
  }

  public void addChild(Activity child) {
    this.activities.add(child);
  }

  public void removeChild(Activity child) {
    this.activities.remove(child);
  }

  public ArrayList<Activity> getActivities() {
    return this.activities;
  }

  public void acceptVisitor(Visitor visitor) {
    visitor.visitProject(this);
  }
}
