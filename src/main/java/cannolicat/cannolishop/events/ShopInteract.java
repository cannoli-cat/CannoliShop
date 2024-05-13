package cannolicat.cannolishop.events;

import cannolicat.cannolishop.CannoliShop;
import cannolicat.cannolishop.Shop;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
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
import java.util.UUID;

public class ShopInteract implements Listener {
    private HashMap<UUID, Shop> shops = new HashMap<>();

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
        if(e.getInventory().getHolder() instanceof Chest) {
            shops.remove(e.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof Chest)) return;
        Shop shop = shops.get(e.getWhoClicked().getUniqueId());

        if(shop == null) return;
        if(e.getClickedInventory() == e.getWhoClicked().getInventory() && !e.getWhoClicked().getUniqueId().equals(shop.getOwner())) {
            e.setCancelled(true);
            return;
        }

        if(e.getClickedInventory() != null && shop.getChestLoc().equals(e.getClickedInventory().getLocation()) && !e.getWhoClicked().getUniqueId().equals(shop.getOwner())) {
            if(shop.getMaterial() == null && shop.getMythicItem() == null) {
                if(CannoliShop.getEconomy().getBalance(Bukkit.getPlayer(e.getWhoClicked().getUniqueId())) < shop.getPrice()) {
                    e.setCancelled(true);
                    e.getWhoClicked().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " +ChatColor.RED + "You cannot afford this item!");
                    return;
                }

                ItemStack item = e.getCurrentItem();
                if(item != null) {
                    EconomyResponse withdrawal = CannoliShop.getEconomy().withdrawPlayer(Bukkit.getPlayer(e.getWhoClicked().getUniqueId()), shop.getPrice());
                    e.getWhoClicked().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " +ChatColor.GREEN + "$" + withdrawal.amount + " has been sent to " + Bukkit.getPlayer(shop.getOwner()).getDisplayName());

                    e.getWhoClicked().getInventory().addItem(item);
                    ItemStack lastItem = e.getCurrentItem();
                    e.setCurrentItem(new ItemStack(Material.AIR));

                    if (Bukkit.getPlayer(shop.getOwner()) != null) {
                        EconomyResponse payment = CannoliShop.getEconomy().depositPlayer(Bukkit.getPlayer(shop.getOwner()), shop.getPrice());

                        String name;
                        try {
                            name = lastItem.getItemMeta().getDisplayName();
                        } catch (NullPointerException npe) {
                            name = lastItem.getType().name();
                        }
                        if(name.isEmpty()) name = lastItem.getType().name();

                        Bukkit.getPlayer(shop.getOwner()).sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " +Bukkit.getPlayer(e.getWhoClicked().getUniqueId()).getDisplayName() + " bought " + lastItem.getAmount() + " " + ChatColor.GOLD + name + ChatColor.RESET + " for " + ChatColor.GREEN + "$" + payment.amount + "!");
                    } else {
                        CannoliShop.getEconomy().depositPlayer(Bukkit.getOfflinePlayer(shop.getOwner()), shop.getPrice());
                    }
                    e.setCancelled(true);
                }
            }
            else {
                if (shop.getMaterial() != null && e.getWhoClicked().getInventory().contains(shop.getMaterial(), shop.getPrice())) {
                    ItemStack item = new ItemStack(shop.getMaterial(), shop.getPrice());
                    handlePurchase(e, item);
                }
                else if (shop.getMythicItem() != null && inventoryHasMythicItem(e.getWhoClicked().getInventory(), shop)) {
                    ItemStack item = BukkitAdapter.adapt(MythicBukkit.inst().getItemManager().getItem(shop.getMythicItem()).get().generateItemStack(shop.getPrice()));
                    handlePurchase(e, item);
                }
                else {
                    if (e.getCurrentItem() == null) return;
                    e.getWhoClicked().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " +ChatColor.RED + "You cannot afford this item!");
                    e.setCancelled(true);
                }
            }
        }
    }

    private void handlePurchase(InventoryClickEvent e, ItemStack item) {
        if (Objects.equals(e.getCurrentItem(), item)) {
            e.setCancelled(true);
            return;
        }

        e.getWhoClicked().getInventory().addItem(e.getCurrentItem());
        e.setCurrentItem(item);
        e.getWhoClicked().getInventory().removeItem(item);

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
