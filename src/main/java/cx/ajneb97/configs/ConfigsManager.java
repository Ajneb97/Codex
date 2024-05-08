package cx.ajneb97.configs;

import cx.ajneb97.Codex;

public class ConfigsManager {

	private PlayerConfigsManager playerConfigsManager;
	private CategoriesConfigsManager categoriesConfigsManager;
	private MensajesConfigManager mensajesConfigManager;
	private InventoryConfigManager inventoryConfigManager;
	
	public ConfigsManager(Codex plugin) {
		this.playerConfigsManager = new PlayerConfigsManager(plugin);
		this.playerConfigsManager.configurar();
		this.categoriesConfigsManager = new CategoriesConfigsManager(plugin);
		this.categoriesConfigsManager.configurar();
		this.mensajesConfigManager = new MensajesConfigManager(plugin);
		this.mensajesConfigManager.configurar();
		this.inventoryConfigManager = new InventoryConfigManager(plugin);
		this.inventoryConfigManager.configurar();	
	}

	public PlayerConfigsManager getPlayerConfigsManager() {
		return playerConfigsManager;
	}

	public CategoriesConfigsManager getCategoriesConfigsManager() {
		return categoriesConfigsManager;
	}

	public MensajesConfigManager getMensajesConfigManager() {
		return mensajesConfigManager;
	}

	public InventoryConfigManager getInventoryConfigManager() {
		return inventoryConfigManager;
	}
	
	
}
