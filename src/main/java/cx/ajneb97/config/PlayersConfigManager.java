package cx.ajneb97.config;

import cx.ajneb97.Codex;
import cx.ajneb97.model.data.PlayerData;
import cx.ajneb97.model.data.PlayerDataCategory;
import cx.ajneb97.model.data.PlayerDataDiscovery;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.*;

public class PlayersConfigManager extends DataFolderConfigManager {


    public PlayersConfigManager(Codex plugin, String folderName) {
        super(plugin, folderName);
    }

    @Override
    public void loadConfigs(){
        Map<UUID, PlayerData> players = new HashMap<>();

        String path = plugin.getDataFolder() + File.separator + folderName;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                CommonConfig commonConfig = new CommonConfig(file.getName(), plugin, folderName, true);
                commonConfig.registerConfig();

                FileConfiguration config = commonConfig.getConfig();
                String uuidString = commonConfig.getPath().replace(".yml", "");
                String name = config.getString("name");
                ArrayList<PlayerDataCategory> playerDataCategories = new ArrayList<>();
                if(config.contains("categories")){
                    for(String key : config.getConfigurationSection("categories").getKeys(false)){
                        List<String> discoveriesStringList = config.getStringList("categories."+key+".discoveries");
                        boolean completed = config.getBoolean("categories."+key+".completed");

                        ArrayList<PlayerDataDiscovery> discoveries = new ArrayList<>();
                        for(String d : discoveriesStringList){
                            String[] sep = d.split(";");
                            discoveries.add(new PlayerDataDiscovery(sep[0],sep[1]));
                        }
                        playerDataCategories.add(new PlayerDataCategory(key,completed,discoveries));
                    }
                }

                UUID uuid = UUID.fromString(uuidString);
                PlayerData playerData = new PlayerData(uuid,name);
                playerData.setCategories(playerDataCategories);

                players.put(uuid,playerData);
            }
        }


        plugin.getPlayerDataManager().setPlayers(players);
    }

    public void saveConfig(PlayerData playerData){
        String playerName = playerData.getName();
        CommonConfig playerConfig = getConfigFile(playerData.getUuid()+".yml");
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

    @Override
    public void createFiles() {

    }

}
