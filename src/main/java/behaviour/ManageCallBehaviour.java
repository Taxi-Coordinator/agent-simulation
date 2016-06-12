package behaviour;

import agents.TaxiCoordinator;
import city.DropoffPoint;
import city.Intersection;
import city.Passenger;
import city.Request;
import jade.core.AID;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

import java.io.IOException;

/**
 * This class Handle the Call Generation and send the request to taxis for auction
 */
public class ManageCallBehaviour extends Behaviour{
    private AID bestSeller; // The agent who provides the best offer
    private int bestPrice; // The best offered price
    private int repliesCnt = 0; // The counter of replies from seller agents
    private MessageTemplate mt; // The template to receive replies
    private int step = 0;
    private TaxiCoordinator agent;

    public ManageCallBehaviour(TaxiCoordinator coordinator){
        agent= coordinator;
    }

    public void action() {
        for (int t = 0; true; t++) {
            agent.runtime.tick();
            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }

            // 2 . Waiting for next call
            if(step == 0 ) {
                if (agent.isCallAvailable(agent.nextTime, agent.runtime.getDate())) {
                    // 3. Pick Random Node but not taxi center
                    int[] exclude = {agent.vCity.taxiCenter};
                    int nextIndex = agent.pickRandomIntersectionIndex(agent.vCity.intersections, exclude);
                    Intersection intersection = agent.vCity.intersections.get(nextIndex);

                    // 4. Receive call
                    intersection.receiveCall(new Passenger(intersection));
                    agent.calls += 1;
                    // 5. DO ACTION PROCESS HERE

                    // Pick random destination
                    int[] exclude2 = {agent.vCity.taxiCenter, nextIndex};
                    int destination = agent.pickRandomIntersectionIndex(agent.vCity.intersections, exclude2);

                    System.out.println("(" + agent.calls + ")" + agent.runtime.getDate().toString() + ": Calling from Node " + intersection.index + ":" + destination + " at " + agent.nextTime.toString());
                    agent.out("Call " + intersection.index);

                    // Send Request to available taxi
                    agent.lastRequest = new Request(agent.vCity.intersections.get(nextIndex), new DropoffPoint(agent.vCity.intersections.get(nextIndex).index), agent.calls++);
                    sentRequest();

                    // 6. Set next Time to call. ONly if step is 0 that means that is waiting for call
                    if (step == 0)
                        agent.nextTime = agent.nextCall(agent.runtime.getDate());

                }
            }else if(step==1){
                //System.out.print("Waiting for all taxis to submit their response");
            }

        }

    }

    public void sentRequest(){
        System.out.println("Init Auction Proccess");
        switch (step) {
            case 0:
                // Send the cfp to all sellers
                ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                for (int i = 0; i < agent.lstTaxi.size(); ++i) {
                    cfp.addReceiver(agent.lstTaxi.get(i));
                    System.out.println("Sending auction to taxi" + agent.lstTaxi.get(i).getName());
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
                step = 1;
                break;
            case 1:
                // Receive all proposals/refusals from seller agents
                System.out.println("Getting Reply for auction");
                ACLMessage reply = agent.receive();
                if (reply != null) {
                    System.out.println("Getting Reply for auction");
                    // Reply received
//                        if (reply.getPerformative() == ACLMessage.PROPOSE) {
//                            // This is an offer
//                            int price = Integer.parseInt(reply.getContent());
//                            if (bestSeller == null || price < bestPrice) {
//                                // This is the best offer at present
//                                bestPrice = price;
//                                bestSeller = reply.getSender();
//                            }
//                        }
//                        repliesCnt++;
//                        if (repliesCnt >= lstTaxi.size()) {
//                            // We received all replies
//                            step = 2;
//                        }
                } else {
                    block();
                }
                break;
        }
    }

    public boolean done() {
        return ((step == 2 && bestSeller == null) || step == 4);
    }
}
