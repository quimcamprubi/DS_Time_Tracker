
/*
PrintTree implements a Visitor which runs through the tree from the bottom to the top. This is called each time there is an update
to the timings of the tree. The information of an Interval is updated, and we then print it, as well as the information of all its parents.
*/
public class PrintTree implements Visitor {
    private static PrintTree uniqueInstance;
    public static PrintTree getInstance() {
        if (uniqueInstance == null) {uniqueInstance = new PrintTree();}
        return uniqueInstance;
    }

    // Function which starts the printing process. It is called from the Interval.
    public void print(Interval interval) {
        interval.acceptVisitor(this);
    }

    // Prints a Project and, if it has a parent (the root will not have one), it propagates the Visitor to the parent.
    @Override
    public void visitProject(Project project) {
        System.out.println(project.toString());
        Activity parent = project.getParent();
        if (parent != null) parent.acceptVisitor(this);
    }

    // Prints a Task and, if it has a parent (the root will not have one), it propagates the Visitor to the parent.
    @Override
    public void visitTask(Task task) {
        System.out.println(task.toString());
        Activity parent = task.getParent();
        if (parent != null) parent.acceptVisitor(this);
    }

    // Prints a Task and, if it has a parent (Task), it propagates the Visitor to the parent.
    @Override
    public void visitInterval(Interval interval) {
        System.out.println(interval.toString());
        Task parent = interval.getParent();
        if (parent != null) parent.acceptVisitor(this);
    }
}
