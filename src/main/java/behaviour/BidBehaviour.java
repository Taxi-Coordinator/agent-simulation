package behaviour;

import agents.Taxi;
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

            Request bid = agent.bid(request);//THis should have the bid value

            if (agent.activity == Activity.INIT) {
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

            agent.send(reply);
        }
    }

//    public Integer calculatePrice() {
//        return StdRandom.uniform(0, 100);
//    }
}