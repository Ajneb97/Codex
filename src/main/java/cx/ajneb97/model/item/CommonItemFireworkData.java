package cx.ajneb97.model.item;

import java.util.ArrayList;
import java.util.List;

public class CommonItemFireworkData {

    private List<String> fireworkRocketEffects;
    private String fireworkStarEffect;
    private int fireworkPower;
    public CommonItemFireworkData(List<String> fireworkRocketEffects, String fireworkStarEffect, int fireworkPower) {
        super();
        this.fireworkRocketEffects = fireworkRocketEffects;
        this.fireworkStarEffect = fireworkStarEffect;
        this.fireworkPower = fireworkPower;
    }
    public List<String> getFireworkRocketEffects() {
        return fireworkRocketEffects;
    }
    public void setFireworkRocketEffects(List<String> fireworkRocketEffects) {
        this.fireworkRocketEffects = fireworkRocketEffects;
    }
    public String getFireworkStarEffect() {
        return fireworkStarEffect;
    }
    public void setFireworkStarEffect(String fireworkStarEffect) {
        this.fireworkStarEffect = fireworkStarEffect;
    }
    public int getFireworkPower() {
        return fireworkPower;
    }
    public void setFireworkPower(int fireworkPower) {
        this.fireworkPower = fireworkPower;
    }

    public CommonItemFireworkData clone(){
        return new CommonItemFireworkData(new ArrayList<>(fireworkRocketEffects),fireworkStarEffect,fireworkPower);
    }
}
