package city;

import java.util.ArrayList;
import java.util.List;

public class Intersection {
    public int calls = 0;
    public int index;

    public List<Integer> connections = new ArrayList<Integer>();

    public Intersection(int index, List<Integer> connections) {
        this.index = index;
        this.connections = connections;
    }

    public Intersection() {
    }


    public void receiveCall() {
        this.calls++;
    }

    @Override
    public String toString() {
        return "Intersection{" +
                ", calls=" + calls +
                ", index=" + index +
                ", connections=" + connections +
                '}';
    }

}
