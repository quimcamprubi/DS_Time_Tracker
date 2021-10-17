import java.time.Duration;
import java.util.ArrayList;

public class Project extends Component {
    // ----- ATTRIBUTES -----
    private ArrayList<Component> components;


    // ----- CONSTRUCTOR -----
    public Project(String name, ArrayList<String> tags, Component parent) {
        super(name, tags, parent);
        this.components = new ArrayList<Component>();
    }

    // ----- METHODS -----
    // Function used to update the Interval's parent's duration. After the Interval is updated with a Clock update call,
    // the Interval calls this function to propagate the duration from the bottom to the top of the tree. At first, it updates
    // its parent Task, but the Task then propagates the information upwards to any type of Component.
    @Override
    public void updateParentDuration(){
        Duration taskDuration = Duration.ZERO;
        for (Component component : this.components) {
            taskDuration = taskDuration.plus(component.getDuration());
        }
        this.setDuration(taskDuration);
        if (this.getParent() != null) this.getParent().updateParentDuration();
    }

    public ArrayList<Component> getChildren() {
        return this.components;
    }
    @Override
    public void addChild(Component child) {
        this.components.add(child);
    }
    public void removeChild(Component child) {
        this.components.remove(child);
    }

    public void acceptVisitor(Visitor visitor) {
        visitor.visitProject(this);
        for(Component component : this.components){
            component.acceptVisitor(visitor);
        }
    }
}
