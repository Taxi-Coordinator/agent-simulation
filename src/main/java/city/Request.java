package city;

/**
 * Created by jherez on 6/11/16.
 */
public class Request {
    public Intersection origin;
    public DropoffPoint destination;
    public int passengerID;

    public Request(Intersection origin, DropoffPoint destination, int passengerID) {
        this.origin = origin;
        this.destination = destination;
        this.passengerID = passengerID;
    }
}
