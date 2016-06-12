package city;

import java.io.Serializable;

/**
 * Created by jherez on 6/11/16.
 */
public class Request implements Serializable{
    public Intersection origin;
    public DropoffPoint destination;

    public Request(Intersection origin, DropoffPoint destination) {
        this.origin = origin;
        this.destination = destination;
    }
}
