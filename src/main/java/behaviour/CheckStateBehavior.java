package behaviour;

import agents.Taxi;
import jade.core.behaviours.CyclicBehaviour;

/**
 * Created by eduardosalazar1 on 6/13/16.
 */
public class CheckStateBehavior extends CyclicBehaviour {
<<<<<<< 0bceec54a79387dac8cdc0d0b6a604289d2aa0e6
    private final Taxi agent;
    public CheckStateBehavior(Taxi taxi){
=======
    Taxi agent;

    public CheckStateBehavior(Taxi taxi) {
>>>>>>> Finally working
        agent = taxi;
    }

    @Override
    public void action() {
        // Check call taxi self check
        agent.checkStatus();
    }
}
