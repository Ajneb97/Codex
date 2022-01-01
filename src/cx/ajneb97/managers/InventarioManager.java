package cx.ajneb97.managers;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import cx.ajneb97.Codex;
import cx.ajneb97.api.CodexAPI;
import cx.ajneb97.data.JugadorCodex;
import cx.ajneb97.data.JugadorCodexCallback;
import cx.ajneb97.model.CategoriaCodex;
import cx.ajneb97.model.EntradaCodex;
import cx.ajneb97.model.InventarioCodex;
import cx.ajneb97.model.ItemInventarioCodex;
import cx.ajneb97.model.JugadorInventario;
import cx.ajneb97.utilidades.UtilidadesItems;
import cx.ajneb97.utilidades.UtilidadesOtros;
import me.clip.placeholderapi.PlaceholderAPI;

public class InventarioManager {

	private ArrayList<InventarioCodex> inventarios;
	private ArrayList<JugadorInventario> jugadores;
	private Codex plugin;
	public InventarioManager(Codex plugin) {
		this.inventarios = new ArrayList<InventarioCodex>();
		this.jugadores = new ArrayList<JugadorInventario>();
		this.plugin = plugin;
	}

	public ArrayList<InventarioCodex> getInventarios() {
		return inventarios;
	}

	public void setInventarios(ArrayList<InventarioCodex> inventarios) {
		this.inventarios = inventarios;
	}
	
	public void agregarJugadorInventario(Player jugador,String inventario) {
		this.jugadores.add(new JugadorInventario(jugador,inventario));
	}
	
	public JugadorInventario getJugadorInventario(Player jugador) {
		for(JugadorInventario j : jugadores) {
			if(jugador.getName().equals(j.getJugador().getName())) {
				return j;
			}
		}
		return null;
	}
	
	public void eliminarJugadorInventario(String nombre) {
		for(int i=0;i<jugadores.size();i++) {
			if(jugadores.get(i).getJugador().getName().equals(nombre)) {
				jugadores.remove(i);
			}
		}
	}
	
	public InventarioCodex getInventario(String nombre) {
		for(InventarioCodex inv : inventarios) {
			if(inv.getNombre().equals(nombre)) {
				return inv;
			}
		}
		return null;
	}

