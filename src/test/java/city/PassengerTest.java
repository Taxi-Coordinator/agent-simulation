package city;

import org.junit.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class PassengerTest {
    private Passenger passenger;
    private List<Integer> connections = Arrays.asList(1,6);
    Intersection intersection = new Intersection(0,connections);

    @Before
    public void setUp() throws Exception {
        passenger = new Passenger(intersection,0);
    }

    @After
    public void tearDown() throws Exception {
        passenger.clear();
    }

    @Test
    public void getTravelDistance() throws Exception {
        double d = passenger.getTravelDistance();
        assertTrue(d > 0);
        assertTrue(Math.abs(d) - (int) d == 0 || Math.abs(d) - (int) d == 0.5);
        System.out.println("@Test - setTravelDistance");
    }
}