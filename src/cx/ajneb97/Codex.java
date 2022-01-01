package cx.ajneb97;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import cx.ajneb97.api.CodexAPI;
import cx.ajneb97.api.ExpansionCodex;
import cx.ajneb97.configs.ConfigsManager;
import cx.ajneb97.data.ConexionMySQL;
import cx.ajneb97.data.MySQL;
import cx.ajneb97.libs.worldguard.WorldGuardAPI;
import cx.ajneb97.listeners.Entrar;
import cx.ajneb97.listeners.JugadorListener;
import cx.ajneb97.listeners.MythicMobsListener;
import cx.ajneb97.managers.CategoriasManager;
import cx.ajneb97.managers.CodexManager;
import cx.ajneb97.managers.InventarioManager;
import cx.ajneb97.managers.JugadorDataManager;
import cx.ajneb97.managers.MensajesManager;
import cx.ajneb97.tasks.SavePlayersTask;

public class Codex extends JavaPlugin {
  
	PluginDescriptionFile pdfFile = getDescription();
	public String version = pdfFile.getVersion();
	public String latestversion;
	
	private JugadorDataManager jugadorDataManager;
	private CategoriasManager categoriasManager;
	private MensajesManager mensajesManager;
	private ConfigsManager configsManager;
	private CodexManager codexManager;
	private InventarioManager inventarioManager;
	private SavePlayersTask savePlayersTask;
	
	private WorldGuardAPI worldGuardAPI;
	
	private boolean erroresEnItems;
	private boolean primeraVezConfig;
	
	private ConexionMySQL conexionDatabase;
	
	public String rutaConfig;
	
	public static String nombrePlugin = ChatColor.translateAlternateColorCodes('&', "&4[&cCodex&4] ");
	
	public void onEnable(){
	   primeraVezConfig = false;
	   this.jugadorDataManager = new JugadorDataManager(this);
	   this.categoriasManager = new CategoriasManager(this);
	   this.inventarioManager = new InventarioManager(this);
	   this.configsManager = new ConfigsManager(this);
	   this.codexManager = new CodexManager(this);
	   
	   registerEvents();
	   registerCommands();
	   registerConfig();
	   if(primeraVezConfig) {
		   Checks.checkearYArreglarConfig(this, getConfig());
	   }
	   
	   if(MySQL.isEnabled(getConfig())) {
		   conexionDatabase = new ConexionMySQL();
		   conexionDatabase.setupMySql(this, getConfig());
	   }
	   
	   if(getServer().getPluginManager().getPlugin("WorldGuard") != null){
		   this.worldGuardAPI = new WorldGuardAPI(this);
	   }
	   
	   CodexAPI api = new CodexAPI(this);
	   if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null){
		   new ExpansionCodex(this).register();
	   }
	   
	   restartSavePlayersTask();
	   checkMessagesUpdate();

