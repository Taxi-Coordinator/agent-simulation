package city;

import utils.libs.StdRandom;
import utils.shortestPath.Path;

public class Passenger {
    public static double mu = 2.0;
    public static double sigma = 1.5;
    public Intersection origin;
    public int destination;
    public double d;
    public Path route;

    public Passenger(Intersection origin){
        this.origin = origin;
        this.d = getTravelDistance();
    }

    @Override
    public String toString() {
        return "Passenger{" +
                "origin=" + origin +
                ", destination=" + destination +
                ", d=" + d +
                ", route=" + route +
                '}';
    }

    public double getTravelDistance(){
        double d = StdRandom.gaussian(mu,sigma);
        if(Math.abs(d) - (int) d > City.k){
            d = Math.ceil(d);
        }
        else {
            d = (int)d + City.k;
        }
        return d;
    }

    public void clear(){
        this.origin = null;
        this.d = 0;
    }
}
