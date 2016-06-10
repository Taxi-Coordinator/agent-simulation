package city;


public class DropoffPoint {
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
