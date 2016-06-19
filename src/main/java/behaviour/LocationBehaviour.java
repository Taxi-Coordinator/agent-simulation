package behaviour;

import agents.Taxi;
import city.DropoffPoint;
import jade.core.behaviours.Behaviour;
import utils.agentMethods.TaxiMethods;
import utils.misc.Activity;
import utils.shortestPath.DijkstraUndirectedSP;
import utils.shortestPath.Edge;
import utils.shortestPath.Path;
import utils.simulation.Timer;

/**
 * Created by jherez on 6/12/16.
 */
class LocationBehaviour extends Behaviour {
    private final Taxi agent;
    private final DropoffPoint destination;
    private final Timer timer;
    private double jobTime;
    private final int initTime;


    public LocationBehaviour(DropoffPoint origin, DropoffPoint destination, Taxi taxi, Timer runtime) {
        this.timer = runtime;
        this.initTime = TaxiMethods.timeToSecond(runtime.getDate());
        this.agent = taxi;
        this.destination = destination;
        DijkstraUndirectedSP sp = this.agent.vCity.getShortestPaths(this.agent.vCity.G, origin.index);
        Path path = new Path();
        path.w = origin.index;
        path.v = destination.index;
        path.weight = sp.distTo(destination.index);
        for (Edge e : sp.pathTo(destination.index)) {
            path.list.add(e);
        }
        this.agent.activity = Activity.TRANSPORTING_PASSENGER;
        String msg = "(" + agent.runtime.toString() + ") ---> Taxi " + this.agent.getLocalName() + " travelling from " + origin.index;
        msg += " to " + destination.index + " via " + path.list.toString();
        msg += " for a distance of " + path.weight;

        System.out.println(msg);
        this.jobTime = TaxiMethods.getTotalTravelDistance(this.agent.vCity, this.agent.currentLocation, this.agent.confirmed_request);
        this.jobTime = (int) ((this.jobTime / TaxiMethods.SPEED) * 60 * 60);
    }

    @Override
    public void action() {
        if (TaxiMethods.timeToSecond(this.timer.getDate()) >= this.initTime + this.jobTime) {
            this.agent.activity = Activity.JUST_WON_BID;
            this.agent.destination = this.destination;
            this.agent.currentLocation = this.destination;
            this.agent.currentPassenger = null;
        }
    }

    @Override
    public boolean done() {
        if (this.agent.currentLocation == this.destination) {
            System.out.println("(" + agent.runtime.toString() + ") <--- Taxi " + agent.getLocalName() + ": Arrived at " + this.destination.index);
            return true;
        }
        return false;
    }

}
