package agents;
import city.*;
import jade.core.Agent;
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
    public Path route;
    public Shift shift;
    public Activity activity;
    public ArrayList<Request> requests;
    public Request confirmed_request;
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
        Intersection test = vCity.intersections.get(23);

        this.destination = new DropoffPoint(8);
        System.out.println("Current Location "+this.currentLocation);
        confirmed_request = new Request(test,this.destination,0);
        System.out.println("Distance "+this.destination);
        System.out.println("Distance "+getJobDistance(this.currentLocation,confirmed_request));
    }

    protected void takeDown() {
        System.out.println("Taxi-agent " +getAID().getName()+ "is offline");
        // Make this agent terminate
        doDelete();
    }

    public double getJobDistance(DropoffPoint currentLocation, Request request) {
        double distance = 0;
        DijkstraUndirectedSP pickup_sp = vCity.getShortestPaths(vCity.G,currentLocation.index);
        DijkstraUndirectedSP dropOff_sp = vCity.getShortestPaths(vCity.G,request.destination.index);
        distance += pickup_sp.distTo(request.origin.index);
        distance += dropOff_sp.distTo(request.destination.index);
        return distance;
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
}
