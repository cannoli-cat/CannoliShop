package cannolicat.cannolishop.hooks;

import cannolicat.cannolishop.Shop;
import cannolicat.cannolishop.events.ShopInteract;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.block.Chest;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Objects;
import java.util.Optional;

public class MythicHook {
    public void mythicShopCreate(SignChangeEvent e, String[] lines, int price, Chest against, boolean admin) {
        MythicItem mythicItem = parseMythicItem(lines[2]).orElse(null);

        if (mythicItem == null) {
            e.getPlayer().sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.RED + "Error on line 3! - You must enter a valid item!");
            return;
        }

        e.setLine(lines.length - 2, ChatColor.AQUA + (mythicItem.getDisplayName().length() > 15 ? mythicItem.getInternalName() : mythicItem.getDisplayName()));
        e.setLine(0, "[" + ChatColor.LIGHT_PURPLE + "PRICE" + ChatColor.RESET + "]");

        e.getBlock().getState().update();
        new Shop(e.getPlayer().getUniqueId(), price, mythicItem.getInternalName(), against.getLocation(), e.getBlock().getLocation(), admin);
    }

    public void handleMythicPurchase(Shop shop, InventoryClickEvent e) {
        Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(shop.getMythicItem());

        if(maybeItem.isPresent()) {
            ItemStack item = BukkitAdapter.adapt(maybeItem.get().generateItemStack(shop.getPrice()));
            ShopInteract.handlePurchase(e, item, shop.isAdmin());
        } else
            Bukkit.getLogger().severe("[CannoliShop] Could not handle purchase! Item was not present. Please contact cannoli_cat.");
    }

    private Optional<MythicItem> parseMythicItem(String item) {
        Optional<MythicItem> maybeItem = MythicBukkit.inst().getItemManager().getItem(item);
        if(maybeItem.isPresent()) return maybeItem;

        for(MythicItem curItem : MythicBukkit.inst().getItemManager().getItems()) {
            String internalName = curItem.getInternalName();
            String displayName = curItem.getDisplayName() == null ? "null" : curItem.getDisplayName();
            if(internalName.equalsIgnoreCase(item) || internalName.equalsIgnoreCase(item.replaceAll("\\s+","")) || internalName.replaceAll("_", " ").equalsIgnoreCase(item) || internalName.replaceAll("_", "").equalsIgnoreCase(item) || displayName.equalsIgnoreCase(item) || displayName.equalsIgnoreCase(item.replaceAll("\\s+",""))) {
                return Optional.of(curItem);
            }
        }
        return Optional.empty();
    }

    public static boolean inventoryHasMythicItem(Inventory inv, Shop shop) {
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
