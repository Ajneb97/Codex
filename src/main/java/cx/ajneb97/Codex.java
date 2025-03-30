package cx.ajneb97;

import cx.ajneb97.api.CodexAPI;
import cx.ajneb97.api.ExpansionCodex;
import cx.ajneb97.commands.MainCommand;
import cx.ajneb97.config.ConfigsManager;
import cx.ajneb97.database.MySQLConnection;
import cx.ajneb97.listeners.InventoryListener;
import cx.ajneb97.listeners.PlayerListener;
import cx.ajneb97.listeners.dependencies.EliteMobsListener;
import cx.ajneb97.listeners.dependencies.MythicMobsListener;
import cx.ajneb97.listeners.dependencies.WorldGuardListener;
import cx.ajneb97.managers.*;
import cx.ajneb97.managers.dependencies.DependencyManager;
import cx.ajneb97.managers.dependencies.Metrics;
import cx.ajneb97.model.internal.UpdateCheckerResult;
import cx.ajneb97.tasks.PlayerDataSaveTask;
import cx.ajneb97.utils.ServerVersion;
import cx.ajneb97.versions.NMSManager;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Codex extends JavaPlugin {
    PluginDescriptionFile pdfFile = getDescription();
    public String version = pdfFile.getVersion();
    public static String prefix;
    public static ServerVersion serverVersion;

    private ConfigsManager configsManager;
    private NMSManager nmsManager;
    private CommonItemManager commonItemManager;
    private MessagesManager messagesManager;
    private InventoryManager inventoryManager;
    private CategoryManager categoryManager;
    private DiscoveryManager discoveryManager;
    private DependencyManager dependencyManager;
    private PlayerDataManager playerDataManager;

    private MySQLConnection mySQLConnection;
    private PlayerDataSaveTask playerDataSaveTask;
    private UpdateCheckerManager updateCheckerManager;
    private VerifyManager verifyManager;

    public void onEnable(){
        setVersion();
        setPrefix();
        this.playerDataManager = new PlayerDataManager(this);
        this.inventoryManager = new InventoryManager(this);
        this.commonItemManager = new CommonItemManager(this);
        this.categoryManager = new CategoryManager(this);
        this.discoveryManager = new DiscoveryManager(this);
        this.dependencyManager = new DependencyManager(this);
        this.nmsManager = new NMSManager(this);
        this.configsManager = new ConfigsManager(this);
        this.configsManager.configure();

        if(configsManager.getMainConfigManager().getConfigVersion() != 2){
            legacyVersionError();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }

        registerEvents();
        registerCommands();

        reloadPlayerDataSaveTask();

        CodexAPI api = new CodexAPI(this);
        if(getServer().getPluginManager().getPlugin("PlaceholderAPI") != null){
            new ExpansionCodex(this).register();
        }
        Metrics metrics = new Metrics(this,24230);

        this.verifyManager = new VerifyManager(this);
        this.verifyManager.verify();

        if(configsManager.getMainConfigManager().isMySQL()){
            mySQLConnection = new MySQLConnection(this);
            mySQLConnection.setupMySql();
        }

        Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getColoredMessage("&eHas been enabled! &fVersion: "+version));
        Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getColoredMessage("&eThanks for using my plugin!   &f~Ajneb97"));

        updateCheckerManager = new UpdateCheckerManager(version);
        updateMessage(updateCheckerManager.check());
    }

    public void onDisable(){
        configsManager.getPlayersConfigManager().saveConfigs();

        Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getColoredMessage("&eHas been disabled! &fVersion: "+version));
    }
    public void registerCommands(){
        this.getCommand("codex").setExecutor(new MainCommand(this));
    }

    public void registerEvents(){
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new InventoryListener(this), this);
        pm.registerEvents(new PlayerListener(this), this);
        if(dependencyManager.getWorldGuardManager() != null){
            pm.registerEvents(new WorldGuardListener(this), this);
        }
        if(dependencyManager.isMythicMobs()){
            pm.registerEvents(new MythicMobsListener(this), this);
        }

        if(dependencyManager.isEliteMobs()){
            pm.registerEvents(new EliteMobsListener(this), this);
        }

    }

    public void setPrefix(){
        prefix = MessagesManager.getColoredMessage("&4[&cCodex&4] ");
    }

    public void setVersion(){
        String packageName = Bukkit.getServer().getClass().getPackage().getName();
        String bukkitVersion = Bukkit.getServer().getBukkitVersion().split("-")[0];
        switch(bukkitVersion){
            case "1.20.5":
            case "1.20.6":
                serverVersion = ServerVersion.v1_20_R4;
                break;
            case "1.21":
            case "1.21.1":
                serverVersion = ServerVersion.v1_21_R1;
                break;
            case "1.21.2":
            case "1.21.3":
                serverVersion = ServerVersion.v1_21_R2;
                break;
            case "1.21.4":
                serverVersion = ServerVersion.v1_21_R3;
                break;
            case "1.21.5":
                serverVersion = ServerVersion.v1_21_R4;
                break;
            default:
                serverVersion = ServerVersion.valueOf(packageName.replace("org.bukkit.craftbukkit.", ""));
        }
    }

    public void reloadPlayerDataSaveTask() {
        if(playerDataSaveTask != null) {
            playerDataSaveTask.end();
        }
        playerDataSaveTask = new PlayerDataSaveTask(this);
        playerDataSaveTask.start(configsManager.getMainConfigManager().getPlayerDataSave());
    }

    public CommonItemManager getCommonItemManager() {
        return commonItemManager;
    }

    public ConfigsManager getConfigsManager() {
        return configsManager;
    }

    public NMSManager getNmsManager() {
        return nmsManager;
    }

    public MessagesManager getMessagesManager() {
        return messagesManager;
    }

    public void setMessagesManager(MessagesManager messagesManager) {
        this.messagesManager = messagesManager;
    }

    public FileConfiguration getMessagesConfig(){
        return configsManager.getMessagesConfigManager().getConfigFile().getConfig();
    }

    public InventoryManager getInventoryManager() {
        return inventoryManager;
    }

    public DependencyManager getDependencyManager() {
        return dependencyManager;
    }

    public PlayerDataManager getPlayerDataManager() {
        return playerDataManager;
    }

    public MySQLConnection getMySQLConnection() {
        return mySQLConnection;
    }

    public CategoryManager getCategoryManager() {
        return categoryManager;
    }

    public DiscoveryManager getDiscoveryManager() {
        return discoveryManager;
    }

    public VerifyManager getVerifyManager() {
        return verifyManager;
    }

    public void updateMessage(UpdateCheckerResult result){
        if(!result.isError()){
            String latestVersion = result.getLatestVersion();
            if(latestVersion != null){
                Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cThere is a new version available. &e(&7"+latestVersion+"&e)"));
                Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cYou can download it at: &fhttps://modrinth.com/plugin/codex-rpg-discoveries"));
            }
        }else{
            Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage(prefix+" &cError while checking update."));
        }

    }

    public UpdateCheckerManager getUpdateCheckerManager() {
        return updateCheckerManager;
    }

    public void legacyVersionError(){
        Bukkit.getConsoleSender().sendMessage(prefix+MessagesManager.getColoredMessage("&cERROR ENABLING THE PLUGIN!"));
        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cYour current config is not supported by the newer"));
        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cversions of the plugin. More information on the"));
        Bukkit.getConsoleSender().sendMessage(MessagesManager.getColoredMessage("&cfollowing link: &fhttps://www.spigotmc.org/resources/90371/"));
    }
}
