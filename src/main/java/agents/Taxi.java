package agents;
import city.*;
import utils.agentMethods.TaxiMethods;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import utils.misc.Activity;
import utils.misc.Shift;
import utils.shortestPath.DijkstraUndirectedSP;

import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Created by jherez on 6/11/16.
 */
public class Taxi extends Agent {
    public City vCity;
    public DropoffPoint currentLocation;
    public DropoffPoint destination;
    public ArrayList<Passenger> passengerHistory;
    public Passenger currentPassenger;
    public Shift shift;
    public Activity activity;
    public Request confirmed_request;
    public ArrayList<Request> requests;
    public Path route;
    public ArrayList<Path> routeHistory;
    public DijkstraUndirectedSP pickup_sp;
    public DijkstraUndirectedSP dropOff_sp;

    protected void setup() {
        Object[] args = getArguments();
        this.vCity = (City)args[0];
        this.currentLocation = (DropoffPoint)args[1];
        this.shift = (Shift)args[2];
        this.activity = activity.INIT;
        this.passengerHistory = new ArrayList<>();
        this.routeHistory = new ArrayList<>();
        this.requests = new ArrayList<>();
        this.route = null;
        this.currentPassenger = null;
        this.destination = null;
        System.out.println("Taxi-agent " +getAID().getName()+ "is online");
    }

    protected void takeDown() {
        System.out.println("Taxi-agent " +getAID().getName()+ "is offline");
        // Make this agent terminate
        doDelete();
    }

    public void clear(){
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

    public void testFunctionality(){
        Intersection customerLocation = vCity.intersections.get(1);

        this.destination = new DropoffPoint(10);
        System.out.println("Current Taxi Location "+this.currentLocation.index);
        confirmed_request = new Request(customerLocation,this.destination,0);
        System.out.println("Customer Destination "+this.destination.index);
        System.out.println("Distance "+TaxiMethods.getJobDistance(this.vCity,this.currentLocation,confirmed_request));
    }
}
