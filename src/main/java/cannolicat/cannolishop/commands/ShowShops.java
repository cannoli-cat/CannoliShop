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

public class ShowShops implements CommandExecutor, TabCompleter {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
          if(strings.length == 1) {
              Player sender = (Player) commandSender;
              OfflinePlayer target = null;
              for(Shop shop : CannoliShop.getPlugin().shops) {
                  if (strings[0].equalsIgnoreCase(Bukkit.getOfflinePlayer(shop.getOwner()).getName())) {
                      target = Bukkit.getOfflinePlayer(shop.getOwner());
                  }
              }
              if(target == null) {
                  sender.sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "This player does not have any shops!");
                  return true;
              }
              new ShopMenu(CannoliShop.getPlayerMenuUtility(sender), target).open();
          } else {
            commandSender.sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "Insufficient arguments! You must specify a player.");
          }
        } else Bukkit.getLogger().info("[CannoliShop] Only players can use this command!");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if(strings.length == 1) {
            List<String> list = new ArrayList<>();
            for(Shop shop : CannoliShop.getPlugin().shops) {
                list.add(Bukkit.getOfflinePlayer(shop.getOwner()).getName());
            }
            return list;
        }
        return null;
    }
}
