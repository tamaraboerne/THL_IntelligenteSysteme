package src;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReadFile {

    //Files einlesen:
    public static void citiesFile(String citiesFile, Map<String, City> cities) throws IOException {
        BufferedReader cityReader = new BufferedReader(new FileReader(citiesFile));
        cityReader.readLine(); // Überspringe Header
        String cityLine;
        while ((cityLine = cityReader.readLine()) != null) {
            String[] parts = cityLine.split(";");
            if (parts.length == 3) {
                String cityName = parts[0];
                double h = Double.parseDouble(parts[1]);
                String availablePermit = parts[2];
                cities.put(cityName, new City(cityName, h, availablePermit));
            }
        }
        cityReader.close();
    }

    public static void citiesFileBig(String citiesFile, Map<String, City> cities) throws IOException {
        BufferedReader cityReader = new BufferedReader(new FileReader(citiesFile));
        cityReader.readLine(); // Überspringe Header
        String cityLine;
        while ((cityLine = cityReader.readLine()) != null) {
            String[] parts = cityLine.split(";");
            if (parts.length == 4) {
                String cityName = parts[0];
                double lat = Double.parseDouble(parts[1]);
                double lon = Double.parseDouble(parts[2]);
                String availablePermit = parts[3];
                cities.put(cityName, new City(cityName, lat, lon, availablePermit));
            }
        }
        cityReader.close();
    }

    public static void connectionFile(String connectionsFile, List<Connection> connections) throws IOException {
        BufferedReader connectionReader = new BufferedReader(new FileReader(connectionsFile));
        connectionReader.readLine(); // Überspringe Header
        String connectionLine;
        while ((connectionLine = connectionReader.readLine()) != null) {
            String[] parts = connectionLine.split(";");
            if (parts.length == 4) {
                String city1 = parts[0];
                String city2 = parts[1];
                double distance = Double.parseDouble(parts[2]);
                String requiredPermit = parts[3];
                connections.add(new Connection(city1, city2, distance, requiredPermit));
                connections.add(new Connection(city2, city1, distance, requiredPermit));
            }
        }
        connectionReader.close();
    }

    // Methode zum Einlesen der Start- und Zielpaare
    public static List<String[]> loadCityPairs(String pairsFile) throws IOException {
        List<String[]> pairs = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader(pairsFile));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(";");
            if (parts.length == 2) {
                pairs.add(parts); // Start- und Zielstadt als Paar speichern
            }
        }
        reader.close();
        return pairs;
    }

    //Read optimal PathCosts
    public static List<Double> optCost(String filePath) {
        List<Double> optCosts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                // Erwarte das Format "Stadt1 - Stadt2: Kosten"
                String[] parts = line.split(":");
                if (parts.length == 2) {
                    double cost = Double.parseDouble(parts[1].trim()); // "360.0"
                    optCosts.add(cost);
                }
            }
        } catch (IOException e) {
            System.err.println("Fehler beim Lesen der Datei: " + e.getMessage());
        }
        return optCosts;
    }
}
