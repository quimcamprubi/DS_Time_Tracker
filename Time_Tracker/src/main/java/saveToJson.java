import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

public class saveToJson implements Visitor{
    JSONArray arr = new JSONArray();
    private static saveToJson uniqueInstance;
    public static saveToJson getInstance() {
        if (uniqueInstance == null) {uniqueInstance = new saveToJson();}
        return uniqueInstance;
    }

    //Access to the tree root to start the saving object creation
    public JSONObject store(Project project) {
        JSONObject obj = new JSONObject();
        arr.put(obj);

        obj.put("Name", project.getName());
        obj.put("Class", project.getClass().getSimpleName());
        if(project.getStartTime() == null){
            obj.put("StartTime", "null");
            obj.put("EndTime", "null");
            obj.put("Duration", "null");}
        else{
            obj.put("StartTime", project.getParsedStartTime());
            obj.put("EndTime", project.getParsedEndTime());
            obj.put("Duration", project.getDuration().toString());
        }
        obj.put("Parent", "null");
        for (Activity a: project.getActivities()){
            if(a instanceof Project){
                ((Project) a).acceptVisitor(this);
            }
            else{
                ((Task) a).acceptVisitor(this);
            }
        }
        return obj;
    }

    //Access each project node in order to reacces his childs
    @Override
    public void visitProject(Project project) {
        JSONObject obj = new JSONObject();
        arr.put(obj);

        obj.put("Name", project.getName());
        obj.put("Class", project.getClass().getSimpleName());
        if(project.getStartTime() == null){
            obj.put("StartTime", "null");
            obj.put("EndTime", "null");
            obj.put("Duration", "null");}
        else{
            obj.put("StartTime", project.getParsedStartTime());
            obj.put("EndTime", project.getParsedEndTime());
            obj.put("Duration", project.getDuration().toString());
        }
        obj.put("Parent", project.getParent().getName());
        for (Activity a: project.getActivities()){
            if(a instanceof Project){
                ((Project) a).acceptVisitor(this);
            }
            else{
                ((Task) a).acceptVisitor(this);
            }
        }
    }

    //Acess a child and recover not only his info but his intervals
    @Override
    public void visitTask(Task task) {
        JSONObject obj = new JSONObject();
        arr.put(obj);
        obj.put("Name", task.getName());
        obj.put("Class", task.getClass().getSimpleName());
        if(task.getStartTime() == null){
            obj.put("StartTime", "null");
            obj.put("EndTime", "null");
            obj.put("Duration", "null");}
        else{
            obj.put("StartTime", task.getParsedStartTime());
            obj.put("EndTime", task.getParsedEndTime());
            obj.put("Duration", task.getDuration().toString());
        }
        obj.put("Parent", task.getParent().getName());
        JSONArray Intervals = new JSONArray();
        for(Interval interval: task.getIntervals()){
            JSONObject obj2 = new JSONObject();
            obj2.put("StartTime", interval.getStartTime());
            obj2.put("EndTime", interval.getEndTime());
            Intervals.put(obj2);
        }
    }

    @Override
    public void visitInterval(Interval interval) {

    }

}