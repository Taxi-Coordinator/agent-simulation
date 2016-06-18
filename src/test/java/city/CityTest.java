package city;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import utils.simulation.CallGen;
import utils.io.In;
import utils.shortestPath.Path;
import utils.simulation.StdRandom;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

import static org.junit.Assert.*;

public class CityTest {

    private City vCity;
    private final int sourceNode = 41;
    private final int destinationNode = 40;
    private final List<Integer> connections = Collections.singletonList(destinationNode);
    private final int distance = 3;
    private final In in = new In("src/main/resources/v_city.txt");
    private final Intersection intersection = new Intersection(sourceNode, connections);

    @Before
    public void setUp() throws Exception {
        vCity = new City();
    }

    @After
    public void tearDown() throws Exception {
        vCity.clear();
    }

    @Test
    public void generateCity() throws Exception {
        vCity.clear();
        vCity.generateCity(in);
        assertNotNull(vCity);
        System.out.println("@Test - generateCity");

    }

    @Test
    public void clear() throws Exception {
        vCity.clear();
        assertNull(vCity.intersections);
        assertNull(vCity.dropoffPoints);
        assertNull(vCity.G);
        assertTrue(City.pathLookup.isEmpty());
        assertTrue(vCity.totalCalls == 0);
        assertTrue(vCity.totalPassengers == 0);
        System.out.println("@Test - clear");
    }

    @Test
    public void getShortestPaths() throws Exception {
        vCity.getShortestPaths(vCity.G, sourceNode);
        assertNotNull(City.pathLookup);
        System.out.println("@Test - getShortestPaths");
    }

    @Test
    public void extractIntersections() throws Exception {
        vCity.extractIntersections(vCity.G);
        assertNotNull(vCity.intersections);
        System.out.println("@Test - extractIntersections");
    }

    @Test
    public void extractDropoffPoints() throws Exception {
        vCity.extractDropoffPoints(vCity.G);
        assertNotNull(vCity.dropoffPoints);
        assertTrue(vCity.intersections.size() < vCity.dropoffPoints.size());
        System.out.println("@Test - extractDropoffPoints");
    }

    @Test
    public void extendGraph() throws Exception {
        int n = vCity.G.V();
        vCity.clear();
        vCity.generateCity(in, 0);
        assertTrue(n >= vCity.G.V());
        System.out.println("@Test - extendGraph");
    }

    @Test
    public void getRoutes() throws Exception {
        vCity.getShortestPaths(vCity.G, sourceNode);
        ArrayList<Path> res = vCity.getRoutes(vCity.G, sourceNode, distance);
        assertNotNull(res);
        System.out.println("@Test - getRoutes");
    }

    @Test
    public void printSP() throws Exception {
        vCity.getShortestPaths(vCity.G, sourceNode);
        System.out.println("\nPrinting SP Table");
        vCity.printSP(vCity.G, sourceNode);
        System.out.println("@Test - printSP");
    }

    @Test
    public void printRoutes() throws Exception {
        vCity.getShortestPaths(vCity.G, sourceNode);
        ArrayList<Path> res = vCity.getRoutes(vCity.G, sourceNode, distance);
        System.out.println("\nPrinting Routes");
        vCity.printRoutes(res);
        System.out.println("@Test - printRoutes");
    }

    @Test
    public void addPassenger() throws Exception {
        vCity.addPassenger(intersection,0);
        assertNotNull(vCity.passengerArrayList);
        assertTrue(vCity.passengerArrayList.size() > 0);
        System.out.println("@Test - addPassenger");
    }

    @Test
    public void setPassengerRoute() throws Exception {
        vCity.addPassenger(intersection,0);
        Passenger p = vCity.passengerArrayList.get(0);
        vCity.setPassengerRoute(p);
        assertNotNull(p.route);
        System.out.print(p);
        System.out.println("@Test - setPassengerRoute");
    }

    @Test
    public void testIsIntersection() throws Exception {
        assertTrue((vCity.isIntersection(StdRandom.uniform(0, 41))));
        assertFalse((vCity.isIntersection(StdRandom.uniform(41, vCity.G.V()))));
        System.out.println("@Test - testIsIntersection");
    }

    @Test
    public void testLambdaValues() {
        String[] array = {"07:00:00", "15:00:00", "22:00:00", "24:00:00"};
        Date time = null;
        for (String timeStr : array) {
            try {
                time = new SimpleDateFormat("HH:mm:ss").parse(timeStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(" For " + timeStr + " call equal " + CallGen.getCallsPerHour(time) + ", so lambda equals " + CallGen.getLambda(time));
        }
        System.out.println("@Test - testLambdaValues");
    }

    @Test
    public void testNextCall() {
        String[] array = {"07:00:00", "15:00:00", "22:00:00", "24:00:00"};
        Date time = null;
        for (String timeStr : array) {
            try {
                time = new SimpleDateFormat("HH:mm:ss").parse(timeStr);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            System.out.println(" For " + timeStr + " next call will be  at " + CallGen.nextCall(time).toString());
        }
        System.out.println("@Test - testNextCall");
    }

    @Test
    public void testTimeReading() {
        System.out.print(City.getFileTime().toString());
        System.out.println("@Test - testTimeReading");
    }
}