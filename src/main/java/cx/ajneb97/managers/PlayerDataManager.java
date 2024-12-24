package cx.ajneb97.managers;

import cx.ajneb97.Codex;
import cx.ajneb97.database.MySQLConnection;
import cx.ajneb97.model.data.PlayerData;
import cx.ajneb97.model.data.PlayerDataCategory;
import cx.ajneb97.model.data.PlayerDataDiscovery;
import cx.ajneb97.model.structure.Category;
import cx.ajneb97.model.structure.Discovery;
import cx.ajneb97.utils.OtherUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;

public class PlayerDataManager {

    private Codex plugin;
    private Map<UUID, PlayerData> players;
    private Map<String,UUID> playerNames;

    public PlayerDataManager(Codex plugin){
        this.plugin = plugin;
        this.playerNames = new HashMap<>();
    }

    public Map<UUID,PlayerData> getPlayers() {
        return players;
    }

    public void setPlayers(Map<UUID,PlayerData> players) {
        this.players = players;
        for(Map.Entry<UUID, PlayerData> entry : players.entrySet()){
            playerNames.put(entry.getValue().getName(),entry.getKey());
        }
    }

    public void addPlayer(PlayerData p){
        players.put(p.getUuid(),p);
        playerNames.put(p.getName(), p.getUuid());
    }

    public PlayerData getPlayer(Player player, boolean create){
        PlayerData playerData = players.get(player.getUniqueId());
        if(playerData == null && create){
            playerData = new PlayerData(player.getUniqueId(),player.getName());
            addPlayer(playerData);
        }
        return playerData;
    }

    private void updatePlayerName(String oldName,String newName,UUID uuid){
        if(oldName != null){
            playerNames.remove(oldName);
        }
        playerNames.put(newName,uuid);
    }

    public PlayerData getPlayerByUUID(UUID uuid){
        return players.get(uuid);
    }

    private UUID getPlayerUUID(String name){
        return playerNames.get(name);
    }

    public PlayerData getPlayerByName(String name){
        UUID uuid = getPlayerUUID(name);
        return players.get(uuid);
    }

    public void removePlayerByUUID(UUID uuid){
        players.remove(uuid);
    }

    public void setJoinPlayerData(Player player){
        if(plugin.getMySQLConnection() != null) {
            MySQLConnection mySQLConnection = plugin.getMySQLConnection();
            UUID uuid = player.getUniqueId();
            mySQLConnection.getPlayer(uuid.toString(), playerData -> {
                removePlayerByUUID(uuid); //Remove data if already exists
                if (playerData != null) {
                    addPlayer(playerData);
                    //Update name if different
                    if (!playerData.getName().equals(player.getName())) {
                        updatePlayerName(playerData.getName(), player.getName(), player.getUniqueId());
                        playerData.setName(player.getName());
                        mySQLConnection.updatePlayerName(playerData);
                    }
                } else {
                    playerData = new PlayerData(uuid, player.getName());
                    addPlayer(playerData);
                    //Create if it doesn't exist
                    mySQLConnection.createPlayer(playerData);
                }
            });
        }else{
            PlayerData playerData = getPlayer(player,false);
            if(playerData != null){
                if(playerData.getName() == null || !playerData.getName().equals(player.getName())){
                    updatePlayerName(playerData.getName(),player.getName(),player.getUniqueId());
                    playerData.setName(player.getName());
                    playerData.setModified(true);
                }
            }
        }
    }

    public void addDiscovery(Player player,String categoryName,String discoveryName){
        PlayerData playerData = getPlayer(player,true);
        playerData.setModified(true);

        String date = OtherUtils.getDate(plugin.getConfigsManager().getMainConfigManager().getDiscoveriesDateFormat());
        playerData.addDiscovery(categoryName,discoveryName,date);
        if(plugin.getMySQLConnection() != null){
            plugin.getMySQLConnection().addDiscovery(player.getUniqueId().toString(),categoryName,discoveryName,date);
        }
    }

    public boolean hasDiscovery(Player player,String categoryName,String discoveryName){
        PlayerData playerData = getPlayer(player,false);
        if(playerData == null){
            return false;
        }
        return playerData.hasDiscovery(categoryName,discoveryName);
    }

    public PlayerDataDiscovery getDiscovery(Player player,String categoryName,String discoveryName){
        PlayerData playerData = getPlayer(player,false);
        if(playerData == null){
            return null;
        }
        return playerData.getDiscovery(categoryName,discoveryName);
    }

    public int getTotalDiscoveries(Player player,String categoryName){
        PlayerData playerData = getPlayer(player,false);
        if(playerData == null){
            return 0;
        }
        return playerData.getTotalDiscoveries(categoryName);
    }

    public int getTotalDiscoveries(Player player){
        PlayerData playerData = getPlayer(player,false);
        if(playerData == null){
            return 0;
        }
        return playerData.getTotalDiscoveries();
    }

