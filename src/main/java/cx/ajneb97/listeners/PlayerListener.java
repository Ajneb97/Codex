package cx.ajneb97.listeners;

import cx.ajneb97.Codex;
import cx.ajneb97.managers.MessagesManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class PlayerListener implements Listener {

    private Codex plugin;
    public PlayerListener(Codex plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event){
        plugin.getPlayerDataManager().setJoinPlayerData(event.getPlayer());

        //Update notification
        Player player = event.getPlayer();
        String latestVersion = plugin.getUpdateCheckerManager().getLatestVersion();
        if(player.isOp() && plugin.getConfigsManager().getMainConfigManager().isUpdateNotify() && !plugin.version.equals(latestVersion)){
            player.sendMessage(MessagesManager.getColoredMessage(plugin.prefix+" &cThere is a new version available. &e(&7"+latestVersion+"&e)"));
            player.sendMessage(MessagesManager.getColoredMessage("&cYou can download it at: &fhttps://modrinth.com/plugin/codex-rpg-discoveries"));
        }
    }

    @EventHandler
    public void onMobKill(EntityDeathEvent event){
        LivingEntity e = event.getEntity();
        if(e.getKiller() != null){
            String customName = e.getCustomName() != null ? ChatColor.stripColor(e.getCustomName()) : null;
            plugin.getDiscoveryManager().onMobKill(e.getKiller(),e.getType().name(),customName);
        }
    }
}
