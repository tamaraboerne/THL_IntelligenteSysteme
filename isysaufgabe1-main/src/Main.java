import javax.swing.*;
import java.io.*;
import java.util.*;
public class Main {

    public static ArrayList<Double> wahrscheinlichkeiten = new ArrayList<>();
    public static ArrayList<Integer> gewinne = new ArrayList<>();
    public static HashMap<Integer, Integer> mapErgebnisse = new HashMap<>();
    public static HashMap<Integer, Double> intervall = new HashMap<>();
    public static int ziehungen = 0;

    // Einlesen von Dateien
    public static ArrayList<Double> readFile(String filename) throws FileNotFoundException {
        File myObj = new File(filename);
        Scanner Reader = new Scanner(myObj);
        String data = Reader.nextLine();

        String[] werte = data.split(" ");
        for (String wert : werte) {
            wahrscheinlichkeiten.add(Double.valueOf(wert));
        }

        createIntervall(wahrscheinlichkeiten);
        return wahrscheinlichkeiten;
    }

    // Methode zur Dateiauswahl
    public static String chooseFile() {
        JFileChooser fileChooser = new JFileChooser("src/testcases_isys1");
        fileChooser.setDialogTitle("Wählen Sie eine Textdatei aus");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

        // Filter für nur .txt-Dateien
        fileChooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter("TXT files", "txt"));

        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            return fileChooser.getSelectedFile().getAbsolutePath();
        } else {
            System.out.println("Keine Datei ausgewählt, Programm wird beendet.");
            System.exit(0);
            return null;
        }
    }

    // Methode zum festlegen wie viele Runden gespielt werden sollen
    public static int chooseRounds() {
        int runden = 0;
        try {
            String input = JOptionPane.showInputDialog("Geben Sie die Anzahl der Runden ein:");
            runden = Integer.parseInt(input);
        } catch (NumberFormatException e) {
            System.out.println("Ungültige Eingabe, Programm wird beendet.");
            System.exit(0);
        }
        return runden;
    }

    // Intervalle in Map abbilden
    public static HashMap<Integer, Double> createIntervall(ArrayList<Double> wahrscheinlichkeiten) {
        double summe = 0.0;

        for (int i = 0; i < wahrscheinlichkeiten.size(); i++) {
            summe += wahrscheinlichkeiten.get(i);
            intervall.put(i + 1, summe);
        }

        return intervall;
    }

    // Ziehen anhand der Wahrscheinlichkeiten
    public static int ziehen(HashMap<Integer, Double> intervall) {
        Random random = new Random();
        double zufallszahl = random.nextDouble();

        for (int key : intervall.keySet()) {
            if (zufallszahl <= intervall.get(key)) {
                return key;
            }
        }
        return -1;
    }

    // Berechne Wahrscheinlichkeiten nach den ersten 70 Würfen
    public static HashMap<Integer, Double> calcChance() {
        HashMap<Integer, Double> wahrscheinlichkeitMap = new HashMap<>();
        for (Map.Entry<Integer, Integer> entry : mapErgebnisse.entrySet()) {
            wahrscheinlichkeitMap.put(entry.getKey(), entry.getValue() / (double) ziehungen);
        }
        System.out.println("Ermittelte Wahrscheinlichkeiten: " + wahrscheinlichkeitMap);
        return wahrscheinlichkeitMap;
    }

    // Berechne Erwartungswert basierend auf den Wahrscheinlichkeiten und potenziellem Gewinn
    public static int calcHighestChance(HashMap<Integer, Double> wahrscheinlichkeitMap) {
        int besteZahl = 1;
        double hoechsterErwartungswert = Double.NEGATIVE_INFINITY;

        for (Map.Entry<Integer, Double> entry : wahrscheinlichkeitMap.entrySet()) {
            int zahl = entry.getKey();
            double pZahl = entry.getValue();
            double erwartungswert = (pZahl * 10 * zahl) - ((1 - pZahl) * 10);

            System.out.println("Erwartungswert für Zahl " + zahl + ": " + erwartungswert);

            if (erwartungswert > hoechsterErwartungswert) {
                hoechsterErwartungswert = erwartungswert;
                besteZahl = zahl;
            }
        }

        //System.out.println("Zahl mit höchstem Erwartungswert: " + besteZahl);
        return besteZahl;
    }

    // Spielstrategie
    public static void startSpiel(int runden) {
        int totalGewinn = 0;

        for (int runde = 0; runde < runden; runde++) {
            // Reset für jede Runde
            mapErgebnisse.clear(); // Ergebnisse der vorherigen Runde löschen
            ziehungen = 0;         // Ziehungszähler zurücksetzen
            for (int i = 1; i <= 10; i++) {
                mapErgebnisse.put(i, 0);
            }

            int rundenGewinn = spielRunde();
            gewinne.add(rundenGewinn);
            totalGewinn += rundenGewinn;
            int averageGewinn = calcAverageGewinn(totalGewinn, runden);
            System.out.println("Mittelwert Gewinn: " + averageGewinn + "€");
            System.out.println("Schwankung: " + calcStandardabweichung(calcVarianz(gewinne)) + "€");
            System.out.println("Gewinn in Runde " + (runde + 1) + ": " + rundenGewinn + "€");
            System.out.println("Häufigkeit der gezogenen Zahlen: " + mapErgebnisse);

            // CSV schreiben nach jeder Runde
            writeHäufigkeitCSV(runde + 1, mapErgebnisse, rundenGewinn);
        }
    }

    // Spielrunde mit den ersten 70 Würfen und dann basierend auf Erwartungswerten
    public static int spielRunde() {
        int rundenGewinn = 0;
        HashMap<Integer, Double> wahrscheinlichkeitMap = new HashMap<>();

        // Simuliere 1000 Ziehungen
        for (int i = 0; i < 1000; i++) {
            int gesetzteZahl;

            if (i == 0) {
                // Erste Ziehung: Setze auf 10
                gesetzteZahl = 10;
            } else if (i < 70) {
                // Für die ersten 70 Ziehungen: Wähle die Zahl mit der höchsten Häufigkeit
                gesetzteZahl = calcHighestFrequency(mapErgebnisse);
            } else {
                // Nach den ersten 70 Ziehungen: Wahrscheinlichkeiten berechnen und auf die beste Zahl setzen
                if (i == 70) {
                    wahrscheinlichkeitMap = calcChance();
                }
                gesetzteZahl = calcHighestChance(wahrscheinlichkeitMap);
            }

            int gezogeneZahl = ziehen(intervall);
            ziehungen++;

            // Speichere das Ergebnis in der Map
            mapErgebnisse.put(gezogeneZahl, mapErgebnisse.get(gezogeneZahl) + 1);

            // Ausgabe der gesetzten und gezogenen Zahl
            System.out.println("Gesetzte Zahl: " + gesetzteZahl + ", Gezogene Zahl: " + gezogeneZahl);

            // Berechne den Gewinn oder Verlust
            if (gesetzteZahl == gezogeneZahl) {
                rundenGewinn += 10 * gezogeneZahl;
            } else {
                rundenGewinn -= 10;
            }
        }

        return rundenGewinn;
    }

    // Methode, um die Zahl mit der höchsten Häufigkeit zu berechnen
    public static int calcHighestFrequency(HashMap<Integer, Integer> ziehungenMap) {
        return ziehungenMap.entrySet()
                .stream()
                .max(Map.Entry.comparingByValue())
                .get()
                .getKey();
    }

    // Schreibe das Endergebnis der Runde in eine CSV-Datei
    public static void writeHäufigkeitCSV(int runde, HashMap<Integer, Integer> mapErgebnisse, int gesamtGewinn) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Ergebnisse.csv", true))) {
            writer.write(runde + ","); // Schreibe die Runde
            // Schreibe die Gewinne der Runde (falls benötigt, z.B. 0 als Platzhalter)
            writer.write(gesamtGewinn+ ",");

            // Schreibe die Häufigkeiten der gezogenen Zahlen
            for (int i = 1; i <= 10; i++) {
                // Wenn die Zahl in der Map vorhanden ist, schreibe den Wert, andernfalls 0
                writer.write(mapErgebnisse.getOrDefault(i, 0) + (i < 10 ? "," : ""));
            }
            writer.write("\n");  // Neue Zeile nach jeder Runde
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //Mittelwert des Gewinns bestimmen
    public static int calcAverageGewinn(int totalGewinn, int runden) {
        return totalGewinn / runden;
    }

    // Berechne die Varianz der Gewinne
    public static double calcVarianz(ArrayList<Integer> gewinne) {
        int totalGewinn = gewinne.stream().mapToInt(Integer::intValue).sum();
        int mittelwert = calcAverageGewinn(totalGewinn, gewinne.size());
        double summe = 0;

        for (int gewinn : gewinne) {
            summe += Math.pow(gewinn - mittelwert, 2);
        }

        return summe / gewinne.size();  // Varianz
    }

    // Berechne die Standardabweichung der Gewinne
    public static double calcStandardabweichung(double varianz) {
        return Math.round(Math.sqrt(varianz) * 100.0) / 100.0;  // Runden auf 2 Nachkommastellen
    }

    public static void main(String[] args) throws FileNotFoundException {
        String filename = chooseFile();
        int runden = chooseRounds();

        if(filename != null) {
            readFile(filename);
            startSpiel(runden); // Spiele 10.000 Runden
        }
    }
}

