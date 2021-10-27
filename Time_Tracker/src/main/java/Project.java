import org.json.JSONObject;

import java.time.Duration;
import java.util.ArrayList;

public class Project extends Activity {
    // ----- ATTRIBUTES -----
    private ArrayList<Activity> activities;


    // ----- CONSTRUCTOR -----
    public Project(String name, ArrayList<String> tags, Activity parent) {
        super(name, tags, parent);
        this.activities = new ArrayList<Activity>();
    }

    // ----- METHODS -----
    // Function used to update the Interval's parent's duration. After the Interval is updated with a Clock update call,
    // the Interval calls this function to propagate the duration from the bottom to the top of the tree. At first, it updates
    // its parent Task, but the Task then propagates the information upwards to any type of Activity.
    @Override
    public void updateParentDuration(){
        Duration taskDuration = Duration.ZERO;
        for (Activity activity : this.activities) {
            taskDuration = taskDuration.plus(activity.getDuration());
        }
        this.setDuration(taskDuration);
        if (this.getParent() != null) this.getParent().updateParentDuration();
    }

    public ArrayList<Activity> getChildren() {
        return this.activities;
    }
    @Override
    public void addChild(Activity child) {
        this.activities.add(child);
    }
    public void removeChild(Activity child) {
        this.activities.remove(child);
    }
    public ArrayList<Activity> getActivities(){ return this.activities;}
    public void acceptVisitor(Visitor visitor) {
        visitor.visitProject(this);
    }
}
