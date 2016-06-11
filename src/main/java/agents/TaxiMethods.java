package agents;

import city.City;
import city.DropoffPoint;
import city.Intersection;
import city.Request;
import utils.io.In;
import utils.shortestPath.DijkstraUndirectedSP;

/**
 * Created by jherez on 6/11/16.
 */
public class TaxiMethods {

    public static double getJobDistance(City vCity, DropoffPoint currentLocation, Request request) {
        double distance = 0;
        DijkstraUndirectedSP pickup_sp = vCity.getShortestPaths(vCity.G,currentLocation.index);
        DijkstraUndirectedSP dropOff_sp = vCity.getShortestPaths(vCity.G,request.destination.index);
        distance += pickup_sp.distTo(request.origin.index);
        distance += dropOff_sp.distTo(request.destination.index);
        return distance;
    }

    public static void main(String[] args) {
        In in = new In("src/main/resources/v_city.txt");;
        City vCity = new City();
        vCity.generateCity(in,0);
        DropoffPoint currentLocation = new DropoffPoint(0);;
        DropoffPoint destination = new DropoffPoint(10);;
        Intersection test = vCity.intersections.get(9);
        Request confirmed_request = new Request(test,destination,0);

        System.out.println(test.toString());

        System.out.println("Current Taxi Location "+currentLocation.index);
        System.out.println("Customer Location "+confirmed_request.origin.index);
        System.out.println("Customer Destination "+destination.index);
        System.out.println("Distance "+getJobDistance(vCity,currentLocation,confirmed_request));

//        System.out.println(vCity.G.toString());
        vCity.printIntersections();
    }
}
