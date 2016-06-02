package utils.shortestPath;

import utils.libs.Bag;

/**
 * Created by jherez on 6/2/16.
 */
public class Path {
    public Bag<DirectedEdge> list;
    public int v;
    public int w;
    public double weight;

    public Path(int v, double weight, int w, Bag<DirectedEdge> list) {
        this.v = v;
        this.w = w;
        this.weight = weight;
        this.list = list;
    }

    public Path(){
        this.list = new Bag<DirectedEdge>();
    }

    public int getW() {
        return w;
    }

    public void setW(int w) {
        this.w = w;
    }

    public Bag<DirectedEdge> getList() {
        return list;
    }

    public void setList(Bag<DirectedEdge> list) {
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
