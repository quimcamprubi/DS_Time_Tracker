import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

  public void prettyPrintActivitiesWithTag(){
    for (Activity a:activitiesWithTag){
      System.out.println(a);
      //System.out.println('\n');
    }
  }

  public ArrayList<Activity> searchByTag(Activity root, String tag) {
    this.activitiesWithTag.clear();
    this.tag = tag;
    //Possible error if tag not assigned before acceptVisitor
    root.acceptVisitor(this);
    return this.activitiesWithTag;
  }

  @Override
  public void visitTask(Task task) {
    List<String> lowerCaseTags =
            task.getTags().stream().map(String::toLowerCase).collect(Collectors.toList());
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
