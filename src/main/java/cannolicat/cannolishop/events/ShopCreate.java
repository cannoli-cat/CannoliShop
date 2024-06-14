package cannolicat.cannolishop.events;

import cannolicat.cannolishop.CannoliShop;
import cannolicat.cannolishop.Shop;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;

public class ShopCreate implements Listener {
    private Chest against = null;

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent e) {
        if(e.getBlock().getState() instanceof Sign && e.getBlockAgainst().getState() instanceof Chest chest) {
            against = chest;
        } else against = null;
    }

    @EventHandler
    public void onSignChange(SignChangeEvent e) {
        for(Shop shop : CannoliShop.getPlugin().shops) {
            if(shop.getSignLoc().equals(e.getBlock().getLocation())) {
                e.getPlayer().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "You cannot edit shop signs!");
                e.setCancelled(true);
                return;
            }
        }

        if(against != null) {
            String[] lines = e.getLines();
            if(lines.length >= 3 && lines[0].equalsIgnoreCase("[price]")) {
                int price;

                try {
                    price = Integer.parseInt(lines[1]);
                } catch (NumberFormatException ex) {
                    e.getPlayer().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "You must enter a valid number for a price!");
                    return;
                }

                if(price <= -1) {
                    e.getPlayer().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "You cannot enter negative numbers for a price!");
                    return;
                }

                Material material = Material.getMaterial(lines[2].toUpperCase());

                boolean admin = CannoliShop.getPlugin().admins.contains(e.getPlayer().getUniqueId());
                if(admin) {
                    e.setLine(lines.length-1, ChatColor.DARK_RED + "§nADMIN");
                } else
                    e.setLine(lines.length-1, ChatColor.GOLD + e.getPlayer().getName());

                if(CannoliShop.getMythicHook() != null && material == null) {
                    CannoliShop.getMythicHook().mythicShopCreate(e, lines, price, against, admin);
                } else {
                    if (material == null) {
                        e.getPlayer().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "Error on line 3! - You must enter a valid item!");
                        return;
                    }

                    e.setLine(lines.length - 2, ChatColor.AQUA + material.toString());
                    e.setLine(0, "[" + ChatColor.LIGHT_PURPLE + "PRICE" + ChatColor.RESET + "]");

                    e.getBlock().getState().update();
                    new Shop(e.getPlayer().getUniqueId(), price, material, against.getLocation(), e.getBlock().getLocation(), admin);
                }
            }
        }
    }


}
