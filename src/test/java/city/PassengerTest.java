package city;

import org.junit.*;

import static org.junit.Assert.*;

public class PassengerTest {
    private Passenger passenger;
    Intersection intersection = new Intersection(0, 0, 1);

    @Before
    public void setUp() throws Exception {
        passenger = new Passenger(intersection);
    }

    @After
    public void tearDown() throws Exception {
        passenger.clear();
    }

    @Test
    public void setTravelDistance() throws Exception {
        double d = passenger.getTravelDistance();
        assertTrue(d > 0);
        assertTrue(Math.abs(d) - (int) d == 0 || Math.abs(d) - (int) d == 0.5);
        System.out.println("@Test - setTravelDistance");
    }
}