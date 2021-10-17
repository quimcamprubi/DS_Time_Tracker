import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws
            InterruptedException {
        // create and start the clock
        //...

        Clock clock = Clock.getInstance();

        // make a small tree of projects and tasks
        ArrayList<String> tags= new ArrayList<String>();
        tags.add("tag1");
        tags.add("tag2");

        Project root = new Project("root", tags,null);
        Project p1 = new Project("P1", tags, root);
        Project p2 = new Project("P2", tags, root);
        Task t1 = new Task("T1", tags, root);
        Task t2 = new Task("T2", tags, p1);
        Task t3 = new Task("T3", tags, p2);

        // make the printer
        PrintTree printer = PrintTree.getInstance(root);

        // the printer will periodically print the whole tree
        // from now on

        // test it
        Thread.sleep(4000);
        // this will make some intervals
        t1.start();
        Thread.sleep(4000);
        t2.start();
        Thread.sleep(2000);
        t1.stop();
        Thread.sleep(2000);
        t2.stop();

        // optionally, stop the clock
        // ...
    }
}
