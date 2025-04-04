package hnyb.hnybelo.data;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DataManager {
    private final String filePath;
    private final Map<UUID, Double> eloData = new HashMap<>();

    public DataManager(String filePath) {
        this.filePath = filePath;
    }

    public void loadData() {
        File file = new File(filePath);
        if (!file.exists()) {
            try {
                file.getParentFile().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            eloData.clear();
            while ((line = reader.readLine()) != null) {
                if (line.trim().isEmpty() || !line.contains(":")) continue;
                String[] parts = line.split(":");
                UUID uuid = UUID.fromString(parts[0].trim());
                double elo = Double.parseDouble(parts[1].trim());
                eloData.put(uuid, elo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void saveData() {
        File file = new File(filePath);
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            for (Map.Entry<UUID, Double> entry : eloData.entrySet()) {
                writer.write(entry.getKey().toString() + ":" + entry.getValue());
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public double getElo(UUID uuid, double defaultElo) {
        return eloData.getOrDefault(uuid, defaultElo);
    }

    public void setElo(UUID uuid, double elo) {
        eloData.put(uuid, elo);
    }

    public Map<UUID, Double> getAllData() {
        return eloData;
    }
}
