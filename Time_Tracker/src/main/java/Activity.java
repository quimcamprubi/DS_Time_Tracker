import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public abstract class Activity {
    // ----- ATTRIBUTES -----
    private String name;
    private ArrayList<String> tags;
    private Activity parent;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    // ----- CONSTRUCTOR -----
    public Activity(String name, ArrayList<String> tags, Activity parent) {
        this.name = name;
        this.tags = tags;
        this.parent = parent;
        this.duration = Duration.ZERO;
        if (this.parent != null) this.parent.addChild(this);
    }

    // ----- METHODS -----
    // Setters and Getters
    public String getName() { return this.name; }
    public Activity getParent() {return this.parent;}
    public Duration getDuration(){return this.duration;}
    public void setDuration(Duration duration){this.duration = duration;}

    // Abstract methods
    public abstract void addChild(Activity activity);
    public abstract void acceptVisitor(Visitor visitor);
    public abstract void updateParentDuration();

    // Function used to propagate information to the parent of the Activity. It is used to update the time and duration
    // from the bottom to the top of the tree.
    public void updateParentInformation(LocalDateTime startTime, LocalDateTime endTime){
        if (this.startTime == null) this.startTime = startTime;
        this.endTime = endTime;
        if (this.parent != null) this.parent.updateParentInformation(startTime, endTime);
    }

    // Function that casts the Activity's information into a formatted string
    @Override
    public String toString() {
        DateTimeFormatter timeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


        String name = this.name == null ? "null": this.name;
        //String parentName = this.parent == null ? "null": this.parent.getName();
        String startTime = this.startTime == null ? "null": this.startTime.format(timeFormat);
        String endTime = this.endTime == null ? "null": this.endTime.format(timeFormat);


        return String.format("%-10s %-20s %-30s %-30s %-5d", "activity:",
                name, startTime, endTime, Utils.roundDuration(this.duration));
    }
}
