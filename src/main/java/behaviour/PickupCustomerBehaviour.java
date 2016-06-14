package behaviour;

import agents.Taxi;
import city.DropoffPoint;
import city.Intersection;
import city.Passenger;
import city.Request;
import jade.core.behaviours.Behaviour;

/**
 * Created by jherez on 6/12/16.
 */
public class PickupCustomerBehaviour extends Behaviour {

    public boolean pickup = false;
    public Taxi agent;
    public Request request;


    public PickupCustomerBehaviour(Taxi taxi, Request request) {
        this.agent = taxi;
        this.request = request;
    }

    @Override
    public void action() {
        if (!pickup) {
            this.agent.addRequestToQueue(request);
            System.out.println("(" + agent.runtime.toString() + ") <--- Taxi " + agent.getLocalName() + ": Adding Passenger " + this.request.passengerID + " to queue. Backlog = " + agent.requests.size());
            pickup = true;
        }
    }

    @Override
    public boolean done() {
        this.agent.addBehaviour(new ProcessRequestsBehaviour(this.agent));
        return pickup;
    }
}
