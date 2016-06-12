import city.City;
import jade.core.Agent;
import utils.simulation.CallGen;
import utils.simulation.Timer;
import utils.simulation.StdRandom;
import utils.io.In;
import utils.io.Out;
import city.Intersection;
import java.util.ArrayList;
import java.util.Date;

public class TaxiCoordinator extends Agent {
    static Out out = new Out("src/main/resources/output.txt");

    public void out(String newLine) {
        out.println(newLine);
    }

    public void close() {
        out.close();
    }



    protected void setup() {
        City vCity;
        Date nextTime = null;
        int calls = 0;

        In in = new In("src/main/resources/v_city.txt");

        System.out.println("Init of file");
        System.out.println("Create City");

        vCity = new City();
        vCity.generateCity(in);


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
//            try {
//                Thread.sleep(1);
//            } catch (Exception e) {
//            }

            // 2 . Waiting for next call
            if (isCallAvailable(nextTime, runtime.getDate())) {
                // 3. Pick Random Node but not taxi center
                int nextIndex = pickRandomIntersectionIndex(vCity.intersections, vCity.taxiCenter);
                Intersection intersection = vCity.intersections.get(nextIndex);

                // 4. Receive call
                intersection.receiveCall();
                calls += 1;
                // 5. DO ACTION PROCESS HERE
                System.out.println("(" + calls + ")" + runtime.getDate().toString() + ": Calling from Node " + intersection.index + " at " + nextTime.toString());
                out("Call " + intersection.index);


                // 6. Set next Time to call
                nextTime = nextCall(runtime.getDate());

            }


        }
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
    private int pickRandomIntersectionIndex(ArrayList<Intersection> intersections, int taxiCenter) {
        int index;
        do {
            index = StdRandom.uniform(0, intersections.size() - 1);
        } while (intersections.get(index).index == taxiCenter);

        return index;

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
}
