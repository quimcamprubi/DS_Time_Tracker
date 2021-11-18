import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

/*
Class which implements the Main() function of our project. It declares all the Projects and Tasks, and runs all the necessary
commands to pass the tests.
*/
public class Client {
    public static void main(String[] args) throws
            InterruptedException, FileNotFoundException {

        // Initialization of the Clock and Printer singletons.
        Clock.getInstance(); // Clock implements an Observable which returns the current time every x seconds, where x is 2 in our case.
        PrintTree.getInstance(); // Printer implements a Visitor type class which iterates through the tree vertically from the bottom to the top.
        // Printer is used to print the tree as it is being updated.

        // Initialization of the DataManager class, used to save and load JSON files.
        DataManager dataManager = new DataManager();

        // Initialization of all the Tasks and Projects.
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

        // Test
        System.out.println(String.format("%-35s %-30s %-30s %-1s", "", "initial date","final date", "duration"));
        System.out.println("start test");

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
        // optionally, stop the clock

        // Once the simulation is done, we store the tree in a JSON file called "out.json"
        dataManager.saveUserData(root);

        System.out.println("\n");
        System.out.println("============================");
        // We can now load the tree from the JSON file we created on the previous call.
        Project loadedRoot = dataManager.loadUserData();
        // Initialization of the printWholeTree visitor, which runs through the whole tree from the top to the bottom.
        // We use this Visitor to print both trees (the one we created manually and the JSON reloaded tree) and compare them,
        // in order to check that our JSON implementation works.
        PrintWholeTree printWholeTree = PrintWholeTree.getInstance();
        System.out.println("Printing both trees to compare them");
        System.out.println("FIRST TREE:");
        printWholeTree.print(root);
        System.out.println("\n");
        System.out.println("JSON TREE:");
        printWholeTree.print(loadedRoot);

        System.exit(0);
    }
}
