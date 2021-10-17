import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public abstract class Component {
    // ----- ATTRIBUTES -----
    private String name;
    private ArrayList<String> tags;
    private Component parent;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;


    // ----- CONSTRUCTOR -----
    public Component(String name, ArrayList<String> tags, Component parent) {
        this.name = name;
        this.tags = tags;
        this.parent = parent;
        this.duration = Duration.ZERO;
        if (this.parent != null) this.parent.addChild(this);
    }

    // ----- METHODS -----
    // Setters and Getters
    public String getName() { return this.name; }
    public Component getParent() {return this.parent;}
    public Duration getDuration(){return this.duration;}
    public void setDuration(Duration duration){this.duration = duration;}

    // Abstract methods
    public abstract void addChild(Component component);
    public abstract void acceptVisitor(Visitor visitor);
    public abstract void updateParentDuration();

    // Function used to propagate information to the parent of the Component. It is used to update the time and duration
    // from the bottom to the top of the tree.
    public void updateParentInformation(LocalDateTime startTime, LocalDateTime endTime){
        if (this.startTime == null) this.startTime = startTime;
        this.endTime = endTime;
        if (this.parent != null) this.parent.updateParentInformation(startTime, endTime);
    }

    // Function that casts the Component's information into a formatted string
    @Override
    public String toString() {
        String parentName;
        if (this.parent == null) parentName = "null";
        else parentName = this.parent.getName();
        return String.format("%-10s %-10s child of %-10s %-30s %-30s %-5d", this.getClass().getSimpleName(),
                this.name, parentName, this.startTime,
                this.endTime, Utils.roundDuration(this.duration));
    }
}
