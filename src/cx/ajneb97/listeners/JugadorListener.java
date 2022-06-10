package cx.ajneb97.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import cx.ajneb97.Codex;
import cx.ajneb97.data.AgregarEntradaCallback;
import cx.ajneb97.data.MySQL;
import cx.ajneb97.libs.worldguard.WorldGuardAPIRegionEnterEvent;
import cx.ajneb97.managers.InventarioManager;
import cx.ajneb97.model.CategoriaCodex;
import cx.ajneb97.model.EntradaCodex;
import cx.ajneb97.model.EntradaCodexOpcionesMobKill;
import cx.ajneb97.model.InventarioCodex;
import cx.ajneb97.model.ItemInventarioCodex;
import cx.ajneb97.model.JugadorInventario;

public class JugadorListener implements Listener{

	private Codex plugin;
	public JugadorListener(Codex plugin) {
		this.plugin = plugin;
	}
	
	@EventHandler
	public void alEntrarARegion(WorldGuardAPIRegionEnterEvent event) {
		final Player jugador = event.getPlayer();
		String region = event.getRegion();
		ArrayList<CategoriaCodex> categorias = plugin.getCategoriasManager().getCategorias();
		for(final CategoriaCodex categoria : categorias) {
			List<EntradaCodex> entradas = categoria.getEntradas();
			for(final EntradaCodex entrada : entradas) {
				String nombreRegion = entrada.getDiscoveredOnRegion();
				if(nombreRegion != null && region.equals(nombreRegion)) {
					plugin.getJugadorDataManager().agregarEntrada(jugador, categoria.getPath(), entrada.getId(), new AgregarEntradaCallback() {
						@Override
						public void onDone(boolean agrega) {
							if(agrega) {
								plugin.getCodexManager().desbloquearEntrada(jugador, categoria, entrada);
							}
						}
					});
					return;
				}
			}
		}
	}
	
	@EventHandler
	public void alMatarMob(EntityDeathEvent event) {
		LivingEntity e = event.getEntity();
		final Player jugador = e.getKiller();
		String nombreEntidad = null;
		if(e.getCustomName() != null) {
			nombreEntidad = ChatColor.stripColor(e.getCustomName());
		}
		if(jugador != null) {
			ArrayList<CategoriaCodex> categorias = plugin.getCategoriasManager().getCategorias();
			for(final CategoriaCodex categoria : categorias) {
				List<EntradaCodex> entradas = categoria.getEntradas();
				for(final EntradaCodex entrada : entradas) {
					EntradaCodexOpcionesMobKill opcionesMobKill = entrada.getDiscoveredOnMobKill();
					if(opcionesMobKill != null) {
						String nombre = opcionesMobKill.getNombre();
						String tipo = opcionesMobKill.getType();
						boolean pasa = false;
						if(nombre == null) {
							//Solo el tipo debe ser igual
							if(e.getType().name().equals(tipo)) {
								pasa = true;
							}
						}else if(tipo == null) {
							//Solo el nombre debe ser igual
							if(nombreEntidad != null && nombreEntidad.startsWith(nombre)) {
								pasa = true;
							}
						}else {
							//El nombre y el tipo deben ser iguales
							if(nombreEntidad != null && nombreEntidad.startsWith(nombre)
									&& e.getType().name().equals(tipo)) {
								pasa = true;
							}
						}
						if(pasa) {
							plugin.getJugadorDataManager().agregarEntrada(jugador, categoria.getPath(), entrada.getId(), new AgregarEntradaCallback() {
								@Override
								public void onDone(boolean agrega) {
									if(agrega) {
										plugin.getCodexManager().desbloquearEntrada(jugador, categoria, entrada);
									}
								}
							});
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void alSalir(PlayerQuitEvent event) {
		Player jugador = event.getPlayer();
		plugin.getInventarioManager().eliminarJugadorInventario(jugador.getName());
		if(Bukkit.getServer().getPluginManager().getPlugin("WorldGuard") != null){
			plugin.getWorldGuardAPI().removerJugador(jugador.getName());
		}
	}
	
	@EventHandler
	public void alCerrarInventario(InventoryCloseEvent event) {
		Player jugador = (Player) event.getPlayer();
		plugin.getInventarioManager().eliminarJugadorInventario(jugador.getName());
	}
	
	@EventHandler
	public void alClickearInventario(InventoryClickEvent event) {
		Player jugador = (Player) event.getWhoClicked();
		InventarioManager invManager = plugin.getInventarioManager();
		JugadorInventario jugadorInv = invManager.getJugadorInventario(jugador);
		if(jugadorInv != null) {
			if(event.getCurrentItem() == null){
				event.setCancelled(true);
				return;
			}
			if((event.getSlotType() == null)){
				event.setCancelled(true);
				return;
			}else{
				event.setCancelled(true);
				if(event.getClickedInventory().equals(jugador.getOpenInventory().getTopInventory())) {
					int slot = event.getSlot();
					String inventario = jugadorInv.getInventario();
					InventarioCodex invCodex = invManager.getInventario(inventario);
					if(invCodex == null) {
						return;
					}
					
					for(ItemInventarioCodex itemCodex : invCodex.getItems()) {
						List<Integer> slots = itemCodex.getSlots();
						for(int slotItem : slots) {
							if(slotItem == slot) {
								invManager.clickearItemInventario(itemCodex, jugador);
								return;
							}
						}
					}
				}
			}
		}
	}
}
