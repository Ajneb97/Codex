package cx.ajneb97.model.item;

import java.util.ArrayList;
import java.util.List;

public class CommonItemPotionData {

    private boolean upgraded;
    private boolean extended;
    private String potionType;
    private int potionColor;
    private List<String> potionEffects;
    public CommonItemPotionData(boolean upgraded, boolean extended, String potionType, int potionColor,
                                List<String> potionEffects) {
        super();
        this.upgraded = upgraded;
        this.extended = extended;
        this.potionType = potionType;
        this.potionColor = potionColor;
        this.potionEffects = potionEffects;
    }
    public boolean isUpgraded() {
        return upgraded;
    }
    public void setUpgraded(boolean upgraded) {
        this.upgraded = upgraded;
    }
    public boolean isExtended() {
        return extended;
    }
    public void setExtended(boolean extended) {
        this.extended = extended;
    }
    public String getPotionType() {
        return potionType;
    }
    public void setPotionType(String potionType) {
        this.potionType = potionType;
    }
    public int getPotionColor() {
        return potionColor;
    }
    public void setPotionColor(int potionColor) {
        this.potionColor = potionColor;
    }
    public List<String> getPotionEffects() {
        return potionEffects;
    }
    public void setPotionEffects(List<String> potionEffects) {
        this.potionEffects = potionEffects;
    }

    public CommonItemPotionData clone(){
        return new CommonItemPotionData(upgraded,extended,potionType,potionColor,new ArrayList<>(potionEffects));
    }
}
