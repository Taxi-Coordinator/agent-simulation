import city.City;
import utils.libs.Bag;
import utils.shortestPath.Path;

public class TaxiCoordinator {

    public static void main(String[] args) {
        System.out.println("Hello Taxi");
        int s = Integer.parseInt("1");
        City c = new City();
//        StdOut.println(c.G.toString());
//        for(Intersection i: c.intersections){
//            System.out.println(i.v);
//        }

        c.getShortestPaths(c.G,s);

        Bag<Path> res = c.getRoutes(c.G,c.sp, s, 2);
        c.printRoutes(res);
    }
}
