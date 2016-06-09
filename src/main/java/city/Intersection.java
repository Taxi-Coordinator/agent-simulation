package city;


import utils.Helper;

import java.util.Date;

public class Intersection {
    public int w;
    public Date nextTime;

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

    public void nextTime(Date currentTime){
        this.nextTime = Helper.nextCall(currentTime);
    }

    public void receiveCall() {
        this.calls++;
    }
}
