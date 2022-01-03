package webserver;

import core.*;

import java.util.ArrayList;
import java.util.Arrays;

public class MainWebServer {
  public static void main(String[] args) {
    webServer();
  }

  public static void webServer() {
    final Activity root = makeTreeCourses();
    // implement this method that returns the tree of
    // appendix A in the practicum handout

    Clock.getInstance();

    new WebServer(root);
  }

  static Activity makeTreeCourses() {
    IDgenerator idgen = IDgenerator.getInstance();
    Project root = new Project("root", new ArrayList<String>(), null, idgen.getId());
    Project softwareDesign = new Project("Software Design", new ArrayList<String>(
        Arrays.asList("Java", "Flutter")), root, idgen.getId());
    Project softwareTesting = new Project("Software Testing", new ArrayList<String>(
        Arrays.asList("C++", "Java", "Python")), root, idgen.getId());
    Project databases = new Project("Databases", new ArrayList<String>(
        Arrays.asList("SQL", "Python", "C++")), root, idgen.getId());
    final Task transportation = new Task("Transportation", new ArrayList<String>(), root,
        idgen.getId());

    Project problems = new Project("Problems", new ArrayList<String>(), softwareDesign,
        idgen.getId());
    Project projectTimeTracker = new Project("Project Time Tracker",
        new ArrayList<String>(), softwareDesign, idgen.getId());

    final Task firstList = new Task("First list", new ArrayList<String>(
        Arrays.asList("Java")), problems, idgen.getId());
    final Task secondList = new Task("Second list", new ArrayList<String>(Arrays.asList("Dart")),
        problems, idgen.getId());

    Task readHandout = new Task("Read handout", new ArrayList<String>(), projectTimeTracker,
        idgen.getId());
    Task firstMilestone = new Task("First milestone", new ArrayList<String>(
        Arrays.asList("Java", "IntelliJ")), projectTimeTracker, idgen.getId());
    return root;
  }
}
