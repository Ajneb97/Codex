package cx.ajneb97.model.structure;

import cx.ajneb97.model.item.CommonItem;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private String name;
    private ArrayList<Discovery> discoveries;
    private CommonItem defaultLevelUnlockedItem;
    private CommonItem defaultLevelBlockedItem;
    private CommonItem categoryItem;
    private List<String> defaultRewardsPerDiscovery;
    private List<String> defaultRewardsAllDiscoveries;

    public Category(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<Discovery> getDiscoveries() {
        return discoveries;
    }

    public void setDiscoveries(ArrayList<Discovery> discoveries) {
        this.discoveries = discoveries;
    }

    public CommonItem getDefaultLevelUnlockedItem() {
        return defaultLevelUnlockedItem;
    }

    public void setDefaultLevelUnlockedItem(CommonItem defaultLevelUnlockedItem) {
        this.defaultLevelUnlockedItem = defaultLevelUnlockedItem;
    }

    public CommonItem getDefaultLevelBlockedItem() {
        return defaultLevelBlockedItem;
    }

    public void setDefaultLevelBlockedItem(CommonItem defaultLevelBlockedItem) {
        this.defaultLevelBlockedItem = defaultLevelBlockedItem;
    }

    public CommonItem getCategoryItem() {
        return categoryItem;
    }

    public void setCategoryItem(CommonItem categoryItem) {
        this.categoryItem = categoryItem;
    }

    public List<String> getDefaultRewardsPerDiscovery() {
        return defaultRewardsPerDiscovery;
    }

    public void setDefaultRewardsPerDiscovery(List<String> defaultRewardsPerDiscovery) {
        this.defaultRewardsPerDiscovery = defaultRewardsPerDiscovery;
    }

    public List<String> getDefaultRewardsAllDiscoveries() {
        return defaultRewardsAllDiscoveries;
    }

    public void setDefaultRewardsAllDiscoveries(List<String> defaultRewardsAllDiscoveries) {
        this.defaultRewardsAllDiscoveries = defaultRewardsAllDiscoveries;
    }

    public Discovery getDiscovery(String id){
        for(Discovery d : discoveries){
            if(d.getId().equals(id)){
                return d;
            }
        }
        return null;
    }

    public void addDiscovery(Discovery discovery){
        discoveries.add(discovery);
    }

    public void removeDiscovery(String id){
        discoveries.removeIf(l -> l.getId().equals(id));
    }

}
