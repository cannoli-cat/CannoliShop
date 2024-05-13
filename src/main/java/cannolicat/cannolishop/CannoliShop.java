package cannolicat.cannolishop;

import cannolicat.cannolishop.commands.ShowShops;
import cannolicat.cannolishop.events.ShopCreate;
import cannolicat.cannolishop.events.ShopDestroy;
import cannolicat.cannolishop.events.ShopInteract;
import cannolicat.cannolishop.listeners.MenuListener;
import cannolicat.cannolishop.menus.menusystem.PlayerMenuUtility;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;

public final class CannoliShop extends JavaPlugin {
    public ArrayList<Shop> shops = new ArrayList<>();
    private static CannoliShop plugin;
    private static Economy economy;
    private static Permission perms = null;
    private static Chat chat = null;
    private static final File file = new File("plugins" + File.separator + "CannoliShop" + File.separator + "shops.ser");
    private static final HashMap<Player, PlayerMenuUtility> playerMenuUtilityMap = new HashMap<>();

    @Override
    public void onEnable() {
        plugin = this;

        getServer().getPluginManager().registerEvents(new ShopCreate(), this);
        getServer().getPluginManager().registerEvents(new ShopInteract(), this);
        getServer().getPluginManager().registerEvents(new ShopDestroy(), this);
        getServer().getPluginManager().registerEvents(new MenuListener(), this);

        getCommand("showshops").setExecutor(new ShowShops());

        if(!setupEconomy()) {
            getLogger().severe(String.format("[%s] - Disabled due to no Vault dependency found!", getDescription().getName()));
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        setupPermissions();
        setupChat();

        if(file.exists()) {
            getLogger().info("[CannoliShop] Found saved data... attempting to load...");
            shops = loadShops();
            getLogger().info("[CannoliShop] Successfully loaded saved data!");
        }
    }

    @Override
    public void onDisable() {
        if(!file.exists() && !shops.isEmpty()) {
            try {
                file.getParentFile().mkdirs();
                if (file.createNewFile()) {
                    getLogger().info("[CannoliShop] Save file created: " + file.getName());
                }
            } catch (IOException e) {
                getLogger().severe("[CannoliShop] An error occurred.");
                e.printStackTrace();
            }
        }

        if(!shops.isEmpty() && file.exists()) {
            if (saveShops()) getLogger().info("[CannoliShop] Successfully saved data!");
            else getLogger().severe("[CannoliShop] Could not save data!");
        } else {
            getLogger().info("[CannoliShop] Shops list is empty, cancelling save...");
            if(file.exists()) file.delete();
        }
    }

    public static CannoliShop getPlugin() {
        return plugin;
    }

    public static Economy getEconomy() {
        return economy;
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        economy = rsp.getProvider();
        return economy != null;
    }

    private boolean setupChat() {
        RegisteredServiceProvider<Chat> rsp = getServer().getServicesManager().getRegistration(Chat.class);
        chat = rsp.getProvider();
        return chat != null;
    }

    private boolean setupPermissions() {
        RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
        perms = rsp.getProvider();
        return perms != null;
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
