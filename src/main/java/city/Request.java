package city;

import jade.core.AID;

import java.io.Serializable;

/**
 * Created by jherez on 6/11/16.
 */
public class Request implements Serializable {
    public final Intersection origin;
    public final DropoffPoint destination;
    public Bid bid;
<<<<<<< 2c3deb6d69da5d0da7abddb2353a49d1054f7e1c
    public final int passengerID;
    public AID bidder;
=======
    public int passengerID;
    public Stats stats;
<<<<<<< HEAD
>>>>>>> Finally working
=======
>>>>>>> 38773ce7273421764beb4c396612b164e59fe91a

    public Request(Intersection origin, DropoffPoint destination, int passengerID) {
        this.origin = origin;
        this.destination = destination;
        this.passengerID = passengerID;
        this.stats = new Stats();
    }

}
