package cx.ajneb97.api;

import java.util.concurrent.ExecutionException;

import org.bukkit.entity.Player;

import cx.ajneb97.Codex;
import cx.ajneb97.data.JugadorCodex;
import cx.ajneb97.managers.CategoriasManager;
import cx.ajneb97.model.CategoriaCodex;

public class CodexAPI {

	private static Codex plugin;
	
	public CodexAPI(Codex plugin) {
		this.plugin = plugin;
	}
	
	public static boolean hasUnlockedDiscovery(Player player,String category,String discovery) {
		JugadorCodex j = plugin.getJugadorDataManager().getJugadorSync(player.getName());
		if(j != null) {
			return j.tieneEntrada(category, discovery);
		}
		return false;
	}
	
	public static int getTotalDiscoveries(Player player, String category) {
		JugadorCodex j = plugin.getJugadorDataManager().getJugadorSync(player.getName());
		if(j != null) {
			if(category == null) {
				return j.getEntradasDesbloqueadas();
			}else {
				return j.getEntradasDesbloqueadas(category);
			}
		}
		return 0;
	}
	
	public static String getTotalDiscoveriesPercentage(Player player, String category) {
		CategoriasManager categoriasManager = plugin.getCategoriasManager();
		if(category == null) {
			int entradasDesbloqueadas = CodexAPI.getTotalDiscoveries(player, null);
			int entradasMaximas = categoriasManager.getTotalDescubrimientos();
			int porcentaje = entradasDesbloqueadas*100/entradasMaximas;
			return porcentaje+"%";
		}else {
			CategoriaCodex categoriaCodex = categoriasManager.getCategoria(category);
			if(categoriaCodex != null) {
				int entradasDesbloqueadas = CodexAPI.getTotalDiscoveries(player, category);
				int entradasMaximas = categoriaCodex.getEntradas().size();
				int porcentaje = entradasDesbloqueadas*100/entradasMaximas;
				return porcentaje+"%";
			}
		}
		return "0%";
	}
}
