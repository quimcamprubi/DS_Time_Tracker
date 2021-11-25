//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/*
Abstract class as part of the Composite pattern applied in our implementation. Activity is the
abstract class implemented by both Tasks and Projects. It contains all the relevant information
needed for Time Tracker, including start and end times, duration, parent (reference to the parent
Activity, when talking about the root activity, parent is null), name and tags. It also contains
all the necessary methods required for Time Tracker. There are methods which are usable for both
Tasks and Projects and are therefore implemented in the Activity class, and there are also abstract
methods which differ between Tasks and Projects.
*/
public abstract class Activity {
  // ----- ATTRIBUTES -----
  protected final String name;
  protected final ArrayList<String> tags;
  protected final Project parent;
  protected Duration duration;
  protected LocalDateTime startTime;
  protected LocalDateTime endTime;
  //private static final Logger logger = LoggerFactory.getLogger(Activity.class);

  // ----- CONSTRUCTOR -----
  // Primary constructor used to declare an Activity without any time data.
  public Activity(String name, ArrayList<String> tags, Project parent) {
    //logger.trace("Creating object " + name);
    // Preconditions
    if (name == null) {
      throw new IllegalArgumentException("Activity name cannot be null.");
    }
    if (tags == null) {
      throw new IllegalArgumentException("Tags array cannot be null. It can be empty, but not "
              + "null.");
    }

    this.name = name;
    this.tags = tags;
    this.parent = parent;
    this.duration = Duration.ZERO;
    if (this.parent != null) {
      this.parent.addChild(this);
    }
  }

  // Secondary constructor used mainly for the JSON reloading of the tree.
  public Activity(String name, ArrayList<String> tags, Project parent, Duration duration,
                  LocalDateTime startTime, LocalDateTime endTime) {

    // Preconditions
    if (name == null) {
      throw new IllegalArgumentException("Activity name cannot be null.");
    }
    if (tags == null) {
      throw new IllegalArgumentException("Tags array cannot be null. It can be empty, but not "
              + "null.");
    }
    if (duration.isNegative()) {
      throw new IllegalArgumentException("Duration parameter cannot be negative.");
    }

    this.name = name;
    this.tags = tags;
    this.parent = parent;
    this.duration = duration;
    this.endTime = endTime;
    this.startTime = startTime;

    // Invariant
    assert invariant();

    if (this.parent != null) {
      this.parent.addChild(this);
    }
  }

  // ----- METHODS -----
  // Setters and Getters
  public ArrayList<String> getTags() {
    return tags;
  }

  public String getName() {
    return this.name;
  }

  public Project getParent() {
    return this.parent;
  }

  public Duration getDuration() {
    return this.duration;
  }

  public String getParsedStartTime() {
    return this.startTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  public String getParsedEndTime() {
    return this.endTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  // Abstract methods
  public abstract void acceptVisitor(Visitor visitor);

  public abstract void updateParentDuration();

  // Function used to propagate information to the parent of the Activity. It is used to update the
  // time and duration from the bottom to the top of the tree.
  public void updateParentInformation(LocalDateTime startTime, LocalDateTime endTime) {
    // Preconditions
    if (startTime == null || endTime == null) {
      throw new IllegalArgumentException("Date time parameters cannot be null.");
    }

    if (this.startTime == null) {
      this.startTime = startTime;
    }
    this.endTime = endTime;

    // Invariant
    assert invariant();

    if (this.parent != null) {
      this.parent.updateParentInformation(startTime, endTime);
    }
  }

  // Function that casts the Activity's information into a formatted string
  @Override
  public String toString() {
    DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    String name = this.name == null ? "null" : this.name;
    String startTime = this.startTime == null ? "null" : this.startTime.format(timeFormat);
    String endTime = this.endTime == null ? "null" : this.endTime.format(timeFormat);
    return String.format("%-10s %-20s %-30s %-30s %-5d", "activity:",
        name, startTime, endTime, Utils.roundDuration(this.duration));
  }

  protected boolean invariant() {
    Boolean nameNotNull = this.name != null;
    Boolean durationNotNegative = !this.duration.isNegative();
    if ((this.startTime != null) && (this.endTime != null)) {
      return nameNotNull && durationNotNegative && (!Duration.between(this.startTime,
          this.endTime).isNegative());
    } else {
      return nameNotNull && durationNotNegative;
    }
  }
}
