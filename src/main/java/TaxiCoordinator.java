import utils.libs.In;
import utils.libs.StdOut;
import utils.shortestPath.DijkstraSP;
import utils.shortestPath.DirectedEdge;
import utils.shortestPath.EdgeWeightedDigraph;

public class TaxiCoordinator {
    public static void main(String[] args) {
//        System.out.println("Hello Taxi");
//        int N = 15;
//        double mean = 2.0;
//        double stddev = 1.5;
//        for(int i = 1; i <= N; i++) {
//            System.out.println(RandomNumGen.gaussian(mean,stddev));
//        }

        In in = new In("src/main/resources/v_city.txt");
        EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);
//        StdOut.println(G);
        int s = Integer.parseInt("41");

        // compute shortest paths
        DijkstraSP sp = new DijkstraSP(G, s);


        // print shortest path
        for (int t = 0; t < G.V(); t++) {
            if (sp.hasPathTo(t)) {
                StdOut.printf("%d to %d (%.2f)  ", s, t, sp.distTo(t));
                for (DirectedEdge e : sp.pathTo(t)) {
                    StdOut.print(e + "   ");
                }
                StdOut.println();
            }
            else {
                StdOut.printf("%d to %d         no path\n", s, t);
            }
        }
    }
}
