package hnyb.hnybelo.command;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class HnybEloTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        if(args.length == 1) {
            completions.add("reload");
            completions.add("set");
            completions.add("reset");
            completions.add("resetall");
            completions.add("give");
        } else if(args.length == 2) {
            if(sender instanceof Player) {
                for(Player p : ((Player) sender).getServer().getOnlinePlayers()) {
                    completions.add(p.getName());
                }
            }
        }
        return completions;
    }
}
