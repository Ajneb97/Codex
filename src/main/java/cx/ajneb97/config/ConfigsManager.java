package cx.ajneb97.config;


import cx.ajneb97.Codex;

public class ConfigsManager {

	private Codex plugin;
	private MainConfigManager mainConfigManager;
	private CategoriesConfigManager categoriesConfigManager;
	private MessagesConfigManager messagesConfigManager;
	private PlayersConfigManager playersConfigManager;
	private InventoryConfigManager inventoryConfigManager;
	
	public ConfigsManager(Codex plugin) {
		this.plugin = plugin;
		this.mainConfigManager = new MainConfigManager(plugin);
		this.categoriesConfigManager = new CategoriesConfigManager(plugin,"categories");
		this.playersConfigManager = new PlayersConfigManager(plugin,"players");
		this.messagesConfigManager = new MessagesConfigManager(plugin);
		this.inventoryConfigManager = new InventoryConfigManager(plugin);
	}
	
	public void configure() {
		mainConfigManager.configure();
		categoriesConfigManager.configure();
		inventoryConfigManager.configure();
		playersConfigManager.configure();
		messagesConfigManager.configure();
	}

	public MainConfigManager getMainConfigManager() {
		return mainConfigManager;
	}

	public MessagesConfigManager getMessagesConfigManager() {
		return messagesConfigManager;
	}

	public PlayersConfigManager getPlayersConfigManager() {
		return playersConfigManager;
	}

	public boolean reload(){
		if(!messagesConfigManager.reloadConfig()){
			return false;
		}
		if(!mainConfigManager.reloadConfig()){
			return false;
		}
		if(!inventoryConfigManager.reloadConfig()){
			return false;
		}
		categoriesConfigManager.loadConfigs();
		plugin.reloadPlayerDataSaveTask();

		plugin.getVerifyManager().verify();

		return true;
	}
}
