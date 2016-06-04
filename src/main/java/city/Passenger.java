package city;

import utils.libs.StdRandom;

public class Passenger {
    public static double mu = 2.0;
    public static double sigma = 1.5;
    public Intersection origin;
    public DropoffPoint destination;
    public double d;

    public Passenger(){
//        this.origin = origin;
        this.d = getTravelDistance();
    }

    public double getTravelDistance(){
        return StdRandom.gaussian(mu,sigma);
    }

    public void clear(){
        this.d = 0;
    }
}
