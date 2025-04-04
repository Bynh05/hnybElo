package hnyb.hnybelo.placeholder;

import hnyb.hnybelo.manager.EloManager;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public class HnybEloPlaceholder extends PlaceholderExpansion {
    private final EloManager eloManager;

    public HnybEloPlaceholder(EloManager eloManager) {
        this.eloManager = eloManager;
        if (isPlaceholderAPIInstalled()) {
            register();
        }
    }

    @Override
    public String getIdentifier() {
        return "hnybelo";
    }

    @Override
    public String getAuthor() {
        return "hnyb";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }

    @Override
    public String onPlaceholderRequest(Player player, String identifier) {
        if(player == null) return "";

        if(identifier.equals("elo")) {
            return String.valueOf(eloManager.getElo(player.getUniqueId()));
        }

        if(identifier.startsWith("elo_top_") && identifier.endsWith("_name")) {
            String numberStr = identifier.substring("elo_top_".length(), identifier.length() - "_name".length());
            try {
                int rank = Integer.parseInt(numberStr);
                List<Map.Entry<UUID, Double>> topList = eloManager.getTopPlayers(10);
                if(rank >= 1 && rank <= topList.size()){
                    OfflinePlayer topPlayer = Bukkit.getOfflinePlayer(topList.get(rank - 1).getKey());
                    return topPlayer.getName();
                }
            } catch (NumberFormatException ex) {
                return "";
            }
        }

        if(identifier.startsWith("elo_top_") && identifier.endsWith("_value")) {
            String numberStr = identifier.substring("elo_top_".length(), identifier.length() - "_value".length());
            try {
                int rank = Integer.parseInt(numberStr);
                List<Map.Entry<UUID, Double>> topList = eloManager.getTopPlayers(10);
                if(rank >= 1 && rank <= topList.size()){
                    return String.valueOf(topList.get(rank - 1).getValue());
                }
            } catch (NumberFormatException ex) {
                return "";
            }
        }

        return null;
    }

    private boolean isPlaceholderAPIInstalled() {
        return org.bukkit.Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
    }
}