	   Bukkit.getConsoleSender().sendMessage(nombrePlugin+ChatColor.YELLOW + "Has been enabled! " + ChatColor.WHITE + "Version: " + version);
	   Bukkit.getConsoleSender().sendMessage(nombrePlugin+ChatColor.YELLOW + "Thanks for using my plugin!  " + ChatColor.WHITE + "~Ajneb97");
	   updateChecker();
	}
	  
	public void onDisable(){
		configsManager.getPlayerConfigsManager().guardarJugadores();
		Bukkit.getConsoleSender().sendMessage(nombrePlugin+ChatColor.YELLOW + "Has been disabled! " + ChatColor.WHITE + "Version: " + version);
	}
	public void registerCommands(){
		this.getCommand("codex").setExecutor(new Comando(this));
	}
	
	public void registerEvents(){
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvents(new JugadorListener(this), this);
		pm.registerEvents(new Entrar(this), this);
		if(getServer().getPluginManager().getPlugin("MythicMobs") != null){
			pm.registerEvents(new MythicMobsListener(this), this);
		}
	}
	
	public void registerConfig(){
		File config = new File(this.getDataFolder(),"config.yml");
		rutaConfig = config.getPath();
		if(!config.exists()){
			primeraVezConfig = true;
			this.getConfig().options().copyDefaults(true);
			saveConfig();
		}
	}
	
	public void restartSavePlayersTask() {
		if(savePlayersTask != null) {
			savePlayersTask.end();
		}
		savePlayersTask = new SavePlayersTask(this);
		savePlayersTask.start();
	}
	
	public JugadorDataManager getJugadorDataManager() {
		return jugadorDataManager;
	}

	public CategoriasManager getCategoriasManager() {
		return categoriasManager;
	}

	public MensajesManager getMensajesManager() {
		return mensajesManager;
	}

	public void setMensajesManager(MensajesManager mensajesManager) {
		this.mensajesManager = mensajesManager;
	}

	public ConfigsManager getConfigsManager() {
		return configsManager;
	}
	
	public CodexManager getCodexManager() {
		return codexManager;
	}

	public FileConfiguration getMessages() {
		return configsManager.getMensajesConfigManager().getMessages();
	}

	public WorldGuardAPI getWorldGuardAPI() {
		return worldGuardAPI;
	}

	public InventarioManager getInventarioManager() {
		return inventarioManager;
	}

	public boolean isErroresEnItems() {
		return erroresEnItems;
	}

	public void setErroresEnItems(boolean erroresEnItems) {
		this.erroresEnItems = erroresEnItems;
	}
	
	public Connection getConnection() {
		return this.conexionDatabase.getConnection();
	}
	
	public void checkMessagesUpdate(){
		  Path archivoMessages = Paths.get(configsManager.getMensajesConfigManager().getPath());
		  Path archivoConfig = Paths.get(rutaConfig);
		  try{
			  String textoMessages = new String(Files.readAllBytes(archivoMessages));
			  String textoConfig = new String(Files.readAllBytes(archivoConfig));
			  FileConfiguration messages = configsManager.getMensajesConfigManager().getMessages();
			  FileConfiguration config = getConfig();
			  
			  if(!textoConfig.contains("progress_bar_placeholder:")){
				  config.set("progress_bar_placeholder.filled_symbol", "&a|");
				  config.set("progress_bar_placeholder.empty_symbol", "&c|");
				  config.set("progress_bar_placeholder.amount", 20);
				  config.set("data_auto_save_time", 600);
				  config.set("locked_discoveries_item_custom_model_data", 0);
				  saveConfig();
			  }
			  
			  if(!textoConfig.contains("mysql_database:")){
				  config.set("mysql_database.enabled", false);
				  config.set("mysql_database.host", "localhost");
				  config.set("mysql_database.port", 3306);
				  config.set("mysql_database.username", "root");
				  config.set("mysql_database.password", "root");
				  config.set("mysql_database.database", "database");
				  saveConfig();
			  }

			  if(!textoMessages.contains("commandOpenErrorUse:")){
				  messages.set("commandOpenErrorUse", "&cYou need to use: &7/codex open <player> <inventory>");
				  messages.set("inventoryDoesNotExists", "&cThe inventory &7%inventory% &cdoesn't exists.");
				  messages.set("playerOpenInventory", "&aInventory &7%inventory% &aopened for &e%player%&a!");
				  messages.set("commandHelpNoValidPage", "&cThat page doesn't exists.");
				  configsManager.getMensajesConfigManager().saveMessages();
			  }
			  
			  if(!textoMessages.contains("currentUnlockedDiscoveriesColorNone:")){
				  messages.set("currentUnlockedDiscoveriesColorNone", "&c");
				  messages.set("currentUnlockedDiscoveriesColorAll", "&a");
				  messages.set("currentUnlockedDiscoveriesColorIncomplete", "&e");
				  configsManager.getMensajesConfigManager().saveMessages();
			  }
		  }catch(IOException e){
			  e.printStackTrace();
		  }
	}
	
	public void updateChecker(){
		  try {
			  HttpURLConnection con = (HttpURLConnection) new URL(
	                  "https://api.spigotmc.org/legacy/update.php?resource=90371").openConnection();
	          int timed_out = 1250;
	          con.setConnectTimeout(timed_out);
	          con.setReadTimeout(timed_out);
	          latestversion = new BufferedReader(new InputStreamReader(con.getInputStream())).readLine();
	          if (latestversion.length() <= 7) {
	        	  if(!version.equals(latestversion)){
	        		  Bukkit.getConsoleSender().sendMessage(ChatColor.RED +"There is a new version available. "+ChatColor.YELLOW+
	        				  "("+ChatColor.GRAY+latestversion+ChatColor.YELLOW+")");
	        		  Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"You can download it at: "+ChatColor.WHITE+"https://www.spigotmc.org/resources/90371/");  
	        	  }      	  
	          }
	      } catch (Exception ex) {
	    	  Bukkit.getConsoleSender().sendMessage(nombrePlugin + ChatColor.RED +"Error while checking update.");
	      }
	  }
}
