package cx.ajneb97.model.verify;

import cx.ajneb97.utils.JSONMessage;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class CodexActionError extends CodexBaseError {

    private String actionGroup;
    private String actionId;

    public CodexActionError(String file, String errorText, boolean critical, String actionGroup, String actionId) {
        super(file, errorText, critical);
        this.actionId = actionId;
        this.actionGroup = actionGroup;
    }

    @Override
    public void sendMessage(Player player) {
        List<String> hover = new ArrayList<String>();

        JSONMessage jsonMessage = new JSONMessage(player,prefix+"&7Action (&c"+actionGroup+"&7,&c"+actionId+"&7)" +
                " &7on file &c"+file+" &7is not valid.");
        hover.add("&eTHIS IS A WARNING!");
        hover.add("&fThe action defined is probably");
        hover.add("&fnot formatted correctly:");
        for(String m : getFixedErrorText()) {
            hover.add("&c"+m);
        }
        hover.add(" ");
        hover.add("&fRemember to use a valid action from this list:");
        hover.add("&ahttps://ajneb97.gitbook.io/codex/actions");

        jsonMessage.hover(hover).send();
    }
}
