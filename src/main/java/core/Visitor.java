package core;

// Default core.Visitor interface.
public interface Visitor {
  void visitTask(Task task);

  void visitProject(Project project);

  void visitInterval(Interval interval);
}
