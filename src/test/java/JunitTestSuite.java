import city.CityTest;
import city.PassengerTest;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import utils.agentMethods.TaxiMethodsTest;
import utils.simulation.CallGenTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        CityTest.class,
        PassengerTest.class,
        CallGenTest.class,
        TaxiMethodsTest.class
})
public class JunitTestSuite {
}