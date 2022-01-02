package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/*
core.PrintTree implements a core.Visitor which runs through the tree from the bottom to the top. This is
called each time there is an update to the timings of the tree. The information of an core.Interval is
 updated, and we then print it, as well as the information of all its parents.
*/
public class PrintTree implements Visitor {
  private static PrintTree uniqueInstance;

  //Logger implementation
  final Logger logger = LoggerFactory.getLogger(PrintTree.class);
  final String firstrelease = "FITA1";
  final Marker first = MarkerFactory.getMarker(firstrelease);

  public static PrintTree getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new PrintTree();
    }
    return uniqueInstance;
  }

  // Function which starts the printing process. It is called from the core.Interval.
  public void print(Interval interval) {
    logger.debug(first, "Visiting intervals in order to print the tree");
    interval.acceptVisitor(this);
  }

  // Prints a core.Project and, if it has a parent (the root will not have one), it propagates the
  // core.Visitor to the parent.
  @Override
  public void visitProject(Project project) {
    logger.info(first, project.toString());
    Activity parent = project.getParent();
    if (parent != null) {
      parent.acceptVisitor(this);
    }
  }

  // Prints a core.Task and, if it has a parent (the root will not have one), it propagates the
  // core.Visitor to the parent.
  @Override
  public void visitTask(Task task) {
    logger.info(first, task.toString());
    Activity parent = task.getParent();
    if (parent != null) {
      parent.acceptVisitor(this);
    }
  }

  // Prints a core.Task and, if it has a parent (core.Task), it propagates the core.Visitor to the parent.
  @Override
  public void visitInterval(Interval interval) {
    logger.info(first, interval.toString());
    Task parent = interval.getParent();
    if (parent != null) {
      parent.acceptVisitor(this);
    }
  }
}
