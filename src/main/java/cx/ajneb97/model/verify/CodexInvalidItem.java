package cx.ajneb97.model.verify;

import cx.ajneb97.utils.JSONMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CodexInvalidItem extends CodexBaseError {

    private String material;

    public CodexInvalidItem(String file, String errorText, boolean critical, String material) {
        super(file, errorText, critical);
        this.material = material;
    }

    @Override
    public void sendMessage(Player player) {
        List<String> hover = new ArrayList<>();

        JSONMessage jsonMessage = new JSONMessage(player,prefix+"&7Item material &c"+material+" &7on file &c"+file+" &7is not valid.");
        hover.add("&eTHIS IS AN ERROR!");
        hover.add("&fThe material &c"+material+" &fdefined on");
        hover.add("&ffile &c"+file+" &fdoesn't exists for your");
        hover.add("&fminecraft version.");

        jsonMessage.hover(hover).send();
    }
}
