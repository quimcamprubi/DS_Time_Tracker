public class PrintTree implements Visitor {
    private static PrintTree uniqueInstance;
    public static PrintTree getInstance() {
        if (uniqueInstance == null) {uniqueInstance = new PrintTree();}
        return uniqueInstance;
    }

    private PrintTree(){}

    public void print(Interval interval) {
        interval.acceptVisitor(this);
        //System.out.println("\n");
    }

    @Override
    public void visitProject(Project project) {
        System.out.println(project.toString());
        Activity parent = project.getParent();
        if (parent != null) parent.acceptVisitor(this);
    }

    @Override
    public void visitTask(Task task) {
        System.out.println(task.toString());
        Activity parent = task.getParent();
        if (parent != null) parent.acceptVisitor(this);
    }

    @Override
    public void visitInterval(Interval interval) {
        System.out.println(interval.toString());
        Task parent = interval.getParent();
        if (parent != null) parent.acceptVisitor(this);
    }
}
