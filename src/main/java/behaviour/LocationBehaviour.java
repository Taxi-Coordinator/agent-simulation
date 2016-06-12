package behaviour;

import agents.Taxi;
import city.DropoffPoint;
import jade.core.behaviours.Behaviour;
import utils.misc.Activity;
import utils.shortestPath.DijkstraUndirectedSP;
import utils.shortestPath.Edge;
import utils.shortestPath.Path;

/**
 * Created by jherez on 6/12/16.
 */
public class LocationBehaviour extends Behaviour {
    public Taxi agent;
    public DropoffPoint origin;
    public DropoffPoint destination;
    public Path path = new Path();
    DijkstraUndirectedSP sp;

    public LocationBehaviour(DropoffPoint origin, DropoffPoint destination, Taxi taxi) {
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

        String msg = "Taxi " + this.agent.index + " travelling from " + this.origin.index;
        msg += " to " + destination.index + " via " + this.path.list.toString();
        msg += " for a distance of " + this.path.weight;

        System.out.println(msg);
    }

    @Override
    public void action() {
//        for(Edge e : this.path.list) {
//            System.out.println(e);
//        }
        this.agent.activity = Activity.TRANSPORTING_PASSENGER;
        this.agent.destination = this.destination;
        this.agent.currentLocation = this.destination;
    }

    @Override
    public boolean done() {
        if (this.agent.currentLocation == this.destination) {
            System.out.println("Taxi " + agent.index + ": Arrived at " + this.destination);
            return true;
        }
        return false;
    }

}
