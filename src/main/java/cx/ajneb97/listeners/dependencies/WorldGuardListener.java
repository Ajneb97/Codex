package cx.ajneb97.listeners.dependencies;

import cx.ajneb97.Codex;
import cx.ajneb97.managers.dependencies.worldguard.RegionEnteredEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

public class WorldGuardListener implements Listener {

    private Codex plugin;
    public WorldGuardListener(Codex plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onWorldGuardRegionEnter(RegionEnteredEvent event){
        if(event.isCancelled()){
            return;
        }
        plugin.getDiscoveryManager().onWorldGuardRegionEnter(event.getPlayer(),event.getRegionName());
    }
}
