package cx.ajneb97.configs;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import cx.ajneb97.Codex;

public class CategoriesConfig {

	private FileConfiguration config;
	private File configFile;
	private String filePath;
	private Codex plugin;
	
	public CategoriesConfig(String filePath,Codex plugin){
		this.config = null;
		this.configFile = null;
		this.filePath = filePath;
		this.plugin = plugin;
	}
	
	public String getPath(){
		return this.filePath;
	}
	
	public FileConfiguration getConfig(){
		 if (config == null) {
		        reloadCategoriesConfig();
		    }
		return this.config;
	}
	
	public void registerCategoriesConfig(boolean nueva){
		configFile = new File(plugin.getDataFolder() +File.separator + "categories",filePath);
		if(!configFile.exists()){
			if(nueva) {
				try {
					configFile.createNewFile();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else {
				plugin.saveResource("categories"+File.separator+filePath, false);		
			}  
		 }
		  		  
		  config = new YamlConfiguration();
		  try {
	            config.load(configFile);
	      } catch (IOException e) {
	            e.printStackTrace();
	      } catch (InvalidConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void saveCategoriesConfig() {
		 try {
			 config.save(configFile);
		 } catch (IOException e) {
			 e.printStackTrace();
	 	}
	 }
	  
	public void reloadCategoriesConfig() {
		    if (config == null) {
		    	configFile = new File(plugin.getDataFolder() +File.separator + "categories", filePath);
		    }
		    config = YamlConfiguration.loadConfiguration(configFile);

			if (configFile != null) {
			    YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(configFile);
			    config.setDefaults(defConfig);
			}	    
		}
}
