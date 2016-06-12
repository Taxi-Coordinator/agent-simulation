import agents.Taxi;
import city.City;
import city.DropoffPoint;
import city.Request;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import jade.wrapper.StaleProxyException;
import utils.misc.Shift;
import utils.simulation.CallGen;
import utils.simulation.Timer;
import utils.simulation.StdRandom;
import utils.io.In;
import utils.io.Out;
import city.Intersection;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TaxiCoordinator extends Agent {
    static Out out = new Out("src/main/resources/output.txt");
    City vCity;
    Date nextTime = null;
    int calls = 0;
    int totalTaxis = 0;
    ArrayList<AID> lstTaxi = new ArrayList<AID>(0);
    Request lastRequest;

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
        generateSampleTaxis();


        System.out.println("Done creating city");
        System.out.println("Total Nodes " + vCity.intersections.size());
        System.out.println("Generate Random Call for one intersection");

//        Timer runtime = new Timer(0,0,0,1); //Setting initial time
        Timer runtime = new Timer(vCity.getFileTime(), 1); //Setting initial time

        // 1. Setting a next call Time
        System.out.println("Setting next Call time");
        nextTime = nextCall(runtime.getDate());


        for (int t = 0; true; t++) {
            runtime.tick();
            try {
                Thread.sleep(1);
            } catch (Exception e) {
            }

            // 2 . Waiting for next call
            if (isCallAvailable(nextTime, runtime.getDate())) {
                // 3. Pick Random Node but not taxi center
                int[] exclude = {vCity.taxiCenter};
                int nextIndex = pickRandomIntersectionIndex(vCity.intersections, exclude);
                Intersection intersection = vCity.intersections.get(nextIndex);

                // 4. Receive call
                intersection.receiveCall();
                calls += 1;
                // 5. DO ACTION PROCESS HERE

                // Pick random destination
                int[] exclude2 = {vCity.taxiCenter,nextIndex};
                int destination = pickRandomIntersectionIndex(vCity.intersections, exclude2);

                System.out.println("(" + calls + ")" + runtime.getDate().toString() + ": Calling from Node " + intersection.index + ":"+destination+" at " + nextTime.toString());
                out("Call " + intersection.index);

                // Send Request to available taxi
                lastRequest = new Request(vCity.intersections.get(nextIndex),new DropoffPoint(vCity.intersections.get(nextIndex).index));


                // 6. Set next Time to call
                nextTime = nextCall(runtime.getDate());

            }


        }
    }

    public Taxi sendRequest(Request request){
        Taxi resutl = null;
        addBehaviour(new RequestAuction());
        return resutl;
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
    private int pickRandomIntersectionIndex(ArrayList<Intersection> intersections, int[] taxiCenter) {
        int index;
        do {
            index = StdRandom.uniform(0, intersections.size() - 1);
        } while (find(intersections.get(index).index,taxiCenter));

        return index;

    }

    private boolean find(int index, int[] array){
        for(int i: array){
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

    public void addTaxi(DropoffPoint point, Shift shift){
        Object[] params = {this.vCity,point,shift};
        ContainerController cc = getContainerController();
        String name = "";
        try {
            name = "smith" + totalTaxis++;
            AgentController new_agent = cc.createNewAgent(name, "agents.Taxi", params);
            new_agent.start();
            lstTaxi.add(new AID(name,AID.ISLOCALNAME));
        } catch (StaleProxyException ex) {
            Logger.getLogger(TaxiCoordinator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void generateSampleTaxis(){
        //Gene
        for(int i=1;i<=4;i++){
            this.addTaxi(new DropoffPoint(this.vCity.taxiCenter), Shift.TIME_3AM_TO_1PM);
        }
        for(int i=1;i<=4;i++){
            this.addTaxi(new DropoffPoint(this.vCity.taxiCenter),Shift.TIME_6PM_TO_4AM);
        }
        for(int i=1;i<=4;i++){
            this.addTaxi(new DropoffPoint(this.vCity.taxiCenter),Shift.TIME_9AM_TO_7PM);
        }
    }

    public static void main(String[] args) {
        String[] arg = {"-gui", "-agents" ,"TaxiCoordinator:TaxiCoordinator"};
        jade.Boot.main(arg);
    }

    private class RequestAuction extends Behaviour {
        private AID bestSeller; // The agent who provides the best offer
        private int bestPrice; // The best offered price
        private int repliesCnt = 0; // The counter of replies from seller agents
        private MessageTemplate mt; // The template to receive replies
        private int step = 0;
        public void action() {
            switch (step) {
                case 0:
                    // Send the cfp to all sellers
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < lstTaxi.size(); ++i) {
                        cfp.addReceiver(lstTaxi.get(i));
                    }
                    try {
                        cfp.setContentObject(lastRequest);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    cfp.setConversationId("auction");
                    cfp.setReplyWith("cfp"+System.currentTimeMillis()); // Unique value
                    send(cfp);
                    // Prepare the template to get proposals
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("auction"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
                    step = 1;
                    break;
                case 1:
                    // Receive all proposals/refusals from seller agents
                    ACLMessage reply = receive(mt);
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
                    }
                    else {
                        block();
                    }
                    break;
            }
        }
        public boolean done() {
            return ((step == 2 && bestSeller == null) || step == 4);
        }
    }
}
