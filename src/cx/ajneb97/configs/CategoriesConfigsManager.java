package cx.ajneb97.configs;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.conversations.Conversation;

import cx.ajneb97.Codex;
import cx.ajneb97.model.CategoriaCodex;
import cx.ajneb97.model.CategoriaCodexOpcionesSonido;
import cx.ajneb97.model.CategoriaCodexOpcionesTitle;
import cx.ajneb97.model.EntradaCodex;
import cx.ajneb97.model.EntradaCodexOpcionesMobKill;


public class CategoriesConfigsManager {

	private ArrayList<CategoriesConfig> configCategories;
	private Codex plugin;
	
	public CategoriesConfigsManager(Codex plugin) {
		this.plugin = plugin;
		this.configCategories = new ArrayList<CategoriesConfig>();
	}
	
	public void configurar() {
		createCategoriesFolder();
		registerCategories();
		cargarCategories();
	}
	
	public void createCategoriesFolder(){
		File folder;
        try {
            folder = new File(plugin.getDataFolder() + File.separator + "categories");
            if(!folder.exists()){
                folder.mkdirs();
                String pathName = "history.yml";
                CategoriesConfig config = new CategoriesConfig(pathName,plugin);
		        config.registerCategoriesConfig(false);
		        pathName = "regions.yml";
                config = new CategoriesConfig(pathName,plugin);
		        config.registerCategoriesConfig(false);
		        pathName = "monsters.yml";
                config = new CategoriesConfig(pathName,plugin);
		        config.registerCategoriesConfig(false);
            }
        } catch(SecurityException e) {
            folder = null;
        }
	}
	
	public void registerCategories(){
		String path = plugin.getDataFolder() + File.separator + "categories";
		File folder = new File(path);
		File[] listOfFiles = folder.listFiles();
		for (int i=0;i<listOfFiles.length;i++) {
			if(listOfFiles[i].isFile()) {
		        String pathName = listOfFiles[i].getName();
		        CategoriesConfig config = new CategoriesConfig(pathName,plugin);
		        if(pathName.equals("history.yml") || pathName.equals("regions.yml") || pathName.equals("monsters.yml")) {
		        	config.registerCategoriesConfig(false);
		        }else {
		        	config.registerCategoriesConfig(true);
		        }
		        
		        configCategories.add(config);
		    }
		}
	}
	
	public boolean archivoYaRegistrado(String pathName) {
		for(int i=0;i<configCategories.size();i++) {
			if(configCategories.get(i).getPath().equals(pathName)) {
				return true;
			}
		}
		return false;
	}
  
	public CategoriesConfig getConfigCategories(String path){
		for(int i=0;i<configCategories.size();i++){
			if(configCategories.get(i).getPath().equals(path)){
				return configCategories.get(i);
			}
		}
		return null;
	}
	
	public void reloadCategories() {
		this.configCategories = new ArrayList<CategoriesConfig>();
		configurar();
	}
	
	public ArrayList<CategoriesConfig> getConfigCategories(){
		return this.configCategories;
	}
	
	public void cargarCategories() {
		ArrayList<CategoriaCodex> categoriasCodex = new ArrayList<CategoriaCodex>();
		for(CategoriesConfig c : configCategories) {
			FileConfiguration config = c.getConfig();
			
			String name = config.getString("name");
			List<EntradaCodex> entradas = new ArrayList<EntradaCodex>();
			CategoriaCodexOpcionesTitle opcionesTitle = null;
			CategoriaCodexOpcionesSonido opcionesSonido = null;
			List<String> mensajeDiscover = null;
			if(config.contains("discoveries")) {
				for(String key : config.getConfigurationSection("discoveries").getKeys(false)) {
					String nameCodex = config.getString("discoveries."+key+".name");
					List<String> loreCodex = config.getStringList("discoveries."+key+".lore");
					String discoveredOnRegion = null;
					EntradaCodexOpcionesMobKill discoveredOnMobKill = null;
					List<String> comandos = new ArrayList<String>();
					if(config.contains("discoveries."+key+".discovered_on_region")) {
						discoveredOnRegion = config.getString("discoveries."+key+".discovered_on_region");
					}
					if(config.contains("discoveries."+key+".discovered_on_mob_kill")) {
						String tipoMob = null;
						String nombreMob = null;
						String mythicMobsId = null;
						if(config.contains("discoveries."+key+".discovered_on_mob_kill.type")) {
							tipoMob = config.getString("discoveries."+key+".discovered_on_mob_kill.type");
						}
						if(config.contains("discoveries."+key+".discovered_on_mob_kill.name")) {
							nombreMob = config.getString("discoveries."+key+".discovered_on_mob_kill.name");
						}
						if(config.contains("discoveries."+key+".discovered_on_mob_kill.mythic_mobs_id")) {
							mythicMobsId = config.getString("discoveries."+key+".discovered_on_mob_kill.mythic_mobs_id");
						}
						discoveredOnMobKill = new EntradaCodexOpcionesMobKill(tipoMob,nombreMob,mythicMobsId);
					}
					if(config.contains("discoveries."+key+".commands")) {
						comandos = config.getStringList("discoveries."+key+".commands");
					}
					EntradaCodex entrada = new EntradaCodex(key,nameCodex,loreCodex,discoveredOnRegion,discoveredOnMobKill
							,comandos);
					entradas.add(entrada);
				}
			}
			if(config.contains("discover_title")) {
				String title = config.getString("discover_title.title");
				String subtitle = config.getString("discover_title.subtitle");
				int fadeIn = Integer.valueOf(config.getString("discover_title.fade_in"));
				int stay = Integer.valueOf(config.getString("discover_title.stay"));
				int fadeOut = Integer.valueOf(config.getString("discover_title.fade_out"));
				opcionesTitle = new CategoriaCodexOpcionesTitle(title,subtitle,fadeIn,stay,fadeOut);
			}
			if(config.contains("discover_sound")) {
				String soundName  = config.getString("discover_sound.name");
				int soundVolume = Integer.valueOf(config.getString("discover_sound.volume"));
				float soundPitch = Float.valueOf(config.getString("discover_sound.pitch"));
				opcionesSonido = new CategoriaCodexOpcionesSonido(soundName, soundVolume, soundPitch);
			}
			if(config.contains("discover_message")) {
				mensajeDiscover = config.getStringList("discover_message");
			}
			categoriasCodex.add(new CategoriaCodex(c.getPath(),name,entradas,opcionesTitle,mensajeDiscover,opcionesSonido));
		}
		plugin.getCategoriasManager().setCategorias(categoriasCodex);
	}
}
