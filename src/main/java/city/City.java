package city;

import utils.io.In;
import utils.io.StdOut;
import utils.simulation.StdRandom;
import utils.shortestPath.DijkstraUndirectedSP;
import utils.shortestPath.Edge;
import utils.shortestPath.EdgeWeightedGraph;
import utils.shortestPath.Path;

import java.util.ArrayList;
import java.util.HashMap;

public class City {
    /**
     * &k; is the parameter we use to decide by how much to split the edges of the graph
     * see {@link City}
     *
     * &multiplier; is a bit of a hack, we needed to create intermediary edges in the graph
     * These would require more vertices and edges, so we just simply initialized the
     * values multiplied by 10 to accommodate the new nodes and edges
     * see {@link EdgeWeightedGraph}
     *
     */
    public static double k = 0.5;
    public static int multiplier = 10;

    public EdgeWeightedGraph G;
    public DijkstraUndirectedSP sp;
    int totalCalls = 0;
    public int taxiCenter = 27;
    int totalPassengers = 0;
    public ArrayList<Intersection> intersections;
    public ArrayList<DropoffPoint> dropoffPoints;
    public ArrayList<Passenger> passengerArrayList;

    public City() {
        In in = new In("src/main/resources/v_city.txt");
        generateCity(in);
    }

    public void generateCity(In in) {
        G = new EdgeWeightedGraph(in);
        this.intersections = extractIntersections(G);
        extendGraph(G, k);
        this.dropoffPoints = extractDropoffPoints(G);
        this.totalCalls = 0;
        this.totalPassengers = 0;
        this.passengerArrayList = new ArrayList<Passenger>();
    }

    public void generateCity(In in, int extend) {
        G = new EdgeWeightedGraph(in);
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
        DijkstraUndirectedSP sp = new DijkstraUndirectedSP(this.G, p.origin.index);
        ArrayList<Path> paths = getRoutes(G, sp, p.origin.index, p.d);
        int rand = StdRandom.uniform(0, paths.size());
//        System.out.println(paths.size());
        Path destination = paths.get(rand);
        p.destinationNode = destination.v;
        p.route = destination;
    }

    /**
     * Calculate shortest paths from source &w; to all other vertices &v_i;
     *
     * @param G EdgeWeightedGraph.
     * @param w source node
     * @return an @ArrayList of @Intersections
     */
    public void getShortestPaths(EdgeWeightedGraph G, int w) {
        this.sp = new DijkstraUndirectedSP(G, w);
    }

    /**
     * Extract a list of all intersection indices within the graph
     *
     * @param G EdgeWeightedGraph.
     * @return an @ArrayList of @Intersections
     */
    public ArrayList<Intersection> extractIntersections(EdgeWeightedGraph G) {
        Iterable<Edge> edges = G.edges();
        ArrayList<Intersection> list = new ArrayList<Intersection>();
        HashMap seen = new HashMap();

        Intersection i = new Intersection();
        for (Edge e : edges) {
            i.index = e.other(e.either());
            if(seen.get(i.index) == null) {
                Iterable<Edge> adj = G.adj(i.index);
                for(Edge a : adj) {
                    i.connections.add(a.either());
                }
                list.add(i);
                seen.put(i.index,i.index);
                i = new Intersection();
            }
        }
        return list;
    }

    /**
     * Extract a list of all dropoff points, includes intersections as well
     *
     * @param G EdgeWeightedGraph.
     * @return an @ArrayList of @DropoffPoints
     */
    public ArrayList<DropoffPoint> extractDropoffPoints(EdgeWeightedGraph G) {
        Iterable<Edge> adj = G.edges();
        ArrayList<DropoffPoint> list = new ArrayList<DropoffPoint>();

        DropoffPoint x = new DropoffPoint();
        for (Edge e : adj) {
            x.index = e.other(e.either());
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
    public void extendGraph(EdgeWeightedGraph G, double k) {
        Iterable<Edge> adj = G.edges();
        for (Edge e : adj) {

            double weight = e.weight();
            int initial_vertices = G.V();
            int intermediary_edges = (int) (weight / k);

            G.V(G.V() + (intermediary_edges - 1));
            G.E(G.E() + (intermediary_edges - 1));

            // Attach edge to first and last intermediary nodes
            G.addEdge(new Edge(e.other(e.either()), G.V() - (intermediary_edges - 1), k));
            G.addEdge(new Edge((G.V() - 1), e.either(), k));

            for (int i = 1; i < intermediary_edges - 1; i++) {
                G.addEdge(new Edge(initial_vertices, initial_vertices + 1, k));
                initial_vertices++;
            }
        }
    }

    /**
     * Returns a list of shortest paths from the source vertex &w; to all other vertices &v_i;
     * with a cost <= &d;.
     *
     * @param sp DijkstraUndirectedSP result
     * @param G  the edge weighted graph
     * @param w  the source vertex
     * @param d  the distance to travel
     * @return a shortest path from the source vertex w to vertex v
     * as an iterable of Paths with distance <= d
     */
    public ArrayList<Path> getRoutes(EdgeWeightedGraph G, DijkstraUndirectedSP sp, int w, double d) {
        ArrayList<Path> list = new ArrayList<Path>();
        for (int v = 0; v < G.V(); v++) {
            if (sp.hasPathTo(v) && sp.distTo(v) == d) {
                Path p = new Path();

                p.w = w;
                p.v = v;
                p.weight = sp.distTo(v);
                for (Edge e : sp.pathTo(v)) {
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
    public void printSP(EdgeWeightedGraph G, DijkstraUndirectedSP sp, int w) {
        for (int v = 0; v < G.V(); v++) {
            if (sp.hasPathTo(v)) {
                StdOut.printf("%d to %d (%.2f)  ", w, v, sp.distTo(v));
                for (Edge e : sp.pathTo(v)) {

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
            for (Edge e : p.list) {
                StdOut.print(e + " ");
            }
            StdOut.println();
        }
    }

    /**
     * Prints the list of intersections
     */
    public void printIntersections() {
        for (Intersection i : this.intersections) {
            StdOut.print(i.toString());
            StdOut.println();
        }
    }

}
