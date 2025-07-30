import pandas as pd
import matplotlib.pyplot as plt

# CSV-Datei laden
df = pd.read_csv('Ergebnisse.csv', names=['Runde', 'RundenGewinn', 'BesteZahl'])

# Histogramm der Gewinne
plt.figure(figsize=(10, 6))
plt.hist(df['RundenGewinn'], bins=20, color='blue', alpha=0.7, edgecolor='black')
plt.title('Histogramm der Gewinne')
plt.xlabel('Gewinn')
plt.ylabel('Häufigkeit')
plt.grid(True)

# Speichere das Histogramm als Bilddatei
plt.savefig('gewinn_histogramm.png')

# Histogramm der besten Zahlen
plt.figure(figsize=(10, 6))
plt.hist(df['BesteZahl'], bins=range(1, 12), color='green', alpha=0.7, edgecolor='black')
plt.title('Histogramm der häufigsten Zahlen')
plt.xlabel('Beste Zahl')
plt.ylabel('Häufigkeit')
plt.xticks(range(1, 11))  # Setze X-Achsen-Beschriftung für die Zahlen 1-10
plt.grid(True)

# Speichere das Histogramm als Bilddatei
plt.savefig('beste_zahlen_histogramm.png')

plt.show()
