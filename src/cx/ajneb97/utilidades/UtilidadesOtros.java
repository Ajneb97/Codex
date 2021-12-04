package cx.ajneb97.utilidades;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;

public class UtilidadesOtros {

	public static boolean esLegacy() {
		if(Bukkit.getVersion().contains("1.13") || Bukkit.getVersion().contains("1.14") ||
				Bukkit.getVersion().contains("1.15") || Bukkit.getVersion().contains("1.16")
				 || Bukkit.getVersion().contains("1.17") || Bukkit.getVersion().contains("1.18")) {
			return false;
		}else {
			return true;
		}
	}
	
	public static boolean esNew() {
		if(Bukkit.getVersion().contains("1.16") || Bukkit.getVersion().contains("1.17")
				|| Bukkit.getVersion().contains("1.18")) {
			return true;
		}else {
			return true;
		}
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
}
