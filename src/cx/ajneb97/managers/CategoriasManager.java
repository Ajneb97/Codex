package cx.ajneb97.managers;

import java.util.ArrayList;

import cx.ajneb97.Codex;
import cx.ajneb97.data.JugadorCodex;
import cx.ajneb97.model.CategoriaCodex;

public class CategoriasManager {

	private ArrayList<CategoriaCodex> categorias;
	
	public CategoriasManager(Codex plugin) {
		this.categorias = new ArrayList<CategoriaCodex>();
	}

	public ArrayList<CategoriaCodex> getCategorias() {
		return categorias;
	}

	public void setCategorias(ArrayList<CategoriaCodex> categorias) {
		this.categorias = categorias;
	}
	
	public CategoriaCodex getCategoria(String nombre) {
		for(CategoriaCodex c : categorias) {
			if(c.getPath().replace(".yml", "").equals(nombre)) {
				return c;
			}
		}
		return null;
	}
	
	public int getTotalDescubrimientos() {
		int cantidad = 0;
		for(CategoriaCodex c : categorias) {
			cantidad = cantidad+c.getEntradas().size();
		}
		return cantidad;
	}
}
