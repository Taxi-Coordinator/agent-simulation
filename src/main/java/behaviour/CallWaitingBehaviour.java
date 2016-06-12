package behaviour;

import agents.Taxi;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import utils.misc.Activity;

/**
 * Created by jherez on 6/12/16.
 */
public class CallWaitingBehaviour extends Behaviour {

    public Taxi taxi;
    public boolean call_received = false;

    public CallWaitingBehaviour(Taxi taxi){
        this.taxi = taxi;
        this.taxi.activity = Activity.WAITING_FOR_JOB;
    }

    @Override
    public void action(){
        switch(this.taxi.activity){
            case WAITING_FOR_JOB:
                ACLMessage msg = this.myAgent.receive();
                if(msg != null && !call_received) {
                    System.out.println(msg.getContent());
                    // Here, need to extract the intersection, and the passenger in order to do the lookup
                }
        }
    }

    @Override
    public boolean done() {
        return call_received;
    }
}
