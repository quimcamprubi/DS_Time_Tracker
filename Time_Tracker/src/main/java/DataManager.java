import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Scanner;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/*
DataManager is the class that deals with persistence. it is used to save user data into a JSON
file, and for the inverse process. It is also used to load the tree from the JSON file and create
the resulting tree.
*/
public class DataManager {
  // Arraylist of all activities (not a tree)
  public final ArrayList<Activity> loadedActivities;

  // ----- CONSTRUCTOR -----
  public DataManager() {
    loadedActivities = new ArrayList<Activity>();
  }

  //Initialization of the Logger Instance and its markers
  final Logger logger = LoggerFactory.getLogger(DataManager.class);
  String firstrelease = "FITA1";
  Marker first = MarkerFactory.getMarker(firstrelease);

  // ----- METHODS -----
  public void saveUserData(Project project) {
    try {
      // SaveToJson is a Visitor used to run through the tree and store the data in to the JSON
      // file.
      SaveToJson treeRecovery = SaveToJson.getInstance();
      // The tree will be stored in the "out.json" file
      FileWriter f = new FileWriter("out.json", false);
      // Store is the SaveToJson function which runs through the tree and stores the data into
      // the JSON file.
      JSONArray data = treeRecovery.store(project);
      logger.debug(first, "File created and opened");
      f.write(data.toString());
      f.write("\n");
      logger.debug(first, "File closed");
      f.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  /*
  loadUserData is the function used to loop through the Activities found in the JSON Array and
  reconstruct the tree. First, it creates a list with all the activities, which contain their
  parents. Then, it reconstructs the tree using said parents, and stores the final tree in the
  root variable, which is returned at the end.
  */
  public Project loadUserData() throws FileNotFoundException {
    // Read the JSON file.
    String fileName = "out.json";
    File myObj = new File(fileName);
    Scanner reader = new Scanner(myObj);
    String data = reader.nextLine();
    final JSONArray jsonArray = new JSONArray(data);
    reader.close();
    logger.debug(first, "Initializing saved tree");
    // Loading of the root, which will be necessary to load the rest of the activities (because
    // each activity will need its parent). Parsing of all the values stored in the JSON file.
    // In this first part, we only load the root, as it will be necessary to create its children,
    // as each children needs to reference its parent.
    logger.trace(first, "Rebuilding root");
    JSONObject rootJsonActivity = jsonArray.getJSONObject(0);
    LocalDateTime rootStartTime = LocalDateTime.parse(rootJsonActivity.getString("StartTime"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    LocalDateTime rootEndTime = LocalDateTime.parse(rootJsonActivity.getString("EndTime"),
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    Duration rootDuration = Duration.parse(rootJsonActivity.getString("Duration"));
    String rootName = rootJsonActivity.getString("Name");
    JSONArray tagsJsonArrayRoot = rootJsonActivity.getJSONArray("Tags");
    ArrayList<String> rootTags = new ArrayList<String>();
    logger.trace(first, "Recovered tree root with values: startTime -> {}, endTime -> {}, "
        + "duration  -> {}", rootStartTime, rootEndTime, rootDuration);
    // Recovery of all the tags.
    for (int i = 0; i < tagsJsonArrayRoot.length(); i++) {
      rootTags.add(tagsJsonArrayRoot.getString(i));
    }
    if (!Objects.equals(rootName, "root")) {
      logger.error("The JSON file contains an error with the tree's root. It may have been "
          + "corrupted");
      return null;
    }
    // Creation of the tree's root, and addition to the list of all activities.
    Project root = new Project(rootName, rootTags, null, rootDuration, rootStartTime, rootEndTime);
    loadedActivities.add(root);

    logger.debug(first, "Entering the tree recovery loop");
    for (int i = 1; i < jsonArray.length(); i++) {
      // Parse all the attributes of the activities
      JSONObject jsonActivity = jsonArray.getJSONObject(i);
      // instantiateActivity creates each activity from the JSONArray object.
      instantiateActivity(jsonActivity);
    }
    // Loop through the list of activities, and create the tree by adding children to each activity.
    logger.debug(first, "Repositioning childs and fathers alike");
    for (Activity son : loadedActivities) {
      Project father = son.getParent();
      if (father != null && !loadedActivities.contains(son)) {
        father.addChild(son);
      }
    }
    return root;
  }

  // Function used to create a new activity and append it to the loadedActivities list.
  private void instantiateActivity(JSONObject jsonActivity) {
    // Parsing the main values for each activity.
    String jsonStartTime = jsonActivity.getString("StartTime");
    String jsonEndTime = jsonActivity.getString("EndTime");
    String jsonDuration = jsonActivity.getString("Duration");
    LocalDateTime startTime = null;
    LocalDateTime endTime = null;
    Duration duration = Duration.ZERO;
    // Since dates and duration can be null (for an activity which hasn't started), we need to
    // check before trying to parse them.
    if (!Objects.equals(jsonStartTime, "null") && !Objects.equals(jsonEndTime, "null")) {
      startTime = LocalDateTime.parse(jsonStartTime,  DateTimeFormatter.ofPattern("yyyy-MM-dd "
          + "HH:mm:ss"));
      endTime = LocalDateTime.parse(jsonEndTime,  DateTimeFormatter.ofPattern("yyyy-MM-dd "
          + "HH:mm:ss"));
      duration = Duration.parse(jsonDuration);
    }
    String className = jsonActivity.getString("Class");
    String name = jsonActivity.getString("Name");
    String parent = jsonActivity.getString("Parent");
    JSONArray tagsJsonArray = jsonActivity.getJSONArray("Tags");
    logger.trace("Creating {} {} with values: startTime-> {}, endTime->{}, duration{}", className,
        name, startTime, endTime, duration);
    ArrayList<String> tags = new ArrayList<String>();
    for (int j = 0; j < tagsJsonArray.length(); j++) {
      tags.add(tagsJsonArray.getString(j));
    }

    // Filter the activity list to find the parent of the current activity. Then, we create the
    // new  activity. This is necessary because each activity needs to reference its parent in
    // order to keep an ordered tree structure.
    Project parentProject = (Project) loadedActivities.stream().filter(x ->
        Objects.equals(x.getName(), parent)).findFirst().get();

    // The final creation of the new Activity differs a little bit between Projects and Tasks.
    if (className.equals("Project")) {
      Project project = new Project(name, tags, parentProject, duration, startTime, endTime);
      loadedActivities.add(project);
    } else if (className.equals("Task")) {
      // If the activity is a Task, we must also instantiate its intervals
      Task task = new Task(name, tags, parentProject, duration, startTime, endTime);
      logger.debug(first, "Initializing intervals for task {}", name);
      JSONArray intervals = jsonActivity.getJSONArray("Intervals");
      for (int j = 0; j < intervals.length(); j++) {
        JSONObject jsonInterval = intervals.getJSONObject(j);
        LocalDateTime intervalStartTime = null;
        LocalDateTime intervalEndTime = null;
        String jsonIntervalStartTime = jsonInterval.getString("StartTime");
        String jsonIntervalEndTime = jsonInterval.getString("EndTime");
        if (!Objects.equals(jsonIntervalStartTime, "null") && !Objects.equals(
            jsonIntervalEndTime, "null")) {
          intervalStartTime = LocalDateTime.parse(jsonIntervalStartTime,
              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
          intervalEndTime = LocalDateTime.parse(jsonIntervalEndTime,
              DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }

        task.addInterval(intervalStartTime, intervalEndTime);
        logger.trace(first, "Interval with values: startTime->{}, endTime->{}", intervalStartTime,
            intervalEndTime);
      }
      loadedActivities.add(task);
    } else {
      logger.error("Error, one of the JSON objects is neither a Task nor a Project.");
    }
  }
}
