package utils.agentMethods;

import agents.Taxi;
import city.*;
import utils.io.In;
import utils.shortestPath.DijkstraUndirectedSP;

/**
 * Created by jherez on 6/11/16.
 */
public class TaxiMethods {

    public static double SPEED = 30.0;
    public static double CHARGE_RATE_PER_KILOMETER = 40;
    public static double GAS_COST_PER_KILOMETER = 6;

    /**
     * Calculates the total travel distance for an incoming request &incomingRequest;
     * Considers the cost of the taxi agent travelling to the customer location &incomingrequest.origin.index;
     * from their current position &currentTaxiLocaiton; to &incomingRequest.destination.index;
     *
     * @param vCity               City.
     * @param currentTaxiLocation DropoffPoint
     * @param incomingRequest     Request see {@link Request}
     * @return the distance
     */

    public static double getTotalTravelDistance(City vCity, DropoffPoint currentTaxiLocation, Request incomingRequest) {
        double distance = 0;
        // Return a shortest path graph with the current taxi location as source node
        DijkstraUndirectedSP pickup_sp = vCity.getShortestPaths(vCity.G, currentTaxiLocation.index);
        // Return a shortest path graph with the customer destination as source node
        DijkstraUndirectedSP dropOff_sp = vCity.getShortestPaths(vCity.G, incomingRequest.destination.index);
        distance += pickup_sp.distTo(incomingRequest.origin.index);
        distance += dropOff_sp.distTo(incomingRequest.origin.index);
        return distance;
    }

    /**
     * Returns the shortest distance total travel distance
     * Considers the cost of the taxi agent travelling from &currentTaxiLocaiton; to the customer location
     * &incomingRequest;
     *
     * @param vCity               City.
     * @param currentTaxiLocation DropoffPoint
     * @param incomingRequest     Request see {@link DropoffPoint}
     * @return the distance
     */

    public static double getChargeableDistance(City vCity, DropoffPoint currentTaxiLocation, DropoffPoint incomingRequest) {
        DijkstraUndirectedSP destination_sp = vCity.getShortestPaths(vCity.G, currentTaxiLocation.index);
        return destination_sp.distTo(incomingRequest.index);
    }

    public static Bid getBid(City vCity, DropoffPoint currentTaxiLocation, Request incomingRequest) {
        Bid result = new Bid();
        double total_dist = getTotalTravelDistance(vCity, currentTaxiLocation, incomingRequest);
        double chargeable_dist = getChargeableDistance(vCity, new DropoffPoint(incomingRequest.origin.index), incomingRequest.destination);

        result.company = 0.3 * chargeable_dist * (CHARGE_RATE_PER_KILOMETER - GAS_COST_PER_KILOMETER);
        result.payOff = chargeable_dist * CHARGE_RATE_PER_KILOMETER - total_dist * GAS_COST_PER_KILOMETER - result.company;
        result.price = result.payOff + result.company;

        return result;
    }

    public static int getJobTime(City vCity, Taxi taxi, Request incomingRequest) {
        double total_job_time = 0;
        total_job_time += getTotalTravelDistance(vCity, taxi.currentLocation, incomingRequest);
        return (int) (total_job_time / SPEED);
    }


    /**
     * Calculate the time required to complete an incoming request &confirmed_request;
     * Considers whether the taxi currently has a passenger and needs to complete that job
     * before taking another. Iterates through the list of pending jobs and sums their total time
     *
     * @param incomingRequest Request see {@link Request}
     * @return the distance
     */
    public static int getJobCompletionTime(City vCity, Taxi taxi, Request incomingRequest) {
        double total_job_time = 0;
        DropoffPoint terminus = null;
        Request current_request = null;

        if (taxi.confirmed_request != null) {
            current_request = taxi.confirmed_request;
        } else if (!taxi.requests.isEmpty()) {
            current_request = taxi.requests.dequeue();
        }

        if (current_request != null) {
            if (taxi.currentPassenger != null) {
                // Time to finish current job
                total_job_time += getChargeableDistance(vCity, taxi.currentLocation, current_request.destination);
            } else {
                total_job_time += getTotalTravelDistance(vCity, taxi.currentLocation, current_request);
            }

            for (Request r : taxi.requests) {
                total_job_time += getChargeableDistance(vCity, current_request.destination, new DropoffPoint(r.origin.index));
                total_job_time += getChargeableDistance(vCity, new DropoffPoint(r.origin.index), new DropoffPoint(r.destination.index));
            }

            if (taxi.requests.isEmpty())
                terminus = taxi.confirmed_request.destination;
            else
                // Get last known job destination
                terminus = taxi.last_request.destination;
        } else {
            terminus = taxi.currentLocation;
        }

        total_job_time += getTotalTravelDistance(vCity, terminus, incomingRequest);
        return (int) (total_job_time / SPEED);
    }

    public static void main(String[] args) {
        In in = new In("src/main/resources/v_city.txt");
        City vCity = new City();
        vCity.generateCity(in, 0);
        DropoffPoint currentLocation = new DropoffPoint(0);
        ;
        DropoffPoint destination = new DropoffPoint(10);
        Intersection customerLocation = vCity.intersections.get(1);
        Request confirmed_request = new Request(customerLocation, destination, 0);

        System.out.println(customerLocation.toString());

        System.out.println("Current Taxi Location " + currentLocation.index);
        System.out.println("Customer Location " + confirmed_request.origin.index);
        System.out.println("Customer Destination " + destination.index);
        System.out.println("Distance " + getTotalTravelDistance(vCity, currentLocation, confirmed_request));
    }
}
