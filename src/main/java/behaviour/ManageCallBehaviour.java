package behaviour;

import agents.TaxiCoordinator;
import city.*;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import utils.misc.Activity;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.util.ArrayList;

/**
 * This class Handle the Call Generation and send the request to taxis for auction
 */
public class ManageCallBehaviour extends Behaviour {
    private AID bestTaxi; // The agent who provides the best offer
    private double bestPrice; // The best offered price
    private int repliesCnt = 0; // The counter of replies from seller agents
    private MessageTemplate mt; // The template to receive replies
    private Activity activity = Activity.WAITING_FOR_CALLS;
    private final TaxiCoordinator agent;
    private Request lastBestRequest;
    private final ArrayList<Request> biddingList = new ArrayList<>();

    public ManageCallBehaviour(TaxiCoordinator coordinator) {
        agent = coordinator;
    }

    private void nextCall() {
        agent.nextTime = agent.nextCall(agent.runtime.getDate());
    }

    @SuppressWarnings("InfiniteLoopStatement")
    public void action() {
        //noinspection InfiniteLoopStatement,InfiniteLoopStatement,InfiniteLoopStatement,InfiniteLoopStatement,InfiniteLoopStatement,InfiniteLoopStatement,InfiniteLoopStatement
        for (int t = 0; true; t++) {
            agent.runtime.tick();
            try {
                Thread.sleep(1);
            } catch (Exception ignored) {
            }

            // 2 . Waiting for next call
            if (activity == Activity.WAITING_FOR_CALLS) {
                if (agent.isCallAvailable(agent.nextTime, agent.runtime.getDate())) {
                    // 3. Pick Random Node but not taxi center
                    int[] exclude = {agent.vCity.taxiCenter};
                    int nextIndex = agent.pickRandomIntersectionIndex(agent.vCity.intersections, exclude);
                    Intersection intersection = agent.vCity.intersections.get(nextIndex);

                    // 4. Receive call
                    System.out.println("---------------------------------------------------------------------------------------");
                    Passenger p = new Passenger(intersection, agent.calls++);
                    agent.vCity.totalPassengers++;
                    City.last_req_distance = p.d;
                    agent.receiveCall(p, intersection);
                    // 5. DO ACTION PROCESS HERE

                    // Pick random destination
                    int[] exclude2 = {agent.vCity.taxiCenter, nextIndex};
                    int destination = agent.pickRandomDropoffIndex(agent.vCity.dropoffPoints, exclude2);

                    //System.out.println("("+agent.runtime.toString()+")(Call " + agent.calls + ")");
                    System.out.println("(" + agent.runtime.toString() + ")  Calling from Node " + intersection.index + " to " + destination);
                    agent.out("Call " + intersection.index);

                    // Send Request to available taxi
                    agent.lastRequest = new Request(agent.vCity.intersections.get(nextIndex), new DropoffPoint(agent.vCity.dropoffPoints.get(destination).index), agent.calls);
                    sentRequest();

                    // 6. Set next Time to call. ONly if step is 0 that means that is waiting for call
                    if (activity == Activity.WAITING_FOR_CALLS) {
                        nextCall();
                    }

                }
            } else {
                sentRequest();
            }

        }
    }

