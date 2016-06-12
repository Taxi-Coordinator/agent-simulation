package city;

import utils.ds.DoublingQueue;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Intersection implements Serializable{
    public int calls = 0;
    public int index;
    public DoublingQueue<Passenger> pendingJobs;
    public ArrayList<Passenger> completedJobs;
    public ArrayList<Passenger> passengerHistory;
    public List<Integer> connections = new ArrayList<Integer>();

    public Intersection(int index, List<Integer> connections) {
        this.index = index;
        this.calls = 0;
        this.connections = connections;
        this.pendingJobs = new DoublingQueue<>();
        this.completedJobs = new ArrayList<>();
        this.passengerHistory = new ArrayList<>();
    }

    public Intersection() {
        this.index = -1;
        this.calls = 0;
        this.pendingJobs = new DoublingQueue<>();
        this.completedJobs = new ArrayList<>();
        this.passengerHistory = new ArrayList<>();
    }


    public void receiveCall(Passenger passenger) {
        this.calls++;
        this.pendingJobs.enqueue(passenger);
        this.passengerHistory.add(passenger);
    }

    public Passenger pickupPassenger() {
        Passenger passenger = this.pendingJobs.dequeue();
        return passenger;
    }

    public void completeJob(Passenger passenger) {
        this.completedJobs.add(passenger);
    }

    @Override
    public String toString() {
        return "Intersection{" +
                "calls=" + calls +
                ", index=" + index +
                ", pendingJobs=" + pendingJobs.toString() +
                ", completedJobs=" + completedJobs +
                ", passengerHistory=" + passengerHistory +
                ", connections=" + connections +
                '}';
    }
}
