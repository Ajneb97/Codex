package cx.ajneb97.model;

public class CategoriaCodexOpcionesSonido {

	private String name;
	private int volumen;
	private float pitch;
	public CategoriaCodexOpcionesSonido(String name, int volumen, float pitch) {
		super();
		this.name = name;
		this.volumen = volumen;
		this.pitch = pitch;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getVolumen() {
		return volumen;
	}
	public void setVolumen(int volumen) {
		this.volumen = volumen;
	}
	public float getPitch() {
		return pitch;
	}
	public void setPitch(float pitch) {
		this.pitch = pitch;
	}
	
	
}