    private void sentRequest() {

        switch (activity) {
            case WAITING_FOR_CALLS:
                // Send the cfp to all sellers
                System.out.println("(" + agent.runtime.toString() + ")  Sending request to all agents");
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (int i = 0; i < agent.lstTaxi.size(); ++i) {
                    cfp.addReceiver(agent.lstTaxi.get(i));

                }
                try {
                    cfp.setContentObject(agent.lastRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                cfp.setConversationId("auction");
                cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                agent.send(cfp);
                // Prepare the template to get proposals
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("auction"),
                        MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                activity = Activity.WAITING_FOR_BIDS;
                break;
            case WAITING_FOR_BIDS:
                // Receive all proposals/refusals from seller agents
                ACLMessage reply = agent.receive(mt);
                Request response = null;
                if (reply != null) {

                    // Reply received
                    if (reply.getPerformative() == ACLMessage.PROPOSE) {
                        ByteArrayInputStream bis = new ByteArrayInputStream(reply.getByteSequenceContent());
                        ObjectInput in;
                        try {
                            in = new ObjectInputStream(bis);
                            response = ((Request) in.readObject());
                        } catch (IOException | ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        System.out.println("(" + agent.runtime.toString() + ")  Reply from " + reply.getSender().getLocalName() + " : " + (response != null ? response.bid.payOff : 0) + " NT");
                        // This is an offer

                        assert response != null;
                        response.bidder = reply.getSender();
                        biddingList.add(response);
                    } else {
                        System.out.println("(" + agent.runtime.toString() + ")  Reply from " + reply.getSender().getLocalName() + " : " + reply.getContent() + " NT");
                    }
                    repliesCnt++;
                    if (repliesCnt >= agent.lstTaxi.size()) {
                        processBids();
                        // We received all replies
                        activity = Activity.PROCESSING_BIDS;
                    }
                } else {
                    block();
                }
                break;
            case PROCESSING_BIDS:
                //
                try {
                    Thread.sleep(5);
                } catch (Exception ignored) {
                }
                System.out.println("(" + agent.runtime.toString() + ")  Bid won by " + bestTaxi.getLocalName() + " : " + bestPrice);
                // Sending confirmation to taxi for best offer
                ACLMessage order = new ACLMessage(ACLMessage.ACCEPT_PROPOSAL);
                order.addReceiver(bestTaxi);
                try {
                    order.setContentObject(lastBestRequest);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                order.setConversationId("auction");
                order.setReplyWith("call" + System.currentTimeMillis());
                agent.send(order);
                // Prepare the template to get the purchase order reply
                mt = MessageTemplate.and(MessageTemplate.MatchConversationId("auction"),
                        MessageTemplate.MatchInReplyTo(order.getReplyWith()));

                activity = Activity.WAITING_TAXI_CONFIRMATION;
                //SEND MESSAGE TO CONFIRM BID ACCEPTED
                break;
            case WAITING_TAXI_CONFIRMATION:
                // RESPONSE OF TAXI WITH JOB ALLOCATED
                ACLMessage confirmation = agent.receive(mt);
                if (confirmation != null) {
                    switch (confirmation.getPerformative()) {
                        case ACLMessage.CONFIRM:
                            nextCall();
                            repliesCnt = 0;
                            bestPrice = 0;
                            bestTaxi = null;
                            activity = Activity.WAITING_FOR_CALLS;
                            System.out.println("(" + agent.runtime.toString() + ")  ");
                            break;
                        case ACLMessage.DISCONFIRM:
                            System.out.println("Error allocation job");

                    }
                } else {
                    block();
                }
                break;
        }
    }

    private void processBids() {
        double lowestPayoff, secondLowestPayoff, lowestCo, secondLowestCo;
        lowestPayoff = secondLowestPayoff = Integer.MAX_VALUE;
        lowestCo = secondLowestCo = Integer.MAX_VALUE;
        for (Request r : biddingList) {
            if (r.bid.payOff < lowestPayoff) {
                secondLowestPayoff = lowestPayoff;
                lowestPayoff = r.bid.payOff;
                bestTaxi = r.bidder;
                lastBestRequest = r;
            } else if (r.bid.payOff < secondLowestPayoff && r.bid.payOff != lowestPayoff) {
                secondLowestPayoff = r.bid.payOff;
            }
        }

        for (Request r : biddingList) {
            if (r.bid.company < lowestCo) {
                secondLowestCo = lowestCo;
                lowestCo = r.bid.company;
            } else if (r.bid.company < secondLowestCo && r.bid.company != lowestCo)
                secondLowestCo = r.bid.company;
        }

        lastBestRequest.bid.company = 0.3 * (secondLowestCo - secondLowestPayoff);
        lastBestRequest.bid.payOff = secondLowestPayoff - lastBestRequest.bid.company;
        lastBestRequest.bidder = bestTaxi;
        bestPrice = lastBestRequest.bid.payOff;
    }

    public boolean done() {
        return ((activity == Activity.PROCESSING_BIDS && bestTaxi == null) || activity == Activity.JOB_ALLOCATED);
    }
}
