package hnyb.hnybelo.manager;

import org.bukkit.configuration.file.FileConfiguration;

public class ConfigManager {
    private final FileConfiguration config;

    public ConfigManager(FileConfiguration config) {
        this.config = config;
    }

    public int getDefaultElo() {
        return config.getInt("default_elo", 100);
    }

    public int getKillLimit() {
        return config.getInt("kill_limit", 3);
    }

    public int getCooldown() {
        return config.getInt("cooldown", 86400);
    }

    public String getDeathMessage() {
        return config.getString("death_message");
    }

    public String getDataFilePath() {
        return config.getString("data_file", "plugins/HnybElo/data.txt");
    }

    public String getMenuTitle() {
        return config.getString("menu.title", "&6Top Elo");
    }

    public int getMenuSize() {
        return config.getInt("menu.size", 27);
    }

    public String getMenuItemFormat() {
        return config.getString("menu.item_format", "&eThứ hạng: %rank% - Elo: %elo%");
    }

    public void reload(org.bukkit.configuration.file.FileConfiguration newConfig) {
    }
}
