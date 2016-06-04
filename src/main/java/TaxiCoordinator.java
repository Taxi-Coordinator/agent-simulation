import city.City;
import city.Intersection;
import city.Passenger;

public class TaxiCoordinator {

    public static void main(String[] args) {
        System.out.println("Hello Taxi");
        int s = Integer.parseInt("1");
        City c = new City();
//        StdOut.println(c.G.toString());
//        for(Intersection i: c.intersections){
//            System.out.println(i.v);
//        }

        Intersection x = new Intersection(0, 0, 1);
        c.addPassenger(x);
        Passenger p = c.passengerArrayList.get(0);
        c.setPassengerRoute(p);
        System.out.print(p);
//        for(int i = 0; i < 20; i++){
//            StdOut.println(StdRandom.uniform(0,4));
//        }


//        c.getShortestPaths(c.G, s);
//
//        Bag<Path> res = c.getRoutes(c.G, c.sp, s, 2);
//        c.printRoutes(res);
    }
}
