package cx.ajneb97.data;

import java.util.ArrayList;
import java.util.List;

public class JugadorCodex {

	private String uuid;
	private String name;
	private List<String> discoveries;
	public JugadorCodex(String uuid, String name) {
		this.discoveries = new ArrayList<String>();
		this.uuid = uuid;
		this.name = name;
	}
	public String getUuid() {
		return uuid;
	}
	public void setUuid(String uuid) {
		this.uuid = uuid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public List<String> getDiscoveries() {
		return discoveries;
	}
	public void setDiscoveries(List<String> discoveries) {
		this.discoveries = discoveries;
	}
	
	//Retorna false si ya existe
	public boolean agregarEntrada(String categoria,String discovery) {
		String linea = categoria.replace(".yml", "")+";"+discovery;
		if(discoveries.contains(linea)) {
			return false;
		}else {
			discoveries.add(linea);
			return true;
		}
	}
	
	public boolean tieneEntrada(String categoria,String discovery) {
		String linea = categoria.replace(".yml", "")+";"+discovery;
		if(discoveries.contains(linea)) {
			return true;
		}else {
			return false;
		}
	}
	
	public void resetearEntrada(String categoria,String discovery) {
		String linea = categoria.replace(".yml", "")+";"+discovery;
		discoveries.remove(linea);
	}
	
	public void resetearEntradas() {
		discoveries = new ArrayList<String>();
	}
	
	public int getEntradasDesbloqueadas(String categoria) {
		int cantidad = 0;
		for(String linea : discoveries) {
			String[] sep = linea.split(";");
			if(sep[0].equals(categoria)) {
				cantidad++;
			}
		}
		return cantidad;
	}
	
	public int getEntradasDesbloqueadas() {
		return discoveries.size();
	}
}
