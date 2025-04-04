package hnyb.hnybelo.manager;

import hnyb.hnybelo.HnybEloPlugin;
import hnyb.hnybelo.data.DataManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EloManager {
    private final HnybEloPlugin plugin;
    private final ConfigManager configManager;
    private final DataManager dataManager;

    private final Map<String, KillRecord> killRecords = new HashMap<>();

    public EloManager(HnybEloPlugin plugin, ConfigManager configManager, DataManager dataManager) {
        this.plugin = plugin;
        this.configManager = configManager;
        this.dataManager = dataManager;
    }

    public double calculateEloChange(UUID killerUUID, UUID victimUUID) {
        double killerElo = getElo(killerUUID);
        double victimElo = getElo(victimUUID);
        double change = Math.abs(victimElo - killerElo) * 0.1;
        return change;
    }

    public double getElo(UUID uuid) {
        return dataManager.getElo(uuid, configManager.getDefaultElo());
    }

    public void addElo(UUID uuid, double amount) {
        double newElo = getElo(uuid) + amount;
        dataManager.setElo(uuid, newElo);
    }

    public void subtractElo(UUID uuid, double amount) {
        double newElo = getElo(uuid) - amount;
        if(newElo < 0) newElo = 0;
        dataManager.setElo(uuid, newElo);
    }

    public boolean isInCooldown(UUID killerUUID, UUID victimUUID) {
        String key = killerUUID.toString() + "_" + victimUUID.toString();
        if(killRecords.containsKey(key)) {
            KillRecord record = killRecords.get(key);
            int killLimit = configManager.getKillLimit();
            if(record.getCount() >= killLimit) {
                if(System.currentTimeMillis() - record.getFirstKillTime() < configManager.getCooldown() * 1000L) {
                    return true;
                } else {
                    killRecords.remove(key);
                }
            }
        }
        return false;
    }

    public void recordKill(UUID killerUUID, UUID victimUUID) {
        String key = killerUUID.toString() + "_" + victimUUID.toString();
        KillRecord record = killRecords.getOrDefault(key, new KillRecord(0, System.currentTimeMillis()));
        record.increment();
        killRecords.put(key, record);
    }

    public void saveAll() {
        dataManager.saveData();
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public List<Map.Entry<UUID, Double>> getTopPlayers(int limit) {
        List<Map.Entry<UUID, Double>> list = new ArrayList<>(dataManager.getAllData().entrySet());
        list.sort((a, b) -> Double.compare(b.getValue(), a.getValue()));
        return list.subList(0, Math.min(limit, list.size()));
    }

    private static class KillRecord {
        private int count;
        private final long firstKillTime;

        public KillRecord(int count, long firstKillTime) {
            this.count = count;
            this.firstKillTime = firstKillTime;
        }

        public int getCount() {
            return count;
        }

        public long getFirstKillTime() {
            return firstKillTime;
        }

        public void increment() {
            count++;
        }
    }
}
