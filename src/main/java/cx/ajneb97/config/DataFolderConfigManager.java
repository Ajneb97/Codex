package cx.ajneb97.config;


import cx.ajneb97.Codex;

import java.io.File;

public abstract class DataFolderConfigManager {

    protected Codex plugin;
    protected String folderName;

    public DataFolderConfigManager(Codex plugin, String folderName) {
        this.plugin = plugin;
        this.folderName = folderName;
    }

    public void configure() {
        createFolder();
        loadConfigs();
    }

    public void createFolder(){
        File folder;
        try {
            folder = new File(plugin.getDataFolder() + File.separator + folderName);
            if(!folder.exists()){
                folder.mkdirs();
                createFiles();
            }
        } catch(SecurityException e) {
            folder = null;
        }
    }

    public CommonConfig getConfigFile(String pathName) {
        CommonConfig config = new CommonConfig(pathName, plugin, folderName, true);
        config.registerConfig();
        return config;
    }

    public abstract void createFiles();

    public abstract void loadConfigs();

    public abstract void saveConfigs();
}
