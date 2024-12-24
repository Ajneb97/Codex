package cx.ajneb97.model.data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {
    private UUID uuid;
    private String name;
    private List<PlayerDataCategory> categories;
    private boolean modified;

    public PlayerData(UUID uuid, String name){
        this.uuid = uuid;
        this.name = name;
        this.modified = false;
        this.categories = new ArrayList<>();
    }

    public UUID getUuid() {
        return uuid;
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PlayerDataCategory> getCategories() {
        return categories;
    }

    public void setCategories(List<PlayerDataCategory> categories) {
        this.categories = categories;
    }

    public boolean isModified() {
        return modified;
    }

    public void setModified(boolean modified) {
        this.modified = modified;
    }

    public PlayerDataCategory getCategory(String categoryName){
        for(PlayerDataCategory playerDataCategory : categories){
            if(playerDataCategory.getName().equals(categoryName)){
                return playerDataCategory;
            }
        }
        return null;
    }

    public void addDiscovery(String category, String discovery, String date){
        PlayerDataCategory playerDataCategory = getCategory(category);
        if(playerDataCategory == null){
            playerDataCategory = new PlayerDataCategory(category,false,new ArrayList<>());
            categories.add(playerDataCategory);
        }

        playerDataCategory.addDiscovery(new PlayerDataDiscovery(
                discovery,date
        ));
    }

    public boolean hasDiscovery(String category,String discovery){
        PlayerDataCategory playerDataCategory = getCategory(category);
        if(playerDataCategory == null){
            return false;
        }

        return playerDataCategory.hasDiscovery(discovery);
    }

    public PlayerDataDiscovery getDiscovery(String category,String discovery){
        PlayerDataCategory playerDataCategory = getCategory(category);
        if(playerDataCategory == null){
            return null;
        }

        return playerDataCategory.getDiscovery(discovery);
    }

    public int getTotalDiscoveries(String category){
        PlayerDataCategory playerDataCategory = getCategory(category);
        if(playerDataCategory == null){
            return 0;
        }
        return playerDataCategory.getDiscoveries().size();
    }

    public int getTotalDiscoveries(){
        int total = 0;
        for(PlayerDataCategory c : categories){
            total = total+c.getDiscoveries().size();
        }
        return total;
    }

    public void completeCategory(String category){
        PlayerDataCategory playerDataCategory = getCategory(category);
        if(playerDataCategory == null){
            // Shouldn't happen
            playerDataCategory = new PlayerDataCategory(category,false,new ArrayList<>());
            categories.add(playerDataCategory);
        }
        playerDataCategory.setCompleted(true);
    }

    public boolean isCategoryCompleted(String category){
        PlayerDataCategory playerDataCategory = getCategory(category);
        if(playerDataCategory == null){
            return false;
        }

        return playerDataCategory.isCompleted();
    }

    public void resetData(){
        categories = new ArrayList<>();
        modified = true;
    }

    public void resetCategory(String category){
        categories.removeIf(c -> c.getName().equals(category));
        modified = true;
    }

    public void resetDiscovery(String category,String discovery){
        PlayerDataCategory playerDataCategory = getCategory(category);
        if(playerDataCategory != null){
            playerDataCategory.resetDiscovery(discovery);
            modified = true;
        }
    }
}
