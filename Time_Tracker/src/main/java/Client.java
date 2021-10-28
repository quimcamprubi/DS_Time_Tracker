import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws
            InterruptedException {
        // create and start the clock
        //...

        Clock clock = Clock.getInstance();
        // make the printer
        PrintTree printer = PrintTree.getInstance();
        DataManager saveloader = new DataManager();
        Project root = new Project("root", new ArrayList<String>(), null);
        Project softwareDesign = new Project("software design",  new ArrayList<String>( Arrays.asList("java", "flutter") ), root);
        Project softwareTesting = new Project("software testing",  new ArrayList<String>( Arrays.asList("c++", "Java", "python") ), root);
        Project databases = new Project("databases",  new ArrayList<String>( Arrays.asList("SQL", "python", "C++") ), root);
        Task transportation = new Task("transportation", new ArrayList<String>(), root);

        Project problems = new Project("problems", new ArrayList<String>(), softwareDesign);
        Project projectTimeTracker = new Project("project time tracker", new ArrayList<String>(), softwareDesign);

        Task firstList = new Task("first list",  new ArrayList<String>( Arrays.asList("java") ), problems);
        Task secondList = new Task("second list",  new ArrayList<String>( Arrays.asList("Dart") ), problems);

        Task readHandout = new Task("read handout", new ArrayList<String>(), projectTimeTracker);
        Task firstMilestone = new Task("first milestone",  new ArrayList<String>( Arrays.asList("Java", "IntelliJ") ), projectTimeTracker);

        // the printer will periodically print the whole tree
        // from now on

        // test it
        System.out.println(String.format("%-35s %-30s %-30s %-1s", "", "initial date","final date", "duration"));
        System.out.println("start test");
        //Thread.sleep(2000);

        transportation.start();
        System.out.println("transportation starts");
        Thread.sleep(4000);
        transportation.stop();

        Thread.sleep(2000);
        System.out.println("transportation stops");

        System.out.println("first list starts");
        firstList.start();
        Thread.sleep(6000);

        System.out.println("second list starts");
        secondList.start();
        Thread.sleep(4000);

        System.out.println("first list stops");
        firstList.stop();

        Thread.sleep(2000);
        System.out.println("second list stops");
        secondList.stop();

        Thread.sleep(2000);

        System.out.println("transportation starts");
        transportation.start();
        Thread.sleep(4000);
        System.out.println("transportation stops");
        transportation.stop();
        saveloader.saveUserData(root);
        // optionally, stop the clock
        // ...

        Activity loadedRoot = saveloader.loadUserData();

        System.exit(0);
    }
}
