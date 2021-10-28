import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class DataManager {// Methods
    // ----- ATTRIBUTES -----
    private ArrayList<Activity> activities; // Chosen ArrayList due to higher speed than List
    private Duration totalDuration;

    // ----- CONSTRUCTOR -----
    public DataManager() {

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


    //Problema que me surge en la cabeza, esto ha de tener un return debido a que sino no podemos iniciar el árbol y
    // el return debería devolver todos los objetos de tipo activity con nombres diferentes
    public Project loadUserData() throws FileNotFoundException {
        //Tu decides cual te quedas, no conseguia tirar el tuyo y estaba perdiendo tiempo
        /*String fileName = "/out.json";
        InputStream inputStream = DataManager.class.getResourceAsStream(fileName);
        if (inputStream == null) throw new NullPointerException("Cannot find resource file " + fileName);
        JSONTokener jsonTokener = new JSONTokener(inputStream);
        JSONArray jsonArray = new JSONArray(jsonTokener);
        ArrayList<Activity> loadedObjects;*/

        String fileName = "out.json";
        File myObj = new File(fileName);
        Scanner reader = new Scanner(myObj);
        String data = reader.nextLine();
        JSONArray jsonArray = new JSONArray(data);
        reader.close();

        // Arraylist of all activities (not in a tree)
        ArrayList<Activity> loadedActivities = new ArrayList<Activity>();

        // First, we load the root, which will be necessary to load the rest of the activities
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
            rootTags.add(tagsJsonArrayRoot.getJSONObject(i).getString("value"));
        }
        if (!Objects.equals(rootName, "root")) {
            System.out.println("The JSON file contains an error with the tree's root. It may have been corrupted");
            return null;
        }
        Project root = new Project(rootName, rootTags, null, rootDuration, rootStartTime, rootEndTime);
        loadedActivities.add(root);

        for (int i = 1; i < jsonArray.length(); i++) {
            JSONObject jsonActivity = jsonArray.getJSONObject(i);
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
            String className = jsonActivity.getString("Class");
            String name = jsonActivity.getString("Name");
            String parent = jsonActivity.getString("Parent");
            JSONArray tagsJsonArray = rootJsonActivity.getJSONArray("Tags");
            ArrayList<String> tags = new ArrayList<String>();
            for (int j = 0; j < tagsJsonArray.length(); j++) {
                tags.add(tagsJsonArray.getJSONObject(j).getString("value"));
            }

            Activity parentActivity = loadedActivities.stream().filter(x -> Objects.equals(x.getName(), parent)).findFirst().get();
            if (className.equals("Project")) {
                Project project = new Project(name, tags, parentActivity, duration, startTime, endTime);
                loadedActivities.add(project);
            }
            else if (className.equals("Task")) {
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
                    /*LocalDateTime intervalStartTime = LocalDateTime.parse(jsonInterval.getString("StartTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                    LocalDateTime intervalEndTime = LocalDateTime.parse(jsonInterval.getString("EndTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));*/
                    task.addInterval(intervalStartTime, intervalEndTime);
                }
                loadedActivities.add(task);
            }
            else System.out.println("Error, one of the JSON objects is neither a Task nor a Project.");
        }
        System.out.println(loadedActivities.size());

        for(Activity son : loadedActivities){
            Activity father = son.getParent();
            if (father != null)
                father.addChild(son);
        }

        return root;
    }
}
