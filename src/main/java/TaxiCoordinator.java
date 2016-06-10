import city.City;
import utils.SimTimer;
import utils.io.In;
import utils.io.StdOut;
import city.Intersection;

public class TaxiCoordinator {



    public static void main(String[] args) {
        City vCity;
        int sourceNode = 41;
        int destinationNode = 40;


        In in = new In("src/main/resources/v_city.txt");
//        Intersection intersection = new Intersection(sourceNode, sourceNode, destinationNode);

        System.out.println("Init of file");
        System.out.println("Create City");

        vCity = new City();
//        vCity.clear();
        vCity.generateCity(in);
//        System.out.println(vCity.intersections.size());
//
//        for(Intersection e : vCity.intersections) {
//            System.out.println(e);
//        }


        System.out.println("Done creating city");
        System.out.println("Total Vertix" + vCity.intersections.size());

        for(Intersection i: vCity.intersections){
            System.out.println(i.toString());
        }
        System.out.println("Generate Random Call for one intersecctino");
        System.out.println(vCity.G.toString());
        vCity.G.printToFile();
        SimTimer c = new SimTimer(0,0,0,1);
        for(int t = 0; true; t++){
            c.tick();
            try { Thread.sleep(5); } catch(Exception e){}


        }
    }
}
