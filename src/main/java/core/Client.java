package core;

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

    IDgenerator idgen = IDgenerator.getInstance();
    // Initialization of the core.Clock and Printer singletons.
    Clock.getInstance(); // core.Clock implements an Observable which returns the current time every x
    // seconds, where x is 2 in our case.
    PrintTree.getInstance(); // Printer implements a core.Visitor type class which iterates through the
    // tree vertically from the bottom to the top. Printer is used to print the tree as it is
    // being updated.

    // Initialization of the core.DataManager class, used to save and load JSON files.
    final DataManager dataManager = new DataManager();

    //Initialization of the Logger Instance and its markers
    final Logger logger = LoggerFactory.getLogger(Client.class);
    String firstrelease = "FITA1";
    String secondrelease = "FITA2";
    final Marker first = MarkerFactory.getMarker(firstrelease);
    final Marker second = MarkerFactory.getMarker(secondrelease);

    // Initialization of all the Tasks and Projects.
    logger.warn("Warns will be shown");
    logger.info("Info will be shown");
    logger.debug("Debug mode is enabled");
    logger.trace("Stacktrace is enabled");

    logger.info(first, "Starting tree creation");
    Project root = new Project("root", new ArrayList<String>(), null, idgen.getId());
    Project softwareDesign = new Project("software design", new ArrayList<String>(
        Arrays.asList("java", "flutter")), root, idgen.getId());
    Project softwareTesting = new Project("software testing", new ArrayList<String>(
        Arrays.asList("c++", "Java", "python")), root, idgen.getId());
    Project databases = new Project("databases", new ArrayList<String>(
        Arrays.asList("SQL", "python", "C++")), root, idgen.getId());
    final Task transportation = new Task("transportation", new ArrayList<String>(), root, idgen.getId());

    Project problems = new Project("problems", new ArrayList<String>(), softwareDesign, idgen.getId());
    Project projectTimeTracker = new Project("project time tracker",
        new ArrayList<String>(), softwareDesign, idgen.getId());

    final Task firstList = new Task("first list", new ArrayList<String>(
        Arrays.asList("java")), problems, idgen.getId());
    final Task secondList = new Task("second list", new ArrayList<String>(Arrays.asList("Dart")),
        problems, idgen.getId());

    Task readHandout = new Task("read handout", new ArrayList<String>(), projectTimeTracker, idgen.getId());
    Task firstMilestone = new Task("first milestone", new ArrayList<String>(
        Arrays.asList("Java", "IntelliJ")), projectTimeTracker, idgen.getId());


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

    // We can now load the tree from the JSON file we created on the previous call.
    /*final core.Project loadedRoot = dataManager.loadUserData();

    // Initialization of the printWholeTree visitor, which runs through the whole tree from the
    // top to the bottom. We use this core.Visitor to print both trees (the one we created manually
    // and the JSON reloaded tree) and compare them, in order to check that our JSON
    // implementation works.
    final core.PrintWholeTree printWholeTree = core.PrintWholeTree.getInstance();
    logger.info(first, "Printing both trees to compare them");
    logger.info(first, "FIRST TREE:");
    printWholeTree.print(root);
    logger.info(first, "JSON TREE:");
    printWholeTree.print(loadedRoot);

    logger.info(first, "Here ends first release");

    logger.info(second, "Starts the second release demonstration");
    core.SearchTree searchTree = core.SearchTree.getInstance();
    logger.info(second, "Search Activities with Tag 'Java' ");
    final ArrayList<core.Activity> searchedActivities = searchTree.searchByTag(loadedRoot, "Java");
    searchTree.prettyPrintActivitiesWithTag();

    logger.info(first, "Here ends second release");*/

    System.exit(0);
  }
}
