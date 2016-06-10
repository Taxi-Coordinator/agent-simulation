import city.City;
import city.Intersection;
import utils.Helper;
import utils.StdRandom;
import utils.io.In;

public class TaxiCoordinator {



    public static void main(String[] args) {
        City vCity;
        int sourceNode = 41;
        int destinationNode = 40;
        In in = new In("src/main/resources/v_city.txt");
        Intersection intersection = new Intersection(sourceNode, sourceNode, destinationNode);

        System.out.println("Init of file");
        System.out.println("Create City");

        vCity = new City();
        vCity.clear();
        vCity.generateCity(in);

        System.out.println("Done creating city");
        System.out.println("Total Vertix" + vCity.intersections.size());

        for(Intersection i: vCity.intersections){
            System.out.println(i.toString());
        }

    }
}
