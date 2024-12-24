package cx.ajneb97.model.data;

public class PlayerDataDiscovery {
    private String discoveryName;
    private String discoverDate;

    public PlayerDataDiscovery(String discoveryName, String discoverDate) {
        this.discoveryName = discoveryName;
        this.discoverDate = discoverDate;
    }

    public String getDiscoveryName() {
        return discoveryName;
    }

    public void setDiscoveryName(String discoveryName) {
        this.discoveryName = discoveryName;
    }

    public String getDiscoverDate() {
        return discoverDate;
    }

    public void setDiscoverDate(String discoverDate) {
        this.discoverDate = discoverDate;
    }
}
