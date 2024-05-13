package cannolicat.cannolishop.menus;

import cannolicat.cannolishop.CannoliShop;
import cannolicat.cannolishop.Shop;
import cannolicat.cannolishop.menus.menusystem.PaginatedMenu;
import cannolicat.cannolishop.menus.menusystem.PlayerMenuUtility;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Objects;

public class ShopMenu extends PaginatedMenu {
    private final ArrayList<Shop> shops = new ArrayList<>();

    public ShopMenu(PlayerMenuUtility p) {
        super(p);

        for(Shop shop : CannoliShop.getPlugin().shops) {
            if(shop.getOwner().equals(p.getPlayer().getUniqueId())) {
                shops.add(shop);
            }
        }
    }

    @Override
    public String getMenuName() {
        return playerMenuUtility.getPlayer().getDisplayName() + "'s Shops";
    }

    @Override
    public int getSlots() {
        return 54;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {
        Player p = (Player) e.getWhoClicked();

        if (Objects.requireNonNull(e.getCurrentItem()).getType().equals(Material.BARRIER)) {
            p.closeInventory();
        }
        else if(e.getCurrentItem().getType().equals(Material.CHEST)) {
            if(e.getCurrentItem() != null) {
                p.teleport(shops.get(e.getSlot()-10).getChestLoc());
            }
        }
        else if(e.getCurrentItem().getType().equals(Material.DARK_OAK_BUTTON)) {
            if (ChatColor.stripColor(Objects.requireNonNull(e.getCurrentItem().getItemMeta()).getDisplayName()).equalsIgnoreCase("Left")){
                if (page == 0){
                    p.sendMessage(ChatColor.GRAY + "You are already on the first page.");
                }else{
                    page = page - 1;
                    super.open();
                }
            }else if (ChatColor.stripColor(e.getCurrentItem().getItemMeta().getDisplayName()).equalsIgnoreCase("Right")){
                if (!((index + 1) >= shops.size())){
                    page = page + 1;
                    super.open();
                }else{
                    p.sendMessage(ChatColor.GRAY + "You are on the last page.");
                }
            }
        }
    }

    @Override
    public void setMenuItems() {
        addMenuBorder();

        if(!shops.isEmpty()) {
            for(int i = 0; i < getMaxItemsPerPage(); i++) {
                index = getMaxItemsPerPage() * page + i;
                if(index >= shops.size()) break;
                if (shops.get(index) != null){

                    ItemStack shopItem = new ItemStack(Material.CHEST, 1);
                    ItemMeta meta = shopItem.getItemMeta();

                    assert meta != null;
                    meta.setDisplayName(ChatColor.RESET + "" + ChatColor.BOLD + "Location: " + ChatColor.GOLD + shops.get(i).getChestLoc().getBlockX() + ", " + shops.get(i).getChestLoc().getBlockY() + ", " + shops.get(i).getChestLoc().getBlockZ());

                    ArrayList<String> lore = new ArrayList<>();

                    if(shops.get(i).getMythicItem() != null && shops.get(i).getMaterial() == null) lore.add(ChatColor.LIGHT_PURPLE + "Price" + ChatColor.WHITE + ": " + shops.get(i).getPrice() + " " + shops.get(i).getMythicItem());
                    else if (shops.get(i).getMaterial() != null & shops.get(i).getMythicItem() == null)  lore.add(ChatColor.LIGHT_PURPLE + "Price" + ChatColor.WHITE + ": " + shops.get(i).getPrice() + " " + shops.get(i).getMaterial());
                    else if(shops.get(i).getMaterial() == null && shops.get(i).getMythicItem() == null) lore.add(ChatColor.LIGHT_PURPLE + "Price" + ChatColor.WHITE + ": " + ChatColor.GREEN + "$" + shops.get(i).getPrice());
                    lore.add(ChatColor.RESET + "" + ChatColor.BOLD + ChatColor.GOLD + "<Click to teleport>");

                    meta.setLore(lore);
                    shopItem.setItemMeta(meta);

                    inventory.addItem(shopItem);
                }
            }
        }
    }
}
