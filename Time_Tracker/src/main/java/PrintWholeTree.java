import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

/*
PrintWholeTree implements a Visitor which runs through the tree from the top to the bottom. It is
a call which prints the entire tree, regardless of the state of the tree. It is used by us when
comparing the manually created tree and the JSON-reloaded tree.
*/
public class PrintWholeTree implements Visitor {
  private static PrintWholeTree uniqueInstance;

  //Logger implementation
  final Logger logger = LoggerFactory.getLogger(PrintWholeTree.class);
  String firstrelease = "FITA1";
  Marker first = MarkerFactory.getMarker(firstrelease);

  public static PrintWholeTree getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new PrintWholeTree();
    }
    return uniqueInstance;
  }

  // Function which starts the printing process. It is called using the root of the tree.
  public void print(Activity root) {
    logger.debug(first, "Visiting intervals in order to print the tree");
    root.acceptVisitor(this);
  }

  // Prints a Project and propagates the Visitor to all its children (Activities).
  @Override
  public void visitProject(Project project) {
    logger.info(first, project.toString());
    for (Activity activity : project.getActivities()) {
      activity.acceptVisitor(this);
    }
  }

  // Prints a Task and propagates the Visitor to all its children (Intervals).
  @Override
  public void visitTask(Task task) {
    logger.info(first, task.toString());
    for (Interval interval : task.getIntervals()) {
      interval.acceptVisitor(this);
    }
  }

  // Prints an Interval.
  @Override
  public void visitInterval(Interval interval) {
    logger.info(first, interval.toString());
  }
}
