package city;

import utils.libs.Bag;
import utils.libs.In;
import utils.libs.StdOut;
import utils.shortestPath.DijkstraSP;
import utils.shortestPath.DirectedEdge;
import utils.shortestPath.EdgeWeightedDigraph;
import utils.shortestPath.Path;

import java.util.ArrayList;

public class City {
    public double k = 0.5;
    public EdgeWeightedDigraph G;
    public DijkstraSP sp;
    int totalCalls = 0;
    int totalPassengers = 0;
    public ArrayList<Intersection> intersections;
    public ArrayList<DropoffPoint> dropoffPoints;

    public City() {
        In in = new In("src/main/resources/v_city.txt");
        generateCity(in);
    }

    public void generateCity(In in) {
        G = new EdgeWeightedDigraph(in);
        this.intersections = extractIntersections(G);
        extendGraph(G, k);
        this.dropoffPoints = extractDropoffPoints(G);
        this.totalCalls = 0;
    }

    /**
     * Calculate shortest paths from s to all other vertices
     *
     * @param G EdgeWeightedDigraph.
     * @param s source node
     * @return an @ArrayList of @Intersections
     */
    public void getShortestPaths(EdgeWeightedDigraph G, int s){
        this.sp = new DijkstraSP(G, s);
    }

    /**
     * Extract a list of all intersection indices within the graph
     *
     * @param G EdgeWeightedDigraph.
     * @return an @ArrayList of @Intersections
     */
    public ArrayList<Intersection> extractIntersections(EdgeWeightedDigraph G) {
        Iterable<DirectedEdge> adj = G.edges();
        ArrayList<Intersection> list = new ArrayList<Intersection>();

        for (DirectedEdge e : adj) {
            Intersection i = new Intersection();
            i.w = e.from();
            i.v = e.to();
            i.index = e.from();
            list.add(i);
        }
        return list;
    }

    /**
     * Extract a list of all dropoff points, includes intersections as well
     *
     * @param G EdgeWeightedDigraph.
     * @return an @ArrayList of @DropoffPoints
     */
    public ArrayList<DropoffPoint> extractDropoffPoints(EdgeWeightedDigraph G) {
        Iterable<DirectedEdge> adj = G.edges();
        ArrayList<DropoffPoint> list = new ArrayList<DropoffPoint>();

        DropoffPoint x = new DropoffPoint();
        for (DirectedEdge e : adj) {
            x.index = e.from();
            list.add(x);
        }
        return list;
    }

    /**
     * Split the weights of the edges into k sections. This is necessary
     * for allowing a taxi to stop at a point that is between the edges of
     * the original graph. Ex, split 1KM into 200m sections. 1/0.2
     *
     * @param k the divisor to be used as the new increment.
     */
    public void extendGraph(EdgeWeightedDigraph G, double k) {
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
     * Returns a list of shortest paths from the source vertex w to all other vertices
     * with a cost of <= d.
     *
     * @param sp DijkstraSP result
     * @param G  the edge weighted graph
     * @param w  the source vertex
     * @param d  the distance to travel
     * @return a shortest path from the source vertex w to vertex v
     * as an iterable of Paths with distance <= d
     */
    public Bag<Path> getRoutes(EdgeWeightedDigraph G, DijkstraSP sp, int w, double d) {
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

    /**
     * Prints the shortest path from the source vertex w to all other vertices v
     * @param w the source vertex
     */
    public void printSP(EdgeWeightedDigraph G,DijkstraSP sp, int w) {
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
     * Prints the a list of paths from the source vertex w to all other vertices with
     * distance <= d
     * @param res a list of Paths
     */
    public void printRoutes(Bag<Path> res){
        for (Path p : res) {
            StdOut.printf("%d to %d (%.2f)  ", p.w, p.v, p.weight);
            for (DirectedEdge e : p.list) {
                StdOut.print(e + " ");
            }
            StdOut.println();
        }
    }



}


