package core;

import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.util.Objects;

/*
core.Visitor which runs through the tree and stores all the nodes it finds (Activities and Intervals)
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
    // We add all the important information for each core.Activity
    obj.put("tags", project.getTags());
    obj.put("name", project.getName());
    obj.put("class", project.getClass().getSimpleName().toLowerCase());
    obj.put("id", project.getId());
    logger.trace(first, "Tags, Name and Class stored for root");
    // Since the timings can be null, we check before trying to parse them.
    if (project.getStartTime() == null) {
      obj.put("initialDate", "null");
      obj.put("finalDate", "null");
      obj.put("duration", "null");
    } else {
      obj.put("initialDate", project.getParsedStartTime());
      obj.put("finalDate", project.getParsedEndTime());
      obj.put("duration", project.getDuration().toString());
    }
    obj.put("activities", arr);
    logger.trace(first, "initialDate, finalDate and duration stored for root");

    obj.put("parent", "null");
    // Then, we must propagate the core.Visitor through each children of the current core.Activity.
    for (Activity a : project.getActivities()) {
      // The handling of the activities differs depending on if the child is a core.Project (which has
      // Activities) or a core.Task (which has intervals)
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
    if(obj.get("name") == project.getParent().getName()){
      JSONObject subobj = new JSONObject();
      logger.debug(first, "Starting data storage of project {}", project.getName());
      // We add all the important information for each core.Activity
      subobj.put("tags", project.getTags());
      subobj.put("name", project.getName());
      subobj.put("class", project.getClass().getSimpleName().toLowerCase());
      subobj.put("id", project.getId());
      logger.trace(first, "Tags, Name and Class stored for project {}", project.getName());
      // Since the timings can be null, we check before trying to parse them.
      if (project.getStartTime() == null) {
        subobj.put("initialDate", "null");
        subobj.put("finalDate", "null");
        subobj.put("duration", "null");
      } else {
        subobj.put("initialDate", project.getParsedStartTime());
        subobj.put("finalDate", project.getParsedEndTime());
        subobj.put("duration", project.getDuration().toString());
      }
      JSONArray arr = new JSONArray();
      subobj.put("activities", arr);
      logger.trace(first, "initialDate, finalDate and duration stored for project {}",
          project.getName());
      subobj.put("parent", project.getParent().getName());
      JSONArray fatherarray = obj.getJSONArray("activities");
      fatherarray.put(subobj);
    }
    else{
      JSONArray aux = obj.getJSONArray("activities");
      for (int i = 0; i < aux.length(); i++) {
        String aux2 = aux.getJSONObject(i).get("class").toString();
        if(Objects.equals(aux2, "project"))
          recursiveVisitProject(project, aux.getJSONObject(i));
      }

    }
    // Then, we must propagate the core.Visitor through each children of the current core.Activity.
    for (Activity a : project.getActivities()) {
      // The handling of the activities differs depending on if the child is a core.Project (which has
      // Activities) or a core.Task (which has intervals)
      if (a instanceof Project) {
        ((Project) a).acceptVisitor(this);
      } else {
        ((Task) a).acceptVisitor(this);
      }
    }
  }

  public void recursiveVisitProject(Project project, JSONObject o) {
    if(o.get("name") == project.getParent().getName()){
      JSONObject subobj = new JSONObject();
      logger.debug(first, "Starting data storage of project {}", project.getName());
      // We add all the important information for each core.Activity
      subobj.put("tags", project.getTags());
      subobj.put("name", project.getName());
      subobj.put("class", project.getClass().getSimpleName().toLowerCase());
      subobj.put("id", project.getId());
      logger.trace(first, "Tags, Name and Class stored for project {}", project.getName());
      // Since the timings can be null, we check before trying to parse them.
      if (project.getStartTime() == null) {
        subobj.put("initialDate", "null");
        subobj.put("finalDate", "null");
        subobj.put("duration", "null");
      } else {
        subobj.put("initialDate", project.getParsedStartTime());
        subobj.put("finalDate", project.getParsedEndTime());
        subobj.put("duration", project.getDuration().toString());
      }
      JSONArray arr = new JSONArray();
      subobj.put("activities", arr);
      logger.trace(first, "initialDate, finalDate and duration stored for project {}",
          project.getName());
      subobj.put("parent", project.getParent().getName());
      JSONArray fatherarray = o.getJSONArray("activities");
      fatherarray.put(subobj);
    }
    else{
      JSONArray aux = o.getJSONArray("activities");
      for (int i = 0; i < aux.length(); i++) {
        String aux2 = aux.getJSONObject(i).get("class").toString();
        if(Objects.equals(aux2, "project"))
          recursiveVisitProject(project, aux.getJSONObject(i));
      }

    }
  }

  //Access a child and recover not only his info but his intervals
  @Override
  public void visitTask(Task task) {
  if(obj.get("name") == task.getParent().getName()){
    JSONObject subobj = new JSONObject();
    logger.debug(first, "Starting data storage of task {}", task.getName());
    // We add all the important information for each core.Activity
    subobj.put("tags", task.getTags());
    subobj.put("name", task.getName());
    subobj.put("class", task.getClass().getSimpleName().toLowerCase());
    subobj.put("id", task.getId());
    logger.trace(first, "Tags, Name and Class stored for task {}", task.getName());
    // Since the timings can be null, we check before trying to parse them.
    if (task.getStartTime() == null) {
      subobj.put("initialDate", "null");
      subobj.put("finalDate", "null");
      subobj.put("duration", "null");
    } else {
      subobj.put("initialDate", task.getParsedStartTime());
      subobj.put("finalDate", task.getParsedEndTime());
      subobj.put("duration", task.getDuration().toString());
    }
    logger.trace(first, "initialDate, finalDate and duration stored for task {}", task.getName());
    subobj.put("parent", task.getParent().getName());
    // Since a core.Task contains intervals, we must loop through them and store them in the Intervals
    // JSONArray.
    JSONArray intervals = new JSONArray();
    for (Interval interval : task.getIntervals()) {
      JSONObject obj2 = new JSONObject();
      obj2.put("initialDate", interval.getParsedStartTime());
      obj2.put("finalDate", interval.getParsedEndTime());
      obj2.put("duration", interval.getDuration().toString());
      intervals.put(obj2);
    }
    logger.trace(first, "Intervals of task {} stored", task.getName());
    subobj.put("intervals", intervals);

    JSONArray fatherarray = obj.getJSONArray("activities");
    fatherarray.put(subobj);
  }
  else{
    JSONArray aux = obj.getJSONArray("activities");
    for (int i = 0; i < aux.length(); i++) {
      String aux2 = aux.getJSONObject(i).get("class").toString();
      if(Objects.equals(aux2, "project"))
        recursiveVisitTask(task, aux.getJSONObject(i));
    }

  }
  }


  public void recursiveVisitTask(Task task, JSONObject o) {
    if(o.get("name") == task.getParent().getName()){
      JSONObject subobj = new JSONObject();
      logger.debug(first, "Starting data storage of task {}", task.getName());
      // We add all the important information for each core.Activity
      subobj.put("tags", task.getTags());
      subobj.put("name", task.getName());
      subobj.put("class", task.getClass().getSimpleName().toLowerCase());
      subobj.put("id", task.getId());
      logger.trace(first, "Tags, Name and Class stored for task {}", task.getName());
      // Since the timings can be null, we check before trying to parse them.
      if (task.getStartTime() == null) {
        subobj.put("initialDate", "null");
        subobj.put("finalDate", "null");
        subobj.put("duration", "null");
      } else {
        subobj.put("initialDate", task.getParsedStartTime());
        subobj.put("finalDate", task.getParsedEndTime());
        subobj.put("duration", task.getDuration().toString());
      }
      logger.trace(first, "initialDate, finalDate and duration stored for task {}", task.getName());
      subobj.put("parent", task.getParent().getName());
      // Since a core.Task contains intervals, we must loop through them and store them in the Intervals
      // JSONArray.
      JSONArray intervals = new JSONArray();
      for (Interval interval : task.getIntervals()) {
        JSONObject obj2 = new JSONObject();
        obj2.put("initialDate", interval.getParsedStartTime());
        obj2.put("finalDate", interval.getParsedEndTime());
        obj2.put("duration", interval.getDuration().toString());
        intervals.put(obj2);
      }
      logger.trace(first, "Intervals of task {} stored", task.getName());
      subobj.put("intervals", intervals);

      JSONArray fatherarray = o.getJSONArray("activities");
      fatherarray.put(subobj);
    }
    else{
      JSONArray aux = o.getJSONArray("activities");
      for (int i = 0; i < aux.length(); i++) {
        String aux2 = aux.getJSONObject(i).get("class").toString();
        if(Objects.equals(aux2, "project"))
          recursiveVisitTask(task, aux.getJSONObject(i));
      }

    }
  }

  @Override
  public void visitInterval(Interval interval) {
  }
}
