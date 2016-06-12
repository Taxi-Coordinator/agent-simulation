package agents;

import behaviour.BidBehaviour;
import city.*;
import utils.agentMethods.TaxiMethods;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import utils.ds.DoublingQueue;
import utils.misc.Activity;
import utils.misc.Shift;
import utils.shortestPath.DijkstraUndirectedSP;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by jherez on 6/11/16.
 */
public class Taxi extends Agent {
    public int index;
    public City vCity;
    public DropoffPoint currentLocation;
    public DropoffPoint destination;
    public ArrayList<Passenger> passengerHistory;
    public Passenger currentPassenger;
    public Shift shift;
    public Activity activity;
    public Request confirmed_request;
    public Request last_request;
    public DoublingQueue<Request> requests;
    public Path route;
    public ArrayList<Path> routeHistory;
    public DijkstraUndirectedSP pickup_sp;
    public DijkstraUndirectedSP dropOff_sp;

    protected void setup() {
        Object[] args = getArguments();
        this.vCity = (City) args[0];
        this.currentLocation = (DropoffPoint) args[1];
        this.shift = (Shift) args[2];
        this.index = (Integer) args[3];
        this.activity = activity.INIT;
        this.passengerHistory = new ArrayList<>();
        this.routeHistory = new ArrayList<>();
        this.requests = new DoublingQueue<>();
        this.route = null;
        this.currentPassenger = null;
        this.destination = null;
        System.out.println("Taxi-agent " + getAID().getName() + "is online");
//        testFunctionality();

        this.addBehaviour( new BidBehaviour(this));
    }

    protected void takeDown() {
        System.out.println("Taxi-agent " + getAID().getName() + "is offline");
        // Make this agent terminate
        doDelete();
    }

    public boolean getShitfStatus(int seconds) {
        seconds = (seconds % (60 * 60 * 24));
        boolean on_duty = false;

        switch (this.shift) {
            case TIME_3AM_TO_1PM:
                on_duty = (seconds >= 3 * 3600 && seconds <= 13 * 3600);
                break;
            case TIME_6PM_TO_4AM:
                on_duty = !(seconds >= 4 * 3600 && seconds <= 18 * 3600);
                break;
            case TIME_9AM_TO_7PM:
                on_duty = (seconds >= 9 * 3600 && seconds <= 19 * 3600);
                break;
        }
        return on_duty;
    }

    public void clear() {
        this.index = -1;
        this.vCity = null;
        this.currentLocation = null;
        this.destination = null;
        this.passengerHistory = null;
        this.currentPassenger = null;
        this.route = null;
        this.shift = null;
        this.activity = null;
        this.requests = null;
        this.routeHistory = null;
        this.pickup_sp = null;
        this.dropOff_sp = null;
    }

    public void addPassenger(Passenger passenger){
        this.currentPassenger = passenger;
        this.passengerHistory.add(passenger);
    }

    public void addRequestToQueue(Request request){
        this.requests.enqueue(request);
        this.last_request = request;
    }

    public void testFunctionality() {
        Intersection customerLocation = vCity.intersections.get(1);

        this.destination = new DropoffPoint(10);
        System.out.println("Taxi Index " + this.index);
        System.out.println("Current Taxi Location " + this.currentLocation.index);
        confirmed_request = new Request(customerLocation, this.destination, 0);
        System.out.println("Customer Destination " + this.destination.index);
        System.out.println("Distance " + TaxiMethods.getJobDistance(this.vCity, this.currentLocation, confirmed_request));
    }
}
