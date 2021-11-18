import java.time.LocalDateTime;
import java.util.ArrayList;

public class SearchTree implements Visitor{
    private static PrintTree uniqueInstance;
    public static PrintTree getInstance() {
        if (uniqueInstance == null) {uniqueInstance = new PrintTree();}
        return uniqueInstance;
    }

    public void searchTree(SearchTreeOption option, ArrayList<String> searchList) {

    }

    public void searchTree(SearchTreeOption option, ArrayList<LocalDateTime> searchList) {

    }

    @Override
    public void visitTask(Task task) {
        //TODO
    }

    @Override
    public void visitProject(Project project) {
        //TODO
    }

    @Override
    public void visitInterval(Interval interval) {
        //TODO
    }
}
