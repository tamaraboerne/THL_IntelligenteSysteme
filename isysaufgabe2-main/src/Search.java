package src;

import java.util.*;

public class Search {
    int PERMIT_COST = 10;
    private final double adjustmentFactor = 1.45; // Skalierungsfaktor für die Heuristik

    // Variablen für die Metriken
    public static List<Double> optCostsAverage = new ArrayList<>();
    public static List<Integer> maxOpenSetSizes = new ArrayList<>();
    public static List<Integer> expandedNodeCounts = new ArrayList<>();
    private final Map<String, List<Connection>> connections;
    private final Map<String, City> cities;
    private final Map<State, Boolean> visitedStates = new LinkedHashMap<>();
    private Queue<Node> openSet = new PriorityQueue<>(Comparator.comparingDouble(n -> n.getPathCost() + n.getHeuristic()));
    public List<String> results = new ArrayList<>();

    private Integer expandedNodeCount = 0;
    private  int maxOpenSetSize = 0;

    public Search(Map<String, City> cities, List<Connection> connectionList) {
        this.cities = cities;
        this.connections = new HashMap<>();

        for (Connection connection : connectionList) {
            connections.computeIfAbsent(connection.city1, k -> new ArrayList<>()).add(connection);
        }
    }

    public String aStar(String startCity, String goalCity) {
        //Queue und visited leeren
        openSet.clear();
        visitedStates.clear();

        State startState = new State(startCity, new HashSet<>(), Set.of(startCity));
        Node startNode = new Node(startState, null, 0, calculateHeuristic(startCity, goalCity));
        openSet.add(startNode);

        // Track max open set size
        maxOpenSetSize = openSet.size();


        while (!openSet.isEmpty()) {
            Node current = openSet.poll();
            State currentState = current.getState();

            // Update the maximum size of openSet
            maxOpenSetSize = Math.max(maxOpenSetSize, openSet.size());

            // Wenn der Zustand bereits besucht wurde, überspringe ihn
            if (visitedStates.containsKey(currentState)) {
                continue;
            }

            // Zustand als abgeschlossen markieren
            visitedStates.put(currentState, true);

            // Zielprüfung
            if (currentState.getCity().equals(goalCity)) {
                String path = reconstructPath(current);
                System.out.println("Kosten für " + startCity + " - " + goalCity + ": " + current.getPathCost());
                results.add(startCity + " - " + goalCity + ": " + current.getPathCost());
                optCostsAverage.add(current.getPathCost());
                maxOpenSetSizes.add(maxOpenSetSize);
                expandedNodeCounts.add(expandedNodeCount);
                //FileWriterOutput.writeResultsToFile("src/bigGraph_results.txt", results);
                return path;
            }

            // Nachbarn verarbeiten
            for (Connection connection : connections.getOrDefault(currentState.getCity(), new ArrayList<>())) {
                expandedNodeCount++; // Zähle den expandierten Knoten
                State neighborState = currentState.withVisitedCity(connection.city2);

                double costWithPermit = current.getPathCost() + connection.distance;
                if (!connection.requiredPermit.equals("NONE")) {
                    double permitKosten = permitKosten(currentState, connection.requiredPermit);
                    if (permitKosten == -1) continue; // Überspringen, wenn das Permit nicht erworben werden kann
                    costWithPermit += permitKosten;
                    neighborState = neighborState.withPermit(connection.requiredPermit);
                }

                Node neighborNode = new Node(neighborState, current, costWithPermit, calculateHeuristic(connection.city2, goalCity));

                // Prüfe, ob der Zustand bereits in `visitedStates` ist
                if (visitedStates.containsKey(neighborState)) {
                    continue; // Bereits abgeschlossen, überspringe
                }

                // Prüfe, ob der Zustand bereits in `openSet` ist
                State finalNeighborState = neighborState;
                Optional<Node> existingNode = openSet.stream()
                        .filter(node -> node.getState().equals(finalNeighborState))
                        .findFirst();

                if (existingNode.isPresent()) {
                    // Wenn der neue Zustand günstiger ist, ersetze den alten
                    if (costWithPermit < existingNode.get().getPathCost()) {
                        openSet.remove(existingNode.get()); // Entferne den alten Zustand
                        openSet.add(neighborNode); // Füge den neuen Zustand hinzu
                    }
                } else {
                    // Neuer Zustand, füge ihn hinzu
                    openSet.add(neighborNode);
                }
            }
        }

        throw new IllegalStateException("Kein gültiger Pfad gefunden");
    }

