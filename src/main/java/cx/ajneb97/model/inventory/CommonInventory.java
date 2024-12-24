package cx.ajneb97.model.inventory;

import java.util.ArrayList;
import java.util.List;

public class CommonInventory {

    private String name;
    private int slots;
    private String title;
    private List<CommonInventoryItem> items;

    public CommonInventory(String name, int slots, String title, List<CommonInventoryItem> items) {
        this.name = name;
        this.slots = slots;
        this.title = title;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getSlots() {
        return slots;
    }

    public void setSlots(int slots) {
        this.slots = slots;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<CommonInventoryItem> getItems() {
        return items;
    }

    public void setItems(List<CommonInventoryItem> items) {
        this.items = items;
    }

    public int addDiscoveryItemOnFirstEmptySlot(String discovery){
        List<Integer> occupiedSlots = new ArrayList<>();
        for(CommonInventoryItem item : items){
            occupiedSlots.addAll(item.getSlots());
        }

        for(int i=0;i<slots;i++){
            if(!occupiedSlots.contains(i)){
                items.add(new CommonInventoryItem(i+"",null,null,null,"discovery: "+discovery));
                return i;
            }
        }
        return -1;
    }
}