	public void crearInventario(String nombre,Player jugador) {
		InventarioCodex invCodex = getInventario(nombre);
		if(invCodex == null) {
			return;
		}
		FileConfiguration messages = plugin.getConfigsManager().getMensajesConfigManager().getMessages();
		FileConfiguration config = plugin.getConfig();
		boolean esconderNombres = Boolean.valueOf(config.getString("hide_locked_discoveries_name"));
		CategoriasManager categoriasManager = plugin.getCategoriasManager();
		JugadorDataManager jugadorManager = plugin.getJugadorDataManager();
		Inventory inv = Bukkit.createInventory(null, invCodex.getSlots(), MensajesManager.getMensajeColor(invCodex.getTitle()));
		
		String currentUnlockedDiscoveriesColorNone = messages.getString("currentUnlockedDiscoveriesColorNone");
		String currentUnlockedDiscoveriesColorAll = messages.getString("currentUnlockedDiscoveriesColorAll");
		String currentUnlockedDiscoveriesColorIncomplete = messages.getString("currentUnlockedDiscoveriesColorIncomplete");
		
		jugadorManager.getJugador(jugador.getName(), new JugadorCodexCallback() {
			@Override
			public void onDone(JugadorCodex j) {
				for(ItemInventarioCodex itemCodex : invCodex.getItems()) {
					List<Integer> slots = itemCodex.getSlots();
					for(int slot : slots) {
						String tipo = itemCodex.getTipo();
						ItemStack item = itemCodex.getItem().clone();
						
						
						String variableNombre = "";
						String variableUnlocked = "";
						String variablePorcentaje = "";
						String variableBar = "";
						List<String> variableLoreItem = null;
						boolean desbloqueada = false;
						boolean discovery = false;
						
						if(tipo != null) {
							if(tipo.contains(";")) {
								//Es un discovery
								discovery = true;
								String[] sep = tipo.split(";");
								CategoriaCodex categoriaCodex = categoriasManager.getCategoria(sep[0]);
								if(categoriaCodex != null) {
									EntradaCodex entradaCodex = categoriaCodex.getEntrada(sep[1]);
									if(entradaCodex != null) {
										variableNombre = entradaCodex.getNombre();
										variableLoreItem = entradaCodex.getLore();
										
										if(j != null) {
											desbloqueada = j.tieneEntrada(sep[0], sep[1]);
										}
									}
								}
							}else {
								//Es solo categoria
								CategoriaCodex categoriaCodex = categoriasManager.getCategoria(tipo);
								if(categoriaCodex != null) {
									variableNombre = categoriaCodex.getNombre();
									
									int entradasDesbloqueadas = 0;
									if(j != null) {
										entradasDesbloqueadas = j.getEntradasDesbloqueadas(tipo);
									}
									int entradasMaximas = categoriaCodex.getEntradas().size();
									
									String mensaje = messages.getString("inventoryUnlockedVariable");
									if(entradasDesbloqueadas == 0) {
										mensaje = mensaje.replace("%current%", currentUnlockedDiscoveriesColorNone+""+entradasDesbloqueadas)
												.replace("%max%", currentUnlockedDiscoveriesColorNone+""+entradasMaximas);
									}else if(entradasDesbloqueadas == entradasMaximas) {
										mensaje = mensaje.replace("%current%", currentUnlockedDiscoveriesColorAll+""+entradasDesbloqueadas)
												.replace("%max%", currentUnlockedDiscoveriesColorAll+""+entradasMaximas);
									}else {
										mensaje = mensaje.replace("%current%", currentUnlockedDiscoveriesColorIncomplete+""+entradasDesbloqueadas)
												.replace("%max%", currentUnlockedDiscoveriesColorIncomplete+""+entradasMaximas);
									}
									variableUnlocked = mensaje;
									
									int porcentaje = entradasDesbloqueadas*100/entradasMaximas;
									variablePorcentaje = porcentaje+"%";
									
									variableBar = UtilidadesOtros.getUnlockedBar(config, entradasDesbloqueadas, entradasMaximas);
								}
							}
						}
						
						String itemName = config.getString("locked_discoveries_item");
						if(discovery && !desbloqueada) {
							if(itemName.startsWith("ey")) {
								item = UtilidadesItems.crearSkull(itemName);
							}else {
								try {
									item = UtilidadesItems.crearItem(itemName);
								}catch(Exception e) {
									Bukkit.getConsoleSender().sendMessage(Codex.nombrePlugin+ChatColor.translateAlternateColorCodes('&', 
											"&7Item: &c"+itemName+" &7on: &6config.yml &7is not valid. Change it to fix this!"));
									return;
								}
							}
						}
						
						ItemMeta meta = item.getItemMeta();
						if(itemCodex.getNombre() != null) {
							if(discovery && !desbloqueada && esconderNombres) {
								String nombreItem = itemCodex.getNombre().replace("%name%", 
										messages.getString("inventoryLockedDiscoveryName"));
								meta.setDisplayName(MensajesManager.getMensajeColor(nombreItem));
							}else {
								String nombreItem = itemCodex.getNombre().replace("%name%", variableNombre);
								meta.setDisplayName(MensajesManager.getMensajeColor(nombreItem));
							}
							if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
								String linea = PlaceholderAPI.setPlaceholders(jugador, meta.getDisplayName());
								meta.setDisplayName(MensajesManager.getMensajeColor(linea));
							}
						}
						if(itemCodex.getLore() != null) {
							List<String> lore = new ArrayList<String>(itemCodex.getLore());
							for(int i=0;i<lore.size();i++) {
								if(lore.get(i).startsWith("%lore%") && variableLoreItem != null) {
									if(discovery) {
										if(desbloqueada) {
											for(String linea : variableLoreItem) {
												lore.add(MensajesManager.getMensajeColor(linea));
											}
											lore.remove(i);
										}else {
											List<String> loreBloqueada = messages.getStringList("inventoryLockedDiscoveryLore");
											for(String linea : loreBloqueada) {
												lore.add(MensajesManager.getMensajeColor(linea));
											}
											lore.remove(i);
										}
									}else {
										for(String linea : variableLoreItem) {
											lore.add(MensajesManager.getMensajeColor(linea));
										}
										lore.remove(i);
									}
								}else {
									lore.set(i, MensajesManager.getMensajeColor(lore.get(i).replace("%unlocked%", variableUnlocked).replace("%percentage%", variablePorcentaje)
											.replace("%progress_bar%", variableBar)));
								}
							}
							if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
								for(int i=0;i<lore.size();i++) {
									String linea = PlaceholderAPI.setPlaceholders(jugador, lore.get(i));
									lore.set(i, MensajesManager.getMensajeColor(linea));
								}
							}
							meta.setLore(lore);
						}
						
						if(discovery && !desbloqueada) {
							int customModelData = config.getInt("locked_discoveries_item_custom_model_data");
							if(UtilidadesOtros.esNew() && customModelData != 0) {
								meta.setCustomModelData(customModelData);
							}
						}
						
						item.setItemMeta(meta);
						inv.setItem(slot, item);
					}
				}
				
				jugador.openInventory(inv);
				agregarJugadorInventario(jugador, nombre);
			}
		});
	}
	
	public void clickearItemInventario(ItemInventarioCodex itemCodex,Player jugador) {
		FileConfiguration messages = plugin.getMessages();
		List<String> comandos = itemCodex.getComandos();
		
		//Revisar si el jugador lo tiene desbloqueado
		boolean desbloqueado = true;
		if(itemCodex.getTipo() != null) {
			String[] tipo = itemCodex.getTipo().split(";");
			if(tipo.length == 2) {
				String category = tipo[0];
				String discovery = tipo[1];
				JugadorCodex j = plugin.getJugadorDataManager().getJugadorSync(jugador.getName());
				if(!j.tieneEntrada(category, discovery)) {
					desbloqueado = false;
				}
			}
		}
		
		if(!comandos.isEmpty()) {
			if(!desbloqueado) {
				plugin.getMensajesManager().enviarMensaje(jugador, messages.getString("clickLockedDiscoveryCommand"), true);
				return;
			}
			
			for(String comando : comandos) {
				ConsoleCommandSender sender = Bukkit.getConsoleSender();
				boolean ejecutaPlayer = false;
				if(comando.startsWith("player_command: ")) {
					comando = comando.replace("player_command: ", "");
					ejecutaPlayer = true;
				}
				if(comando.startsWith("msg %player% ")) {
					plugin.getMensajesManager().enviarMensaje(jugador, comando.replace("msg %player% ", ""), false);
				}else {
					if(ejecutaPlayer) {
						jugador.chat("/"+comando);
					}else {
						Bukkit.dispatchCommand(sender, comando.replace("%player%", jugador.getName()));
					}
					
				}
			}
		}
		
		
		String abreInventario = itemCodex.getAbreInventario();
		if(abreInventario != null) {
			if(abreInventario.equalsIgnoreCase("close")) {
				jugador.closeInventory();
				return;
			}
			crearInventario(abreInventario, jugador);
		}
	}
}
