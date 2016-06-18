package agents;

import behaviour.BidBehaviour;
import behaviour.CheckStateBehavior;
import city.*;
import utils.agentMethods.TaxiMethods;
import jade.core.Agent;
import utils.ds.DoublingQueue;
import utils.misc.Activity;
import utils.misc.Shift;
import utils.simulation.Timer;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Calendar;

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
    public boolean on_duty;
    public Request confirmed_request;
    public Request last_request;
    public DoublingQueue<Request> requests;
    public Path route;
    public boolean won_last_round;
    public int time_of_list_win;
    public ArrayList<Path> routeHistory;
    public Timer runtime;

    protected void setup() {
        Object[] args = getArguments();
        this.vCity = (City) args[0];
        this.currentLocation = (DropoffPoint) args[1];
        this.shift = (Shift) args[2];
        this.index = (Integer) args[3];
        this.runtime = (Timer) args[4];
        this.activity = Activity.WAITING_FOR_JOB;
        this.passengerHistory = new ArrayList<>();
        this.routeHistory = new ArrayList<>();
        this.requests = new DoublingQueue<>();
        this.route = null;
        this.currentPassenger = null;
        this.destination = null;
        this.won_last_round = false;
        this.time_of_list_win = 0;
        System.out.println("Taxi-agent " + getAID().getName() + "is online");
//        testFunctionality();
        this.addBehaviour(new CheckStateBehavior(this));
        this.addBehaviour(new BidBehaviour(this));
    }

    protected void takeDown() {
        System.out.println("Taxi-agent " + getAID().getName() + "is offline");
        // Make this agent terminate
        doDelete();
    }

    public int getElapsed() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(runtime.getDate());
        int hours = cal.get(Calendar.HOUR_OF_DAY);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);

        return (hours * 60 * 60) + (minutes * 60) + seconds;
    }

    public void checkStatus() {
        int elapsed = getElapsed();
        boolean stat = getShitfStatus(elapsed);
        if (stat) {
            if (!this.on_duty) {
                this.on_duty = true;
                this.activity = Activity.WAITING_FOR_JOB;
            }
        } else {
            if (this.on_duty) {
                if (this.activity == Activity.WAITING_FOR_JOB) {
                    this.on_duty = false;
                    this.activity = Activity.SHIFT_FINISHED;
                    this.currentLocation = this.vCity.dropoffPoints.get(vCity.taxiCenter);
                }
            }
        }
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
    }

    public void addPassenger(Passenger passenger) {
        this.currentPassenger = passenger;
        this.passengerHistory.add(passenger);
    }

    public void addRequestToQueue(Request request) {
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
        System.out.println("Distance " + TaxiMethods.getTotalTravelDistance(this.vCity, this.currentLocation, confirmed_request));
    }

    public Request bid(Request request) {

        request.bid = TaxiMethods.getBid(this.vCity, this, this.currentLocation, request);
        return request;
    }
}
