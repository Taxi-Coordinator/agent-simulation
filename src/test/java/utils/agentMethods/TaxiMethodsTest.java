package utils.agentMethods;

import city.City;
import city.DropoffPoint;
import city.Intersection;
import city.Request;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.io.In;
import utils.simulation.StdRandom;

import static org.junit.Assert.*;

/**
 * Created by jherez on 6/12/16.
 */
public class TaxiMethodsTest {
    private City vCity;
    private int sourceNode;
    private In in = new In("src/main/resources/v_city.txt");
    DropoffPoint currentTaxiLocation;
    DropoffPoint customerDestination;
    private Request confirmedRequest;
    private Intersection customerLocation;


    @Before
    public void setUp() throws Exception {
        vCity = new City();
        vCity.generateCity(in,0);
    }

    @After
    public void tearDown() throws Exception {
        vCity.clear();
    }

    @Test
    public void getJobDistance() throws Exception {
        double distance;
        double test_distance = 0;
        currentTaxiLocation = new DropoffPoint(vCity.intersections.get(StdRandom.uniform(0,vCity.dropoffPoints.size())).index);
        customerLocation = vCity.intersections.get(StdRandom.uniform(0,vCity.intersections.size()));
        customerDestination = new DropoffPoint(StdRandom.uniform(0,vCity.dropoffPoints.size()));
        confirmedRequest = new Request(customerLocation,customerDestination,0);

        distance = TaxiMethods.getJobDistance(vCity,currentTaxiLocation,confirmedRequest);

        vCity.sp = vCity.getShortestPaths(vCity.G,currentTaxiLocation.index);
        test_distance += vCity.sp.distTo(confirmedRequest.origin.index);

        vCity.sp = vCity.getShortestPaths(vCity.G,confirmedRequest.destination.index);
        test_distance += vCity.sp.distTo(confirmedRequest.origin.index);

//        System.out.println("Current Taxi Location "+currentTaxiLocation.index);
//        System.out.println("Customer Location "+confirmedRequest.origin.index);
//        System.out.println("Customer Destination "+confirmedRequest.destination.index);
//
//        System.out.println("Calculated Distance "+distance);
//        System.out.println("Test Distance "+test_distance);

        assertTrue(distance > 0 && distance == test_distance);
        System.out.println("@Test - getJobDistance");
    }

}