package hnyb.hnybelo.command;

import hnyb.hnybelo.HnybEloPlugin;
import hnyb.hnybelo.manager.ConfigManager;
import hnyb.hnybelo.manager.EloManager;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import java.util.Map;
import java.util.UUID;


public class HnybEloCommand implements CommandExecutor {
    private final HnybEloPlugin plugin;
    private final EloManager eloManager;
    private final ConfigManager configManager;

    public HnybEloCommand(HnybEloPlugin plugin, EloManager eloManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.eloManager = eloManager;
        this.configManager = configManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(!sender.hasPermission("hnybelo.admin")) {
            sender.sendMessage("§cBạn không có quyền sử dụng lệnh này!");
            return true;
        }

        if(args.length == 0) {
            sender.sendMessage("§eSử dụng: /hnybelo <reload|set|reset|resetall|give>");
            return true;
        }

        switch(args[0].toLowerCase()) {
            case "reload":
                plugin.reloadConfig();
                configManager.reload(plugin.getConfig());
                sender.sendMessage("§aPlugin đã được reload!");
                break;
            case "set":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /hnybelo set <TênNgườiChơi> <SốElo>");
                    return true;
                }
                String targetNameSet = args[1];
                OfflinePlayer targetPlayerSet = Bukkit.getOfflinePlayer(targetNameSet);
                if(targetPlayerSet == null || targetPlayerSet.getUniqueId() == null) {
                    sender.sendMessage("§cKhông tìm thấy người chơi: " + targetNameSet);
                    return true;
                }
                double newElo;
                try {
                    newElo = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cSố Elo không hợp lệ: " + args[2]);
                    return true;
                }
                eloManager.subtractElo(targetPlayerSet.getUniqueId(), eloManager.getElo(targetPlayerSet.getUniqueId()));
                eloManager.addElo(targetPlayerSet.getUniqueId(), newElo);
                sender.sendMessage("§aĐã set Elo của " + targetPlayerSet.getName() + " thành " + newElo);
                break;
            case "reset":
                if (args.length < 2) {
                    sender.sendMessage("§cUsage: /hnybelo reset <TênNgườiChơi>");
                    return true;
                }
                String targetNameReset = args[1];
                OfflinePlayer targetPlayerReset = Bukkit.getOfflinePlayer(targetNameReset);
                if(targetPlayerReset == null || targetPlayerReset.getUniqueId() == null) {
                    sender.sendMessage("§cKhông tìm thấy người chơi: " + targetNameReset);
                    return true;
                }
                double defaultElo = configManager.getDefaultElo();
                eloManager.subtractElo(targetPlayerReset.getUniqueId(), eloManager.getElo(targetPlayerReset.getUniqueId()));
                eloManager.addElo(targetPlayerReset.getUniqueId(), defaultElo);
                sender.sendMessage("§aĐã reset Elo của " + targetPlayerReset.getName() + " về mặc định (" + defaultElo + ")");
                break;
            case "resetall":
                int count = 0;
                for (Map.Entry<UUID, Double> entry : eloManager.getDataManager().getAllData().entrySet()) {
                    eloManager.subtractElo(entry.getKey(), eloManager.getElo(entry.getKey()));
                    eloManager.addElo(entry.getKey(), configManager.getDefaultElo());
                    count++;
                }
                sender.sendMessage("§aReset Elo cho " + count + " người chơi về mặc định (" + configManager.getDefaultElo() + ")");
                break;
            case "give":
                if (args.length < 3) {
                    sender.sendMessage("§cUsage: /hnybelo give <TênNgườiChơi> <SốElo>");
                    return true;
                }
                String targetNameGive = args[1];
                OfflinePlayer targetPlayerGive = Bukkit.getOfflinePlayer(targetNameGive);
                if(targetPlayerGive == null || targetPlayerGive.getUniqueId() == null) {
                    sender.sendMessage("§cKhông tìm thấy người chơi: " + targetNameGive);
                    return true;
                }
                double giveElo;
                try {
                    giveElo = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage("§cSố Elo không hợp lệ: " + args[2]);
                    return true;
                }
                eloManager.addElo(targetPlayerGive.getUniqueId(), giveElo);
                sender.sendMessage("§aĐã cộng thêm " + giveElo + " Elo cho " + targetPlayerGive.getName());
                break;
            default:
                sender.sendMessage("§cLệnh không hợp lệ! Sử dụng: /hnybelo <reload|set|reset|resetall|give>");
        }
        return true;
    }
}
