package cx.ajneb97.managers;

import cx.ajneb97.Codex;
import cx.ajneb97.model.data.PlayerDataCategory;
import cx.ajneb97.model.data.PlayerDataDiscovery;
import cx.ajneb97.model.internal.CommonVariable;
import cx.ajneb97.model.structure.Category;
import cx.ajneb97.model.structure.DiscoveredOn;
import cx.ajneb97.model.structure.Discovery;
import cx.ajneb97.utils.ActionUtils;
import org.bukkit.entity.Player;

import java.util.*;

public class DiscoveryManager {
    private Codex plugin;

    public DiscoveryManager(Codex plugin){
        this.plugin = plugin;
    }

    private ArrayList<Discovery> getPossibleDiscoveries(DiscoveredOn.DiscoveredOnType type){
        ArrayList<Discovery> possibleDiscoveries = new ArrayList<>();
        ArrayList<Category> categories = plugin.getCategoryManager().getCategories();
        for(Category category : categories){
            for(Discovery discovery : category.getDiscoveries()){
                if(discovery.getDiscoveredOn() != null && discovery.getDiscoveredOn().getType().equals(type)){
                    possibleDiscoveries.add(discovery);
                }
            }
        }
        return possibleDiscoveries;
    }

    private ArrayList<Discovery> getNotFoundDiscoveries(ArrayList<PlayerDataCategory> foundDiscoveries){
        ArrayList<Discovery> notFoundDiscoveries = new ArrayList<>();
        ArrayList<Category> categories = plugin.getCategoryManager().getCategories();

        Set<String> foundSet = new HashSet<>();
        for (PlayerDataCategory c : foundDiscoveries) {
            for (PlayerDataDiscovery d : c.getDiscoveries()) {
                foundSet.add(c.getName() + ":" + d.getDiscoveryName());
            }
        }

        for (Category category : categories) {
            String categoryName = category.getName();
            for (Discovery discovery : category.getDiscoveries()) {
                String key = categoryName + ":" + discovery.getId();
                if (!foundSet.contains(key)) {
                    notFoundDiscoveries.add(discovery);
                }
            }
        }

        return notFoundDiscoveries;
    }

    public void onMobKill(Player player, String mobType, String mobName){
        ArrayList<Discovery> discoveries = getPossibleDiscoveries(DiscoveredOn.DiscoveredOnType.MOB_KILL);
        for(Discovery discovery : discoveries){
            DiscoveredOn discoveredOn = discovery.getDiscoveredOn();
            String discoveryMobName = discoveredOn.getMobName();
            String discoveryMobType = discoveredOn.getMobType();
            if(discoveryMobType != null && !discoveryMobType.equals(mobType)){
                continue;
            }
            if(discoveryMobName != null && !discoveryMobName.equals(mobName)){
                continue;
            }

            onDiscover(player,discovery.getCategoryName(),discovery.getId());

            return;
        }
    }

    public void onMythicMobKill(Player player, String mythicMobType){
        ArrayList<Discovery> discoveries = getPossibleDiscoveries(DiscoveredOn.DiscoveredOnType.MYTHIC_MOB_KILL);
        onPluginMobKill(player,mythicMobType,discoveries);
    }

    public void onEliteMobKill(Player player, String eliteMobType){
        ArrayList<Discovery> discoveries = getPossibleDiscoveries(DiscoveredOn.DiscoveredOnType.ELITE_MOB_KILL);
        onPluginMobKill(player,eliteMobType.replace(".yml",""),discoveries);
    }

    private void onPluginMobKill(Player player, String mobType, ArrayList<Discovery> discoveries){
        for(Discovery discovery : discoveries){
            DiscoveredOn discoveredOn = discovery.getDiscoveredOn();
            String discoveryMobType = discoveredOn.getMobType();
            if(discoveryMobType != null){
                String[] sep = discoveryMobType.split(";");
                if(Arrays.stream(sep).noneMatch(mobType::equals)){
                    continue;
                }
            }

            onDiscover(player,discovery.getCategoryName(),discovery.getId());

            return;
        }
    }

    public void onWorldGuardRegionEnter(Player player, String regionName){
        ArrayList<Discovery> discoveries = getPossibleDiscoveries(DiscoveredOn.DiscoveredOnType.WORLDGUARD_REGION);
        for(Discovery discovery : discoveries){
            DiscoveredOn discoveredOn = discovery.getDiscoveredOn();
            String discoveryRegionName = discoveredOn.getRegionName();
            if(discoveryRegionName != null && !discoveryRegionName.equals(regionName)){
                continue;
            }

            onDiscover(player,discovery.getCategoryName(),discovery.getId());

            return;
        }
    }

    public boolean onDiscover(Player player,String categoryName,String discoveryName){
        Category category = plugin.getCategoryManager().getCategory(categoryName);
        Discovery discovery = category.getDiscovery(discoveryName);

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        if(playerDataManager.hasDiscovery(player,categoryName,discoveryName)){
            return false;
        }
        playerDataManager.addDiscovery(player,categoryName,discoveryName);

        // Rewards
        ArrayList<CommonVariable> variables = new ArrayList<>();
        variables.add(new CommonVariable("%name%",discovery.getName()));
        boolean completed = playerDataManager.hasAllDiscoveries(player, categoryName, category.getDiscoveries().size()) &&
                !playerDataManager.hasCompletedCategory(player, category.getName());
        List<String> rewards = category.getDefaultRewardsPerDiscovery();
        if(discovery.getCustomRewards() != null){
            rewards = discovery.getCustomRewards();
        }

        if(rewards != null){
            for(String action : rewards){
                ActionUtils.executeAction(player,action,plugin,variables);
            }
        }

        if(completed){
            playerDataManager.completeCategory(player,categoryName);
            rewards = category.getDefaultRewardsAllDiscoveries();
            if(rewards != null){
                for(String action : rewards){
                    ActionUtils.executeAction(player,action,plugin,variables);
                }
            }
        }

        return true;
    }
}
