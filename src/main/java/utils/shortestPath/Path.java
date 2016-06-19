package utils.shortestPath;

import java.util.ArrayList;


public class Path {
    public final ArrayList<Edge> list;
    public int v;
    public int w;
    public double weight;

    /**
     * @param v the tail vertex
     * @param w the head vertex
     */
    public Path(int v, double weight, int w, ArrayList<Edge> list) {
        this.v = v;
        this.w = w;
        this.weight = weight;
        this.list = list;
    }

    public Path() {
        this.list = new ArrayList<Edge>();
    }


    @Override
    public String toString() {
        return "Path{" +
                "w=" + w +
                ", weight=" + weight +
                ", v=" + v +
                ", list=" + list +
                '}';
    }
}
