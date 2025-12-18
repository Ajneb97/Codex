package cx.ajneb97.config;

import cx.ajneb97.Codex;
import cx.ajneb97.model.data.PlayerData;
import cx.ajneb97.model.data.PlayerDataCategory;
import cx.ajneb97.model.data.PlayerDataDiscovery;
import cx.ajneb97.model.internal.GenericCallback;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;

public class PlayersConfigManager extends DataFolderConfigManager {


    public PlayersConfigManager(Codex plugin, String folderName) {
        super(plugin, folderName);
    }

    @Override
    public void loadConfigs(){
        // No use for player config
    }

    public void loadConfig(UUID uuid, GenericCallback<PlayerData> callback){
        new BukkitRunnable(){
            @Override
            public void run() {
                PlayerData playerData = null;
                CommonConfig playerConfig = getConfigFile(uuid+".yml",false);
                if(playerConfig != null){
                    // If config exists
                    FileConfiguration config = playerConfig.getConfig();
                    String name = config.getString("name");
                    ArrayList<PlayerDataCategory> playerDataCategories = new ArrayList<>();
                    if(config.contains("categories")){
                        for(String key : config.getConfigurationSection("categories").getKeys(false)){
                            List<String> discoveriesStringList = config.getStringList("categories."+key+".discoveries");
                            boolean completed = config.getBoolean("categories."+key+".completed");

                            ArrayList<PlayerDataDiscovery> discoveries = new ArrayList<>();
                            for(String d : discoveriesStringList){
                                String[] sep = d.split(";");
                                long millisActionsExecuted = 0;
                                if(sep.length == 3){
                                    millisActionsExecuted = Long.parseLong(sep[2]);
                                }
                                discoveries.add(new PlayerDataDiscovery(sep[0],sep[1],millisActionsExecuted));
                            }
                            playerDataCategories.add(new PlayerDataCategory(key,completed,discoveries));
                        }
                    }

                    playerData = new PlayerData(uuid,name);
                    playerData.setCategories(playerDataCategories);
                }

                PlayerData finalPlayer = playerData;

                new BukkitRunnable(){
                    @Override
                    public void run() {
                        callback.onDone(finalPlayer);
                    }
                }.runTask(plugin);
            }
        }.runTaskAsynchronously(plugin);
    }

    public void saveConfig(PlayerData playerData){
        String playerName = playerData.getName();
        CommonConfig playerConfig = getConfigFile(playerData.getUuid()+".yml",true);
        FileConfiguration config = playerConfig.getConfig();

        config.set("name", playerName);
        config.set("categories",null);
        List<PlayerDataCategory> categories = playerData.getCategories();
        for(PlayerDataCategory category : categories){
            String path = "categories."+category.getName();
            config.set(path+".completed",category.isCompleted());
            config.set(path+".discoveries",category.getDiscoveriesToStringList());
        }

        playerConfig.saveConfig();
    }

    @Override
    public void saveConfigs(){
        Map<UUID, PlayerData> players = plugin.getPlayerDataManager().getPlayers();
        boolean isMySQL = plugin.getConfigsManager().getMainConfigManager().isMySQL();
        if(!isMySQL){
            for(Map.Entry<UUID, PlayerData> entry : players.entrySet()) {
                PlayerData playerData = entry.getValue();
                if(playerData.isModified()){
                    saveConfig(playerData);
                }
                playerData.setModified(false);
            }
        }

    }

    public void resetDataForAllPlayers(String categoryName,String discoveryName){
        ArrayList<CommonConfig> configs = getConfigs();
        for(CommonConfig commonConfig : configs) {
            FileConfiguration config = commonConfig.getConfig();

            if(categoryName == null){
                config.set("categories",null);
                commonConfig.saveConfig();
                continue;
            }

            if(discoveryName == null){
                if(!config.contains("categories."+categoryName)){
                    continue;
                }
                config.set("categories."+categoryName,null);
                commonConfig.saveConfig();
                continue;
            }

            List<String> discoveries = config.getStringList("categories."+categoryName+".discoveries");
            int findIndex = -1;
            for(int i=0;i<discoveries.size();i++){
                if(discoveries.get(i).split(";")[0].equals(discoveryName)){
                    findIndex = i;
                    break;
                }
            }
            if(findIndex != -1){
                discoveries.remove(findIndex);
                config.set("categories."+categoryName+".discoveries",discoveries);
                commonConfig.saveConfig();
            }
        }
    }

    @Override
    public void createFiles() {

    }

}
