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

import java.util.Optional;

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
                MythicItem mythicItem = parseMythicItem(lines[2]);

                if (material == null && mythicItem == null) {
                    e.getPlayer().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "Error on line 3! - You must enter a valid item!");
                    return;
                }

                e.setLine(0, "["+ ChatColor.LIGHT_PURPLE + "PRICE" + ChatColor.RESET + "]");

                boolean admin = CannoliShop.getPlugin().admins.contains(e.getPlayer().getUniqueId());
                if(admin) {
                    e.setLine(lines.length-1, ChatColor.DARK_RED + "§nADMIN");
                } else
                    e.setLine(lines.length-1, ChatColor.GOLD + e.getPlayer().getName());

                if(mythicItem != null && material == null) e.setLine(lines.length-2, ChatColor.AQUA + (mythicItem.getDisplayName().length() > 15 ? mythicItem.getInternalName() : mythicItem.getDisplayName()));
                else if (mythicItem == null) e.setLine(lines.length-2, ChatColor.AQUA + material.toString());
                e.getBlock().getState().update();

                if (material != null) new Shop(e.getPlayer().getUniqueId(), price, material, against.getLocation(), e.getBlock().getLocation(), admin);
                else new Shop(e.getPlayer().getUniqueId(), price, mythicItem, against.getLocation(), e.getBlock().getLocation(), admin);
            }
        }
    }

    private MythicItem parseMythicItem(String item) {
        Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(item);
        if(maybeItem.isPresent()) return maybeItem.get();

        for(MythicItem curItem : MythicBukkit.inst().getItemManager().getItems()) {
            String internalName = curItem.getInternalName();
            String displayName = curItem.getDisplayName() == null ? "null" : curItem.getDisplayName();
            if(internalName.equalsIgnoreCase(item) || internalName.equalsIgnoreCase(item.replaceAll("\\s+","")) || internalName.replaceAll("_", " ").equalsIgnoreCase(item) || internalName.replaceAll("_", "").equalsIgnoreCase(item) || displayName.equalsIgnoreCase(item) || displayName.equalsIgnoreCase(item.replaceAll("\\s+",""))) {
                return curItem;
            }
        }
        return null;
    }
}
