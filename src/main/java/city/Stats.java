package city;

import utils.misc.Shift;

import java.io.Serializable;

/**
 * Created by eduardosalazar1 on 6/13/16.
 */
//Per taxi
public class Stats implements Serializable, Comparable<Object>{
    public String name;
    public int total_passengers = 0;
    public double total_money_company = 0.0;
    public double total_money_earn = 0.0;
    public double min_price = 1234123.0;
    public double max_price = 0;
    public Shift shift;

    public void addBid(Bid bid){
        total_money_company += bid.company;
        total_money_earn += bid.payOff;

        if(min_price > bid.price){
            min_price = bid.price;
        }

        if(max_price < bid.price){
            max_price = bid.price;
        }
    }

    @Override
    public int compareTo(Object o) {
            Stats  s=(Stats) o;
            return (this.total_money_earn+"").compareTo(s.total_money_earn+"");

    }
}
