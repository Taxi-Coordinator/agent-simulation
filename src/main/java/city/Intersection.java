package city;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Intersection implements Serializable{
    private int calls = 0;
    public int index;
    private final ArrayList<Passenger> passengerHistory;
    public List<Integer> connections = new ArrayList<Integer>();

    public Intersection(int index, List<Integer> connections) {
        this.index = index;
        this.calls = 0;
        this.connections = connections;
        this.passengerHistory = new ArrayList<>();
    }

    public Intersection() {
        this.index = -1;
        this.calls = 0;
        this.passengerHistory = new ArrayList<>();
    }


    public void receiveCall(Passenger passenger) {
        this.calls++;
        this.passengerHistory.add(passenger);
    }


    @Override
    public String toString() {
        return "Intersection{" +
                "calls=" + calls +
                ", index=" + index +
                ", passengerHistory=" + passengerHistory +
                ", connections=" + connections +
                '}';
    }
}
