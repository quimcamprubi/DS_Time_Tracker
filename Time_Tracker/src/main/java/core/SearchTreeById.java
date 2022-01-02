package core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;


public class SearchTreeById implements Visitor {
  private static SearchTreeById uniqueInstance;
  private int id;
  private Activity foundActivity;

  static final Logger logger = LoggerFactory.getLogger(SearchTreeById.class);
  static final String secondrelease = "FITA3";
  static final Marker second = MarkerFactory.getMarker(secondrelease);

  public static SearchTreeById getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new SearchTreeById();
      logger.debug(second, "Initializing core.SearchTreeById");
    }
    return uniqueInstance;
  }

  public Activity searchById(Activity root, Integer id) {
    this.id = id;
    this.foundActivity = null;
    //Possible error if tag not assigned before acceptVisitor
    logger.warn(second, "Id must be assigned to prevent errors in this search");
    logger.debug(second, "Visiting the root to search tree by id.");
    root.acceptVisitor(this);
    return this.foundActivity;
  }

  @Override
  public void visitTask(Task task) {
    logger.trace(second, "Retrieving task from tree with id {}", this.id);
    if (task.getId() == this.id) {
      if (this.foundActivity == null) {
        foundActivity = task;
      }
      else {
        throw new IllegalStateException("Error, there are multiple activities with the same id.");
      }
    }
  }

  @Override
  public void visitProject(Project project) {
    logger.trace(second, "Retrieving project from tree with id {}", this.id);
    if (project.getId() == this.id) {
      if (this.foundActivity == null) {
        foundActivity = project;
      }
      else {
        throw new IllegalStateException("Error, there are multiple activities with the same id.");
      }
    }
    for (Activity activity : project.getActivities()) {
      activity.acceptVisitor(this);
    }
  }

  @Override
  public void visitInterval(Interval interval) {}
}
