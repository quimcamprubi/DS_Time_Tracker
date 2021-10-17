public class PrintTree implements Visitor {
    private static PrintTree uniqueInstance;
    public static PrintTree getInstance(Component root) {
        if (uniqueInstance == null) {uniqueInstance = new PrintTree(root);}
        return uniqueInstance;
    }

    private PrintTree(Component root){
        root.acceptVisitor(this);
    }

    @Override
    public void visitProject(Project project) {
        System.out.println(project.toString());
    }

    @Override
    public void visitTask(Task task) {
        System.out.println(task.toString());
    }

    @Override
    public void visitInterval(Interval interval) {
        System.out.println(interval.toString());
    }
}
