package cx.ajneb97.managers;

import cx.ajneb97.Codex;
import cx.ajneb97.model.inventory.CommonInventory;
import cx.ajneb97.model.inventory.CommonInventoryItem;
import cx.ajneb97.model.item.CommonItem;
import cx.ajneb97.model.structure.Category;
import cx.ajneb97.model.structure.Discovery;
import cx.ajneb97.model.verify.*;
import cx.ajneb97.utils.ItemUtils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class VerifyManager {
    private Codex plugin;
    private ArrayList<CodexBaseError> errors;
    private boolean criticalErrors;
    public VerifyManager(Codex plugin) {
        this.plugin = plugin;
        this.errors = new ArrayList<>();
        this.criticalErrors = false;
    }

    public void sendVerification(Player player) {
        player.sendMessage(MessagesManager.getColoredMessage("&f&l- - - - - - - - &6&lCODEX VERIFY &f&l- - - - - - - -"));
        player.sendMessage(MessagesManager.getColoredMessage(""));
        if(errors.isEmpty()) {
            player.sendMessage(MessagesManager.getColoredMessage("&aThere are no errors in the plugin ;)"));
        }else {
            player.sendMessage(MessagesManager.getColoredMessage("&e&oHover on the errors to see more information."));
            for(CodexBaseError error : errors) {
                error.sendMessage(player);
            }
        }
        player.sendMessage(MessagesManager.getColoredMessage(""));
        player.sendMessage(MessagesManager.getColoredMessage("&f&l- - - - - - - - &6&lCODEX VERIFY &f&l- - - - - - - -"));
    }

    public void verify() {
        this.errors = new ArrayList<>();
        this.criticalErrors = false;

        //CHECK CATEGORIES/DISCOVERIES
        ArrayList<Category> categories = plugin.getCategoryManager().getCategories();
        for(Category category : categories) {
            verifyCategory(category);
        }

        //CHECK INVENTORIES
        InventoryManager inventoryManager = plugin.getInventoryManager();
        ArrayList<CommonInventory> inventories = inventoryManager.getInventories();
        for(CommonInventory inventory : inventories){
            verifyInventory(inventory);
        }
        if(inventoryManager.getInventory("main_inventory") == null){
            errors.add(new CodexInventoryDefaultNotExistsError("inventory.yml",null,true,"main_inventory"));
            criticalErrors = true;
        }
    }

    public void verifyCategory(Category category) {
        //Actions
        verifyActions(category.getDefaultRewardsPerDiscovery(),"category rewards per_discovery",category.getName()+".yml");
        verifyActions(category.getDefaultRewardsAllDiscoveries(),"category rewards all_discoveries",category.getName()+".yml");
        for(Discovery d : category.getDiscoveries()){
            verifyActions(d.getCustomRewards(),d.getId()+" rewards",category.getName()+".yml");
            verifyActions(d.getClickActions(),d.getId()+" click_actions",category.getName()+".yml");
        }

        //Items
        ArrayList<CommonItem> allItems = new ArrayList<>();
        allItems.add(category.getCategoryItem());
        allItems.add(category.getDefaultLevelBlockedItem());
        allItems.add(category.getDefaultLevelUnlockedItem());
        for(Discovery d : category.getDiscoveries()){
            allItems.add(d.getCustomLevelBlockedItem());
            allItems.add(d.getCustomLevelUnlockedItem());
        }
        for(CommonItem item : allItems){
            if(item != null){
                if(!verifyItem(item.getId())){
                    errors.add(new CodexInvalidItem(category.getName()+".yml",null,true,item.getId()));
                    criticalErrors = true;
                }
            }
        }
    }

    public void verifyActions(List<String> actions,String actionGroup,String fileName){
        if(actions == null){
            return;
        }
        for(int i=0;i<actions.size();i++){
            if(actions.get(i).equals("close_inventory")){
                continue;
            }
            String[] actionText = actions.get(i).split(" ");
            String actionName = actionText[0];
            if(actionName.equals("console_command:") || actionName.equals("player_command:")
                    || actionName.equals("playsound:") || actionName.equals("message:")
                    || actionName.equals("centered_message:") || actionName.equals("title:")){
                continue;
            }
            errors.add(new CodexActionError(fileName,actions.get(i),false,actionGroup,(i+1)+""));
        }
    }

    public void verifyInventory(CommonInventory inventory){
        CategoryManager categoryManager = plugin.getCategoryManager();
        List<CommonInventoryItem> items = inventory.getItems();
        InventoryManager inventoryManager = plugin.getInventoryManager();
        int maxSlots = inventory.getSlots();
        for(CommonInventoryItem item : items){
           String type = item.getType();
           if(type != null){
               if(type.startsWith("category: ")){
                   String categoryName = type.replace("category: ","");
                   if(categoryManager.getCategory(categoryName) == null){
                       errors.add(new CodexInventoryInvalidCategoryError("inventory.yml",null,true,categoryName,
                               inventory.getName(),item.getSlotsString()));
                       criticalErrors = true;
                   }
               }else if(type.startsWith("discovery: ")){
                   String discoveryName = type.replace("discovery: ","");
                   String categoryName = inventory.getName().replace("category_","").split(";")[0];
                   Category category = categoryManager.getCategory(categoryName);
                   if(category == null || category.getDiscovery(discoveryName) == null){
                       errors.add(new CodexInventoryInvalidDiscoveryError("inventory.yml",null,true,discoveryName,categoryName,
                               inventory.getName(),item.getSlotsString()));
                       criticalErrors = true;
                   }
               }
           }

           String openInventory = item.getOpenInventory();
           if(openInventory != null){
               if(inventoryManager.getInventory(openInventory) == null){
                   errors.add(new CodexInventoryNotExistsError("inventory.yml",null,true,inventory.getName(),
                           item.getSlotsString(),openInventory));
                   criticalErrors = true;
               }
           }

           //Items
           CommonItem commonItem = item.getItem();
           if(commonItem != null){
               if(!verifyItem(commonItem.getId())){
                   errors.add(new CodexInvalidItem("inventory.yml",null,true,commonItem.getId()));
                   criticalErrors = true;
               }
           }

           //Valid slots
           for(int slot : item.getSlots()){
               if(slot >= maxSlots){
                   errors.add(new CodexInventoryInvalidSlotError("inventory.yml",null,true,slot,
                           inventory.getName(),maxSlots));
                   criticalErrors = true;
               }
           }
        }
    }

    public boolean isCriticalErrors() {
        return criticalErrors;
    }

    public boolean verifyItem(String material){
        try{
            ItemUtils.createItemFromID(material);
            return true;
        }catch(Exception e){
            return false;
        }
    }
}
