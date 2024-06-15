package cannolicat.cannolishop.events;

import cannolicat.cannolishop.CannoliShop;
import cannolicat.cannolishop.Shop;
import cannolicat.cannolishop.hooks.MythicHook;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class ShopInteract implements Listener {
    private final HashMap<UUID, Shop> shops = new HashMap<>();

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent e) {
        if(e.getInventory().getHolder() instanceof Chest) {
            for(Shop curShop : CannoliShop.getPlugin().shops) {
                if(curShop.getChestLoc().equals(((Chest) e.getInventory().getHolder()).getLocation())) {
                    int i = CannoliShop.getPlugin().shops.indexOf(curShop);
                    shops.put(e.getPlayer().getUniqueId(), CannoliShop.getPlugin().shops.get(i));
                    break;
                }
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent e) {
        shops.remove(e.getPlayer().getUniqueId());
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof Chest)) return;
        Shop shop = shops.get(e.getWhoClicked().getUniqueId());

        if(shop == null) return;
        if(e.getClickedInventory() == e.getWhoClicked().getInventory() && (!e.getWhoClicked().getUniqueId().equals(shop.getOwner()) || shop.isAdmin()) && !CannoliShop.getPlugin().admins.contains(e.getWhoClicked().getUniqueId())) {
            e.setCancelled(true);
            return;
        }

        if(e.getClickedInventory() != null && shop.getChestLoc().equals(e.getClickedInventory().getLocation()) && (!e.getWhoClicked().getUniqueId().equals(shop.getOwner()) || shop.isAdmin()) && !CannoliShop.getPlugin().admins.contains(e.getWhoClicked().getUniqueId())) {
            if(e.getWhoClicked().getInventory().firstEmpty() != -1) {
                if (shop.getMaterial() != null && e.getWhoClicked().getInventory().contains(shop.getMaterial(), shop.getPrice())) {
                    ItemStack item = new ItemStack(shop.getMaterial(), shop.getPrice());
                    handlePurchase(e, item, shop.isAdmin());
                } else if (CannoliShop.getMythicHook() != null && shop.getMythicItem() != null && MythicHook.inventoryHasMythicItem(e.getWhoClicked().getInventory(), shop)) {
                    CannoliShop.getMythicHook().handleMythicPurchase(shop, e);
                } else {
                    if (e.getCurrentItem() == null) return;
                    e.getWhoClicked().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "You cannot afford this item!");
                    e.setCancelled(true);
                }
            } else
                e.getWhoClicked().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "You don't have enough space to make this purchase!");
        }
    }

    public static void handlePurchase(InventoryClickEvent e, ItemStack price, boolean isAdmin) {
        if (Objects.equals(e.getCurrentItem(), price)) {
            e.setCancelled(true);
            return;
        }

        e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
        if(!isAdmin) e.setCurrentItem(price);
        e.getWhoClicked().getInventory().removeItem(price);

        e.setCancelled(true);
    }
}
