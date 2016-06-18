package behaviour;

import agents.Taxi;
import city.DropoffPoint;
import city.Passenger;
import city.Request;
import jade.core.behaviours.Behaviour;

/**
 * Created by jherez on 6/15/16.
 */
public class ProcessRequestsBehaviour extends Behaviour {
    public boolean endjob = false;
    public Taxi agent;
    public Request request;


    public ProcessRequestsBehaviour(Taxi taxi) {
        this.agent = taxi;
        this.request = taxi.last_request;
    }

    @Override
    public void action() {
        if (!endjob) {
            Passenger passenger = new Passenger(this.request.origin, this.request.passengerID);
            System.out.println("(" + agent.runtime.toString() + ") <--- Taxi " + agent.getLocalName() + ": Processing Passenger " + passenger.id);
            this.agent.addPassenger(passenger);
            this.agent.confirmed_request = this.request;
            this.agent.currentPassenger = passenger;
            this.agent.destination = this.request.destination;
            this.agent.addBehaviour(new LocationBehaviour(new DropoffPoint(this.agent.currentLocation.index), this.request.destination, this.agent, this.agent.runtime));
            this.agent.confirmed_request = null;
            System.out.println("(" + agent.runtime.toString() + ") <--- Taxi " + agent.getLocalName() + ": Finished job for Passenger " + passenger.id);
        }
        endjob = true;
    }

    @Override
    public boolean done() {
        return endjob;
    }
}
