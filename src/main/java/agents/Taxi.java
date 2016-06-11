package agents;
import jade.core.Agent;
/**
 * Created by jherez on 6/11/16.
 */
public class Taxi extends Agent {
    protected void setup() {
        // Printout a welcome message
        System.out.println("Taxi-agent " +getAID().getName()+ "is online");
    }

    protected void takeDown() {
        // Printout a dismissal message
        System.out.println("Taxi-agent " +getAID().getName()+ "is offline");
    }
}
