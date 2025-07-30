package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;

public class FileWriterOutput {

        public static void writeResultsToFile(String filePath, List<String> results) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (String line : results) {
                    writer.write(line);
                    writer.newLine(); // Neue Zeile
                }
                //System.out.println("Ergebnisse erfolgreich in " + filePath + " geschrieben.");
            } catch (IOException e) {
                System.err.println("Fehler beim Schreiben in die Datei: " + e.getMessage());
            }
        }

}
