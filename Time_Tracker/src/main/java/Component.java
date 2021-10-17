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
        this.tags = new ArrayList<String>();
        this.parent = parent;
        if (this.parent != null) this.parent.addChild(this);
    }

    // ----- METHODS -----
    public abstract void addChild(Component component);
    public abstract void acceptVisitor(Visitor visitor);

    public abstract void updateParentDuration();

    public String getName() { return this.name; }
    public Component getParent() {return this.parent;}
    public Duration getDuration(){return this.duration;}

    public void setDuration(Duration duration){this.duration = duration;}

    public void updateParentInformation(LocalDateTime startTime, LocalDateTime endTime){
        if (this.startTime == null) this.startTime = startTime;
        this.endTime = endTime;
        if (this.parent != null) updateParentInformation(startTime, endTime);

    }
}
