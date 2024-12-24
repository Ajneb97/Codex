package cx.ajneb97.utils;

import cx.ajneb97.Codex;
import cx.ajneb97.libs.titles.TitleAPI;
import cx.ajneb97.managers.MessagesManager;
import cx.ajneb97.model.internal.CommonVariable;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class ActionUtils {

    public static void executeAction(Player player, String actionText, Codex plugin, ArrayList<CommonVariable> variables){
        if(actionText.equals("close_inventory")){
            player.closeInventory();
            return;
        }

        int indexFirst = actionText.indexOf(" ");
        String actionType = actionText.substring(0,indexFirst).replace(":","");
        String actionLine = actionText.substring(indexFirst+1);
        actionLine = OtherUtils.replaceGlobalVariables(actionLine,player,plugin);
        for(CommonVariable variable : variables){
            actionLine = actionLine.replace(variable.getVariable(),variable.getValue());
        }

        switch(actionType){
            case "message":
                ActionUtils.message(player,actionLine);
                break;
            case "centered_message":
                ActionUtils.centeredMessage(player,actionLine);
                break;
            case "console_command":
                ActionUtils.consoleCommand(actionLine);
                break;
            case "player_command":
                ActionUtils.playerCommand(player,actionLine);
                break;
            case "playsound":
                ActionUtils.playSound(player,actionLine);
                break;
            case "title":
                ActionUtils.title(player,actionLine);
                break;
        }
    }

    public static void playSound(Player player, String soundLine){
        String[] sep = soundLine.split(";");
        Sound sound = null;
        float volume = 0;
        float pitch = 0;
        try {
            sound = getSoundByName(sep[0]);
            volume = Float.parseFloat(sep[1]);
            pitch = Float.parseFloat(sep[2]);
        }catch(Exception e ) {
            Bukkit.getConsoleSender().sendMessage(Codex.prefix+
                    MessagesManager.getColoredMessage("&7Sound Name: &c"+sep[0]+" &7is not valid. Change it in the config!"));
            return;
        }

        player.playSound(player.getLocation(), sound, volume, pitch);
    }

    private static Sound getSoundByName(String name){
        try {
            Class<?> soundTypeClass = Class.forName("org.bukkit.Sound");
            Method valueOf = soundTypeClass.getMethod("valueOf", String.class);
            return (Sound) valueOf.invoke(null,name);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static void consoleCommand(String actionLine){
        ConsoleCommandSender sender = Bukkit.getConsoleSender();
        Bukkit.dispatchCommand(sender, actionLine);
    }

    public static void playerCommand(Player player, String actionLine){
        player.performCommand(actionLine);
    }

    public static void message(Player player,String actionLine){
        player.sendMessage(MessagesManager.getColoredMessage(actionLine));
    }

    public static void centeredMessage(Player player,String actionLine){
        actionLine = MessagesManager.getColoredMessage(actionLine);
        player.sendMessage(MessagesManager.getCenteredMessage(actionLine));
    }

    public static void title(Player player,String actionLine){
        String[] sep = actionLine.split(";");
        int fadeIn = Integer.parseInt(sep[0]);
        int stay = Integer.parseInt(sep[1]);
        int fadeOut = Integer.parseInt(sep[2]);

        String title = sep[3];
        String subtitle = sep[4];
        if(title.equals("none")) {
            title = "";
        }
        if(subtitle.equals("none")) {
            subtitle = "";
        }
        TitleAPI.sendTitle(player,fadeIn,stay,fadeOut,title,subtitle);
    }
}
