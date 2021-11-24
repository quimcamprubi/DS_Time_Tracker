import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/*
Class which implements the Main() function of our project. It declares all the
Projects and Tasks, and runs all the necessary commands to pass the tests.
*/
public class Client {
  public static void main(String[] args) throws
      InterruptedException, FileNotFoundException {


    // Initialization of the Clock and Printer singletons.
    Clock.getInstance(); // Clock implements an Observable which returns the current time every x
    // seconds, where x is 2 in our case.
    PrintTree.getInstance(); // Printer implements a Visitor type class which iterates through the
    // tree vertically from the bottom to the top. Printer is used to print the tree as it is
    // being updated.

    // Initialization of the DataManager class, used to save and load JSON files.
    final DataManager dataManager = new DataManager();

    //Initialization of the Logger Instance and its markers
    final Logger logger = LoggerFactory.getLogger(Client.class);
    String firstrelease = "FITA1";
    String secondrelease = "FITA2";
    Marker first = MarkerFactory.getMarker(firstrelease);
    Marker second = MarkerFactory.getMarker(secondrelease);

    // Initialization of all the Tasks and Projects.
    logger.warn("Warns will be shown");
    logger.info("Info will be shown");
    logger.debug("Debug mode is enabled");
    logger.trace("Stacktrace is enabled");

    logger.info(first, "Starting tree creation");
    Project root = new Project("root", new ArrayList<String>(), null);
    Project softwareDesign = new Project("software design", new ArrayList<String>(
        Arrays.asList("java", "flutter")), root);
    Project softwareTesting = new Project("software testing", new ArrayList<String>(
        Arrays.asList("c++", "Java", "python")), root);
    Project databases = new Project("databases", new ArrayList<String>(
        Arrays.asList("SQL", "python", "C++")), root);
    Task transportation = new Task("transportation", new ArrayList<String>(), root);

    Project problems = new Project("problems", new ArrayList<String>(), softwareDesign);
    Project projectTimeTracker = new Project("project time tracker",
        new ArrayList<String>(), softwareDesign);

    final Task firstList = new Task("first list", new ArrayList<String>(
        Arrays.asList("java")), problems);
    final Task secondList = new Task("second list", new ArrayList<String>(Arrays.asList("Dart")),
        problems);

    Task readHandout = new Task("read handout", new ArrayList<String>(), projectTimeTracker);
    Task firstMilestone = new Task("first milestone", new ArrayList<String>(
        Arrays.asList("Java", "IntelliJ")), projectTimeTracker);


    // Test B
    logger.info(first, "Here starts first release");
    logger.info(first, String.format("%-35s %-30s %-30s %-1s", "", "initial date", "final date",
        "duration"));
    logger.info(first, "start test");

    transportation.start();
    logger.info(first, "transportation starts");
    Thread.sleep(4000);
    transportation.stop();

    Thread.sleep(2000);
    logger.info(first, "transportation stops");

    logger.info(first, "first list starts");
    firstList.start();
    Thread.sleep(6000);

    logger.info(first, "second list starts");
    secondList.start();
    Thread.sleep(4000);

    logger.info(first, "first list stops");
    firstList.stop();

    Thread.sleep(2000);
    logger.info(first, "second list stops");
    secondList.stop();

    Thread.sleep(2000);

    logger.info(first, "transportation starts");
    transportation.start();
    Thread.sleep(4000);
    logger.info(first, "transportation stops");
    transportation.stop();
    // optionally, stop the clock

    // Once the simulation is done, we store the tree in a JSON file called "out.json"
    dataManager.saveUserData(root);


    System.out.println("============================");
    // We can now load the tree from the JSON file we created on the previous call.
    final Project loadedRoot = dataManager.loadUserData();

    // Initialization of the printWholeTree visitor, which runs through the whole tree from the
    // top to the bottom. We use this Visitor to print both trees (the one we created manually
    // and the JSON reloaded tree) and compare them, in order to check that our JSON
    // implementation works.
    final PrintWholeTree printWholeTree = PrintWholeTree.getInstance();
    logger.info(first, "Printing both trees to compare them");
    logger.info(first, "FIRST TREE:");
    printWholeTree.print(root);
    logger.info(first, "JSON TREE:");
    printWholeTree.print(loadedRoot);

    logger.info(first, "Here ends first release");

    logger.info(second, "Starts the second release demonstration");
    SearchTree searchTree = SearchTree.getInstance();
    logger.info(second, "Search Activities with Tag 'Java' ");
    searchTree.searchByTag(loadedRoot, "Java");

    logger.info(second, "Printing Search results:");
    for(Activity a: searchTree.activitiesWithTag)
      logger.info(second, a.toString());

    logger.info(first, "Here ends second release");

    System.exit(0);
  }
}
