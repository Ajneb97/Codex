package cx.ajneb97.configs;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import cx.ajneb97.Checks;
import cx.ajneb97.Codex;
import cx.ajneb97.model.InventarioCodex;
import cx.ajneb97.model.ItemInventarioCodex;
import cx.ajneb97.utilidades.UtilidadesItems;

public class InventoryConfigManager {

	private Codex plugin;
	private FileConfiguration inventory = null;
	private File inventoryFile = null;
	private boolean esPrimeraVez = false;
	
	public InventoryConfigManager(Codex plugin) {
		this.plugin = plugin;
	}
	
	public void configurar() {
		registerInventory();
		if(esPrimeraVez) {
			Checks.checkearYArreglarInventarios(this);
		}
		esPrimeraVez = false;
		cargarInventarios();
	}
	
	public void registerInventory(){
		inventoryFile = new File(plugin.getDataFolder(), "inventory.yml");
		  if(!inventoryFile.exists()){
			    esPrimeraVez = true;
		    	this.getInventory().options().copyDefaults(true);
				saveInventory();
		    }
	  }
	  public void saveInventory() {
		 try {
			 inventory.save(inventoryFile);
		 } catch (IOException e) {
			 e.printStackTrace();
	 	}
	 }
	  
	  public FileConfiguration getInventory() {
		    if (inventory == null) {
		        reloadInventory();
		    }
		    return inventory;
		}
	  
	  public void reloadInventory() {
		    if (inventory == null) {
		    	inventoryFile = new File(plugin.getDataFolder(), "inventory.yml");
		    }
		    inventory = YamlConfiguration.loadConfiguration(inventoryFile);

		    if(esPrimeraVez){
		    	 Reader defConfigStream;
					try {
						defConfigStream = new InputStreamReader(plugin.getResource("inventory.yml"), "UTF8");
						if (defConfigStream != null) {
					        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
					        inventory.setDefaults(defConfig);
					    }
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
		    }
		   
		}
	  
	  public void recargar() {
		  reloadInventory();
		  cargarInventarios();
	  }

	public void cargarInventarios() {
		ArrayList<InventarioCodex> inventarios = new ArrayList<InventarioCodex>();
		plugin.setErroresEnItems(false);
		FileConfiguration inventory = this.getInventory();
		if(inventory.contains("Inventories")) {
			for(String key : inventory.getConfigurationSection("Inventories").getKeys(false)) {
				int slots = Integer.valueOf(inventory.getString("Inventories."+key+".slots"));
				String title = inventory.getString("Inventories."+key+".title");
				List<ItemInventarioCodex> listaItems = new ArrayList<ItemInventarioCodex>();
				for(String slotString : inventory.getConfigurationSection("Inventories."+key).getKeys(false)) {
					if(!slotString.equals("slots") && !slotString.equals("title")) {
						String[] slotsSep = slotString.split(";");
						List<Integer> slotsList = new ArrayList<Integer>();
						for(int i=0;i<slotsSep.length;i++) {
							slotsList.add(Integer.valueOf(slotsSep[i]));
						}
						String path = "Inventories."+key+"."+slotString;
						String tipo = null;
						if(inventory.contains(path+".type")) {
							tipo = inventory.getString(path+".type");
						}
						String name = null;
						if(inventory.contains(path+".name")) {
							name = inventory.getString(path+".name");
						}
						List<String> lore = null;
						if(inventory.contains(path+".lore")) {
							lore = inventory.getStringList(path+".lore");
						}
						String openInventory = null;
						if(inventory.contains(path+".open_inventory")) {
							openInventory = inventory.getString(path+".open_inventory");
						}
						List<String> comandos = new ArrayList<String>();
						if(inventory.contains(path+".click_commands")) {
							comandos = inventory.getStringList(path+".click_commands");
						}
						ItemStack item = null;
						if(inventory.contains(path+".item")) {
							try {
								item = UtilidadesItems.crearItem(inventory.getString(path+".item"));
							}catch(Exception e) {
								plugin.setErroresEnItems(true);
								Bukkit.getConsoleSender().sendMessage(Codex.nombrePlugin+ChatColor.translateAlternateColorCodes('&', 
										"&7Item: &c"+inventory.getString(path+".item")+" &7on path: &6"+path+" &7is not valid. Change it in the config!"));
							}
						}else {
							if(inventory.contains(path+".skull_data")) {
								item = UtilidadesItems.crearSkull(inventory.getString(path+".skull_data"));
							}else {
								plugin.setErroresEnItems(true);
								Bukkit.getConsoleSender().sendMessage(Codex.nombrePlugin+ChatColor.translateAlternateColorCodes('&', 
										"&7There is no item on path: &6"+path+"&7! Fix it in the config!"));
							}
						}
						if(inventory.contains(path+".custom_model_data")) {
							int customModelData = Integer.valueOf(inventory.getString(path+".custom_model_data"));
							ItemMeta meta = item.getItemMeta();
							meta.setCustomModelData(customModelData);
							item.setItemMeta(meta);
						}
						
						ItemInventarioCodex itemCodex = new ItemInventarioCodex(slotsList,tipo,item,name,lore,openInventory,comandos);
						listaItems.add(itemCodex);
					}
				}	
				
				InventarioCodex inv = new InventarioCodex(key,slots,title,listaItems);
				inventarios.add(inv);
			}
		}
		
		plugin.getInventarioManager().setInventarios(inventarios);
	}
}
