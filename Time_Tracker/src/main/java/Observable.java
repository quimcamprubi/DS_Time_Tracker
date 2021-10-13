import java.util.ArrayList;

public class Observable {
    private ArrayList<Observer> observers;


    public void registerObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void removeObserver(Observer observer) {
        this.observers.remove(observer);
    }

    public void notifyObserver(Observer observer) {
        //TODO
    }
}
