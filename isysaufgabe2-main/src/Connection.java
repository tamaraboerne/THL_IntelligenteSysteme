package src;

public class Connection {
    String city1;
    String city2;
    double distance;
    String requiredPermit;

    Connection(String city1, String city2, double distance, String requiredPermit) {
        this.city1 = city1;
        this.city2 = city2;
        this.distance = distance;
        this.requiredPermit = requiredPermit;
    }

    @Override
    public String toString() {
        return "Connection{city1='" + city1 + "', city2='" + city2 + "', distance=" + distance + ", requiredPermit='" + requiredPermit + "'}";
    }
}

