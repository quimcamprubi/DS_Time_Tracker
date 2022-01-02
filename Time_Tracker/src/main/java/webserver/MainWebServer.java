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
    return root;
  }
}
