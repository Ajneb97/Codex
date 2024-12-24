package cx.ajneb97.model.structure;

import cx.ajneb97.model.item.CommonItem;

import java.util.List;

public class Discovery {
    private String id;
    private String categoryName;
    private String name;
    private List<String> description;
    private DiscoveredOn discoveredOn;
    private CommonItem customLevelUnlockedItem;
    private CommonItem customLevelBlockedItem;
    private List<String> customRewards;
    private List<String> clickActions;

    public Discovery(String id, String categoryName) {
        this.id = id;
        this.categoryName = categoryName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getDescription() {
        return description;
    }

    public void setDescription(List<String> description) {
        this.description = description;
    }

    public DiscoveredOn getDiscoveredOn() {
        return discoveredOn;
    }

    public void setDiscoveredOn(DiscoveredOn discoveredOn) {
        this.discoveredOn = discoveredOn;
    }

    public CommonItem getCustomLevelUnlockedItem() {
        return customLevelUnlockedItem;
    }

    public void setCustomLevelUnlockedItem(CommonItem customLevelUnlockedItem) {
        this.customLevelUnlockedItem = customLevelUnlockedItem;
    }

    public CommonItem getCustomLevelBlockedItem() {
        return customLevelBlockedItem;
    }

    public void setCustomLevelBlockedItem(CommonItem customLevelBlockedItem) {
        this.customLevelBlockedItem = customLevelBlockedItem;
    }

    public List<String> getCustomRewards() {
        return customRewards;
    }

    public void setCustomRewards(List<String> customRewards) {
        this.customRewards = customRewards;
    }

    public List<String> getClickActions() {
        return clickActions;
    }

    public void setClickActions(List<String> clickActions) {
        this.clickActions = clickActions;
    }
}
