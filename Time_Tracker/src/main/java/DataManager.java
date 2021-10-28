import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

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

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonActivity = jsonArray.getJSONObject(1);
            System.out.println(jsonActivity);
            LocalDateTime startTime = LocalDateTime.parse(jsonActivity.getString("StartTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            LocalDateTime endTime = LocalDateTime.parse(jsonActivity.getString("EndTime"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Duration duration = Duration.parse(jsonActivity.getString("Duration"));
            String className = jsonActivity.getString("Class");
            String name = jsonActivity.getString("Name");
            String parent = jsonActivity.getString("Parent");
            String unparsedTags = jsonActivity.getString("Tags");
            String splitedTags[] = unparsedTags.split(" ");
            ArrayList<String> tags = new ArrayList<String>(Arrays.asList(splitedTags));
            System.out.println(name);
            switch (className) {
                case "Project":
                    //Project problems = new Project(name, tags , FORMATO PARENT ES STRING DEBERÍA SER ACTIVITY, duration, startTime, endTime);
                    break;
                case "Task":
                    //Project task = new Task(name, tags , FORMATO PARENT ES STRING DEBERÍA SER ACTIVITY, duration, startTime, endTime);
                    JSONArray Intervals = jsonActivity.getJSONArray("Intervals");
                    for(int j = 0; j < Intervals.length(); j++){
                        JSONObject jsonInterval = jsonArray.getJSONObject(i);
                        //He indagado un poco y aparentemente el formato ISO_INSTANT es el predeterminado de LocalDateTime
                        LocalDateTime intervalStartTime = LocalDateTime.parse(jsonInterval.getString("StartTime"), DateTimeFormatter.ISO_INSTANT);
                        LocalDateTime intervalEndTime = LocalDateTime.parse(jsonInterval.getString("EndTime"), DateTimeFormatter.ISO_INSTANT);
                        //objetotask.addInterval(intervalStartTime, intervalEndTime);
                    }
                    break;
                default:
                    System.out.println("Error, one of the JSON objects is neither a Task nor a Project.");
                    break;
            }

        }
        return null;
    }
}
