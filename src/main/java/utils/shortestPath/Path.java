package utils.shortestPath;

import java.util.ArrayList;


public class Path {
    public ArrayList<Edge> list;
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

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public ArrayList<Edge> getList() {
        return list;
    }

    public void setList(ArrayList<Edge> list) {
        this.list = list;
    }

    public int getV() {
        return v;
    }

    public void setV(int v) {
        this.v = v;
    }

    public double getWeight() {
        return weight;
    }

    public void setWeight(double weight) {
        this.weight = weight;
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
