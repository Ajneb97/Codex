package cx.ajneb97.listeners;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import cx.ajneb97.Codex;

public class Entrar implements Listener{
	private Codex plugin;
	public Entrar(Codex plugin){		
		this.plugin = plugin;		
	}
	@EventHandler
	public void Join(PlayerJoinEvent event){
		Player jugador = event.getPlayer();	
		if(jugador.isOp() && !(plugin.version.equals(plugin.latestversion))){
			jugador.sendMessage(plugin.nombrePlugin + ChatColor.RED +" There is a new version available. "+ChatColor.YELLOW+
	  				  "("+ChatColor.GRAY+plugin.latestversion+ChatColor.YELLOW+")");
	  		    jugador.sendMessage(ChatColor.RED+"You can download it at: "+ChatColor.GREEN+"https://www.spigotmc.org/resources/90371/");			 
		}
	}
}
