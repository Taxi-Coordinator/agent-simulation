package utils.agentMethods;

import agents.Taxi;
import city.*;
import utils.io.In;
import utils.shortestPath.DijkstraUndirectedSP;
import utils.simulation.CallGen;
import utils.simulation.StdRandom;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jherez on 6/11/16.
 */
public class TaxiMethods {

    public static final double SPEED = 30.0;
    private static final double CHARGE_RATE_PER_KILOMETER = 40;
    private static final double GAS_COST_PER_KILOMETER = 6;


    /**
     * Converts a date object to seconds
     *
     * @param current
     * @return the distance
     */
    public static int timeToSecond(Date current) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        return (hour * 60 * 60) + (minute * 60) + (second);
    }

    /**
     * Returns the shortest travel distance
     * Considers the cost of the taxi agent travelling from &currentTaxiLocaiton; to the customer location
     * &incomingRequest;
     *
     * @param vCity               City.
     * @param currentTaxiLocation DropoffPoint
     * @param incomingRequest     Request see {@link DropoffPoint}
     * @return the distance
     */
    private static double getChargeableDistance(City vCity, DropoffPoint currentTaxiLocation, DropoffPoint incomingRequest) {
        DijkstraUndirectedSP destination_sp = vCity.getShortestPaths(vCity.G, currentTaxiLocation.index);
        return destination_sp.distTo(incomingRequest.index);
    }

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
     * Calculate the bid for an incoming request &incoming_request;
     * Considers whether the taxi currently has a passenger and needs to complete that job
     * before taking another. Iterates through the list of pending jobs and sums their total time
     * the bidding location is set to either the current taxi location if they are not processing a job
     * or to the destination of the job last processed
     *
     * @param vCity               City see {@link City}
     * @param taxi                Taxi see {@link Taxi}
     * @param currentTaxiLocation DropoffPont see {@link DropoffPoint}
     * @param incomingRequest     Request see {@link Request}
     * @return the bid
     */
    public static Bid getBid(City vCity, Taxi taxi, DropoffPoint currentTaxiLocation, Request incomingRequest) {
        Bid result = new Bid();
        double request_queue_time = 0;
        double total_dist = 0;
        // If a taxi has pending job, process the time it would take to complete those jobs
        if (taxi.confirmed_request != null) {
            request_queue_time = getJobCompletionTime(vCity, taxi, taxi.last_request);
            total_dist = getTotalTravelDistance(vCity, taxi.last_request.destination, incomingRequest);
        } else if (taxi.currentPassenger == null) {
            request_queue_time = 0;
            total_dist = getTotalTravelDistance(vCity, currentTaxiLocation, incomingRequest);
        }

        total_dist += request_queue_time;
        double chargeable_dist = getChargeableDistance(vCity, new DropoffPoint(incomingRequest.origin.index), incomingRequest.destination);


        result.payOff = (chargeable_dist * CHARGE_RATE_PER_KILOMETER) - (total_dist * GAS_COST_PER_KILOMETER);
        result.company = (CHARGE_RATE_PER_KILOMETER - GAS_COST_PER_KILOMETER) * chargeable_dist;

        boolean markup = StdRandom.bernoulli(StdRandom.uniform());
        double multiplier = getBidMultiplier(taxi);
        double bid_scaler = StdRandom.uniform(0.01, 0.2);

        result.payOff *= multiplier;
        result.company *= multiplier;
        bid_scaler *= result.payOff;
        if (markup) {
            result.payOff += bid_scaler;
            result.company += bid_scaler;
        }
        return result;
    }

    /**
     * Returns a bid multiplier based on the time of the day. When the calls per hour
     * is at it's lowest, we return the highest multiplier
     * Considers the time of the day and the associated calls per hour
     *
     * @param taxi Taxi see {@link Taxi}
     * @return a multiplier based on the set lambda
     */
    public static double getBidMultiplier(Taxi taxi) {
        int callsPerHour = (int) CallGen.getCallsPerHour(taxi.runtime.getDate());
        if (callsPerHour == 3)
            return 1.5;
        else if (callsPerHour == 2)
            return 0.2;
        else
            return 2.0;
    }

    /**
     * Calculate the time required to complete an incoming request &confirmed_request;
     * Considers whether the taxi currently has a passenger and needs to complete that job
     * before taking another. Iterates through the list of pending jobs and sums their total time
     *
     * @param vCity           City see {@link City}
     * @param taxi            Taxi see {@link Taxi}
     * @param incomingRequest Request see {@link Request}
     * @return the distance to complete all pending jobs
     */
    public static int getJobCompletionTime(City vCity, Taxi taxi, Request incomingRequest) {
        double total_job_time = 0;
        DropoffPoint terminus;
        Request current_request = null;

        if (taxi.confirmed_request != null) {
            current_request = taxi.confirmed_request;
        }

        if (current_request != null) {
            if (taxi.currentPassenger != null) {
                // Time to finish current job
                total_job_time += getChargeableDistance(vCity, taxi.currentLocation, current_request.destination);
            } else {
                // Passenger does not exist, calculate distance to complete current_request
                total_job_time += getTotalTravelDistance(vCity, taxi.currentLocation, current_request);
            }
            // Get last known job destination
            terminus = current_request.destination;
        } else {
            if (taxi.last_request == null)
                terminus = vCity.dropoffPoints.get(vCity.taxiCenter);
            else
                terminus = taxi.last_request.destination;
        }

        total_job_time += getTotalTravelDistance(vCity, terminus, incomingRequest);
        return (int) (total_job_time / SPEED);
    }


    public static void main(String[] args) {
        In in = new In("src/main/resources/v_city.txt");
        City vCity = new City();
        vCity.generateCity(in, 0);
        DropoffPoint currentLocation = new DropoffPoint(0);
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
