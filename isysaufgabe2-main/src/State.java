package src;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class State {

    private final String city;
    private final Set<String> permits;
    private final Set<String> visitedCities;


    public State(String city, Set<String> permits, Set<String> visitedCities) {
        this.city = city;
        this.permits = new HashSet<>(permits);
        this.visitedCities = new HashSet<>(visitedCities);
    }

    // Erstelle einen neuen Zustand mit einer weiteren besuchten Stadt
    public State withVisitedCity(String newCity) {
        Set<String> newVisitedCities = new HashSet<>(this.visitedCities.size());
        newVisitedCities.addAll(this.visitedCities);
        newVisitedCities.add(newCity);
        return new State(newCity, this.permits, newVisitedCities);
    }

    public State withPermit(String permit) {
        if (permits.contains(permit)) return this; // Keine Änderung erforderlich
        Set<String> newPermits = new HashSet<>(permits);
        newPermits.add(permit);
        return new State(city, newPermits, visitedCities);
    }

    // Getter
    public String getCity() {
        return city;
    }

    public Set<String> getPermits() {
        return new HashSet<>(permits);
    }

    public Set<String> getVisitedCities() {
        return visitedCities;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        State state = (State) o;
        return city.equals(state.city) &&
                permits.equals(state.permits);
                //&& visitedCities.equals(state.visitedCities);
    }


    @Override
    public int hashCode() {
        return Objects.hash(city, permits); // Entferne visitedCities
    }
}
