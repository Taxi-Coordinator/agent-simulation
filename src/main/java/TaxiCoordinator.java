import city.City;
import utils.Helper;
import utils.SimTimer;
import utils.StdRandom;
import utils.io.In;
import utils.io.Out;
import utils.io.StdOut;
import city.Intersection;

import java.util.ArrayList;
import java.util.Date;

public class TaxiCoordinator {
    static Out out= new Out("src/main/resources/output.txt");

    public static void out(String newLine){
        out.println(newLine);
    }

    public static void close(){
        out.close();
    }
    public static void main(String[] args) {
        City vCity;
        Date nextTime = null;
        int calls = 0;

        In in = new In("src/main/resources/v_city.txt");

        System.out.println("Init of file");
        System.out.println("Create City");

        vCity = new City();
        vCity.generateCity(in);


        System.out.println("Done creating city");
        System.out.println("Total Nodes" + vCity.intersections.size());
        System.out.println("Generate Random Call for one intersecctino");

        SimTimer c = new SimTimer(0,0,0,1); //Setting initial time

        // 1. Setting a next call Time
        System.out.println("Setting next Call time");
        nextTime = nextCall(c.getDate());


        for(int t = 0; true; t++){
            c.tick();
            try { Thread.sleep(5); } catch(Exception e){}

            // 2 . Waiting for next call
            if(isCallAvailable(nextTime,c.getDate())) {
                // 3. Pick Random Node but not taxi center
                int nextIndex = pickRandomIntersectionIndex(vCity.intersections, vCity.taxiCenter);
                Intersection intersection = vCity.intersections.get(nextIndex);

                // 4. Receive call
                intersection.receiveCall();
                calls += 1;
                // 5. DO ACTION PROCESS HERE
                System.out.println("("+calls+")"+c.getDate().toString()+": Calling from Node " + intersection.index + " at " + nextTime.toString());
                out("Call "+intersection.index);




                // 6. Set next Time to call
                nextTime = nextCall(c.getDate());

            }


        }
    }

    public static Date nextCall(Date currentTime){
        return Helper.nextCall(currentTime);
    }

    /**
     * Choose a random intersection but not Taxi Center
     * @param taxiCenter
     * @return
     */
    private static int pickRandomIntersectionIndex(ArrayList<Intersection> intersections, int taxiCenter){
        int index;
        do{
            index = StdRandom.uniform(0,intersections.size() - 1 );
        }while(intersections.get(index).index == taxiCenter);

        return index;

    }

    /**
     * This method check if this Intersection should process a pending call
     * @param nextCall Date for next Call
     * @param currentTime Date of current time
     * @return true when there is a call to be trigger
     * false is there is no pending call to specific intersection
     */
    public static boolean isCallAvailable(Date nextCall, Date currentTime){
        if (nextCall!=null && nextCall.before(currentTime))
            return true;
        return false;
    }
}
