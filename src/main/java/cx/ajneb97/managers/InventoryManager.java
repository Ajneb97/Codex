package cx.ajneb97.managers;

import cx.ajneb97.Codex;
import cx.ajneb97.model.data.PlayerDataDiscovery;
import cx.ajneb97.model.internal.CommonVariable;
import cx.ajneb97.model.inventory.CommonInventory;
import cx.ajneb97.model.inventory.CommonInventoryItem;
import cx.ajneb97.model.inventory.InventoryPlayer;
import cx.ajneb97.model.item.CommonItem;
import cx.ajneb97.model.structure.Category;
import cx.ajneb97.model.structure.Discovery;
import cx.ajneb97.utils.ActionUtils;
import cx.ajneb97.utils.ItemUtils;
import cx.ajneb97.utils.OtherUtils;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class InventoryManager {

    private Codex plugin;
    private ArrayList<CommonInventory> inventories;
    private ArrayList<InventoryPlayer> players;

    public InventoryManager(Codex plugin){
        this.plugin = plugin;
        this.inventories = new ArrayList<>();
        this.players = new ArrayList<>();
    }

    public ArrayList<CommonInventory> getInventories() {
        return inventories;
    }

    public void setInventories(ArrayList<CommonInventory> inventories) {
        this.inventories = inventories;
    }

    public ArrayList<InventoryPlayer> getPlayers() {
        return players;
    }

    public CommonInventory getInventory(String name){
        for(CommonInventory inventory : inventories){
            if(inventory.getName().equals(name)){
                return inventory;
            }
        }
        return null;
    }

    public InventoryPlayer getInventoryPlayer(Player player){
        for(InventoryPlayer inventoryPlayer : players){
            if(inventoryPlayer.getPlayer().equals(player)){
                return inventoryPlayer;
            }
        }
        return null;
    }

    public void removeInventoryPlayer(Player player){
        players.removeIf(p -> p.getPlayer().equals(player));
    }

    public void openInventory(InventoryPlayer inventoryPlayer){
        CommonInventory inventory = getInventory(inventoryPlayer.getInventoryName());

        String title = inventory.getTitle();
        Inventory inv = Bukkit.createInventory(null,inventory.getSlots(), MessagesManager.getColoredMessage(title));

        List<CommonInventoryItem> items = inventory.getItems();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();
        Player player = inventoryPlayer.getPlayer();

        //Add items for all inventories
        for(CommonInventoryItem itemInventory : items){
            for(int slot : itemInventory.getSlots()){
                String type = itemInventory.getType();
                if(type != null){
                    ItemStack item = null;
                    if(type.startsWith("discovery: ")){
                        item = setDiscovery(type.replace("discovery: ",""),inventoryPlayer);
                    }else if(type.startsWith("category: ")){
                        item = setCategory(type.replace("category: ",""),player);
                    }
                    if(item != null){
                        item = setItemActions(itemInventory,item);
                        inv.setItem(slot,item);
                    }
                    continue;
                }

                ItemStack item = commonItemManager.createItemFromCommonItem(itemInventory.getItem(),player);

                String openInventory = itemInventory.getOpenInventory();
                if(openInventory != null) {
                    item = ItemUtils.setTagStringItem(plugin,item, "codex_open_inventory", openInventory);
                }
                item = setItemActions(itemInventory,item);

                inv.setItem(slot,item);
            }
        }

        inventoryPlayer.getPlayer().openInventory(inv);
        players.add(inventoryPlayer);
    }

    private ItemStack setItemActions(CommonInventoryItem commonItem, ItemStack item) {
        List<String> clickActions = commonItem.getClickActions();
        if(clickActions != null && !clickActions.isEmpty()) {
            String actionsList = "";
            for(int i=0;i<clickActions.size();i++) {
                if(i==clickActions.size()-1) {
                    actionsList=actionsList+clickActions.get(i);
                }else {
                    actionsList=actionsList+clickActions.get(i)+"|";
                }
            }
            item = ItemUtils.setTagStringItem(plugin, item, "codex_item_actions", actionsList);
        }
        return item;
    }

    private void clickOnDiscoveryItem(InventoryPlayer inventoryPlayer,String discoveryName,ClickType clickType){
        String categoryName = inventoryPlayer.getInventoryName().replace("category_","").split(";")[0];
        Category category = plugin.getCategoryManager().getCategory(categoryName);
        if(category == null){
            return;
        }

        Discovery discovery = category.getDiscovery(discoveryName);
        if(discovery == null){
            return;
        }

        Player player = inventoryPlayer.getPlayer();
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        boolean hasDiscovered = playerDataManager.hasDiscovery(player,category.getName(),discoveryName);

        if(!hasDiscovered){
            return;
        }

        List<String> actions = discovery.getClickActions();
        if(actions != null){
            for(String action : actions){
                ActionUtils.executeAction(player,action,plugin,new ArrayList<>());
            }
        }
    }

    private void clickOnCategoryItem(InventoryPlayer inventoryPlayer, String categoryName, ClickType clickType){
        Category category = plugin.getCategoryManager().getCategory(categoryName);
        if(category == null){
            return;
        }

        Player player = inventoryPlayer.getPlayer();

        inventoryPlayer.setInventoryName("category_"+categoryName);
        openInventory(inventoryPlayer);
    }

    private void clickOnOpenInventoryItem(InventoryPlayer inventoryPlayer,String openInventory){
        inventoryPlayer.setInventoryName(openInventory);
        openInventory(inventoryPlayer);
    }

    private void clickActionsItem(InventoryPlayer inventoryPlayer,String itemCommands){
        String[] sep = itemCommands.split("\\|");
        for(String action : sep){
            ActionUtils.executeAction(inventoryPlayer.getPlayer(),action,plugin,new ArrayList<>());
        }
    }

    public void clickInventory(InventoryPlayer inventoryPlayer, ItemStack item, ClickType clickType){
        String itemActions = ItemUtils.getTagStringItem(plugin,item,"codex_item_actions");
        if(itemActions != null){
            clickActionsItem(inventoryPlayer,itemActions);
        }

        String categoryName = ItemUtils.getTagStringItem(plugin,item,"codex_category");
        if(categoryName != null){
            clickOnCategoryItem(inventoryPlayer,categoryName,clickType);
            return;
        }

        String discoveryName = ItemUtils.getTagStringItem(plugin,item,"codex_discovery");
        if(discoveryName != null){
            clickOnDiscoveryItem(inventoryPlayer,discoveryName,clickType);
            return;
        }

        String openInventory = ItemUtils.getTagStringItem(plugin,item,"codex_open_inventory");
        if(openInventory != null){
            clickOnOpenInventoryItem(inventoryPlayer,openInventory);
            return;
        }
    }

    public ItemStack setCategory(String categoryName,Player player){
        Category category = plugin.getCategoryManager().getCategory(categoryName);
        if(category == null){
            return null;
        }

        CommonItem commonItem;
        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();
        ArrayList<CommonVariable> variables = new ArrayList<>();

        int max = category.getDiscoveries().size();
        int totalDiscoveries = playerDataManager.getTotalDiscoveries(player,categoryName);
        String unlockedVariable = OtherUtils.getCurrentUnlockedVariable(totalDiscoveries,max,plugin.getMessagesConfig());
        commonItem = category.getCategoryItem();
        variables.add(new CommonVariable("%progress_bar%", OtherUtils.getProgressBar(totalDiscoveries,max,plugin.getConfigsManager().getMainConfigManager())));
        variables.add(new CommonVariable("%percentage%", OtherUtils.getPercentage(totalDiscoveries,max)+"%"));
        variables.add(new CommonVariable("%unlocked%", MessagesManager.getColoredMessage(unlockedVariable)));

        ItemStack item = commonItemManager.createItemFromCommonItem(commonItem,player);
        commonItemManager.replaceVariables(item,variables,player);

        item = ItemUtils.setTagStringItem(plugin,item,"codex_category",categoryName);
        return item;
    }

    public ItemStack setDiscovery(String discoveryName,InventoryPlayer inventoryPlayer){
        // Category could be:
        // category_<name>
        // category_<name>;<something>
        String categoryName = inventoryPlayer.getInventoryName().replace("category_","").split(";")[0];
        Category category = plugin.getCategoryManager().getCategory(categoryName);
        if(category == null){
            return null;
        }

        Discovery discovery = category.getDiscovery(discoveryName);
        if(discovery == null){
            return null;
        }

        Player player = inventoryPlayer.getPlayer();

        PlayerDataManager playerDataManager = plugin.getPlayerDataManager();
        CommonItemManager commonItemManager = plugin.getCommonItemManager();

        ArrayList<CommonVariable> variables = new ArrayList<>();
        PlayerDataDiscovery playerDataDiscovery = playerDataManager.getDiscovery(player,category.getName(),discoveryName);

        ItemStack item;
        if(playerDataDiscovery != null){
            if(discovery.getCustomLevelUnlockedItem() != null){
                item = commonItemManager.createItemFromCommonItem(discovery.getCustomLevelUnlockedItem(),player);
            }else{
                item = commonItemManager.createItemFromCommonItem(category.getDefaultLevelUnlockedItem(),player);
            }
            variables.add(new CommonVariable("%name%",MessagesManager.getColoredMessage(discovery.getName())));
            variables.add(new CommonVariable("%date%",MessagesManager.getColoredMessage(playerDataDiscovery.getDiscoverDate())));
        }else{
            if(discovery.getCustomLevelBlockedItem() != null){
                item = commonItemManager.createItemFromCommonItem(discovery.getCustomLevelBlockedItem(),player);
            }else{
                item = commonItemManager.createItemFromCommonItem(category.getDefaultLevelBlockedItem(),player);
            }
        }

        // Replace %description% variable
        ItemMeta meta = item.getItemMeta();
        List<String> newLore = new ArrayList<>();
        List<String> lore = meta.getLore();
        for (String s : lore){
            if(s.contains("%description%")){
                List<String> description = discovery.getDescription();
                for(String line : description){
                    newLore.add(MessagesManager.getColoredMessage(line));
                }
            }else{
                newLore.add(MessagesManager.getColoredMessage(s));
            }
        }
        meta.setLore(newLore);
        item.setItemMeta(meta);

        commonItemManager.replaceVariables(item,variables,player);

        item = ItemUtils.setTagStringItem(plugin,item,"codex_discovery",discoveryName);
        return item;
    }
}
