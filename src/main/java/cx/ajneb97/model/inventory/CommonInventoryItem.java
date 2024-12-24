package cx.ajneb97.model.inventory;

import cx.ajneb97.model.item.CommonItem;
import java.util.ArrayList;
import java.util.List;

public class CommonInventoryItem {
    private List<Integer> slots;
    private String slotsString;
    private CommonItem item;
    private String openInventory;
    private List<String> clickActions;
    private String type;

    public CommonInventoryItem(String slotsString, CommonItem item, String openInventory, List<String> clickActions, String type) {
        slots = new ArrayList<>();
        this.slotsString = slotsString;
        String[] slotsSep = slotsString.split(";");
        for(int i=0;i<slotsSep.length;i++) {
            if(slotsSep[i].contains("-")) {
                String[] newSep = slotsSep[i].split("-");
                int sMin = Integer.parseInt(newSep[0]);
                int sMax = Integer.parseInt(newSep[1]);
                for(int c=sMin;c<=sMax;c++) {
                    slots.add(c);
                }
            }else {
                slots.add(Integer.valueOf(slotsSep[i]));
            }
        }

        this.item = item;
        this.openInventory = openInventory;
        this.clickActions = clickActions;
        this.type = type;
    }

    public List<Integer> getSlots() {
        return slots;
    }

    public void setSlots(List<Integer> slots) {
        this.slots = slots;
    }

    public CommonItem getItem() {
        return item;
    }

    public void setItem(CommonItem item) {
        this.item = item;
    }

    public String getOpenInventory() {
        return openInventory;
    }

    public void setOpenInventory(String openInventory) {
        this.openInventory = openInventory;
    }

    public List<String> getClickActions() {
        return clickActions;
    }

    public void setClickActions(List<String> clickActions) {
        this.clickActions = clickActions;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSlotsString() {
        return slotsString;
    }

    public void setSlotsString(String slotsString) {
        this.slotsString = slotsString;
    }
}
