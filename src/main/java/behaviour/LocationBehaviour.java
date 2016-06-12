package behaviour;

import agents.Taxi;
import city.DropoffPoint;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import utils.agentMethods.TaxiMethods;
import utils.misc.Activity;
import utils.shortestPath.DijkstraUndirectedSP;
import utils.shortestPath.Edge;
import utils.shortestPath.Path;
import utils.simulation.Timer;

/**
 * Created by jherez on 6/12/16.
 */
public class LocationBehaviour extends Behaviour {
    public Taxi agent;
    public DropoffPoint origin;
    public DropoffPoint destination;
    public Path path = new Path();
    DijkstraUndirectedSP sp;
    public Timer timer;
    public double jobTime;
    public int initTime;

    public LocationBehaviour(DropoffPoint origin, DropoffPoint destination, Taxi taxi, Timer runtime) {
        this.timer = runtime;
        this.initTime = runtime.getSecond();
        this.agent = taxi;
        this.origin = origin;
        this.destination = destination;
        sp = this.agent.vCity.getShortestPaths(this.agent.vCity.G, origin.index);
        this.path.w = origin.index;
        this.path.v = destination.index;
        this.path.weight = sp.distTo(destination.index);
        for (Edge e : sp.pathTo(destination.index)) {
            this.path.list.add(e);
        }
        this.agent.activity = Activity.TRANSPORTING_PASSENGER;
        String msg = "("+agent.runtime.toString()+")  Taxi " + this.agent.getLocalName() + " travelling from " + this.origin.index;
        msg += " to " + destination.index + " via " + this.path.list.toString();
        msg += " for a distance of " + this.path.weight;

        System.out.println(msg);
        this.jobTime = TaxiMethods.getTotalJobDistance(this.agent.vCity,this.agent.currentLocation,this.agent.confirmed_request);
        this.jobTime = (int) (this.jobTime / TaxiMethods.SPEED);
        this.jobTime = this.jobTime * 100 * 60;
    }

    @Override
    public void action() {
        if(this.timer.getSecond() >= this.initTime + this.jobTime) {
            this.agent.activity = Activity.WAITING_FOR_JOB;
            this.agent.destination = this.destination;
            this.agent.currentLocation = this.destination;
            this.agent.currentPassenger = null;
        }
    }

    @Override
    public boolean done() {
        if (this.agent.currentLocation == this.destination) {
            System.out.println("("+agent.runtime.toString()+")  Taxi " + agent.getLocalName() + ": Arrived at " + this.destination.index);
            return true;
        }
        return false;
    }

}
