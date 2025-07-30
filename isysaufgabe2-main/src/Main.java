package src;

import java.io.IOException;
import java.util.*;

import static src.ReadFile.*;

public class Main {
    public static void main(String[] args) throws IOException {

        //--------------BigGraph-----------
        Map<String, City> cities = new HashMap<>();
        List<Connection> connections = new ArrayList<>();
        try {
            citiesFileBig("src/testcases_Teilaufgabe_3/bigGraph_cities.txt", cities);
            connectionFile("src/testcases_Teilaufgabe_3/bigGraph_connections.txt", connections);
        } catch (IOException e) {
            System.err.println("Fehler beim Einlesen der Dateien.");
        }
        List<String[]> bigGraphCityPairs = loadCityPairs("src/testcases_Teilaufgabe_3/testcases_bigGraph.txt");
        Search searchBigCities = new Search(cities, connections);
        long startTime = System.nanoTime(); // Startzeitpunkt, Zeitmessung starten
        for (String[] pair : bigGraphCityPairs) {
            String startCity = pair[0];
            String goalCity = pair[1];
            try {
                String path = searchBigCities.aStar(startCity, goalCity);
                //System.out.println("Optimaler Pfad für BigGraph (" + startCity + " - " + goalCity + "): " + path);
            } catch (IllegalStateException e) {
                System.out.println("Kein gültiger Pfad gefunden (" + startCity + " - " + goalCity + ")");
            }
        }
        long endTime = System.nanoTime(); // Endzeitpunkt
        // Berechne und gebe den durchschnittlichen Kostenwert aus
        System.out.println("Durchschnittliche Kosten aller Testfälle: " + Search.averagePathCost(Search.optCostsAverage));
        System.out.println("Durchschnittliche maximale Größe des offenen Sets: " + Search.averageMaxOpenSetSize(Search.maxOpenSetSizes));
        System.out.println("Durchschnittliche Anzahl expandierter Knoten: " + Search.averageExpandedNodeCount(Search.expandedNodeCounts));

        //print time
        System.out.printf("Zeit: %.2f Sekunden%n", ((endTime - startTime) / 1_000_000_000.0));
        // Durchschnittliche Abweichung berechnen
        List<Double> actualCosts = Search.optCostsAverage;
        String optCostsFile = "src/bigGraph_results.txt";
        double deviation = calculateDeviation(ReadFile.optCost(optCostsFile), actualCosts);
        System.out.printf("Durchschnittliche Abweichung der Pfade: %.2f%%", deviation);
        //------------------------------------
        /*
        //--------------Cities----------------
        String[] testCases = {"t0", "t1", "t2", "t3", "t4", "t5", "t6", "t7"};
        String basePath = "src/testcases_Teilaufgabe_2/";

        for (String testCase : testCases) {
            //System.out.println("Testfall " + testCase + ":");
            String citiesFile = basePath + testCase + "_cities.txt";
            String connectionsFile = basePath + testCase + "_connections.txt";
            Map<String, City> cities = new HashMap<>();
            List<Connection> connections = new ArrayList<>();
            try {
                citiesFile(citiesFile, cities);
                connectionFile(connectionsFile, connections);
            } catch (IOException e) {
                System.err.println("Fehler beim Einlesen der Dateien für Testfall " + testCase + ": " + e.getMessage());
                continue; // Gehe zum nächsten Testfall
            }
            // Suche starten
            Search search = new Search(cities, connections);
            try {
                String path = search.aStar("A", "B");
                //System.out.println("Optimaler Pfad für Testfall " + testCase + ": " + path);
            } catch (IllegalStateException e) {
                System.out.println("Kein gültiger Pfad gefunden für " + testCase);
            }
        }
        -----------------------------------*/
    }

    // Berechne die Abweichung der Pfadlänge vom Optimum
    public static double calculateDeviation(List<Double> optCosts, List<Double> actualCosts) {
        double totalDeviation = 0.0;
        for (int i = 0; i < optCosts.size(); i++) {
            double deviation = (actualCosts.get(i) - optCosts.get(i)) / optCosts.get(i) * 100;
            totalDeviation += deviation;
        }
        return totalDeviation / optCosts.size();
    }

}
