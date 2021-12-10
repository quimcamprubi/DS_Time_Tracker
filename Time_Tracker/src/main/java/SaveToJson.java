import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/*
Visitor which runs through the tree and stores all the nodes it finds (Activities and Intervals)
into the JSON file.
*/
public class SaveToJson implements Visitor {
  // arr is the JSONArray which contains all the Activities.
  final JSONObject obj = new JSONObject();
  // Singleton implementation
  private static SaveToJson uniqueInstance;
  //Logger implementation
  final Logger logger = LoggerFactory.getLogger(SaveToJson.class);
  final String firstrelease = "FITA1";
  final Marker first = MarkerFactory.getMarker(firstrelease);

  public static SaveToJson getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new SaveToJson();

    }
    return uniqueInstance;
  }

  // Access to the tree root to start the saving object creation
  public JSONObject store(Project project) {
    JSONArray arr = new JSONArray();
    logger.warn(first, "Program is saving your data, please do not close the program until it is "
        + "done, doing so can create corrupted files or damage the capabilities of the program to "
        + "recover your data");
    logger.debug(first, "Starting data storage of root");
    // We add all the important information for each Activity
    obj.put("Tags", project.getTags());
    obj.put("Name", project.getName());
    obj.put("Class", project.getClass().getSimpleName());
    obj.put("ID", project.getId());
    obj.put("Activities", arr);
    logger.trace(first, "Tags, Name and Class stored for root");
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
    logger.trace(first, "StartTime, EndTime and Duration stored for root");

    obj.put("Parent", "null");
    // Then, we must propagate the Visitor through each children of the current Activity.
    for (Activity a : project.getActivities()) {
      // The handling of the activities differs depending on if the child is a Project (which has
      // Activities) or a Task (which has intervals)
      logger.debug(first, "Visiting activities in order to save");
      if (a instanceof Project) {
        ((Project) a).acceptVisitor(this);
      } else {
        ((Task) a).acceptVisitor(this);
      }
    }
    logger.info(first, "Data saved succesfully");
    return obj;
  }

  //Access each project node in order to re access his children
  @Override
  public void visitProject(Project project) {
    if(obj.get("Name") == project.getParent().getName()){
      JSONObject subobj = new JSONObject();
      logger.debug(first, "Starting data storage of project {}", project.getName());
      // We add all the important information for each Activity
      subobj.put("Tags", project.getTags());
      subobj.put("Name", project.getName());
      subobj.put("Class", project.getClass().getSimpleName());
      subobj.put("ID", project.getId());
      logger.trace(first, "Tags, Name and Class stored for project {}", project.getName());
      // Since the timings can be null, we check before trying to parse them.
      if (project.getStartTime() == null) {
        subobj.put("StartTime", "null");
        subobj.put("EndTime", "null");
        subobj.put("Duration", "null");
      } else {
        subobj.put("StartTime", project.getParsedStartTime());
        subobj.put("EndTime", project.getParsedEndTime());
        subobj.put("Duration", project.getDuration().toString());
      }
      logger.trace(first, "StartTime, EndTime and Duration stored for project {}", project.getName());
      subobj.put("Parent", project.getParent().getName());
      JSONArray fatherarray = obj.getJSONArray("Activities");
      fatherarray.put(subobj);
    }
    else{
      JSONArray aux = obj.getJSONArray("Activities");
      for (int i = 0; i < aux.length(); i++) {
        recursiveVisitProject(project, aux.getJSONObject(i));
      }

    }
    // Then, we must propagate the Visitor through each children of the current Activity.
    for (Activity a : project.getActivities()) {
      // The handling of the activities differs depending on if the child is a Project (which has
      // Activities) or a Task (which has intervals)
      if (a instanceof Project) {
        ((Project) a).acceptVisitor(this);
      } else {
        ((Task) a).acceptVisitor(this);
      }
    }
  }

  public void recursiveVisitProject(Project project, JSONObject o) {}

  //Access a child and recover not only his info but his intervals
  @Override
  public void visitTask(Task task) {
  if(obj.get("Name") == task.getParent().getName()){
    JSONObject subobj = new JSONObject();
    logger.debug(first, "Starting data storage of task {}", task.getName());
    // We add all the important information for each Activity
    subobj.put("Tags", task.getTags());
    subobj.put("Name", task.getName());
    subobj.put("Class", task.getClass().getSimpleName());
    subobj.put("ID", task.getId());
    logger.trace(first, "Tags, Name and Class stored for task {}", task.getName());
    // Since the timings can be null, we check before trying to parse them.
    if (task.getStartTime() == null) {
      subobj.put("StartTime", "null");
      subobj.put("EndTime", "null");
      subobj.put("Duration", "null");
    } else {
      subobj.put("StartTime", task.getParsedStartTime());
      subobj.put("EndTime", task.getParsedEndTime());
      subobj.put("Duration", task.getDuration().toString());
    }
    logger.trace(first, "StartTime, EndTime and Duration stored for task {}", task.getName());
    subobj.put("Parent", task.getParent().getName());
    // Since a Task contains intervals, we must loop through them and store them in the Intervals
    // JSONArray.
    JSONArray intervals = new JSONArray();
    for (Interval interval : task.getIntervals()) {
      JSONObject obj2 = new JSONObject();
      obj2.put("StartTime", interval.getParsedStartTime());
      obj2.put("EndTime", interval.getParsedEndTime());
      obj2.put("Duration", interval.getDuration().toString());
      intervals.put(obj2);
    }
    logger.trace(first, "Intervals of task {} stored", task.getName());
    subobj.put("Intervals", intervals);

    JSONArray fatherarray = obj.getJSONArray("Activities");
    fatherarray.put(subobj);
  }
    else{
    JSONArray aux = obj.getJSONArray("Activities");
    for (int i = 0; i < aux.length(); i++) {
      recursiveVisitTask(task, aux.getJSONObject(i));
    }

  }
  }


  public void recursiveVisitTask(Task Task, JSONObject o) {}

  @Override
  public void visitInterval(Interval interval) {
  }
}
