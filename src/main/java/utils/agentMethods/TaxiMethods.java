package utils.agentMethods;

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

    public static double getJobDistance(City vCity, DropoffPoint currentTaxiLocation, Request confirmedRequest) {
        double distance = 0;
        // Return a shortest path graph with the current taxi location as source node
        DijkstraUndirectedSP pickup_sp = vCity.getShortestPaths(vCity.G,currentTaxiLocation.index);
        // Return a shortest path graph with the customer destination as source node
        DijkstraUndirectedSP dropOff_sp = vCity.getShortestPaths(vCity.G,confirmedRequest.destination.index);
        distance += pickup_sp.distTo(confirmedRequest.origin.index);
        distance += dropOff_sp.distTo(confirmedRequest.origin.index);
        return distance;
    }

    public static void main(String[] args) {
        In in = new In("src/main/resources/v_city.txt");;
        City vCity = new City();
        vCity.generateCity(in,0);
        DropoffPoint currentLocation = new DropoffPoint(0);;
        DropoffPoint destination = new DropoffPoint(10);
        Intersection customerLocation = vCity.intersections.get(1);
        Request confirmed_request = new Request(customerLocation,destination,0);

        System.out.println(customerLocation.toString());

        System.out.println("Current Taxi Location "+currentLocation.index);
        System.out.println("Customer Location "+confirmed_request.origin.index);
        System.out.println("Customer Destination "+destination.index);
        System.out.println("Distance "+getJobDistance(vCity,currentLocation,confirmed_request));
    }
}
