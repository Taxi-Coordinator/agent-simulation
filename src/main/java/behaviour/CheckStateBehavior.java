package behaviour;

import agents.Taxi;
import jade.core.behaviours.CyclicBehaviour;

/**
 * Created by eduardosalazar1 on 6/13/16.
 */
public class CheckStateBehavior extends CyclicBehaviour {
    private final Taxi agent;
    public CheckStateBehavior(Taxi taxi){
        agent = taxi;
    }

    @Override
    public void action() {
        // Check call taxi self check
        agent.checkStatus();
    }
}
