package cannolicat.cannolishop;

import cannolicat.cannolishop.commands.CShop;
import cannolicat.cannolishop.events.ShopCreate;
import cannolicat.cannolishop.events.ShopDestroy;
import cannolicat.cannolishop.events.ShopInteract;
import cannolicat.cannolishop.hooks.MythicHook;
import cannolicat.cannolishop.listeners.MenuListener;
import cannolicat.cannolishop.listeners.PlayerQuitListener;
import cannolicat.cannolishop.menus.menusystem.PlayerMenuUtility;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public final class CannoliShop extends JavaPlugin {
    public ArrayList<Shop> shops = new ArrayList<>();
    public ArrayList<UUID> admins = new ArrayList<>();
    private static CannoliShop plugin;
    private static MythicHook mythicHook = null;
    private static final File file = new File("plugins" + File.separator + "CannoliShop" + File.separator + "shops.ser");
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;

        if (getServer().getPluginManager().getPlugin("MythicMobs") != null) {
            getLogger().info("Hooked to MythicMobs!");
            mythicHook = new MythicHook();
        }

        getServer().getPluginManager().registerEvents(new ShopCreate(), this);
        getServer().getPluginManager().registerEvents(new ShopInteract(), this);
        getServer().getPluginManager().registerEvents(new ShopDestroy(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        Objects.requireNonNull(getCommand("cshop")).setExecutor(new CShop());

        if(file.exists()) {
            shops = loadShops();
            getLogger().info( "Successfully loaded saved data!");
        }
    }

    @Override
    public void onDisable() {
        if(!file.exists() && !shops.isEmpty()) {
            try {
                if(!file.getParentFile().mkdirs()) {
                    getLogger().severe("Failed to create parent directory! Data might not be saved correctly.");
                }
                if (file.createNewFile()) {
                    getLogger().info("Save file created: " + file.getName());
                }
            } catch (IOException e) {
                throw new RuntimeException("An error occurred while trying to create " + file.getName(), e);
            }
        }

        if(!shops.isEmpty() && file.exists()) {
            if (saveShops()) getLogger().info("Successfully saved data! Goodbye!");
            else getLogger().severe("Could not save data!");
        } else {
            getLogger().info("No shops to save, cancelling save...");
            if(file.exists()) {
                if(!file.delete()) {
                    getLogger().severe("Could not delete " + file.getName());
                }
            }
        }
    }

    public static CannoliShop getPlugin() {
        return plugin;
    }

    public static MythicHook getMythicHook() {
        return mythicHook;
    }

    private boolean saveShops() {
        try {
            FileOutputStream fos = new FileOutputStream(file);
            ObjectOutputStream oos = new ObjectOutputStream(fos);

            oos.writeObject(shops);
            oos.close();
            fos.close();

            return true;
        } catch(IOException e) {
            throw new RuntimeException("An error occurred while trying to save file data.", e);
        }
    }

    @SuppressWarnings("unchecked")
    private ArrayList<Shop> loadShops() {
        ArrayList<Shop> list;

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            if(file.length() != 0) list = (ArrayList<Shop>) ois.readObject();
            else list = new ArrayList<>();

            if(mythicHook == null) {
                list.removeIf(shop -> shop.getMythicItem() != null);
            }

            ois.close();
            fis.close();

            return list;
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("An error occurred while trying to load file data.", e);
        }
    }

    public static PlayerMenuUtility getPlayerMenuUtility(Player p) {
        PlayerMenuUtility playerMenuUtility;
        if (!(playerMenuUtilityMap.containsKey(p))) {

            playerMenuUtility = new PlayerMenuUtility(p);
            playerMenuUtilityMap.put(p, playerMenuUtility);

            return playerMenuUtility;
        } else {
            return playerMenuUtilityMap.get(p);
        }
    }
}
