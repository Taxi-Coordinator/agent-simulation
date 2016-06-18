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


    public ProcessRequestsBehaviour(Taxi taxi) {
        this.agent = taxi;
    }

    @Override
    public void action() {
        if (!endjob) {
            for (Request r : this.agent.requests) {
                Passenger passenger = new Passenger(r.origin, r.passengerID);
                System.out.println("(" + agent.runtime.toString() + ") <--- Taxi " + agent.getLocalName() + ": Processing Passenger " + passenger.id);
                this.agent.addPassenger(passenger);
                this.agent.confirmed_request = r;
                this.agent.currentPassenger = passenger;
                this.agent.destination = r.destination;
                this.agent.addBehaviour(new LocationBehaviour(new DropoffPoint(this.agent.currentLocation.index), r.destination, this.agent, this.agent.runtime));
                this.agent.requests.dequeue();
                System.out.println("(" + agent.runtime.toString() + ") <--- Taxi " + agent.getLocalName() + ": Finished job for Passenger " + r.passengerID + " Backlog = " + agent.requests.size());
            }
        }
        endjob = true;
    }

    @Override
    public boolean done() {
        return endjob;
    }
}
