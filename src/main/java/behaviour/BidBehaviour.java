package behaviour;

import agents.Taxi;
import jade.core.behaviours.CyclicBehaviour;
import jade.lang.acl.ACLMessage;
import utils.misc.Activity;
import utils.simulation.StdRandom;

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
        if (msg != null) {
            // Message received. Process it
            String title = msg.getContent();
            ACLMessage reply = msg.createReply();

            Integer bid = calculatePrice();//THis should have the bid value

            if (agent.activity == Activity.INIT) {
                //Calculate biding
                if (bid != null) {
                    // The bid is available . Reply with the value
                    reply.setPerformative(ACLMessage.PROPOSE);
                    reply.setContent(String.valueOf(price.intValue()));
                } else {
                    // Error bidding
                    reply.setPerformative(ACLMessage.REFUSE);
                    reply.setContent("not - available");
                }
            } else {
                reply.setPerformative(ACLMessage.REFUSE);
                reply.setContent("not - available");
            }
            agent.send(reply);
        }
    }

    public Integer calculatePrice() {
        return StdRandom.uniform(0, 100);
    }
}