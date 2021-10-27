import org.json.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.time.Duration;
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
            JSONObject data = TreeRecovery.store(project);

            f.write(data.toString());
            f.write("\n");

            f.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadUserData() {
        //TODO
    }


}
