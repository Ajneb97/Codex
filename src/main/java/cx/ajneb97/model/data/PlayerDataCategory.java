package cx.ajneb97.model.data;

import java.util.ArrayList;
import java.util.List;

public class PlayerDataCategory {
    private String name;
    private boolean completed;
    private ArrayList<PlayerDataDiscovery> discoveries;

    public PlayerDataCategory(String name, boolean completed, ArrayList<PlayerDataDiscovery> discoveries) {
        this.name = name;
        this.completed = completed;
        this.discoveries = discoveries;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<PlayerDataDiscovery> getDiscoveries() {
        return discoveries;
    }

    public void setDiscoveries(ArrayList<PlayerDataDiscovery> discoveries) {
        this.discoveries = discoveries;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public PlayerDataDiscovery getDiscovery(String discoveryName){
        for(PlayerDataDiscovery d : discoveries){
            if(d.getDiscoveryName().equals(discoveryName)){
                return d;
            }
        }
        return null;
    }

    public void addDiscovery(PlayerDataDiscovery discovery){
        discoveries.add(discovery);
    }

    public boolean hasDiscovery(String discoveryName){
        return getDiscovery(discoveryName) != null;
    }

    public void resetDiscovery(String discoveryName){
        discoveries.removeIf(d -> d.getDiscoveryName().equals(discoveryName));
    }

    public List<String> getDiscoveriesToStringList(){
        List<String> list = new ArrayList<>();
        for(PlayerDataDiscovery d : discoveries){
            list.add(d.getDiscoveryName()+";"+d.getDiscoverDate());
        }
        return list;
    }

}