    //Hilfsmethoden
    private double permitKosten(State state, String permit) {
        if (state.getPermits().contains(permit)) {
            return 0; // Genehmigung bereits vorhanden
        }
        for (String visitedCity : state.getVisitedCities()) {
            City city = cities.get(visitedCity);
            if (city != null && city.availablePermit.equals(permit)) {
                return PERMIT_COST; // Genehmigung kann hier gekauft werden
            }
        }
        return -1; // Genehmigung nicht verfügbar
    }
    //Heuristik
    /**
     *
     * @param lat1 Latitude of location 1
     * @param lon1 Longitude of location 1
     * @param lat2 Latitude of location 2
     * @param lon2 Longitude of location 2
     * @return Haversine distance between the two locations
     */
    private double haversine_distance(double lat1, double lon1, double lat2, double lon2) {
        final int d = 12742;
        double sinHalfDeltaLat = Math.sin(Math.toRadians(lat2 - lat1) / 2);
        double sinHalfDeltaLon = Math.sin(Math.toRadians(lon2 - lon1) / 2);
        double latARadians = Math.toRadians(lat1);
        double latBRadians = Math.toRadians(lat2);
        double a = sinHalfDeltaLat * sinHalfDeltaLat
                + Math.cos(latARadians) * Math.cos(latBRadians) * sinHalfDeltaLon * sinHalfDeltaLon;
        return d * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    }

    // Berechnung der Heuristik mit Skalierung
    private double calculateHeuristic(String city1, String city2) {
        City startCity = cities.get(city1);
        City goalCity = cities.get(city2);
        double heuristic = haversine_distance(
                startCity.getLatitude(), startCity.getLongitude(),
                goalCity.getLatitude(), goalCity.getLongitude()
        );
        return heuristic * adjustmentFactor;
    }/* vor Optimierung Heuristik
    private double calculateHeuristic(String city1, String city2) {
        City startCity = cities.get(city1);
        City goalCity = cities.get(city2);
        return haversine_distance(startCity.getLatitude(), startCity.getLatitude(), goalCity.getLatitude(), goalCity.getLongitude());
    }*/


    private String reconstructPath(Node node) {
        StringBuilder path = new StringBuilder();
        Node current = node;

        while (current != null) {
            String permits = current.getState().getPermits().isEmpty() ? "" :
                    " (" + String.join(", ", current.getState().getPermits()) + ")";

            // Überprüfen, ob ein Permit an diesem Knoten gekauft wurde
            String currentCity = current.getState().getCity();
            String parentPermits = current.getParent() != null ? String.join(", ", current.getParent().getState().getPermits()) : "";
            if (!parentPermits.equals(String.join(", ", current.getState().getPermits()))) {
                permits = " (gekauft: " + current.getState().getPermits() + ")";
            }

            path.insert(0, currentCity + permits + " -> ");
            current = current.getParent();
        }
        // Entferne das letzte " -> " aus der Ausgabe
        path.setLength(path.length() - 4);
        return path.toString();
    }



    //Mittelwerte berechnen
    public static double averagePathCost(List<Double> optCostsAverage) {
        double optPathCost = 0;
        for (double optCost : optCostsAverage) {
            optPathCost += optCost;
        }
        double average = optPathCost / optCostsAverage.size();
        return Math.round(average * 100.0) / 100.0; // Runden auf 2 Dezimalstellen
    }

    public static double averageMaxOpenSetSize(List<Integer> maxOpenSetSizes) {
        int total = 0;
        for (int size : maxOpenSetSizes) {
            total += size;
        }
        double average = (double) total / maxOpenSetSizes.size();
        return Math.round(average * 100.0) / 100.0; // Runden auf 2 Dezimalstellen
    }

    public static double averageExpandedNodeCount(List<Integer> expandedNodeCounts) {
        int total = 0;
        for (int count : expandedNodeCounts) {
            total += count;
        }
        double average = (double) total / expandedNodeCounts.size();
        return Math.round(average * 100.0) / 100.0; // Runden auf 2 Dezimalstellen
    }


}

// startknoten heuristik prüfen + vergleichen mit gesamtkosten am ende -> mittelwert von differenz
// heuristik ausgleichen *1,45?

//Zeit / dauer angeben -> um wie viel schneller nach 4?