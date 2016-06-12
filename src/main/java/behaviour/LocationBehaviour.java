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
public class LocationBehaviour extends Behaviour{
    public Taxi taxi;
    public DropoffPoint origin;
    public DropoffPoint destination;
    public Path path;
    DijkstraUndirectedSP sp;

    public LocationBehaviour(DropoffPoint origin, DropoffPoint destination, Taxi taxi){
        this.taxi = taxi;
        this.origin = origin;
        this.destination = destination;
        sp = this.taxi.vCity.getShortestPaths(this.taxi.vCity.G,origin.index);

        this.path.w = origin.index;
        this.path.v = destination.index;
        this.path.weight = sp.distTo(destination.index);
        for (Edge e : sp.pathTo(destination.index)) {
            this.path.list.add(e);
        }

        String msg = "Taxi " + this.taxi.index + " travelling from " + this.origin.index;
        msg += " to " + destination.index + " via " + this.path.list.toString();
        msg += " for a distance of " + this.path.weight;

        System.out.println(msg);
    }

    @Override
    public void action(){
        for(Edge e : this.path.list) {
            System.out.println(e);
        }
        this.taxi.destination = this.destination;
        this.taxi.currentLocation = this.destination;
        this.taxi.activity = Activity.TRANSPORTING_PASSENGER;
    }

    @Override
    public boolean done() {
        if( this.taxi.currentLocation == this.destination ){
            System.out.println("Taxi " + taxi.index + ": Arrived at " + this.destination );
            return true;
        }
        return false;
    }


}
