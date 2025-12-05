package cx.ajneb97.utils;

import cx.ajneb97.Codex;
import cx.ajneb97.managers.MessagesManager;
import cx.ajneb97.model.internal.CommonVariable;
import cx.ajneb97.model.item.CommonItem;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextReplacementConfig;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class MiniMessageUtils {

    public static void messagePrefix(CommandSender sender, String message, boolean isPrefix, String prefix){
        if(isPrefix){
            sender.sendMessage(MiniMessage.miniMessage().deserialize(prefix+message));
        }else{
            sender.sendMessage(MiniMessage.miniMessage().deserialize(message));
        }
    }

    public static void title(Player player, String title, String subtitle){
        player.showTitle(Title.title(
                MiniMessage.miniMessage().deserialize(title),MiniMessage.miniMessage().deserialize(subtitle)
        ));
    }

    public static void message(Player player,String message){
        player.sendMessage(MiniMessage.miniMessage().deserialize(message));
    }

    public static void centeredMessage(Player player,String message){
        MiniMessage mm = MiniMessage.miniMessage();
        Component component = mm.deserialize(message);
        String centeredTextLegacy = MessagesManager.getCenteredMessage(LegacyComponentSerializer.legacySection().serialize(component)); // to legacy
        Component centeredTextMiniMessage = LegacyComponentSerializer.legacySection().deserialize(centeredTextLegacy); // to minimessage
        player.sendMessage(centeredTextMiniMessage);
    }

    public static Inventory createInventory(int slots, String title){
        return Bukkit.createInventory(null,slots, MiniMessage.miniMessage().deserialize(title));
    }

    public static void setCommonItemName(CommonItem commonItem,ItemMeta meta){
        commonItem.setName(LegacyComponentSerializer.legacyAmpersand().serialize(meta.displayName()));
    }

    public static void setCommonItemLore(List<String> lore, ItemMeta meta){
        for (Component line : meta.lore()) {
            lore.add(LegacyComponentSerializer.legacyAmpersand().serialize(line));
        }
    }

    public static void setItemName(ItemMeta meta,String name){
        meta.displayName(MiniMessage.miniMessage().deserialize(name).decoration(TextDecoration.ITALIC, false));
    }

    public static void setItemLore(ItemMeta meta, List<String> lore, Player player, Codex plugin){
        List<Component> loreComponent = new ArrayList<>();
        for(int i=0;i<lore.size();i++) {
            String line = OtherUtils.replaceGlobalVariables(lore.get(i),player,plugin);
            loreComponent.add(MiniMessage.miniMessage().deserialize(line).decoration(TextDecoration.ITALIC, false));
        }
        meta.lore(loreComponent);
    }

    public static void replaceVariablesItemName(ItemMeta meta,ArrayList<CommonVariable> variables, Player player, Codex plugin){
        Component name = meta.displayName();
        Component newName = name;
        for(CommonVariable variable : variables){
            String finalValue = OtherUtils.replaceGlobalVariables(variable.getValue(),player,plugin);
            newName = newName.replaceText(TextReplacementConfig.builder()
                    .matchLiteral(variable.getVariable())
                    .replacement(MiniMessage.miniMessage().deserialize(finalValue))
                    .build());
        }
        meta.displayName(newName);
    }

    public static void replaceVariablesItemLore(ItemMeta meta,ArrayList<CommonVariable> variables, Player player, Codex plugin){
        List<Component> lore = meta.lore();
        List<Component> newLore = new ArrayList<>();
        for(Component c : lore){
            Component newComponent = c;
            for(CommonVariable variable : variables){
                String finalValue = OtherUtils.replaceGlobalVariables(variable.getValue(),player,plugin);
                newComponent = newComponent.replaceText(TextReplacementConfig.builder()
                        .matchLiteral(variable.getVariable())
                        .replacement(MiniMessage.miniMessage().deserialize(finalValue))
                        .build());
            }
            newLore.add(newComponent);
        }
        meta.lore(newLore);
    }

    public static void setLoreDescription(ItemMeta meta, List<String> description){
        List<Component> newLore = new ArrayList<>();
        List<Component> lore = meta.lore();
        PlainTextComponentSerializer plainSerializer = PlainTextComponentSerializer.plainText();
        for(Component c : lore){
            String plainText = plainSerializer.serialize(c);
            if(plainText.contains("%description%")){
                for(String line : description){
                    newLore.add(MiniMessage.miniMessage().deserialize(line).decoration(TextDecoration.ITALIC, false));
                }
            }else{
                newLore.add(c);
            }
        }
        meta.lore(newLore);
    }
}
