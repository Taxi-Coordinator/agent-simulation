package city;

import utils.Helper;
import java.util.ArrayList;
import java.util.Date;
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
                "nextTime=" + nextTime +
                ", calls=" + calls +
                ", index=" + index +
                ", connections=" + connections +
                '}';
    }

}
