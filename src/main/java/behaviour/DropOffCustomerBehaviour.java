package behaviour;

import agents.Taxi;
import city.DropoffPoint;
import city.Intersection;
import jade.core.behaviours.Behaviour;

/**
 * Created by jherez on 6/12/16.
 */
public class DropOffCustomerBehaviour extends Behaviour {
    public Intersection destination;
    public DropoffPoint dest;
    public boolean endjob = false;
    public Taxi taxi;


    public DropOffCustomerBehaviour(Taxi taxi) {
        this.taxi = taxi;
    }

    @Override
    public void action(){
        if(!endjob) {
            System.out.println("Taxi " + taxi.index + " - dropped off customer");
            endjob = true;
            if(taxi.vCity.isIntersection(taxi.confirmed_request.destination.index)){
                destination = taxi.vCity.intersections.get(taxi.confirmed_request.destination.index);
                destination.completeJob(taxi.currentPassenger);
            }
            else {
                dest = taxi.vCity.dropoffPoints.get(taxi.confirmed_request.destination.index);
                dest.completeJob(taxi.currentPassenger);
            }
            // Add code to update company profit
        }
    }
    @Override
    public boolean done(){
        return endjob;
    }
}
