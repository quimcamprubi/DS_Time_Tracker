import java.time.Duration;

public abstract class Component {
    // ----- ATTRIBUTES -----
    private String name;
    private String description;
    private Component parent;
    private Duration componentDuration;

    // ----- CONSTRUCTOR -----
    public Component(String name, String description, Component parent) {
        this.name = name;
        this.description = description;
        this.parent = parent;
    }

    // ----- METHODS -----
    public Duration computeComponentDuration() {
        //TODO
        return this.componentDuration;
    }

    public Component getParent() {
        return this.parent;
    }
}
