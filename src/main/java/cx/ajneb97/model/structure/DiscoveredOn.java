package cx.ajneb97.model.structure;

public class DiscoveredOn {

    private DiscoveredOnType type;
    private String mobType;
    private String mobName;
    private String regionName;
    private String location;
    private String range;

    public DiscoveredOn(DiscoveredOnType type) {
        this.type = type;
    }

    public DiscoveredOnType getType() {
        return type;
    }

    public void setType(DiscoveredOnType type) {
        this.type = type;
    }

    public String getMobType() {
        return mobType;
    }

    public void setMobType(String mobType) {
        this.mobType = mobType;
    }

    public String getMobName() {
        return mobName;
    }

    public void setMobName(String mobName) {
        this.mobName = mobName;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getLocation() {
        return location;
    }
    public String getRange() {
        return range;
    }

    public void setLocation(String location) { this.location = location; }

    public void setRange(String range) { this.range = range; }

    public void setRegionName(String regionName) {
        this.regionName = regionName;
    }

    public enum DiscoveredOnType{
        MOB_KILL,
        MYTHIC_MOB_KILL,
        ELITE_MOB_KILL,
        WORLDGUARD_REGION,
        LOCATION
    }
}
