package behaviour;

import agents.Taxi;
import city.DropoffPoint;
import city.Passenger;
import city.Request;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.misc.Activity;
import utils.simulation.StdRandom;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;

/**
 * Created by eduardosalazar1 on 6/12/16.
 */
public class BidBehaviour extends CyclicBehaviour {
    Taxi agent;

    public BidBehaviour(Taxi taxi) {
        agent = taxi;
    }

    public void action() {
        ACLMessage msg = agent.receive();
        Request request = null;
        if (msg != null) {
            // Message received. Process it
            ByteArrayInputStream bis = new ByteArrayInputStream(msg.getByteSequenceContent());
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                request = ((Request)in.readObject());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ACLMessage reply = msg.createReply();
            switch (msg.getPerformative()) {
                case ACLMessage.CFP:
                    Request bid = agent.bid(request);//THis should have the bid value

                    if (agent.activity == Activity.ON_DUTY) {
                        //Calculate biding
                        if (bid != null) {
                            // The bid is available . Reply with the value
                            reply.setPerformative(ACLMessage.PROPOSE);
                            try {
                                reply.setContentObject(bid);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            // Error bidding
                            reply.setPerformative(ACLMessage.REFUSE);
                            reply.setContent("not - available");
                        }
                    } else {
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent("not - available");
                    }
                    break;
                case ACLMessage.ACCEPT_PROPOSAL:
                    // HERE CODE WHEN TAXI IS TAKING THE JOB
                    // Use object Request to get information and create Passenger
                    Passenger p = new Passenger(request.origin,request.passengerID);
                    this.agent.addPassenger(p);
                    this.agent.confirmed_request = request;
                    this.agent.currentPassenger = p;
                    this.agent.passengerHistory.add(p);
                    this.agent.destination = request.destination;
                    this.agent.addBehaviour(new LocationBehaviour(new DropoffPoint(this.agent.currentLocation.index),request.destination,this.agent, this.agent.runtime));
                    System.out.println("Taxi " + agent.getName() + " job taked");
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent("not - available");
                    break;
            }

            agent.send(reply);
        }
    }

//    public Integer calculatePrice() {
//        return StdRandom.uniform(0, 100);
//    }
}