import utils.RandomNumGen;

public class TaxiCoordinator {
    public static void main(String[] args) {
        System.out.println("Hello Taxi");
        int N = 15;
        double mean = 2.0;
        double stddev = 1.5;
        for(int i = 1; i <= N; i++) {
            System.out.println(RandomNumGen.gaussian(mean,stddev));
        }
    }
}
