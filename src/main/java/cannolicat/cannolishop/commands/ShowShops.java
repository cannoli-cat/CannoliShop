package cannolicat.cannolishop.commands;

import cannolicat.cannolishop.CannoliShop;
import cannolicat.cannolishop.menus.ShopMenu;
import cannolicat.cannolishop.menus.menusystem.PlayerMenuUtility;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ShowShops implements CommandExecutor {
    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] strings) {
        if (commandSender instanceof Player) {
          if(strings.length == 1) {
              Player target = Bukkit.getPlayer(strings[0]);

              PlayerMenuUtility playerMenuUtility = CannoliShop.getPlayerMenuUtility(target);
              playerMenuUtility.setPlayer(target);
              new ShopMenu(playerMenuUtility).open();
          } else {
            commandSender.sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "Insufficient arguments! You must specify a player.");
          }
        } else Bukkit.getLogger().info("[CannoliShop] Only players can use this command!");
        return true;
    }
}
