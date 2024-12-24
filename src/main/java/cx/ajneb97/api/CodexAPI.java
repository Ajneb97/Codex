package cx.ajneb97.api;

import cx.ajneb97.Codex;
import cx.ajneb97.model.data.PlayerData;
import org.bukkit.entity.Player;

public class CodexAPI {

    private static Codex plugin;
    public CodexAPI(Codex plugin){
        this.plugin = plugin;
    }

    public static Codex getPlugin() {
        return plugin;
    }

    public static boolean hasCompletedCategory(Player player, String categoryName){
        return plugin.getPlayerDataManager().hasCompletedCategory(player,categoryName);
    }

    public static boolean hasCompletedCategory(String playerName, String categoryName){
        return plugin.getPlayerDataManager().hasCompletedCategory(playerName,categoryName);
    }

    public static PlayerData getPlayerData(Player player){
        return plugin.getPlayerDataManager().getPlayer(player,false);
    }

    public static PlayerData getPlayerData(String playerName){
        return plugin.getPlayerDataManager().getPlayerByName(playerName);
    }

    public static int getTotalDiscoveries(Player player){
        return plugin.getPlayerDataManager().getTotalDiscoveries(player);
    }

    public static int getTotalDiscoveries(Player player,String categoryName){
        return plugin.getPlayerDataManager().getTotalDiscoveries(player,categoryName);
    }

    public static int getTotalDiscoveriesPercentage(Player player,String categoryName){
        return plugin.getPlayerDataManager().getTotalDiscoveriesPercentage(player,categoryName,plugin.getCategoryManager().getTotalDiscoveries(categoryName));
    }

    public static int getTotalDiscoveriesPercentage(Player player){
        return plugin.getPlayerDataManager().getTotalDiscoveriesPercentage(player,plugin.getCategoryManager().getTotalDiscoveries());
    }

    public static boolean hasDiscovery(Player player,String categoryName,String discoveryName){
        return plugin.getPlayerDataManager().hasDiscovery(player,categoryName,discoveryName);
    }
}
