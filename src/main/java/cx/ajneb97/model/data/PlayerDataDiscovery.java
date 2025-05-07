package cx.ajneb97.model.data;

public class PlayerDataDiscovery {
    private String discoveryName;
    private String discoverDate;
    private long millisActionsExecuted;

    public PlayerDataDiscovery(String discoveryName, String discoverDate, long millisActionsExecuted) {
        this.discoveryName = discoveryName;
        this.discoverDate = discoverDate;
        this.millisActionsExecuted = millisActionsExecuted;
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

    public long getMillisActionsExecuted() {
        return millisActionsExecuted;
    }

    public void setMillisActionsExecuted(long millisActionsExecuted) {
        this.millisActionsExecuted = millisActionsExecuted;
    }
}
