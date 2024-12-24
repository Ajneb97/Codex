package cx.ajneb97.model.inventory;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class InventoryPlayer {
    private Player player;
    private String inventoryName;

    public InventoryPlayer(Player player, String inventoryName) {
        this.player = player;
        this.inventoryName = inventoryName;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getInventoryName() {
        return inventoryName;
    }

    public void setInventoryName(String inventoryName) {
        this.inventoryName = inventoryName;
    }
}
