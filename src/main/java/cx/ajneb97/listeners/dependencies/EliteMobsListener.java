package cx.ajneb97.listeners.dependencies;

import com.magmaguy.elitemobs.api.EliteMobDeathEvent;
import com.magmaguy.elitemobs.mobconstructor.custombosses.CustomBossEntity;
import cx.ajneb97.Codex;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EliteMobsListener implements Listener {

    private Codex plugin;
    public EliteMobsListener(Codex plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onEliteMobKill(EliteMobDeathEvent event){
        LivingEntity e = event.getEntityDeathEvent().getEntity();
        if(e.getKiller() != null){
            if(event.getEliteEntity() instanceof CustomBossEntity){
                CustomBossEntity customBossEntity = (CustomBossEntity)event.getEliteEntity();
                plugin.getDiscoveryManager().onEliteMobKill(e.getKiller(),customBossEntity.getCustomBossesConfigFields().getFilename());
            }
        }
    }
}
