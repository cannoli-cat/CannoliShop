package cannolicat.cannolishop;

import io.lumine.mythic.core.items.MythicItem;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.Serializable;
import java.util.UUID;

public class Shop implements Serializable {
    private static final long serialVersionUID = 3458203942363203527L;
    private final UUID owner, world;
    private final int price;
    private final Material material;
    private final String mythicItemInternalName;
    private final int chestX, chestY, chestZ, signX, signY, signZ;
    private final boolean isAdmin;

    public Shop(UUID owner, int price, Material material, Location chestLocation, Location signLocation, boolean isAdmin) {
        this.owner = owner;
        this.price = price;
        this.material = material;
        world = Bukkit.getPlayer(owner).getWorld().getUID();
        chestX = chestLocation.getBlockX();
        chestY = chestLocation.getBlockY();
        chestZ = chestLocation.getBlockZ();
        signX = signLocation.getBlockX();
        signY = signLocation.getBlockY();
        signZ = signLocation.getBlockZ();
        mythicItemInternalName = null;
        this.isAdmin = isAdmin;

        CannoliShop.getPlugin().shops.add(this);
        Bukkit.getPlayer(owner).sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.GREEN + "Shop successfully created!");
    }

    public Shop(UUID owner, int price, MythicItem mythicItem, Location chestLocation, Location signLocation, boolean isAdmin) {
        this.owner = owner;
        this.price = price;
        mythicItemInternalName = mythicItem.getInternalName();
        world = Bukkit.getPlayer(owner).getWorld().getUID();
        chestX = chestLocation.getBlockX();
        chestY = chestLocation.getBlockY();
        chestZ = chestLocation.getBlockZ();
        signX = signLocation.getBlockX();
        signY = signLocation.getBlockY();
        signZ = signLocation.getBlockZ();
        material = null;
        this.isAdmin = isAdmin;

        CannoliShop.getPlugin().shops.add(this);
        Bukkit.getPlayer(owner).sendMessage("[" + ChatColor.GOLD + "CannoliShop" + ChatColor.RESET + "]: " + ChatColor.GREEN + "Shop successfully created!");
    }

    public UUID getOwner() {
        return owner;
    }

    public int getPrice() {
        return price;
    }

    public Material getMaterial() {
        return material;
    }

    public Location getChestLoc() {
        return new Location(Bukkit.getWorld(world), chestX, chestY, chestZ);
    }

    public Location getSignLoc() {
        return new Location(Bukkit.getWorld(world), signX, signY, signZ);
    }

    public String getMythicItem() {
        return mythicItemInternalName;
    }

    public void destroyShop() {
        CannoliShop.getPlugin().shops.remove(this);
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
