package behaviour;

import agents.Taxi;
import city.City;
import city.Request;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import utils.agentMethods.TaxiMethods;
import utils.misc.Activity;

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
                request = ((Request) in.readObject());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ACLMessage reply = msg.createReply();
            switch (msg.getPerformative()) {
                case ACLMessage.CFP:

                    //waiting for jobs, transporting passenger, travelling to passenger
                    if (agent.activity == Activity.WAITING_FOR_JOB || agent.activity == Activity.JUST_WON_BID
                            || agent.activity == Activity.TRANSPORTING_PASSENGER
                            || agent.activity == Activity.TRAVELING_TO_PASSENGER) {


                        if (getBidAvailability(this.agent, request)) {
                            Request bid = agent.bid(request);//THis should have the bid value
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
                                reply.setContent("Not Available");
                            }
                        } else {
                            reply.setPerformative(ACLMessage.REFUSE);
                            reply.setContent(agent.activity.name());
                        }
                    } else {
                        reply.setPerformative(ACLMessage.REFUSE);
                        reply.setContent(agent.activity.name());
                    }
                    break;
                case ACLMessage.ACCEPT_PROPOSAL:
                    // HERE CODE WHEN TAXI IS TAKING THE JOB
                    this.agent.won_last_round = true;
                    this.agent.time_of_list_win = TaxiMethods.timeToSecond(this.agent.runtime.getDate());
                    this.agent.addBehaviour(new PickupCustomerBehaviour(this.agent, request));
                    reply.setPerformative(ACLMessage.CONFIRM);
                    reply.setContent("Not Available");
                    break;
            }

            agent.send(reply);
        }
    }

    /**
     * This method checks if an agent can participate in a bid. It considers
     * the time that has elapsed since this agent won the last bid and uses the restriction
     * travel time calculated from &distance; / &SPEED;. If this time has not elapsed the agent
     * cannot bid. It also checks whether an agent will still be on duty within the timeframe
     * of the request. If the request would cause the agent to go beyond the bounds of their shift
     * then they cannot bid
     *
     * @param taxi    Taxi see {@link Taxi}
     * @param request Request see {@link Request}
     * @return true when the above conditions are met
     */
    public boolean getBidAvailability(Taxi taxi, Request request) {
        boolean result = false;
        boolean can_bid = true;
        if (taxi.getShitfStatus(taxi.getElapsed())) {
            int jobTime = TaxiMethods.getJobCompletionTime(this.agent.vCity, this.agent, request);
            result = taxi.getShitfStatus((jobTime * 60 * 60) + TaxiMethods.timeToSecond(taxi.runtime.getDate()));
        }
        int time_for_last_distance = (int) ((City.last_req_distance / TaxiMethods.SPEED) * 60 * 60);
        if (TaxiMethods.timeToSecond(taxi.runtime.getDate()) < this.agent.time_of_list_win + time_for_last_distance) {
            can_bid = false;
        } else {
            taxi.activity = Activity.WAITING_FOR_JOB;
        }

        return (can_bid && result);
    }
}
