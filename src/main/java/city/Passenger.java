package city;

import utils.libs.StdRandom;
import utils.shortestPath.Path;

public class Passenger {
    public static double mu = 2.0;
    public static double sigma = 1.5;
    public Intersection origin;
    public Path route;
    public int destinationNode;
    public double d;

    public Passenger(Intersection origin) {
        this.origin = origin;
        this.d = getTravelDistance();
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "origin=" + origin +
                ", destinationNode=" + destinationNode +
                ", d=" + d +
                ", route=" + route +
                '}';
    }

    public double getTravelDistance() {
        double d;
        // Force a positive number from the normal distribution
        do {
            d = StdRandom.gaussian(mu, sigma);
        } while (d < 0);

        // Ensure that the value is a legal distance, based on the
        // division of the graph edges
        if (Math.abs(d) - (int) d > City.k) {
            d = Math.ceil(d);
        } else {
            d = (int) d + City.k;
        }
        return d;
    }

    public void clear() {
        this.origin = null;
        this.route = null;
        this.destinationNode = -1;
        this.d = 0;
    }
}
