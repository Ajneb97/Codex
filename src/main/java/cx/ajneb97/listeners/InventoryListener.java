package cx.ajneb97.listeners;

import cx.ajneb97.Codex;
import cx.ajneb97.managers.InventoryManager;
import cx.ajneb97.model.inventory.InventoryPlayer;
import cx.ajneb97.utils.InventoryUtils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryListener implements Listener {

    private Codex plugin;
    public InventoryListener(Codex plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void closeInventory(InventoryCloseEvent event){
        Player player = (Player) event.getPlayer();
        plugin.getInventoryManager().removeInventoryPlayer(player);
    }

    @EventHandler
    public void clickInventory(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        InventoryManager invManager = plugin.getInventoryManager();
        InventoryPlayer inventoryPlayer = invManager.getInventoryPlayer(player);
        if(inventoryPlayer != null) {
            event.setCancelled(true);
            if(event.getCurrentItem() == null || event.getClickedInventory() == null){
                return;
            }

            if(event.getClickedInventory().equals(InventoryUtils.getTopInventory(player))) {
                plugin.getInventoryManager().clickInventory(inventoryPlayer,event.getCurrentItem(),event.getClick());
            }
        }
    }
}
