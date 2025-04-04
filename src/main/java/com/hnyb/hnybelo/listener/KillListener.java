package hnyb.hnybelo.listener;

import hnyb.hnybelo.HnybEloPlugin;
import hnyb.hnybelo.manager.ConfigManager;
import hnyb.hnybelo.manager.EloManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class KillListener implements Listener {
    private final HnybEloPlugin plugin;
    private final EloManager eloManager;
    private final ConfigManager configManager;

    public KillListener(HnybEloPlugin plugin, EloManager eloManager, ConfigManager configManager) {
        this.plugin = plugin;
        this.eloManager = eloManager;
        this.configManager = configManager;
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player victim = event.getEntity();
        Player killer = victim.getKiller();
        if(killer == null || killer.equals(victim)) return;
        if(killer.getAddress().getAddress().equals(victim.getAddress().getAddress())) {
            killer.sendMessage("§cKhông được cộng/trừ điểm do cùng IP!");
            return;
        }
        if(eloManager.isInCooldown(killer.getUniqueId(), victim.getUniqueId())) {
            return;
        }
        double eloChange = eloManager.calculateEloChange(killer.getUniqueId(), victim.getUniqueId());
        eloChange = Math.min(eloChange, eloManager.getElo(victim.getUniqueId()));
        eloManager.addElo(killer.getUniqueId(), eloChange);
        eloManager.subtractElo(victim.getUniqueId(), eloChange);
        eloManager.recordKill(killer.getUniqueId(), victim.getUniqueId());
        String weapon = killer.getInventory().getItemInMainHand().getType().toString();
        String deathMessage = configManager.getDeathMessage()
                .replace("<%player_killer%>", killer.getName())
                .replace("<%elo_change%>", String.valueOf(eloChange))
                .replace("<%player_victim%>", victim.getName())
                .replace("<%weapon%>", weapon);
        event.setDeathMessage(deathMessage);
    }
}
