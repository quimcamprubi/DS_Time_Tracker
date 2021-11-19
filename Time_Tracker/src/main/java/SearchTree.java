import java.time.LocalDateTime;
import java.util.ArrayList;

public class SearchTree implements Visitor {
  private static SearchTree uniqueInstance;
  public ArrayList<Activity> activitiesWithTag = new ArrayList<Activity>();
  public String tag;

  public static SearchTree getInstance() {
    if (uniqueInstance == null) {
      uniqueInstance = new SearchTree();
    }
    return uniqueInstance;
  }

  public ArrayList<Activity> searchByTag(Activity root, String tag){
    this.tag = tag;
    //Possible error if tag not assigned before acceptVisitor
    root.acceptVisitor(this);
    return this.activitiesWithTag;
  }

  @Override
  public void visitTask(Task task) {
    if (task.getTags().contains(tag)){activitiesWithTag.add(task);}
  }

  @Override
  public void visitProject(Project project) {
    //Check if .contains is the best option, maybe we want to add task with tag Pepe, if Pep searched
    if (project.getTags().contains(tag)){activitiesWithTag.add(project);}
    for (Activity activity : project.getActivities()) {
      activity.acceptVisitor(this);
    }
  }

  @Override
  public void visitInterval(Interval interval) {
    //TODO
  }
}
