package city;


import java.io.Serializable;

public class DropoffPoint implements Serializable{
    public int index;

    @Override
    public String toString() {
        return "DropoffPoint{" +
                "index=" + index +
                '}';
    }

    public DropoffPoint(int index) {
        this.index = index;
    }

    public DropoffPoint() {
    }
}
