package cannolicat.cannolishop;

import cannolicat.cannolishop.commands.CShop;
import cannolicat.cannolishop.events.ShopCreate;
import cannolicat.cannolishop.events.ShopDestroy;
import cannolicat.cannolishop.events.ShopInteract;
import cannolicat.cannolishop.listeners.MenuListener;
import cannolicat.cannolishop.listeners.PlayerQuitListener;
import cannolicat.cannolishop.menus.menusystem.PlayerMenuUtility;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public final class CannoliShop extends JavaPlugin {
    public ArrayList<Shop> shops = new ArrayList<>();
    public ArrayList<UUID> admins = new ArrayList<>();
    private static CannoliShop plugin;
    private static final File file = new File("plugins" + File.separator + "CannoliShop" + File.separator + "shops.ser");
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;

        getServer().getPluginManager().registerEvents(new ShopCreate(), this);
        getServer().getPluginManager().registerEvents(new ShopInteract(), this);
        getServer().getPluginManager().registerEvents(new ShopDestroy(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        getCommand("cshop").setExecutor(new CShop());

        if(file.exists()) {
            getLogger().info("Found saved data... attempting to load...");
            shops = loadShops();
            getLogger().info("Successfully loaded saved data!");
        }
    }

    @Override
    public void onDisable() {
        if(!file.exists() && !shops.isEmpty()) {
            try {
                file.getParentFile().mkdirs();
                if (file.createNewFile()) {
                    getLogger().info("Save file created: " + file.getName());
                }
            } catch (IOException e) {
                getLogger().severe("An error occurred.");
                e.printStackTrace();
            }
        }

        if(!shops.isEmpty() && file.exists()) {
            if (saveShops()) getLogger().info("Successfully saved data!");
            else getLogger().severe("Could not save data!");
        } else {
            getLogger().info("Shops list is empty, cancelling save...");
            if(file.exists()) file.delete();
        }
    }

    public static CannoliShop getPlugin() {
        return plugin;
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
            e.printStackTrace();
            return false;
        }
    }

    private ArrayList<Shop> loadShops() {
        ArrayList<Shop> list;

        try {
            FileInputStream fis = new FileInputStream(file);
            ObjectInputStream ois = new ObjectInputStream(fis);

            if(file.length() != 0) list = (ArrayList<Shop>) ois.readObject();
            else list = new ArrayList<>();

            ois.close();
            fis.close();

            return list;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
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
