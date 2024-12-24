package cx.ajneb97.model.verify;

import cx.ajneb97.utils.JSONMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CodexInventoryDefaultNotExistsError extends CodexBaseError {

    private String inventoryName;

    public CodexInventoryDefaultNotExistsError(String file, String errorText, boolean critical, String inventoryName) {
        super(file, errorText, critical);
        this.inventoryName = inventoryName;
    }

    @Override
    public void sendMessage(Player player) {
        List<String> hover = new ArrayList<String>();

        JSONMessage jsonMessage = new JSONMessage(player,prefix+"&7Inventory &c"+inventoryName+" &7not found");
        hover.add("&eTHIS IS AN ERROR!");
        hover.add("&fThe &c"+inventoryName+" &fis a needed inventory for");
        hover.add("&fthe plugin to work. You MUST NOT delete it");
        hover.add("&ffrom the inventory.yml file. You can find the default");
        hover.add("&fconfig for this inventory on the wiki.");

        jsonMessage.hover(hover).send();
    }
}
