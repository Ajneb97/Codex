package cx.ajneb97.listeners;

import cx.ajneb97.Codex;
import org.bukkit.Bukkit;

public class MovementListener {

    private Codex plugin;
    public MovementListener(Codex plugin){
        this.plugin = plugin;
    }

    private int taskId = 0;

    public void register() {
        taskId = Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            Bukkit.getOnlinePlayers().forEach(player -> {
                plugin.getDiscoveryManager().onLocationNear(player, player.getLocation());
            });
        }, 100, 100).getTaskId();
    }

    public void unregister() {
        Bukkit.getScheduler().cancelTask(taskId);
    }

}
