package cx.ajneb97.libs.worldguard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.regions.RegionQuery;

import cx.ajneb97.Codex;

public class WorldGuardAPI {
	
	private Codex plugin;
	private ArrayList<JugadorWorldGuardAPI> jugadores;
	private WorldGuardAPITask task;
	private int version;
	
	public WorldGuardAPI(Codex plugin) {
		this.plugin = plugin;
		this.jugadores = new ArrayList<JugadorWorldGuardAPI>();
		this.version = getVersion();
		this.task = new WorldGuardAPITask(plugin);
		this.task.iniciar();
	}
	
	public List<String> getRegionesAntesJugador(Player jugador){
		JugadorWorldGuardAPI j = getJugador(jugador);
		if(j == null) {
			return new ArrayList<String>();
		}else {
			return j.getRegionesActuales();
		}
	}

	public List<String> getRegionesAhoraJugador(Player jugador) {
		if(version == 6) {
			List<String> regiones = new ArrayList<String>();
			try{
				RegionManager regionManager = (RegionManager)Class.forName("com.sk89q.worldguard.bukkit.WGBukkit")
						.getMethod("getRegionManager", World.class).invoke(null,jugador.getWorld());
				ApplicableRegionSet set = (ApplicableRegionSet)regionManager.getClass().getMethod("getApplicableRegions", Location.class)
						.invoke(regionManager,jugador.getLocation());

				for (ProtectedRegion region : set) {
					regiones.add(region.getId());
				}
			}catch(Exception e){
				e.printStackTrace();
			}
	        return regiones;
		}else {
			com.sk89q.worldedit.util.Location loc = BukkitAdapter.adapt(jugador.getLocation());
			RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
			RegionQuery query = container.createQuery();
			ApplicableRegionSet set = query.getApplicableRegions(loc);
			
			List<String> regiones = new ArrayList<String>();
	        for (ProtectedRegion region : set) {
	        	regiones.add(region.getId());
	        }
	        return regiones;
		}
	}
	
	public int getVersion() {
		String version = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard").getDescription().getVersion();
		if(version.startsWith("6")) {
			return 6;
		}else {
			return 7;
		}
	}
	
	public void setRegionesJugador(Player jugador,List<String> regionesAhora) {
		JugadorWorldGuardAPI j = getJugador(jugador);
		if(j == null) {
			jugadores.add(new JugadorWorldGuardAPI(regionesAhora,jugador));
		}else {
			j.setRegionesActuales(regionesAhora);
		}
	}
	
	public JugadorWorldGuardAPI getJugador(Player jugador) {
		for(JugadorWorldGuardAPI j : jugadores) {
			if(j.getJugador().equals(jugador)) {
				return j;
			}
		}
		return null;
	}

	public void removerJugador(String nombre) {
		for(int i=0;i<jugadores.size();i++) {
			if(jugadores.get(i).getJugador().getName().equals(nombre)) {
				jugadores.remove(i);
			}
		}
	}
}
