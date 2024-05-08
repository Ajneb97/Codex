package cx.ajneb97.model;

import java.util.ArrayList;
import java.util.List;

public class CategoriaCodex {

	private String path;
	private String nombre;
	private List<EntradaCodex> entradas;
	private CategoriaCodexOpcionesTitle opcionesTitle;
	private List<String> mensajeDiscover;
	private CategoriaCodexOpcionesSonido opcionesSonido;
	
	public CategoriaCodex(String path,String nombre, List<EntradaCodex> entradas,CategoriaCodexOpcionesTitle opcionesTitle
			,List<String> mensajeDiscover,CategoriaCodexOpcionesSonido opcionesSonido) {
		this.path = path;
		this.nombre = nombre;
		this.entradas = entradas;
		this.opcionesTitle = opcionesTitle;
		this.mensajeDiscover = mensajeDiscover;
		this.opcionesSonido = opcionesSonido;
	}
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public List<EntradaCodex> getEntradas() {
		return entradas;
	}
	public void setEntradas(ArrayList<EntradaCodex> entradas) {
		this.entradas = entradas;
	}
	
	public CategoriaCodexOpcionesSonido getOpcionesSonido() {
		return opcionesSonido;
	}
	public void setOpcionesSonido(CategoriaCodexOpcionesSonido opcionesSonido) {
		this.opcionesSonido = opcionesSonido;
	}
	public List<String> getMensajeDiscover() {
		return mensajeDiscover;
	}
	public void setMensajeDiscover(List<String> mensajeDiscover) {
		this.mensajeDiscover = mensajeDiscover;
	}
	public CategoriaCodexOpcionesTitle getOpcionesTitle() {
		return opcionesTitle;
	}
	public void setOpcionesTitle(CategoriaCodexOpcionesTitle opcionesTitle) {
		this.opcionesTitle = opcionesTitle;
	}
	public EntradaCodex getEntrada(String nombre) {
		for(EntradaCodex entrada : entradas) {
			if(entrada.getId().equals(nombre)) {
				return entrada;
			}
		}
		return null;
	}
	
}
