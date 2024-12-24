package cx.ajneb97.tasks;

import cx.ajneb97.Codex;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerDataSaveTask {

	private Codex plugin;
	private boolean end;
	public PlayerDataSaveTask(Codex plugin) {
		this.plugin = plugin;
		this.end = false;
	}
	
	public void end() {
		end = true;
	}
	
	public void start(int seconds) {
		long ticks = seconds* 20L;
		
		new BukkitRunnable() {
			@Override
			public void run() {
				if(end) {
					this.cancel();
				}else {
					execute();
				}
			}
			
		}.runTaskTimerAsynchronously(plugin, 0L, ticks);
	}
	
	public void execute() {
		plugin.getConfigsManager().getPlayersConfigManager().saveConfigs();
	}
}
