package utils;

/*************************************************************************
 *  Referenced from https://www.cs.duke.edu/courses/cps100/fall10/snarf/jaritup/stego/princeton/StdRandom.java
 *  A library of static methods to generate random numbers from
 *  different distributions (poisson, uniform, gaussian,
 *  discrete, and exponential).
 *
 *  Remark
 *  ------
 *    - Uses Math.random() which generates a pseudorandom real number
 *      in [0, 1)
 *
 *    - This library does not allow you to set the pseudorandom number
 *      seed. See java.util.Random.
 *
 *************************************************************************/

public class RandomNumGen {

    // return a real number uniformly in [0, 1]
    public static double uniform() {
        return Math.random();
    }

    // return a real number uniformly in [a, b]
    public static double uniform(double a, double b) {
        return a + Math.random() * (b-a);
    }

    // return an integer uniformly between 0 and N-1
    public static int uniform(int N) {
        return (int) (Math.random() * N);
    }

    // return a boolean, which is true with prob p and false otherwise
    public static boolean bernoulli(double p) {
        return Math.random() < p;
    }

    // return a real number with a standard Gaussian distribution
    // uses the polar form of the Box-Muller transform
    public static double gaussian() {
        double r, x, y;
        do {
            x = uniform(-1.0, 1.0);
            y = uniform(-1.0, 1.0);
            r = x*x + y*y;
        } while (r >= 1 || r == 0);
        return x * Math.sqrt(-2 * Math.log(r) / r);
    }

    // return a real number from a gaussian distribution with given mean and stddev
    public static double gaussian(double mean, double stddev) {
        return mean + stddev * gaussian();
    }

    // return i with probability a[i]
    // precondition: sum of array entries equals 1
    public static int discrete(double[] a)  {
        double r = Math.random();
        double sum = 0.0;
        for (int i = 0; i < a.length; i++) {
            sum = sum + a[i];
            if (sum >= r) return i;
        }
        assert(false);
        return -1;
    }

    // exponential random variable with rate lambda
    public static double exp(double lambda) {
        return -Math.log(1 - Math.random()) / lambda;
    }
}
