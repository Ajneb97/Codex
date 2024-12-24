package cx.ajneb97.config;

import cx.ajneb97.Codex;
import cx.ajneb97.managers.MessagesManager;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MessagesConfigManager {

    private Codex plugin;
    private CommonConfig configFile;

    public MessagesConfigManager(Codex plugin){
        this.plugin = plugin;
        this.configFile = new CommonConfig("messages.yml",plugin,null,false);
        configFile.registerConfig();
        checkUpdates();
    }

    public void configure() {
        FileConfiguration config = configFile.getConfig();

        MessagesManager messagesManager = new MessagesManager();
        messagesManager.setPrefix(config.getString("prefix"));

        plugin.setMessagesManager(messagesManager);
    }

    public CommonConfig getConfigFile() {
        return configFile;
    }

    public boolean reloadConfig(){
        if(!configFile.reloadConfig()){
            return false;
        }
        configure();
        return true;
    }

    public void checkUpdates(){
        Path pathConfig = Paths.get(configFile.getRoute());
        try{
            String text = new String(Files.readAllBytes(pathConfig));
            FileConfiguration config = configFile.getConfig();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
}
