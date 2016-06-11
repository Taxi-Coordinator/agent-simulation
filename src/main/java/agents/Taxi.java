package agents;
import city.City;
import city.DropoffPoint;
import city.Intersection;
import city.Passenger;
import jade.core.Agent;
import utils.misc.Activity;
import utils.misc.Shift;

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
    public ArrayList<Path> routeHistory;

    @Override
    protected void setup() {
        Object[] args = getArguments();
        this.vCity = (City)args[0];
        this.currentLocation = (DropoffPoint)args[1];
        this.shift = (Shift)args[2];
        this.activity = activity.INIT;
        this.passengerHistory = new ArrayList<>();
        this.routeHistory = new ArrayList<>();
        this.route = null;
        this.currentPassenger = null;
        this.destination = null;
        System.out.println("Taxi-agent " +getAID().getName()+ "is online");
    }

    @Override
    protected void takeDown() {
        System.out.println("Taxi-agent " +getAID().getName()+ "is offline");
    }
}
