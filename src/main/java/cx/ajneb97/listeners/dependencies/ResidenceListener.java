package cx.ajneb97.listeners.dependencies;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import cx.ajneb97.Codex;
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
    // Track each player's current residence
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

        // Acquire residence at the target location
        ClaimedResidence toResidence = residence.getResidenceManager().getByLoc(to);
        String toRegionName = toResidence != null ? toResidence.getName() : null;

        // Acquire the player's previous residence
        String fromRegionName = playerRegions.get(playerUUID);

        // Check if the residence has changed
        if (hasRegionChanged(fromRegionName, toRegionName)) {
            // Update player's current residence
            if (toRegionName != null) {
                playerRegions.put(playerUUID, toRegionName);
                // Player enters a new residence
                plugin.getDiscoveryManager().onResidenceRegionEnter(player, toRegionName);
            } else {
                playerRegions.remove(playerUUID);
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clean player data
        playerRegions.remove(event.getPlayer().getUniqueId());
    }

    /**
     * Check if the residence has changed.
     */
    private boolean hasRegionChanged(String from, String to) {
        if (from == null && to == null) return false;
        if (from == null) return true;
        if (to == null) return true;
        return !from.equals(to);
    }
}
