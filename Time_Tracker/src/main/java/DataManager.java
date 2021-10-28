import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataManager {// Methods
    // Arraylist of all activities (not a tree)
    public ArrayList<Activity> loadedActivities;

    // ----- CONSTRUCTOR -----
    public DataManager() {
        loadedActivities = new ArrayList<Activity>();
    }

    // ----- METHODS -----
    public void saveUserData(Project project) {
        try {
            saveToJson TreeRecovery = saveToJson.getInstance();
            FileWriter f = new FileWriter("out.json", false);
            JSONArray data = TreeRecovery.store(project);

            f.write(data.toString());
            f.write("\n");

            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Project loadUserData() throws FileNotFoundException {
        String fileName = "out.json";
        File myObj = new File(fileName);
        Scanner reader = new Scanner(myObj);
        String data = reader.nextLine();
        JSONArray jsonArray = new JSONArray(data);
        reader.close();

        // First, we load the root, which will be necessary to load the rest of the activities (because each activity will need its parent)
        JSONObject rootJsonActivity = jsonArray.getJSONObject(0);
        LocalDateTime rootStartTime = LocalDateTime.parse(rootJsonActivity.getString("StartTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        LocalDateTime rootEndTime = LocalDateTime.parse(rootJsonActivity.getString("EndTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        Duration rootDuration = Duration.parse(rootJsonActivity.getString("Duration"));
        String rootClassName = rootJsonActivity.getString("Class");
        String rootName = rootJsonActivity.getString("Name");
        String rootParent = rootJsonActivity.getString("Parent");
        JSONArray tagsJsonArrayRoot = rootJsonActivity.getJSONArray("Tags");
        ArrayList<String> rootTags = new ArrayList<String>();
        for (int i = 0; i < tagsJsonArrayRoot.length(); i++) {
            rootTags.add(tagsJsonArrayRoot.getString(i));
        }
        if (!Objects.equals(rootName, "root")) {
            System.out.println("The JSON file contains an error with the tree's root. It may have been corrupted");
            return null;
        }
        // Creation of the tree's root, and we add it to the list of all activities.
        Project root = new Project(rootName, rootTags, null, rootDuration, rootStartTime, rootEndTime);
        loadedActivities.add(root);

        for (int i = 1; i < jsonArray.length(); i++) {
            // We parse all the attributes of the activities
            JSONObject jsonActivity = jsonArray.getJSONObject(i);
            instantiateActivity(jsonActivity);
        }
        // Loop through the list of activities, and we create the tree by adding children to each activity.
        for(Activity son : loadedActivities){
            Activity father = son.getParent();
            if (father != null && !loadedActivities.contains(son))
                father.addChild(son);
        }
        return root;
    }

    private void instantiateActivity(JSONObject jsonActivity) {
        String jsonStartTime = jsonActivity.getString("StartTime");
        String jsonEndTime = jsonActivity.getString("EndTime");
        String jsonDuration = jsonActivity.getString("Duration");
        LocalDateTime startTime = null;
        LocalDateTime endTime = null;
        Duration duration = null;
        if (!Objects.equals(jsonStartTime, "null") && !Objects.equals(jsonEndTime, "null"))
        {
            startTime = LocalDateTime.parse(jsonStartTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            endTime = LocalDateTime.parse(jsonEndTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            duration = Duration.parse(jsonDuration);
        }
        if (duration == null) duration = Duration.ZERO;
        String className = jsonActivity.getString("Class");
        String name = jsonActivity.getString("Name");
        String parent = jsonActivity.getString("Parent");
        JSONArray tagsJsonArray = jsonActivity.getJSONArray("Tags");
        ArrayList<String> tags = new ArrayList<String>();
        for (int j = 0; j < tagsJsonArray.length(); j++) {
            tags.add(tagsJsonArray.getString(j));
        }

        // We filter the activity list to find the parent of the current activity. Then, we create the new activity.
        Activity parentActivity = loadedActivities.stream().filter(x -> Objects.equals(x.getName(), parent)).findFirst().get();

        if (className.equals("Project")) {
            Project project = new Project(name, tags, parentActivity, duration, startTime, endTime);
            loadedActivities.add(project);
        }
        else if (className.equals("Task")) {
            // If the activity is a task, we must also instantiate its intervals
            Task task = new Task(name, tags, parentActivity, duration, startTime, endTime);
            JSONArray intervals = jsonActivity.getJSONArray("Intervals");
            for(int j = 0; j < intervals.length(); j++) {
                JSONObject jsonInterval = intervals.getJSONObject(j);
                LocalDateTime intervalStartTime = null;
                LocalDateTime intervalEndTime = null;
                String jsonIntervalStartTime = jsonInterval.getString("StartTime");
                String jsonIntervalEndTime = jsonInterval.getString("EndTime");
                if (!Objects.equals(jsonIntervalStartTime, "null") && !Objects.equals(jsonIntervalEndTime, "null"))
                {
                    intervalStartTime = LocalDateTime.parse(jsonIntervalStartTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    intervalEndTime = LocalDateTime.parse(jsonIntervalEndTime, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                task.addInterval(intervalStartTime, intervalEndTime);
            }
            loadedActivities.add(task);
        }
        else System.out.println("Error, one of the JSON objects is neither a Task nor a Project.");
    }
}
