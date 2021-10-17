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
    public Duration computeComponentDuration(){
        Duration duration = Duration.ZERO;
        for (Component component : this.components){
            duration = duration.plus(component.computeComponentDuration());
        }
        return duration;
    }

    public ArrayList<Component> getChildren() {
        return this.components;
    }

    public void addChild(Component child) {
        this.components.add(child);
    }

    public void removeChild(Component child) {
        this.components.remove(child);
    }

    public void acceptVisitor(Visitor v) {
        //TODO;
    }
}
