import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

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

    public Project loadUserData() {
        String fileName = "/out.json";
        InputStream inputStream = DataManager.class.getResourceAsStream(fileName);
        if (inputStream == null) throw new NullPointerException("Cannot find resource file " + fileName);
        JSONTokener jsonTokener = new JSONTokener(inputStream);
        JSONArray jsonArray = new JSONArray(jsonTokener);
        ArrayList<Activity> loadedObjects;

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonActivity = jsonArray.getJSONObject(i);
            LocalDateTime startTime = LocalDateTime.parse(jsonActivity.getString("StartTime"));
            LocalDateTime endTime = LocalDateTime.parse(jsonActivity.getString("EndTime"));
            Duration duration = Duration.parse(jsonActivity.getString("Duration"));
            String className = jsonActivity.getString("Class");
            String name = jsonActivity.getString("Name");
            String parent = jsonActivity.getString("Parent");
            switch(name) {
                case "Project":
                    // Required parent;
                    break;
                case "Task":
                    // Required parent;
                    break;
                default:
                    System.out.println("Error, one of the JSON objects is neither a Task nor a Project.");
                    break;
            }
        }
        return null;
    }
}
