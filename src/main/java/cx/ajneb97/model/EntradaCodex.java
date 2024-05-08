package cx.ajneb97.model;

import java.util.List;

public class EntradaCodex {

	private String id;
	private String nombre;
	private List<String> lore;
	private String discoveredOnRegion;
	private EntradaCodexOpcionesMobKill discoveredOnMobKill;
	private List<String> comandos;
	public EntradaCodex(String id, String nombre, List<String> lore, String discoveredOnRegion,
			EntradaCodexOpcionesMobKill discoveredOnMobKill,List<String> comandos) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.lore = lore;
		this.discoveredOnRegion = discoveredOnRegion;
		this.discoveredOnMobKill = discoveredOnMobKill;
		this.comandos = comandos;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<String> getLore() {
		return lore;
	}
	public void setLore(List<String> lore) {
		this.lore = lore;
	}
	public String getDiscoveredOnRegion() {
		return discoveredOnRegion;
	}
	public void setDiscoveredOnRegion(String discoveredOnRegion) {
		this.discoveredOnRegion = discoveredOnRegion;
	}
	public EntradaCodexOpcionesMobKill getDiscoveredOnMobKill() {
		return discoveredOnMobKill;
	}
	public void setDiscoveredOnMobKill(EntradaCodexOpcionesMobKill discoveredOnMobKill) {
		this.discoveredOnMobKill = discoveredOnMobKill;
	}
	public List<String> getComandos() {
		return comandos;
	}
	public void setComandos(List<String> comandos) {
		this.comandos = comandos;
	}
	
}
