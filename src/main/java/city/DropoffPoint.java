package city;


import java.io.Serializable;
import java.util.ArrayList;
public class DropoffPoint implements Serializable{

    public int index;
    public ArrayList<Passenger> completedJobs;

    @Override
    public String toString() {
        return "DropoffPoint{" +
                "index=" + index +
                ", completedJobs=" + completedJobs +
                '}';
    }

    public DropoffPoint(int index) {
        this.index = index;
        this.completedJobs = new ArrayList<>();
    }

    public DropoffPoint() {
    }

    public void completeJob(Passenger passenger) {
        this.completedJobs.add(passenger);
    }
}
