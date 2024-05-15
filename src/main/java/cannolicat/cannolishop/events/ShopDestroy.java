package cannolicat.cannolishop.events;

import cannolicat.cannolishop.CannoliShop;
import cannolicat.cannolishop.Shop;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

public class ShopDestroy implements Listener {
    @EventHandler
    public void onShopDestroy(BlockBreakEvent e) {
        if(e.getBlock().getState() instanceof Sign) {
            Sign sign = (Sign) e.getBlock().getState();
            for(Shop shop : CannoliShop.getPlugin().shops) {
                if(!CannoliShop.getPlugin().admins.contains(e.getPlayer().getUniqueId()) && shop.getSignLoc().equals(sign.getLocation()) && !e.getPlayer().getUniqueId().equals(shop.getOwner())) {
                    e.setCancelled(true);
                    return;
                } else if(shop.getSignLoc().equals(sign.getLocation()) && e.getPlayer().getUniqueId().equals(shop.getOwner()) || shop.getSignLoc().equals(sign.getLocation()) && CannoliShop.getPlugin().admins.contains(e.getPlayer().getUniqueId())) {
                    shop.destroyShop();
                    e.getPlayer().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " +ChatColor.GREEN + "Shop successfully removed!");
                    return;
                }
            }
        }
        if(e.getBlock().getState() instanceof Chest) {
            Chest chest = (Chest) e.getBlock().getState();
            for(Shop shop : CannoliShop.getPlugin().shops) {
                if(!CannoliShop.getPlugin().admins.contains(e.getPlayer().getUniqueId()) && shop.getChestLoc().equals(chest.getLocation()) && !e.getPlayer().getUniqueId().equals(shop.getOwner())) {
                    e.setCancelled(true);
                    return;
                } else if(shop.getChestLoc().equals(chest.getLocation()) && e.getPlayer().getUniqueId().equals(shop.getOwner()) || shop.getChestLoc().equals(chest.getLocation()) && CannoliShop.getPlugin().admins.contains(e.getPlayer().getUniqueId())) {
                    shop.destroyShop();
                    e.getPlayer().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " +ChatColor.GREEN + "Shop successfully removed!");
                    return;
                }
            }
        }
    }
}
