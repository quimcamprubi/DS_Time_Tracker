public class PrintWholeTree implements Visitor {
    private static PrintWholeTree uniqueInstance;
    public static PrintWholeTree getInstance() {
        if (uniqueInstance == null) {uniqueInstance = new PrintWholeTree();}
        return uniqueInstance;
    }

    public void print(Activity root) {
        root.acceptVisitor(this);
    }

    @Override
    public void visitProject(Project project) {
        System.out.println(project.toString());
        for (Activity activity : project.getActivities()) {
            activity.acceptVisitor(this);
        }
    }

    @Override
    public void visitTask(Task task) {
        System.out.println(task.toString());
        for (Interval interval : task.getIntervals()) {
            interval.acceptVisitor(this);
        }
    }

    @Override
    public void visitInterval(Interval interval) {
        System.out.println(interval.toString());
    }
}
