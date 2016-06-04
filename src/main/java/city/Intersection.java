package city;

public class Intersection {
    public int w;

    @Override
    public String toString() {
        return "Intersection{" +
                "w=" + w +
                ", v=" + v +
                ", calls=" + calls +
                ", index=" + index +
                '}';
    }

    public int v;
    public int calls = 0;
    public int index;

    public Intersection(int w, int index, int v) {
        this.w = w;
        this.index = index;
        this.v = v;
    }

    public Intersection() {
    }

    public void receiveCall() {
        this.calls++;
    }
}
