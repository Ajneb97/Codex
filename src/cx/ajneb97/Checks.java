package cx.ajneb97;

import org.bukkit.configuration.file.FileConfiguration;

import cx.ajneb97.configs.ConfigsManager;
import cx.ajneb97.configs.InventoryConfigManager;
import cx.ajneb97.utilidades.UtilidadesOtros;

public class Checks {

	public static void checkearYArreglarInventarios(InventoryConfigManager inventoryConfig) {
		if(UtilidadesOtros.esLegacy()) {
			FileConfiguration config = inventoryConfig.getInventory();
			config.set("Inventories.main.0;8;36;44.item", "STAINED_GLASS_PANE:14");
			config.set("Inventories.main.1;7;9;17;27;35;37;43.item", "STAINED_GLASS_PANE:15");
			config.set("Inventories.history_discoveries.0;8;36;44.item", "STAINED_GLASS_PANE:11");
			config.set("Inventories.history_discoveries.1;7;9;17;27;35;37;43.item", "STAINED_GLASS_PANE:15");
			config.set("Inventories.regions_discoveries.0;8;36;44.item", "STAINED_GLASS_PANE:11");
			config.set("Inventories.regions_discoveries.1;7;9;17;27;35;37;43.item", "STAINED_GLASS_PANE:15");
			config.set("Inventories.monsters_discoveries.0;8;36;44.item", "STAINED_GLASS_PANE:11");
			config.set("Inventories.monsters_discoveries.1;7;9;17;27;35;37;43.item", "STAINED_GLASS_PANE:15");
			inventoryConfig.saveInventory();
		}
	}
	
	public static void checkearYArreglarConfig(Codex plugin,FileConfiguration config) {
		if(UtilidadesOtros.esLegacy()) {
			config.set("locked_discoveries_item", "INK_SACK:8");
			plugin.saveConfig();
		}
	}
}
