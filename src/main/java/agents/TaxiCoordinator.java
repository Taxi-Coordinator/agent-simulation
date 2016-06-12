package agents;

import behaviour.ManageCallBehaviour;
import city.*;
import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.misc.Shift;
import utils.simulation.CallGen;
import utils.simulation.Timer;
import utils.simulation.StdRandom;
import utils.io.In;
import utils.io.Out;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaxiCoordinator extends Agent {
    public static Out out = new Out("src/main/resources/output.txt");
    public City vCity;
    public Date nextTime = null;
    public int calls = 0;
    public int totalTaxis = 0;
    public ArrayList<AID> lstTaxi = new ArrayList<AID>(0);
    public ArrayList<Taxi> taxiDrivers = new ArrayList<>();
    public Request lastRequest;
    public ArrayList<Passenger> passengerArrayList;
    public Timer runtime;

    public void out(String newLine) {
        out.println(newLine);
    }

    public void close() {
        out.close();
    }


    protected void setup() {
        In in = new In("src/main/resources/v_city.txt");

        System.out.println("Init of file");
        System.out.println("Create City");

        vCity = new City();
        vCity.generateCity(in);
        passengerArrayList = new ArrayList<>();


        System.out.println("Done creating city");
        System.out.println("Total Nodes " + vCity.intersections.size());
        System.out.println("Generate Random Call for one intersection");

//        Timer runtime = new Timer(0,0,0,1); //Setting initial time
        runtime = new Timer(vCity.getFileTime(), 1); //Setting initial time

        // 1. Setting a next call Time
        generateSampleTaxis();
        System.out.println("Setting next Call time");
        nextTime = nextCall(runtime.getDate());

        addBehaviour(new ManageCallBehaviour(this));

    }

    public void receiveCall(Passenger passenger, Intersection intersection){
        intersection.receiveCall(passenger);
        //this.calls += 1;
        this.passengerArrayList.add(passenger);
        this.vCity.passengerArrayList.add(passenger);
        System.out.println("TaxiCoordinator: Received a call from Passenger " + passenger.id);
    }

    public Date nextCall(Date currentTime) {
        return CallGen.nextCall(currentTime);
    }

    /**
     * Choose a random intersection but not Taxi Center
     *
     * @param taxiCenter
     * @return
     */
    public int pickRandomIntersectionIndex(ArrayList<Intersection> intersections, int[] taxiCenter) {
        int index;
        do {
            index = StdRandom.uniform(0, intersections.size() - 1);
        } while (find(intersections.get(index).index, taxiCenter));

        return index;

    }

    /**
     * Choose a random intersection but not Taxi Center
     *
     * @param taxiCenter
     * @return
     */
    public int pickRandomDropoffIndex(ArrayList<DropoffPoint> dropoffPoints, int[] taxiCenter) {
        int index;
        do {
            index = StdRandom.uniform(0, dropoffPoints.size() - 1);
        } while (find(dropoffPoints.get(index).index, taxiCenter));

        return index;

    }

    public boolean find(int index, int[] array) {
        for (int i : array) {
            if (i == index)
                return true;
        }
        return false;
    }

    /**
     * This method check if this Intersection should process a pending call
     *
     * @param nextCall    Date for next Call
     * @param currentTime Date of current time
     * @return true when there is a call to be trigger
     * false is there is no pending call to specific intersection
     */
    public boolean isCallAvailable(Date nextCall, Date currentTime) {
        if (nextCall != null && nextCall.before(currentTime))
            return true;
        return false;
    }

    public void addTaxi(DropoffPoint point, Shift shift) {
        Object[] params = {this.vCity, point, shift, totalTaxis+1, runtime};
        ContainerController cc = getContainerController();
        String name = "";
        try {
            name = "smith" + totalTaxis++;
            AgentController new_agent = cc.createNewAgent(name, "agents.Taxi", params);
            new_agent.start();
            lstTaxi.add(new AID(name, AID.ISLOCALNAME));
            //taxiDrivers.add((Taxi)params[0]);
        } catch (StaleProxyException ex) {
            Logger.getLogger(TaxiCoordinator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateSampleTaxis() {
        //Gene
        for (int i = 1; i <= 4; i++) {
            this.addTaxi(new DropoffPoint(this.vCity.taxiCenter), Shift.TIME_3AM_TO_1PM);
        }
        for (int i = 1; i <= 4; i++) {
            this.addTaxi(new DropoffPoint(this.vCity.taxiCenter), Shift.TIME_6PM_TO_4AM);
        }
        for (int i = 1; i <= 4; i++) {
            this.addTaxi(new DropoffPoint(this.vCity.taxiCenter), Shift.TIME_9AM_TO_7PM);
        }
    }

    public static void main(String[] args) {
        String[] arg = {"-gui", "-agents", "agents.TaxiCoordinator:agents.TaxiCoordinator"};
        jade.Boot.main(arg);
    }
}
