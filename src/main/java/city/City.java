package city;

import utils.libs.In;
import utils.libs.StdOut;
import utils.libs.StdRandom;
import utils.shortestPath.DijkstraSP;
import utils.shortestPath.DirectedEdge;
import utils.shortestPath.EdgeWeightedDigraph;
import utils.shortestPath.Path;

import java.util.ArrayList;

public class City {
    public static double k = 0.5;
    public EdgeWeightedDigraph G;
    public DijkstraSP sp;
    int totalCalls = 0;
    int taxiCenter = 27;
    int totalPassengers = 0;
    public ArrayList<Intersection> intersections;
    public ArrayList<DropoffPoint> dropoffPoints;
    public ArrayList<Passenger> passengerArrayList;

    public City() {
        In in = new In("src/main/resources/v_city.txt");
        generateCity(in);
    }

    public void generateCity(In in) {
        G = new EdgeWeightedDigraph(in);
        this.intersections = extractIntersections(G);
//        extendGraph(G, k);
        this.dropoffPoints = extractDropoffPoints(G);
        this.totalCalls = 0;
        this.totalPassengers = 0;
        this.passengerArrayList = new ArrayList<Passenger>();
    }

    public void generateCity(In in, int extend) {
        G = new EdgeWeightedDigraph(in);
        this.intersections = extractIntersections(G);
        if (extend == 1) {
            extendGraph(G, k);
        }
        this.dropoffPoints = extractDropoffPoints(G);
        this.totalCalls = 0;
        this.totalPassengers = 0;
        this.passengerArrayList = new ArrayList<Passenger>();
    }

    public void clear() {
        this.G = null;
        this.sp = null;
        this.totalCalls = 0;
        this.totalPassengers = 0;
        this.intersections = null;
        this.dropoffPoints = null;
        this.passengerArrayList = null;
    }

    public void addPassenger(Intersection intersection) {
        this.passengerArrayList.add(new Passenger(intersection));
    }

    public void setPassengerRoute(Passenger p) {
        DijkstraSP sp = new DijkstraSP(this.G, p.origin.w);
        ArrayList<Path> paths = getRoutes(G, sp, p.origin.w, p.d);
        System.out.println(paths.size());
        int rand = StdRandom.uniform(0, paths.size());
        Path destination = paths.get(rand);
        p.destinationNode = destination.v;
        p.route = destination;
    }

    /**
     * Calculate shortest paths from source &w; to all other vertices &v_i;
     *
     * @param G EdgeWeightedDigraph.
     * @param w source node
     * @return an @ArrayList of @Intersections
     */
    public void getShortestPaths(EdgeWeightedDigraph G, int w) {
        this.sp = new DijkstraSP(G, w);
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
     * Split the weights of the edges into &k; sections. This is necessary
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
     * Returns a list of shortest paths from the source vertex &w; to all other vertices &v_i;
     * with a cost <= &d;.
     *
     * @param sp DijkstraSP result
     * @param G  the edge weighted graph
     * @param w  the source vertex
     * @param d  the distance to travel
     * @return a shortest path from the source vertex w to vertex v
     * as an iterable of Paths with distance <= d
     */
    public ArrayList<Path> getRoutes(EdgeWeightedDigraph G, DijkstraSP sp, int w, double d) {
        ArrayList<Path> list = new ArrayList<Path>();
        for (int v = 0; v < G.V(); v++) {
            if (sp.hasPathTo(v) && sp.distTo(v) == d) {
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
     * Prints the shortest path from the source vertex &w; to all other vertices &v;
     *
     * @param w the source vertex
     */
    public void printSP(EdgeWeightedDigraph G, DijkstraSP sp, int w) {
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
     * Prints the list of paths from the source vertex &w; to all other vertices with
     * distance <= &d;
     *
     * @param res a list of Paths
     */
    public void printRoutes(ArrayList<Path> res) {
        for (Path p : res) {
            StdOut.printf("%d to %d (%.2f)  ", p.w, p.v, p.weight);
            for (DirectedEdge e : p.list) {
                StdOut.print(e + " ");
            }
            StdOut.println();
        }
    }


}


