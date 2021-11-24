import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SearchTree implements Visitor {
  private static SearchTree uniqueInstance;
  public ArrayList<Activity> activitiesWithTag = new ArrayList<Activity>();
  public String tag;

  final static Logger logger = LoggerFactory.getLogger(SearchTree.class);
  static String secondrelease = "FITA2";
  static Marker second = MarkerFactory.getMarker(secondrelease);

  public static SearchTree getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new SearchTree();
      logger.debug(second, "Initializing SearchTree");
    }
    return uniqueInstance;
  }

  public ArrayList<Activity> searchByTag(Activity root, String tag) {
    this.activitiesWithTag.clear();
    this.tag = tag;
    //Possible error if tag not assigned before acceptVisitor
    logger.warn(second, "Tags must be assigned to prevent errors in this search");
    logger.debug(second, "Visiting the root to search by tag");
    root.acceptVisitor(this);
    return this.activitiesWithTag;
  }

  @Override
  public void visitTask(Task task) {
    List<String> lowerCaseTags =
            task.getTags().stream().map(String::toLowerCase).collect(Collectors.toList());
    logger.trace(second, "Retrieving tags from {}, tags are: {}", task.getName(), lowerCaseTags.toString());
    if (lowerCaseTags.contains(tag.toLowerCase())) {
      this.activitiesWithTag.add(task);
    }
  }

  @Override
  public void visitProject(Project project) {
    // Check if .contains is the best option, maybe we want to add task with tag Pepe, if Pep
    // searched
    List<String> lowerCaseTags =
            project.getTags().stream().map(String::toLowerCase).collect(Collectors.toList());
    logger.trace(second, "Retrieving tags from {}, tags are: {}", project.getName(), lowerCaseTags.toString());
    if (lowerCaseTags.contains(tag.toLowerCase())) {
      this.activitiesWithTag.add(project);
    }
    for (Activity activity : project.getActivities()) {
      activity.acceptVisitor(this);
    }
  }

  @Override
  public void visitInterval(Interval interval) {
    //TODO
  }
}
