package cx.ajneb97.managers;

import java.util.ArrayList;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import cx.ajneb97.Codex;
import cx.ajneb97.data.AgregarEntradaCallback;
import cx.ajneb97.data.JugadorCodex;
import cx.ajneb97.data.JugadorCodexCallback;
import cx.ajneb97.data.MySQL;

public class JugadorDataManager {
	
	//Cuando MySQL esta activado no deberia usar esta lista
	private ArrayList<JugadorCodex> jugadores;
	private Codex plugin;
	
	public JugadorDataManager(Codex plugin) {
		this.jugadores = new ArrayList<JugadorCodex>();
		this.plugin = plugin;
	}
	
	public ArrayList<JugadorCodex> getJugadores() {
		return jugadores;
	}

	public void setJugadores(ArrayList<JugadorCodex> jugadores) {
		this.jugadores = jugadores;
	}
	
	public void agregarJugador(JugadorCodex j) {
		jugadores.add(j);
	}
	
	public JugadorCodex getJugadorSync(String nombre) {
		JugadorCodex jugadorCodex = null;
		if(MySQL.isEnabled(plugin.getConfig())) {
			jugadorCodex = MySQL.getJugador(nombre, plugin);
		}else {
			for(JugadorCodex j : jugadores) {
				if(j.getName() != null && j.getName().equals(nombre)) {
					jugadorCodex = j;
					break;
				}
			}
		}
		return jugadorCodex;
	}
	
	public void getJugador(final String nombre,final JugadorCodexCallback callback) {
		new BukkitRunnable() {
			@Override
			public void run() {
				final JugadorCodex jugadorCodex = getJugadorSync(nombre);

				new BukkitRunnable() {
					@Override
					public void run() {
						callback.onDone(jugadorCodex);
					}
					
				}.runTask(plugin);
			}
		}.runTaskAsynchronously(plugin);
	}

	public JugadorCodex getJugadorSyncUUID(String uuid) {
		JugadorCodex jugadorCodex = null;
		if(MySQL.isEnabled(plugin.getConfig())) {
			jugadorCodex = MySQL.getJugadorUUID(uuid, plugin);
		}else {
			for(JugadorCodex j : jugadores) {
				if(j.getUuid() != null && j.getUuid().equals(uuid)) {
					jugadorCodex = j;
					break;
				}
			}
		}
		return jugadorCodex;
	}

	public void getJugadorUUID(final String uuid,final JugadorCodexCallback callback) {
		new BukkitRunnable() {
			@Override
			public void run() {
				final JugadorCodex jugadorCodex = getJugadorSyncUUID(uuid);

				new BukkitRunnable() {
					@Override
					public void run() {
						callback.onDone(jugadorCodex);
					}

				}.runTask(plugin);
			}
		}.runTaskAsynchronously(plugin);
	}
	
	public void agregarEntrada(final Player jugador,final String categoria,final String discovery,final AgregarEntradaCallback callback) {
		getJugador(jugador.getName(),new JugadorCodexCallback() {
			@Override
			public void onDone(JugadorCodex j) {
				boolean agrega = false;
				if(MySQL.isEnabled(plugin.getConfig())) {
					if(j == null) {
						j = new JugadorCodex(jugador.getUniqueId().toString(),jugador.getName());
						j.agregarEntrada(categoria, discovery);
						agrega = true;
						MySQL.actualizarDiscoveriesJugador(plugin, j);
					}else {
						agrega = j.agregarEntrada(categoria, discovery);
						if(agrega) {
							MySQL.actualizarDiscoveriesJugador(plugin, j);
						}
					}
				}else {
					if(j == null) {
						//Lo crea
						j = new JugadorCodex(jugador.getUniqueId().toString(),jugador.getName());
						agregarJugador(j);
					}
					
					agrega = j.agregarEntrada(categoria, discovery);
				}
				
				final boolean agregaFinal = agrega;
				
				new BukkitRunnable() {
					@Override
					public void run() {
						callback.onDone(agregaFinal);
					}
					
				}.runTask(plugin);
			}
		});
	}
	
	public void resetearEntrada(JugadorCodex j,String categoria,String discovery,boolean todas) {
		if(MySQL.isEnabled(plugin.getConfig())) {
			if(todas) {
				j.resetearEntradas();
			}else {
				j.resetearEntrada(categoria, discovery);
			}
			MySQL.actualizarDiscoveriesJugador(plugin, j);
		}else {
			if(todas) {
				j.resetearEntradas();
			}else {
				j.resetearEntrada(categoria, discovery);
			}
			
		}
	}

	public void actualizarNombreJugador(Player player){
		getJugadorUUID(player.getUniqueId().toString(), j -> {
            if(j != null){
				if(MySQL.isEnabled(plugin.getConfig())) {
					MySQL.actualizarNombreJugador(plugin,player.getUniqueId().toString(),player.getName());
				}else{
					j.setName(player.getName());
				}
			}
        });
	}

}
