package cannolicat.cannolishop.menus.menusystem;

import org.bukkit.entity.Player;

public class PlayerMenuUtility {
    private Player owner;
    private Player player;

    public PlayerMenuUtility(Player p) {
        this.owner = p;
    }

    public Player getOwner() {
        return owner;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
