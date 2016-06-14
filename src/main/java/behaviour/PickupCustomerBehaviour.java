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
    public Passenger passenger;


    public PickupCustomerBehaviour(Taxi taxi, Request request, Passenger passenger) {
        this.agent = taxi;
        this.request = request;
        this.passenger = passenger;
    }

    @Override
    public void action(){
        if(!pickup) {
            System.out.println("("+agent.runtime.toString()+") <--- Taxi " + agent.getLocalName() + ": Picked up Passenger " + this.passenger.id);
            pickup = true;
            this.agent.addPassenger(passenger);
            this.agent.confirmed_request = request;
            this.agent.currentPassenger = passenger;
            this.agent.passengerHistory.add(passenger);
            this.agent.destination = request.destination;
        }
    }
    @Override
    public boolean done(){
        this.agent.addBehaviour(new LocationBehaviour(new DropoffPoint(this.agent.currentLocation.index),request.destination,this.agent, this.agent.runtime));
        return pickup;
    }
}
