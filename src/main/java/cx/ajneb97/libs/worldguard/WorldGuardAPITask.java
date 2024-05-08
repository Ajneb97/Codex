package cx.ajneb97.libs.worldguard;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import cx.ajneb97.Codex;

public class WorldGuardAPITask {

	private Codex plugin;
	public WorldGuardAPITask(Codex plugin) {
		this.plugin = plugin;
	}
	
	public void iniciar() {
		new BukkitRunnable() {
			@Override
			public void run() {
				if(!comprobarJugadores()) {
					this.cancel();
				}
			}
			
		}.runTaskTimerAsynchronously(plugin, 15L, 15L);
	}
	
	public boolean comprobarJugadores() {
		WorldGuardAPI api = plugin.getWorldGuardAPI();
		for(final Player jugador : Bukkit.getOnlinePlayers()) {
			//Obtener las regiones en las que SE ENCONTRABA el jugador
			List<String> regionesAntes = api.getRegionesAntesJugador(jugador);
			
			//Obtener las regiones en las que SE ENCUENTRA AHORA el jugador
			List<String> regionesAhora = api.getRegionesAhoraJugador(jugador);
			
			//Si en regionesAntes habia una region y desaparece en regionesAhora
			//significa que el usuario ACABA DE SALIR de esa region
			for(final String regionAntes : regionesAntes) {
				if(!regionesAhora.contains(regionAntes)) {
					//Sale de "regionAntes"
					new BukkitRunnable() {
						@Override
						public void run() {
							WorldGuardAPIRegionLeaveEvent event = new WorldGuardAPIRegionLeaveEvent(jugador,regionAntes);
							plugin.getServer().getPluginManager().callEvent(event);
						}
					}.runTask(plugin);
					
				}
			}
			
			//Si en regionesAhora aparece una region que no estaba en
			//regionesAntes, significa que el usuario ACABA DE ENTRAR a la region
			for(final String regionAhora : regionesAhora) {
				if(!regionesAntes.contains(regionAhora)) {
					//Entra a "regionAhora"
					new BukkitRunnable() {
						@Override
						public void run() {
							WorldGuardAPIRegionEnterEvent event = new WorldGuardAPIRegionEnterEvent(jugador,regionAhora);
							plugin.getServer().getPluginManager().callEvent(event);
						}
					}.runTask(plugin);
				}
			}
			
			//Setear regiones actuales
			api.setRegionesJugador(jugador,regionesAhora);
		}
		
		return true;
	}
}
