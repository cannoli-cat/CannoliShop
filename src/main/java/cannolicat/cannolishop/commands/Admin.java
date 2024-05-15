package cannolicat.cannolishop.commands;

import cannolicat.cannolishop.CannoliShop;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class Admin implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(commandSender instanceof Player) {
            Player player = (Player) commandSender;
            if(CannoliShop.getPlugin().admins.contains(player.getUniqueId())) {
                player.sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "Admin mode disabled!");
                CannoliShop.getPlugin().admins.remove(player.getUniqueId());
                return true;
            }
            player.sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.GREEN + "Admin mode enabled!");
            CannoliShop.getPlugin().admins.add(player.getUniqueId());
        } else
            Bukkit.getLogger().warning("[CannoliShop] This command can only be used by players!");
        return true;
    }
}
