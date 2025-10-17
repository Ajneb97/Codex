package cx.ajneb97.listeners.dependencies;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import cx.ajneb97.Codex;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class ResidenceListener implements Listener {

    private Codex plugin;
    // 追踪每个玩家当前所在的领地
    private final HashMap<UUID, String> playerRegions = new HashMap<>();

    public ResidenceListener(Codex plugin){
        this.plugin = plugin;
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlayerMove(PlayerMoveEvent event) {
        Location from = event.getFrom();
        Location to = event.getTo();

        if (to == null) return;

        if (from.getBlockX() == to.getBlockX() &&
            from.getBlockY() == to.getBlockY() &&
            from.getBlockZ() == to.getBlockZ()) {
            return;
        }

        Residence residence = Residence.getInstance();
        if (residence == null || residence.getResidenceManager() == null) {
            return;
        }

        Player player = event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // 获取目标位置的领地
        ClaimedResidence toResidence = residence.getResidenceManager().getByLoc(to);
        String toRegionName = toResidence != null ? toResidence.getName() : null;

        // 获取玩家之前所在的领地
        String fromRegionName = playerRegions.get(playerUUID);

        // 检查领地是否发生变化
        if (hasRegionChanged(fromRegionName, toRegionName)) {
            // 更新玩家当前领地
            if (toRegionName != null) {
                playerRegions.put(playerUUID, toRegionName);
                // 玩家进入了新领地
                plugin.getDiscoveryManager().onResidenceRegionEnter(player, toRegionName);
            } else {
                playerRegions.remove(playerUUID);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // 清理玩家数据
        playerRegions.remove(event.getPlayer().getUniqueId());
    }

    /**
     * 检查领地是否发生变化
     */
    private boolean hasRegionChanged(String from, String to) {
        if (from == null && to == null) return false;
        if (from == null) return true;
        if (to == null) return true;
        return !from.equals(to);
    }
}
