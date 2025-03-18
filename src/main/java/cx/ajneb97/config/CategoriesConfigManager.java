package cx.ajneb97.config;

import cx.ajneb97.Codex;
import cx.ajneb97.managers.CategoryManager;
import cx.ajneb97.managers.CommonItemManager;
import cx.ajneb97.model.item.CommonItem;
import cx.ajneb97.model.structure.Category;
import cx.ajneb97.model.structure.DiscoveredOn;
import cx.ajneb97.model.structure.Discovery;
import cx.ajneb97.utils.OtherUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class CategoriesConfigManager extends DataFolderConfigManager {

    public CategoriesConfigManager(Codex plugin, String folderName) {
        super(plugin, folderName);
    }

    @Override
    public void loadConfigs() {
        ArrayList<Category> categories = new ArrayList<>();
        CategoryManager categoryManager = plugin.getCategoryManager();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();

        String path = plugin.getDataFolder() + File.separator + folderName;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                CommonConfig commonConfig = new CommonConfig(file.getName(), plugin, folderName, true);
                commonConfig.registerConfig();

                FileConfiguration config = commonConfig.getConfig();
                String name = commonConfig.getPath().replace(".yml","");

                CommonItem itemCategory = commonItemManager.getCommonItemFromConfig(config,"config.inventory_items.category");

                CommonItem itemDiscoveryUnlocked = commonItemManager.getCommonItemFromConfig(config,"config.inventory_items.discovery_unlocked");
                CommonItem itemDiscoveryBlocked = commonItemManager.getCommonItemFromConfig(config,"config.inventory_items.discovery_blocked");

                List<String> rewardsPerDiscovery = config.getStringList("config.rewards.per_discovery");
                List<String> rewardsAllDiscoveries = config.getStringList("config.rewards.all_discoveries");

                ArrayList<Discovery> discoveries = new ArrayList<>();
                if(config.contains("discoveries")){
                    for(String key : config.getConfigurationSection("discoveries").getKeys(false)){
                        String discoveryName = config.getString("discoveries."+key+".name");
                        List<String> discoveryDescription = config.getStringList("discoveries."+key+".description");

                        DiscoveredOn discoveredOn = null;
                        if(config.contains("discoveries."+key+".discovered_on")){
                            discoveredOn = new DiscoveredOn(
                                    DiscoveredOn.DiscoveredOnType.valueOf(config.getString("discoveries."+key+".discovered_on.type"))
                            );
                            String pathValue = "discoveries."+key+".discovered_on.value";
                            discoveredOn.setMobName(config.getString(pathValue+".mob_name"));
                            discoveredOn.setMobType(config.getString(pathValue+".mob_type"));
                            discoveredOn.setRegionName(config.getString(pathValue+".region_name"));
                        }

                        CommonItem customDiscoveryItemUnlocked = null;
                        CommonItem customDiscoveryItemBlocked = null;
                        if(config.contains("discoveries."+key+".inventory_items.discovery_unlocked")){
                            customDiscoveryItemUnlocked = commonItemManager.getCommonItemFromConfig(config,"discoveries."+key+".inventory_items.discovery_unlocked");
                        }
                        if(config.contains("discoveries."+key+".inventory_items.discovery_blocked")){
                            customDiscoveryItemBlocked = commonItemManager.getCommonItemFromConfig(config,"discoveries."+key+".inventory_items.discovery_blocked");
                        }

                        List<String> rewards = null;
                        if(config.contains("discoveries."+key+".rewards")){
                            rewards = config.getStringList("discoveries."+key+".rewards");
                        }

                        List<String> clickActions = null;
                        if(config.contains("discoveries."+key+".click_actions")){
                            clickActions = config.getStringList("discoveries."+key+".click_actions");
                        }

                        Discovery discovery = new Discovery(key,name);
                        discovery.setName(discoveryName);
                        discovery.setDescription(discoveryDescription);
                        discovery.setDiscoveredOn(discoveredOn);
                        discovery.setCustomRewards(rewards);
                        discovery.setClickActions(clickActions);
                        discovery.setCustomLevelBlockedItem(customDiscoveryItemBlocked);
                        discovery.setCustomLevelUnlockedItem(customDiscoveryItemUnlocked);
                        discoveries.add(discovery);
                    }
                }


                Category category = new Category(name);
                category.setCategoryItem(itemCategory);
                category.setDefaultLevelUnlockedItem(itemDiscoveryUnlocked);
                category.setDefaultLevelBlockedItem(itemDiscoveryBlocked);
                category.setDefaultRewardsAllDiscoveries(rewardsAllDiscoveries);
                category.setDefaultRewardsPerDiscovery(rewardsPerDiscovery);
                category.setDiscoveries(discoveries);

                categories.add(category);
            }
        }

        categoryManager.setCategories(categories);
    }

    @Override
    public void saveConfigs() {

    }

    @Override
    public void createFiles() {
        new CommonConfig("history.yml",plugin,folderName,false).registerConfig();
        new CommonConfig("monsters.yml",plugin,folderName,false).registerConfig();
        new CommonConfig("regions.yml",plugin,folderName,false).registerConfig();
    }
}
