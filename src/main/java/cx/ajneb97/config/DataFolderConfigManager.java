package cx.ajneb97.config;


import cx.ajneb97.Codex;

import java.io.File;
import java.util.ArrayList;

public abstract class DataFolderConfigManager {

    protected ArrayList<CommonConfig> configFiles;
    protected Codex plugin;
    protected String folderName;

    public DataFolderConfigManager(Codex plugin, String folderName) {
        this.plugin = plugin;
        this.folderName = folderName;
        this.configFiles = new ArrayList<>();
    }

    public void configure() {
        createFolder();
        reloadConfigs();
    }

    public void reloadConfigs(){
        this.configFiles = new ArrayList<>();
        registerConfigFiles();
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

    public void saveConfigFiles() {
        for(int i=0;i<configFiles.size();i++) {
            configFiles.get(i).saveConfig();
        }
    }

    public void registerConfigFiles(){
        String path = plugin.getDataFolder() + File.separator + folderName;
        File folder = new File(path);
        File[] listOfFiles = folder.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                registerConfigFile(file.getName());
            }
        }
    }

    public ArrayList<CommonConfig> getConfigs(){
        return this.configFiles;
    }

    public boolean fileAlreadyRegistered(String pathName) {
        for(int i=0;i<configFiles.size();i++) {
            if(configFiles.get(i).getPath().equals(pathName)) {
                return true;
            }
        }
        return false;
    }

    public CommonConfig getConfigFile(String pathName) {
        for(int i=0;i<configFiles.size();i++) {
            if(configFiles.get(i).getPath().equals(pathName)) {
                return configFiles.get(i);
            }
        }
        return null;
    }

    public CommonConfig registerConfigFile(String pathName) {
        CommonConfig config = new CommonConfig(pathName, plugin, folderName, true);
        config.registerConfig();
        configFiles.add(config);
        return config;
    }

    public abstract void createFiles();

    public abstract void loadConfigs();

    public abstract void saveConfigs();
}
