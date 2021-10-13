import java.time.Duration;
import java.util.ArrayList;

public class DataManager {// Methods
    // ----- ATTRIBUTES -----
    private Printer printer;
    private ArrayList<Component> components; // Chosen ArrayList due to higher speed than List
    private Duration totalDuration;

    // ----- CONSTRUCTOR -----
    public DataManager() {
    }

    // ----- METHODS -----
    public void saveUserData() {
        //TODO
    }

    private void loadUserData() {
        //TODO
    }

    public Duration computeTotalDuration() {
        //TODO
        return this.totalDuration;
    }
}
