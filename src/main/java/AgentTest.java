import agents.Taxi;
import city.City;
import city.DropoffPoint;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.io.In;
import utils.misc.Activity;
import utils.misc.Shift;

import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by jherez on 6/11/16.
 */
public class AgentTest extends Agent {

    public City vCity;
    public In in;
    public ArrayList<Taxi> taxiagents = new ArrayList<>();


    protected void setup() {


        in = new In("src/main/resources/v_city.txt");
        vCity = new City();
        vCity.generateCity(in);

        DropoffPoint point = new DropoffPoint(0);
        Object[] taxi_params = {
                vCity,
                point,
                Shift.TIME_3AM_TO_1PM
        };

        ContainerController cc = getContainerController();
        try {


            AgentController new_agent = cc.createNewAgent("agentsmith", "agents.Taxi", taxi_params);

            new_agent.start();

        } catch (StaleProxyException ex) {
            Logger.getLogger(AgentTest.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("Test-agent " + getAID().getName() + "is online");
    }

    protected void takeDown() {
        System.out.println("Test-agent " + getAID().getName() + "is offline");
    }



    public static void main(String[] args) {
        String[] arg = {"-gui", "-agents" ,"test:AgentTest"};
        jade.Boot.main(arg);
    }
}
