package hnyb.hnybelo;

import hnyb.hnybelo.command.HnybEloCommand;
import hnyb.hnybelo.command.HnybEloTabCompleter;
import hnyb.hnybelo.data.DataManager;
import hnyb.hnybelo.listener.KillListener;
import hnyb.hnybelo.manager.ConfigManager;
import hnyb.hnybelo.manager.EloManager;
import hnyb.hnybelo.placeholder.HnybEloPlaceholder;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import me.clip.placeholderapi.PlaceholderAPI;
import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class HnybEloPlugin extends JavaPlugin {
    private ConfigManager configManager;
    private EloManager eloManager;
    private DataManager dataManager;
    private boolean placeholderAPILoaded = false;
    private boolean confirmEnabled = false;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        configManager = new ConfigManager(getConfig());
        dataManager = new DataManager(configManager.getDataFilePath());
        dataManager.loadData();
        eloManager = new EloManager(this, configManager, dataManager);

        getServer().getPluginManager().registerEvents(new KillListener(this, eloManager, configManager), this);
        getCommand("hnybelo").setExecutor(new HnybEloCommand(this, eloManager, configManager));
        getCommand("hnybelo").setTabCompleter(new HnybEloTabCompleter());

        Plugin plugin = Bukkit.getPluginManager().getPlugin("PlaceholderAPI");
        if (plugin != null && plugin.isEnabled()) {
            placeholderAPILoaded = true;
            new HnybEloPlaceholder(eloManager).register();
            getLogger().info("PlaceholderAPI đã được tìm thấy và tích hợp!");
        } else {
            getLogger().warning("PlaceholderAPI không tìm thấy! Plugin sẽ chạy mà không có PlaceholderAPI.");
        }
        displayBanner();
    }

    @Override
    public void onDisable() {
        displayBanner();
        eloManager.saveAll();
    }

    public boolean isPlaceholderAPILoaded() {
        return placeholderAPILoaded;
    }
    public void displayBanner() {
        String banner = ChatColor.BOLD +
                "██╗  ██╗███╗   ██╗██╗   ██╗██████╗     ███████╗██╗      ██████╗\n" +
                "██║  ██║████╗  ██║╚██╗ ██╔╝██╔══██╗    ██╔════╝██║     ██╔═══██╗\n" +
                "███████║██╔██╗ ██║ ╚████╔╝ ██████╔╝    █████╗  ██║     ██║   ██║\n" +
                "██╔══██║██║╚██╗██║  ╚██╔╝  ██╔══██╗    ██╔══╝  ██║     ██║   ██║\n" +
                "██║  ██║██║ ╚████║   ██║   ██████╔╝    ███████╗███████╗╚██████╔╝\n" +
                "╚═╝  ╚═╝╚═╝  ╚═══╝   ╚═╝   ╚═════╝     ╚══════╝╚══════╝ ╚═════╝ ";

        if (confirmEnabled) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.BLUE + banner);
        } else {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + banner);
        }
    }
}
