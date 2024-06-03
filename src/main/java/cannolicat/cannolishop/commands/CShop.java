package cannolicat.cannolishop.commands;

import cannolicat.cannolishop.CannoliShop;
import cannolicat.cannolishop.Shop;
import cannolicat.cannolishop.menus.ShopMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class CShop implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player sender) {
            if(strings.length >= 1) {
                if(strings.length == 2 && strings[0].equalsIgnoreCase("show") && sender.hasPermission("cannolishop.show")) {
                    OfflinePlayer target = null;

                    for (Shop shop : CannoliShop.getPlugin().shops) {
                        if (strings[1].equalsIgnoreCase(Bukkit.getOfflinePlayer(shop.getOwner()).getName())) {
                            target = Bukkit.getOfflinePlayer(shop.getOwner());
                        }
                    }

                    if (target == null) {
                        sender.sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "This player does not have any shops!");
                        return true;
                    }

                    new ShopMenu(CannoliShop.getPlayerMenuUtility(sender), target).open();
                } else if (strings.length < 2 && strings[0].equalsIgnoreCase("show") && sender.hasPermission("cannolishop.show")) {
                    sender.sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "Insufficient arguments! You must specify a player.");
                    return true;
                }

                if(strings[0].equalsIgnoreCase("admin") && sender.hasPermission("cannolishop.admin")) {
                    if(CannoliShop.getPlugin().admins.contains(sender.getUniqueId())) {
                        sender.sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "Admin mode disabled!");
                        CannoliShop.getPlugin().admins.remove(sender.getUniqueId());
                        return true;
                    }
                    sender.sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.GREEN + "Admin mode enabled!");
                    CannoliShop.getPlugin().admins.add(sender.getUniqueId());
                }
            } else
                commandSender.sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "Insufficient arguments!");
        } else Bukkit.getLogger().info("[CannoliShop] Only players can use this command!");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        List<String> list = new ArrayList<>();

        if(strings[0].equalsIgnoreCase("show")) {
            for(Shop shop : CannoliShop.getPlugin().shops) {
                list.add(Bukkit.getOfflinePlayer(shop.getOwner()).getName());
            }
        } else if (!strings[0].equalsIgnoreCase("admin")) {
            list.add("admin");
            list.add("show");
        }

        return list.isEmpty() ? null : list;
    }
}
