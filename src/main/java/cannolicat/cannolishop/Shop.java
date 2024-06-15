package cannolicat.cannolishop;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

public class Shop implements Serializable {
    @Serial
    private static final long serialVersionUID = 3458203942363203527L;
    private final UUID owner, world;
    private final int price;
    private final Object item;
    private final int chestX, chestY, chestZ, signX, signY, signZ;
    private final boolean isAdmin;

    public Shop(UUID owner, int price, Object item, Location chestLocation, Location signLocation, boolean isAdmin) {
        this.owner = owner;
        this.price = price;
        this.item = item;
        world = Bukkit.getPlayer(owner).getWorld().getUID();
        chestX = chestLocation.getBlockX();
        chestY = chestLocation.getBlockY();
        chestZ = chestLocation.getBlockZ();
        signX = signLocation.getBlockX();
        signY = signLocation.getBlockY();
        signZ = signLocation.getBlockZ();
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
        return item instanceof Material ? (Material) item : null;
    }

    public Location getChestLoc() {
        return new Location(Bukkit.getWorld(world), chestX, chestY, chestZ);
    }

    public Location getSignLoc() {
        return new Location(Bukkit.getWorld(world), signX, signY, signZ);
    }

    public String getMythicItem() {
        return item instanceof String ? (String) item : null;
    }

    public void destroyShop() {
        CannoliShop.getPlugin().shops.remove(this);
    }

    public boolean isAdmin() {
        return isAdmin;
    }
}
