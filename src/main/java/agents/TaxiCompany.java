package agents;

import city.City;
import city.DropoffPoint;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import javafx.beans.binding.ObjectExpression;
import utils.misc.Shift;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by eduardosalazar1 on 6/12/16.
 */
public class TaxiCompany  extends Agent {
    int totalTaxis;
    int totalCalls;
    City city;

    public TaxiCompany(City city){
        this.city = city;
        totalTaxis = 0;
        totalCalls = 0;
        String[] arg = {"-gui", "-agents" ,"test:AgentTest"};
        jade.Boot.main(arg);
    }

    public void addTaxi(DropoffPoint point, Shift shift){
        Object[] params = {this.city,point,shift};
        ContainerController cc = getContainerController();
        try {

            AgentController new_agent = cc.createNewAgent("smith" + totalTaxis, "agents.Taxi", params);
            new_agent.start();
        } catch (StaleProxyException ex) {
            Logger.getLogger(TaxiCompany.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateSampleTaxis(){
        //Gene
        for(int i=1;i<=4;i++){
            this.addTaxi(new DropoffPoint(this.city.taxiCenter),Shift.TIME_3AM_TO_1PM);
        }
        for(int i=1;i<=4;i++){
            this.addTaxi(new DropoffPoint(this.city.taxiCenter),Shift.TIME_6PM_TO_4AM);
        }
        for(int i=1;i<=4;i++){
            this.addTaxi(new DropoffPoint(this.city.taxiCenter),Shift.TIME_9AM_TO_7PM);
        }
    }


    public void startAction(){

    }
    
}
