package cx.ajneb97.utils;

import java.util.ArrayList;
import java.util.List;

import cx.ajneb97.Codex;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;


public class UtilidadesOtros {

	public static boolean esLegacy() {
		ServerVersion serverVersion = Codex.serverVersion;
		if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_13_R1)) {
			return false;
		}
		return true;
	}

	public static boolean esNew() {
		ServerVersion serverVersion = Codex.serverVersion;
		if(serverVersion.serverVersionGreaterEqualThan(serverVersion,ServerVersion.v1_16_R1)){
			return true;
		}
		return false;
	}
	
	public static String discoveriesToText(List<String> discoveries) {
		//Lista:
		//regions;2
		//npcs;3
		
		//String:
		//regions;2&npcs;3
		
		String texto = "";
		for(int i=0;i<discoveries.size();i++) {
			if(i == discoveries.size()-1) {
				texto = texto+discoveries.get(i);
			}else {
				texto = texto+discoveries.get(i)+"&";
			}
		}
		
		return texto;
	}
	
	public static List<String> textToDiscoveries(String discoveries){
		String[] sep = discoveries.split("&");
		List<String> discoveriesList = new ArrayList<String>();
		for(int i=0;i<sep.length;i++) {
			discoveriesList.add(sep[i]);
		}
		return discoveriesList;
	}
	
	public static String getUnlockedBar(FileConfiguration config,int currentDiscoveries,int maxDiscoveries) {
		
		String healthBar = "";
		if(maxDiscoveries <= 0) {
			return healthBar;
		}
		double amount = config.getInt("progress_bar_placeholder.amount");
		String symbolFilled = config.getString("progress_bar_placeholder.filled_symbol");
		String symbolEmpty = config.getString("progress_bar_placeholder.empty_symbol");
		
		double division = (double) maxDiscoveries/currentDiscoveries;
		int filledLines = (int) (amount/division);
		
		for(int i=1;i<=amount;i++) {
			if(i <= filledLines) {
				healthBar = healthBar+symbolFilled;
			}else {
				healthBar = healthBar+symbolEmpty;
			}
		}
		
		return healthBar;
	}
}
