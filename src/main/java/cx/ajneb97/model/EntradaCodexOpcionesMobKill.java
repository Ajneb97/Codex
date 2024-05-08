package cx.ajneb97.model;

public class EntradaCodexOpcionesMobKill {

	private String type;
	private String nombre;
	private String mythicMobsId;
	public EntradaCodexOpcionesMobKill(String type, String nombre, String mythicMobsId) {
		super();
		this.type = type;
		this.nombre = nombre;
		this.mythicMobsId = mythicMobsId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public String getMythicMobsId() {
		return mythicMobsId;
	}
	public void setMythicMobsId(String mythicMobsId) {
		this.mythicMobsId = mythicMobsId;
	}
	
	
}
