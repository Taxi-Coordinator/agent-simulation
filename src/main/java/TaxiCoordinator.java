import utils.libs.Bag;
import utils.libs.In;
import utils.libs.StdOut;
import utils.shortestPath.DijkstraSP;
import utils.shortestPath.DirectedEdge;
import utils.shortestPath.EdgeWeightedDigraph;
import utils.shortestPath.Path;

public class TaxiCoordinator {

    public static In in = new In("src/main/resources/v_city.txt");
    public static EdgeWeightedDigraph G = new EdgeWeightedDigraph(in);

    public static void main(String[] args) {
        System.out.println("Hello Taxi");
        int s = Integer.parseInt("1");
        // compute shortest paths


        extendGraph(0.5);
//        printSP(sp, s);
//        StdOut.println(G.toString());
//        DijkstraSP sp = new DijkstraSP(G, s);

//        Bag<Path> res = getRoute(sp, 1, 2);
//        for (Path p : res) {
//            StdOut.printf("%d to %d (%.2f)  ", p.w, p.v, p.weight);
//            for (DirectedEdge e : p.list) {
//                StdOut.print(e + " ");
//            }
//            StdOut.println();
//        }


//        printSP(sp, s);

//        for (int v = 0; v <= res.length; v++) {
//            for (DirectedEdge e : res[v]) {
//                StdOut.println(e);
//            }
//        }
    }

    /**
     * Split the weights of the edges into k sections. This is necessary
     * for allowing a taxi to stop at a point that is between the edges of
     * the original graph. Ex, split 1KM into 200m sections. 1/0.2
     *
     * @param k the divisor to be used as the new increment.
     */
    public static void extendGraph(double k) {
        Iterable<DirectedEdge> adj = G.edges();
        for (DirectedEdge e : adj) {

            double weight = e.weight();
            int initial_vertices = G.V();
            int intermediary_edges = (int) (weight / k);

            G.V(G.V() + (intermediary_edges - 1));
            G.E(G.E() + (intermediary_edges - 1));

            // Attach edge to first and last intermediary nodes
            G.addEdge(new DirectedEdge(e.from(), G.V() - (intermediary_edges - 1), k));
            G.addEdge(new DirectedEdge((G.V() - 1), e.to(), k));

            for (int i = 1; i < intermediary_edges - 1; i++) {
                G.addEdge(new DirectedEdge(initial_vertices, initial_vertices + 1, k));
                initial_vertices++;
            }
        }
    }

    /**
     * Prints the shortest path from the source vertex w to all other vertices v
     * @param w the source vertex
     */
    public static void printSP(DijkstraSP sp, int w) {
        for (int v = 0; v < G.V(); v++) {
            if (sp.hasPathTo(v)) {
                StdOut.printf("%d to %d (%.2f)  ", w, v, sp.distTo(v));
                for (DirectedEdge e : sp.pathTo(v)) {

                    StdOut.print(e + "   ");
                }
                StdOut.println();
            } else {
                StdOut.printf("%d to %d         no path\n", w, v);
            }
        }
    }

    /**
     * Returns a list of shortest paths from the source vertex w to all other vertices
     * with a cost of <= d.
     *
     * @param w the source vertex
     * @param d the distance to travel
     * @return a shortest path from the source vertex w to vertex v
     * as an iterable of Paths with distance <= d
     */
    public static Bag<Path> getRoute(DijkstraSP sp, int w, double d) {
        Bag<Path> list = new Bag<Path>();
        for (int v = 0; v < G.V(); v++) {
            if (sp.hasPathTo(v) && sp.distTo(v) <= d) {
                Path p = new Path();

                p.w = w;
                p.v = v;
                p.weight = sp.distTo(v);
                for (DirectedEdge e : sp.pathTo(v)) {
                    p.list.add(e);
                }
                list.add(p);
            }
        }
        return list;
    }
}
