package cx.ajneb97.utils;

import org.bukkit.command.CommandSender;

public class PlayerUtils {

    public static boolean isCodexAdmin(CommandSender sender){
        return sender.hasPermission("codex.admin");
    }
}
