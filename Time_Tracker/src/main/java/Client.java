import java.util.ArrayList;
import java.util.Arrays;

public class Client {
    public static void main(String[] args) throws
            InterruptedException {
        // create and start the clock
        //...

        Clock clock = Clock.getInstance();


        // make a small tree of projects and tasks
        Project root = new Project("software design",  new ArrayList<String>( Arrays.asList("java", "flutter") ),null);
        Project p1 = new Project("software testing",  new ArrayList<String>( Arrays.asList("c++", "Java", "python") ), null);
        Project p2 = new Project("databases",  new ArrayList<String>( Arrays.asList("SQL", "python", "C++") ), null);
        Task t1 = new Task("transportation", new ArrayList<String>(), null);
        Project p3= new Project("problems", new ArrayList<String>(), root);
        Project p4= new Project("project time tracker", new ArrayList<String>(), root);

        Task t2 = new Task("first list",  new ArrayList<String>( Arrays.asList("java") ), p3);
        Task t3 = new Task("second list",  new ArrayList<String>( Arrays.asList("Dart") ), p3);

        Task t4 = new Task("read handout", new ArrayList<String>(), p4);
        Task t5 = new Task("first milestone",  new ArrayList<String>( Arrays.asList("Java", "IntelliJ") ), p4);

        // make the printer
        PrintTree printer = PrintTree.getInstance(root);

        // the printer will periodically print the whole tree
        // from now on

        // test it
        Thread.sleep(2000);

        t1.start();
        Thread.sleep(4000);
        t1.stop();

        Thread.sleep(2000);

        t2.start();
        Thread.sleep(6000);

        t3.start();
        Thread.sleep(4000);

        t2.stop();

        Thread.sleep(2000);
        t3.stop();

        Thread.sleep(2000);

        t1.start();
        Thread.sleep(4000);
        t1.stop();

        // optionally, stop the clock
        // ...
    }
}
