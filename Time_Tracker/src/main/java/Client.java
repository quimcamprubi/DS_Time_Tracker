import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws
            InterruptedException {
        // create and start the clock
        //...

        Clock clock = Clock.getInstance();

        // make a small tree of projects and tasks


        Project root = new Project("root", new ArrayList<String>(),null);
        Project p1 = new Project("P1", new ArrayList<String>(), root);
        Project p2 = new Project("P2", new ArrayList<String>(), root);
        Task t1 = new Task("T1", new ArrayList<String>(), root);
        Task t2 = new Task("T2", new ArrayList<String>(), p1);
        Task t3 = new Task("T3", new ArrayList<String>(), p2);

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
