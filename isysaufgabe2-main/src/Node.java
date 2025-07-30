package src;

public class Node {
    private final State state;     // Zustand des Knotens
    private final Node parent;     // Vorgängerknoten
    private final double pathCost; // Gesamtkosten des Pfads
    private final double heuristic; // Heuristikwert (h)

    public Node(State state, Node parent, double pathCost, double heuristic) {
        this.state = state;
        this.parent = parent;
        this.pathCost = pathCost;
        this.heuristic = heuristic;
    }

    // Getter
    public State getState() {
        return state;
    }

    public Node getParent() {
        return parent;
    }

    public double getPathCost() {
        return pathCost;
    }

    public double getHeuristic() {
        return heuristic;
    }

    @Override
    public String toString() {
        return "Node{" +
                "state=" + state +
                ", pathCost=" + pathCost +
                ", heuristic=" + heuristic +
                '}';
    }


}
