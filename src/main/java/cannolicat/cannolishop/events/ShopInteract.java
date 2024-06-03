package cannolicat.cannolishop.events;

import cannolicat.cannolishop.CannoliShop;
import cannolicat.cannolishop.Shop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.block.Chest;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;
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
                } else if (shop.getMythicItem() != null && inventoryHasMythicItem(e.getWhoClicked().getInventory(), shop)) {
                    Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(shop.getMythicItem());

                    if(maybeItem.isPresent()) {
                        ItemStack item = BukkitAdapter.adapt(maybeItem.get().generateItemStack(shop.getPrice()));
                        handlePurchase(e, item, shop.isAdmin());
                    } else
                        Bukkit.getLogger().severe("[CannoliShop] Could not handle purchase! Item was not present. Please contact cannoli_cat.");
                } else {
                    if (e.getCurrentItem() == null) return;
                    e.getWhoClicked().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "You cannot afford this item!");
                    e.setCancelled(true);
                }
            } else
                e.getWhoClicked().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "You don't have enough space to make this purchase!");
        }
    }

    private void handlePurchase(InventoryClickEvent e, ItemStack price, boolean isAdmin) {
        if (Objects.equals(e.getCurrentItem(), price)) {
            e.setCancelled(true);
            return;
        }

        e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
        if(!isAdmin) e.setCurrentItem(price);
        e.getWhoClicked().getInventory().removeItem(price);

        e.setCancelled(true);
    }

    private boolean inventoryHasMythicItem(Inventory inv, Shop shop) {
        for (int i = 0; i < inv.getSize(); i++) {
            ItemStack item = inv.getItem(i);
            if(item == null) continue;

            if(MythicBukkit.inst().getItemManager().isMythicItem(item)) {
                if(Objects.equals(MythicBukkit.inst().getItemManager().getMythicTypeFromItem(item), shop.getMythicItem())) {
                    return true;
                }
            }
        }
        return false;
    }
}
