package cx.ajneb97.config;

import cx.ajneb97.Codex;
import org.bukkit.configuration.file.FileConfiguration;

public class MainConfigManager {

    private Codex plugin;
    private CommonConfig configFile;

    private boolean isMySQL;
    private boolean updateNotify;
    private String discoveriesDateFormat;
    private int playerDataSave;
    private String progressBarPlaceholderFillSymbol;
    private String progressBarPlaceholderEmptySymbol;
    private int progressBarPlaceholderAmount;
    private int configVersion;

    public MainConfigManager(Codex plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("config.yml",plugin,null,false);
        configFile.registerConfig();
    }

    public void configure() {
        FileConfiguration config = configFile.getConfig();
        isMySQL = config.getBoolean("mysql_database.enabled");
        updateNotify = config.getBoolean("update_notify");
        discoveriesDateFormat = config.getString("discoveries_date_format");
        playerDataSave = config.getInt("player_data_save");
        progressBarPlaceholderFillSymbol = config.getString("progress_bar_placeholder.filled_symbol");
        progressBarPlaceholderEmptySymbol = config.getString("progress_bar_placeholder.empty_symbol");
        progressBarPlaceholderAmount = config.getInt("progress_bar_placeholder.amount");
        configVersion = config.getInt("config_version");
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    public CommonConfig getConfigFile() {
        return configFile;
    }

    public FileConfiguration getConfig(){
        return configFile.getConfig();
    }

    public boolean isMySQL() {
        return isMySQL;
    }

    public boolean isUpdateNotify() {
        return updateNotify;
    }

    public String getDiscoveriesDateFormat() {
        return discoveriesDateFormat;
    }

    public int getPlayerDataSave() {
        return playerDataSave;
    }

    public int getConfigVersion() {
        return configVersion;
    }

    public String getProgressBarPlaceholderFillSymbol() {
        return progressBarPlaceholderFillSymbol;
    }

    public String getProgressBarPlaceholderEmptySymbol() {
        return progressBarPlaceholderEmptySymbol;
    }

    public int getProgressBarPlaceholderAmount() {
        return progressBarPlaceholderAmount;
    }
}