    public int getTotalDiscoveriesPercentage(Player player,String categoryName,int max){
        int totalDiscoveries = getTotalDiscoveries(player,categoryName);
        return OtherUtils.getPercentage(totalDiscoveries,max);
    }

    public int getTotalDiscoveriesPercentage(Player player,int max){
        int totalDiscoveries = getTotalDiscoveries(player);
        return OtherUtils.getPercentage(totalDiscoveries,max);
    }

    public void completeCategory(Player player,String categoryName){
        PlayerData playerData = getPlayer(player,true);
        playerData.completeCategory(categoryName);

        if(plugin.getMySQLConnection() != null){
            plugin.getMySQLConnection().updateCompletedCategories(player.getUniqueId().toString(),categoryName);
        }
    }

    public boolean hasAllDiscoveries(Player player,String categoryName,int totalDiscoveries){
        PlayerData playerData = getPlayer(player,false);
        if(playerData == null){
            return false;
        }

        return playerData.getTotalDiscoveries(categoryName) >= totalDiscoveries;
    }

    public List<PlayerDataCategory> getCategories(Player player){
        PlayerData playerData = getPlayer(player,false);
        if(playerData == null){
            return new ArrayList<>();
        }

        return playerData.getCategories();
    }

    public boolean hasCompletedCategory(Player player,String categoryName){
        PlayerData playerData = getPlayer(player,false);
        if(playerData == null){
            return false;
        }
        return playerData.isCategoryCompleted(categoryName);
    }

    public boolean hasCompletedCategory(String playerName,String categoryName){
        PlayerData playerData = getPlayerByName(playerName);
        if(playerData == null){
            return false;
        }
        return playerData.isCategoryCompleted(categoryName);
    }

    public String resetDataPlayer(String playerName, String categoryName, String discoveryName, FileConfiguration messagesConfig){
        PlayerData playerData = getPlayerByName(playerName);
        if(!playerName.equals("*") && playerData == null){
            return messagesConfig.getString("playerDoesNotExists");
        }

        CategoryManager categoryManager = plugin.getCategoryManager();
        if(categoryName != null){
            Category category = categoryManager.getCategory(categoryName);
            if(category == null){
                return messagesConfig.getString("categoryDoesNotExist");
            }
            if(discoveryName != null && category.getDiscovery(discoveryName) == null){
                return messagesConfig.getString("discoveryDoesNotExist").replace("%category%",categoryName);
            }
        }

        MySQLConnection mySQLConnection = plugin.getMySQLConnection();

        if(categoryName == null){
            if(playerName.equals("*")){
                // Reset all data for all players
                for(Map.Entry<UUID, PlayerData> entry : players.entrySet()){
                    entry.getValue().resetData();
                }
                if(mySQLConnection != null) mySQLConnection.resetDataPlayer("*",null,null);
                return messagesConfig.getString("commandResetAllForAllPlayers");
            }else{
                // Reset all data for player
                playerData.resetData();
                if(mySQLConnection != null) mySQLConnection.resetDataPlayer(playerData.getUuid().toString(),null,null);
                return messagesConfig.getString("commandResetAllForPlayer").replace("%player%",playerName);
            }
        }

        if(discoveryName == null){
            if(playerName.equals("*")){
                // Reset category for all players
                for(Map.Entry<UUID, PlayerData> entry : players.entrySet()){
                    entry.getValue().resetCategory(categoryName);
                }
                if(mySQLConnection != null) mySQLConnection.resetDataPlayer("*",categoryName,null);
                return messagesConfig.getString("commandResetCategoryForAllPlayers").replace("%category%",categoryName);
            }else{
                // Reset category for player
                playerData.resetCategory(categoryName);
                if(mySQLConnection != null) mySQLConnection.resetDataPlayer(playerData.getUuid().toString(),categoryName,null);
                return messagesConfig.getString("commandResetCategoryForPlayer").replace("%player%",playerName)
                        .replace("%category%",categoryName);
            }
        }else{
            if(playerName.equals("*")){
                // Reset discovery for all players
                for(Map.Entry<UUID, PlayerData> entry : players.entrySet()){
                    entry.getValue().resetDiscovery(categoryName,discoveryName);
                }
                if(mySQLConnection != null) mySQLConnection.resetDataPlayer("*",categoryName,discoveryName);
                return messagesConfig.getString("commandResetDiscoveryForAllPlayers").replace("%category%",categoryName)
                        .replace("%discovery%",discoveryName);
            }else{
                // Reset discovery for player
                playerData.resetDiscovery(categoryName,discoveryName);
                if(mySQLConnection != null) mySQLConnection.resetDataPlayer(playerData.getUuid().toString(),categoryName,discoveryName);
                return messagesConfig.getString("commandResetDiscoveryForPlayer").replace("%player%",playerName)
                        .replace("%category%",categoryName).replace("%discovery%",discoveryName);
            }
        }
    }
}
