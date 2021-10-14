import java.util.ArrayList;

public class Project extends Component {
    // ----- ATTRIBUTES -----
    private ArrayList<Component> components;

    // ----- CONSTRUCTOR -----
    public Project(String name, String description, Component parent) {
        super(name, description, parent);
    }

    // ----- METHODS -----
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
