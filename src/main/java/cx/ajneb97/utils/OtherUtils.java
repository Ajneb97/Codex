package cx.ajneb97.utils;

import cx.ajneb97.Codex;
import cx.ajneb97.config.MainConfigManager;
import cx.ajneb97.managers.MessagesManager;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OtherUtils {
    public static boolean isNew() {
        ServerVersion serverVersion = Codex.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_16_R1)){
            return true;
        }
        return false;
    }

    public static boolean isLegacy() {
        ServerVersion serverVersion = Codex.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_13_R1)) {
            return false;
        }
        return true;
    }

    // 1.20+
    public static boolean isTrimNew() {
        ServerVersion serverVersion = Codex.serverVersion;
        if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_20_R1)) {
            return true;
        }else {
            return false;
        }
    }

    public static String replaceGlobalVariables(String text, Player player, Codex plugin) {
        if(player == null){
            return text;
        }
        text = text.replace("%player%",player.getName());
        if(plugin.getDependencyManager().isPlaceholderAPI()) {
            text = PlaceholderAPI.setPlaceholders(player, text);
        }

        return text;
    }

    public static int getPercentage(int value,int max){
        if(max == 0){
            return 0;
        }
        return value*100/max;
    }

    public static String getProgressBar(int value, int max, MainConfigManager mainConfigManager) {
        int length = mainConfigManager.getProgressBarPlaceholderAmount();
        String fillSymbol = mainConfigManager.getProgressBarPlaceholderFillSymbol();
        String emptySymbol = mainConfigManager.getProgressBarPlaceholderEmptySymbol();

        int completedLength = value == 0 || max == 0 ? 0 : Math.max(1, (int) ((double) value / max * length));
        int remainingLength = length - completedLength;

        StringBuilder completed = new StringBuilder();
        StringBuilder remaining = new StringBuilder();

        for (int i = 0; i < completedLength; i++) {
            completed.append(fillSymbol);
        }
        for (int i = 0; i < remainingLength; i++) {
            remaining.append(emptySymbol);
        }

        return MessagesManager.getColoredMessage(completed.toString() + remaining.toString());
    }

    public static String getCurrentUnlockedVariable(int value, int max, FileConfiguration messagesConfig){
        if(value >= max){
            return messagesConfig.getString("currentUnlockedDiscoveriesColorAll")
                    .replace("%current%",value+"").replace("%max%",max+"");
        }else if(value == 0){
            return messagesConfig.getString("currentUnlockedDiscoveriesColorNone")
                    .replace("%current%",value+"").replace("%max%",max+"");
        }else{
            return messagesConfig.getString("currentUnlockedDiscoveriesColorIncomplete")
                    .replace("%current%",value+"").replace("%max%",max+"");
        }
    }

    public static String getDate(String format){
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return currentDate.format(formatter);
    }
}
