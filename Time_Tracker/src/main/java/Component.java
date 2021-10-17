import java.time.Duration;
import java.util.ArrayList;

public abstract class Component {
    // ----- ATTRIBUTES -----
    private String name;
    private ArrayList<String> tags;
    private Component parent;
    private Duration componentDuration;

    // ----- CONSTRUCTOR -----
    public Component(String name, ArrayList<String> tags, Component parent) {
        this.name = name;
        this.tags = new ArrayList<String>();
        this.parent = parent;
    }

    // ----- METHODS -----
    public abstract Duration computeComponentDuration();

    public Component getParent() {
        return this.parent;
    }
}
