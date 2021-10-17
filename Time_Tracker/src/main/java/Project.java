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
    @Override
    public void updateParentDuration(){
        Duration taskDuration = Duration.ZERO;
        for (Component component : this.components) {
            taskDuration = taskDuration.plus(component.getDuration());
        }
        this.setDuration(taskDuration);
        if (this.getParent() != null)this.updateParentDuration();
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
