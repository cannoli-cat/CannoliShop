package cannolicat.cannolishop.events;

import cannolicat.cannolishop.CannoliShop;
import cannolicat.cannolishop.Shop;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
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
        if(e.getBlock().getState() instanceof Sign && e.getBlockAgainst().getState() instanceof Chest) {
            against = (Chest) e.getBlockAgainst().getState();
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

                Material material = null;
                MythicItem mythicItem = null;
                if(!lines[2].equalsIgnoreCase("$")) {
                    material = Material.getMaterial(lines[2].toUpperCase());
                    mythicItem = tryParseMythicItem(lines[2]);

                    if (material == null && mythicItem == null) {
                        e.getPlayer().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "Error on line 3! - You must enter a valid item or '$' for cash payment!");
                        return;
                    }
                }

                e.setLine(0, "["+ ChatColor.LIGHT_PURPLE + "PRICE" + ChatColor.RESET + "]");
                e.setLine(lines.length-1, ChatColor.GOLD + e.getPlayer().getName());
                if(mythicItem != null && material == null) e.setLine(lines.length-2, mythicItem.getInternalName());
                else if (material != null && mythicItem == null) e.setLine(lines.length-2, material + "");
                e.getBlock().getState().update();

                if (material != null && mythicItem == null || material == null && mythicItem == null) new Shop(e.getPlayer().getUniqueId(), price, material, against.getLocation(), e.getBlock().getLocation());
                else if (material == null) new Shop(e.getPlayer().getUniqueId(), price, mythicItem, against.getLocation(), e.getBlock().getLocation());
            }
        }
    }

    private MythicItem tryParseMythicItem(String item) {
        for(MythicItem curItem : MythicBukkit.inst().getItemManager().getItems()) {
            String name = curItem.getInternalName();
            if(name.equalsIgnoreCase(item) || name.equalsIgnoreCase(item.replaceAll("\\s+","")) || name.replaceAll("_", " ").equalsIgnoreCase(item) || name.replaceAll("_", "").equalsIgnoreCase(item)) {
                return curItem;
            }
        }
        return null;
    }
}
