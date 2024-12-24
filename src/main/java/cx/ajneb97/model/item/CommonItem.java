package cx.ajneb97.model.item;

import java.util.ArrayList;
import java.util.List;

public class CommonItem {

    private String id;
    private int amount;
    private String name;
    private List<String> lore;
    private short durability;
    private int customModelData;
    private List<String> enchants;
    private List<String> flags;
    private List<String> bookEnchants;
    private int color;
    private List<String> nbt;
    //Format:
    //<name>;<operation>;<amount>;<uuid>;<slot>
    private List<String> attributes;

    private CommonItemSkullData skullData;
    private CommonItemPotionData potionData;
    private CommonItemFireworkData fireworkData;
    private CommonItemBannerData bannerData;
    private CommonItemBookData bookData;
    private CommonItemTrimData trimData;
    private CommonItemCustomModelComponentData customModelComponentData;

    public CommonItem(String id) {
        this.id = id;
        this.amount = 1;
        this.durability = 0;
        this.customModelData = 0;
        this.color = 0;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getLore() {
        return lore;
    }

    public void setLore(List<String> lore) {
        this.lore = lore;
    }

    public short getDurability() {
        return durability;
    }

    public void setDurability(short durability) {
        this.durability = durability;
    }

    public int getCustomModelData() {
        return customModelData;
    }

    public void setCustomModelData(int customModelData) {
        this.customModelData = customModelData;
    }

    public List<String> getEnchants() {
        return enchants;
    }

    public void setEnchants(List<String> enchants) {
        this.enchants = enchants;
    }

    public List<String> getFlags() {
        return flags;
    }

    public void setFlags(List<String> flags) {
        this.flags = flags;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public CommonItemSkullData getSkullData() {
        return skullData;
    }

    public void setSkullData(CommonItemSkullData skullData) {
        this.skullData = skullData;
    }

    public CommonItemPotionData getPotionData() {
        return potionData;
    }

    public void setPotionData(CommonItemPotionData potionData) {
        this.potionData = potionData;
    }

    public List<String> getBookEnchants() {
        return bookEnchants;
    }

    public void setBookEnchants(List<String> bookEnchants) {
        this.bookEnchants = bookEnchants;
    }

    public List<String> getNbt() {
        return nbt;
    }

    public void setNbt(List<String> nbt) {
        this.nbt = nbt;
    }

    public List<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(List<String> attributes) {
        this.attributes = attributes;
    }

    public CommonItemFireworkData getFireworkData() {
        return fireworkData;
    }

    public void setFireworkData(CommonItemFireworkData fireworkData) {
        this.fireworkData = fireworkData;
    }

    public CommonItemBannerData getBannerData() {
        return bannerData;
    }

    public void setBannerData(CommonItemBannerData bannerData) {
        this.bannerData = bannerData;
    }

    public CommonItemBookData getBookData() {
        return bookData;
    }

    public void setBookData(CommonItemBookData bookData) {
        this.bookData = bookData;
    }

    public CommonItemTrimData getTrimData() {
        return trimData;
    }

    public void setTrimData(CommonItemTrimData trimData) {
        this.trimData = trimData;
    }

    public CommonItemCustomModelComponentData getCustomModelComponentData() {
        return customModelComponentData;
    }

    public void setCustomModelComponentData(CommonItemCustomModelComponentData customModelComponentData) {
        this.customModelComponentData = customModelComponentData;
    }

    public CommonItem clone(){
        CommonItem kitItem = new CommonItem(id);
        kitItem.setAmount(amount);
        kitItem.setName(name);
        kitItem.setLore(lore != null ? new ArrayList<>(lore) : null);
        kitItem.setDurability(durability);
        kitItem.setCustomModelData(customModelData);
        kitItem.setEnchants(enchants != null ? new ArrayList<>(enchants) : null);
        kitItem.setFlags(flags != null ? new ArrayList<>(flags) : null);
        kitItem.setBookEnchants(bookEnchants != null ? new ArrayList<>(bookEnchants) : null);
        kitItem.setColor(color);
        kitItem.setNbt(nbt != null ? new ArrayList<>(nbt) : null);
        kitItem.setAttributes(attributes != null ? new ArrayList<>(attributes) : null);

        kitItem.setSkullData(skullData != null ? skullData.clone() : null);
        kitItem.setPotionData(potionData != null ? potionData.clone() : null);
        kitItem.setFireworkData(fireworkData != null ? fireworkData.clone() : null);
        kitItem.setBannerData(bannerData != null ? bannerData.clone() : null);
        kitItem.setBookData(bookData != null ? bookData.clone() : null);
        kitItem.setTrimData(trimData != null ? trimData.clone() : null);
        kitItem.setCustomModelComponentData(customModelComponentData != null ? customModelComponentData.clone() : null);

        return kitItem;
    }
}