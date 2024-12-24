package cx.ajneb97.listeners.dependencies;

import cx.ajneb97.Codex;
import io.lumine.mythic.bukkit.events.MythicMobDeathEvent;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MythicMobsListener implements Listener {

    private Codex plugin;
    public MythicMobsListener(Codex plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onMythicMobKill(MythicMobDeathEvent event){
        LivingEntity killer = event.getKiller();
        if(killer instanceof Player) {
            Player player = (Player) killer;
            plugin.getDiscoveryManager().onMythicMobKill(player,event.getMob().getMobType());
        }
    }
}
