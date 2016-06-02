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

//        printSP(sp, s);
//        updateGraph();
//        StdOut.println(G.toString());
        DijkstraSP sp = new DijkstraSP(G, s);

        Bag<Path> res = getRoute(sp,1,2);
        for(Path p: res){
            StdOut.printf("%d to %d (%.2f)  ", p.v, p.w, p.weight);
            for (DirectedEdge e : p.list){
                StdOut.print(e +" ");
            }
            StdOut.println();
        }


//        printSP(sp, s);

//        for (int v = 0; v <= res.length; v++) {
//            for (DirectedEdge e : res[v]) {
//                StdOut.println(e);
//            }
//        }
    }

    public static void updateGraph(){
//        Iterable<DirectedEdge> adj = G.adj(1);
        Iterable<DirectedEdge> adj = G.edges();
        for (DirectedEdge e : adj) {

            double weight = e.weight();
            int initial_vertices = G.V();
            int intermediary_edges = (int)(weight/0.5);

            G.V(G.V()+(intermediary_edges-1));
            G.E(G.E()+(intermediary_edges-1));

            // Attach edge to first and last intermediary nodes
            G.addEdge(new DirectedEdge(e.from(),G.V() - (intermediary_edges-1),0.5));
            G.addEdge(new DirectedEdge((G.V()-1),e.to(),0.5));

            for(int i = 1; i < intermediary_edges-1; i++){
                G.addEdge(new DirectedEdge(initial_vertices,initial_vertices+1,0.5));
                initial_vertices++;
            }
        }
    }

    public static void printSP(DijkstraSP sp, int s) {
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

    public static Bag<Path> getRoute(DijkstraSP sp, int x, double d) {
//        Bag<DirectedEdge> list = new Bag<DirectedEdge>();
        Bag<Path> list = new Bag<Path>();
        int n = 0;
        for (int t = 0; t < G.V(); t++) {
            if (sp.hasPathTo(t) && sp.distTo(t) <= d) {
//                StdOut.printf("%d to %d (%.2f)  ", x, t, sp.distTo(t));
                Path temp = new Path();
                temp.v = x;
                temp.w = t;
                temp.weight = sp.distTo(t);

                for (DirectedEdge e : sp.pathTo(t)) {
                    temp.list.add(e);
                    n++;
//                    StdOut.print(e + "   ");
                }
                list.add(temp);
//                StdOut.println();
            }
            else {
//                StdOut.printf("%d to %d         no path\n", x, t);
            }
        }
        return list;
    }
}
