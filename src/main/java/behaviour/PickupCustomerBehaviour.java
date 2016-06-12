package behaviour;

import agents.Taxi;
import city.Intersection;
import jade.core.behaviours.Behaviour;

/**
 * Created by jherez on 6/12/16.
 */
public class PickupCustomerBehaviour extends Behaviour {

    public Intersection origin;
    public boolean pickup = false;
    public Taxi taxi;


    public PickupCustomerBehaviour(Taxi taxi) {
        this.taxi = taxi;
    }

    @Override
    public void action(){
        if(!pickup) {
            System.out.println("Taxi " + taxi.index + " - picked up customer");
            pickup = true;
        }
    }
    @Override
    public boolean done(){
        return pickup;
    }
}
