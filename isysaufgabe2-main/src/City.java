package src;

public class City {

        String name;
        double heuristic;
        double lat;
        double lon;
        String availablePermit;

        City(String name, double heuristic, String availablePermit) {
            this.name = name;
            this.heuristic = heuristic;
            this.availablePermit = availablePermit;
        }

        City(String name, double lat, double lon, String availablePermit) {
            this.name = name;
            this.lat = lat;
            this.lon = lon;
            this.availablePermit = availablePermit;
        }

        //Getter
        public double getLatitude() {
            return lat;
        }

        public double getLongitude() {
            return lon;
        }

        @Override
        public String toString() {
            return "City{name='" + name + "', heuristic=" + heuristic + ", availablePermit='" + availablePermit + "'}";
        }
    }

