package core;

public class IDgenerator{
    // ----- ATTRIBUTES -----
    private int id; //Precision in seconds
    private static IDgenerator uniqueInstance;

    // Since core.Clock is a Singleton, its constructor is private, and will only be called once (from
    // getInstance()).
    private IDgenerator() {
        id = 0;
    }

    // Singleton implementation
    public static IDgenerator getInstance() {
        if (uniqueInstance == null) {
            uniqueInstance = new IDgenerator();
        }
        return uniqueInstance;
    }

    public int getId(){
        id = id + 1;
        return id;
    }
}
