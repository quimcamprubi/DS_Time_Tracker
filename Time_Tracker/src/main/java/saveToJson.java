import org.json.JSONArray;
import org.json.JSONObject;

/*
Visitor which runs through the tree and stores all the nodes it finds (Activities and Intervals) into the JSON file.
*/
public class saveToJson implements Visitor {
  // arr is the JSONArray which contains all the Activities.
  final JSONArray arr = new JSONArray();
  // Singleton implementation
  private static saveToJson uniqueInstance;

  public static saveToJson getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new saveToJson();
    }
    return uniqueInstance;
  }

  // Access to the tree root to start the saving object creation
  public JSONArray store(Project project) {
    JSONObject obj = new JSONObject();
    arr.put(obj);
    // We add all the important information for each Activity
    obj.put("Tags", project.getTags());
    obj.put("Name", project.getName());
    obj.put("Class", project.getClass().getSimpleName());
    // Since the timings can be null, we check before trying to parse them.
    if (project.getStartTime() == null) {
      obj.put("StartTime", "null");
      obj.put("EndTime", "null");
      obj.put("Duration", "null");
    } else {
      obj.put("StartTime", project.getParsedStartTime());
      obj.put("EndTime", project.getParsedEndTime());
      obj.put("Duration", project.getDuration().toString());
    }
    obj.put("Parent", "null");
    // Then, we must propagate the Visitor through each children of the current Activity.
    for (Activity a : project.getActivities()) {
      // The handling of the activities differs depending on if the child is a Project (which has Activities) or a Task (which has intervals)
      if (a instanceof Project) {
        ((Project) a).acceptVisitor(this);
      } else {
        ((Task) a).acceptVisitor(this);
      }
    }
    return arr;
  }

  //Access each project node in order to re access his children
  @Override
  public void visitProject(Project project) {
    JSONObject obj = new JSONObject();
    arr.put(obj);
    // We add all the important information for each Activity
    obj.put("Tags", project.getTags());
    obj.put("Name", project.getName());
    obj.put("Class", project.getClass().getSimpleName());
    // Since the timings can be null, we check before trying to parse them.
    if (project.getStartTime() == null) {
      obj.put("StartTime", "null");
      obj.put("EndTime", "null");
      obj.put("Duration", "null");
    } else {
      obj.put("StartTime", project.getParsedStartTime());
      obj.put("EndTime", project.getParsedEndTime());
      obj.put("Duration", project.getDuration().toString());
    }
    obj.put("Parent", project.getParent().getName());
    // Then, we must propagate the Visitor through each children of the current Activity.
    for (Activity a : project.getActivities()) {
      // The handling of the activities differs depending on if the child is a Project (which has Activities) or a Task (which has intervals)
      if (a instanceof Project) {
        ((Project) a).acceptVisitor(this);
      } else {
        ((Task) a).acceptVisitor(this);
      }
    }
  }

  //Access a child and recover not only his info but his intervals
  @Override
  public void visitTask(Task task) {
    JSONObject obj = new JSONObject();
    arr.put(obj);
    // We add all the important information for each Activity
    obj.put("Tags", task.getTags());
    obj.put("Name", task.getName());
    obj.put("Class", task.getClass().getSimpleName());
    // Since the timings can be null, we check before trying to parse them.
    if (task.getStartTime() == null) {
      obj.put("StartTime", "null");
      obj.put("EndTime", "null");
      obj.put("Duration", "null");
    } else {
      obj.put("StartTime", task.getParsedStartTime());
      obj.put("EndTime", task.getParsedEndTime());
      obj.put("Duration", task.getDuration().toString());
    }
    obj.put("Parent", task.getParent().getName());
    // Since a Task contains intervals, we must loop through them and store them in the Intervals JSONArray.
    JSONArray Intervals = new JSONArray();
    for (Interval interval : task.getIntervals()) {
      JSONObject obj2 = new JSONObject();
      obj2.put("StartTime", interval.getParsedStartTime());
      obj2.put("EndTime", interval.getParsedEndTime());
      obj2.put("Duration", interval.getDuration().toString());
      Intervals.put(obj2);
    }
    obj.put("Intervals", Intervals);
  }

  @Override
  public void visitInterval(Interval interval) {
  }
}
