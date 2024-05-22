package cannolicat.cannolishop.listeners;

import cannolicat.cannolishop.CannoliShop;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent e) {
        CannoliShop.getPlugin().admins.remove(e.getPlayer().getUniqueId());
    }
}
