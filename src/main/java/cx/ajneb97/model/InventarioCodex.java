package cx.ajneb97.model;

import java.util.List;

public class InventarioCodex {

	private String nombre;
	private int slots;
	private String title;
	private List<ItemInventarioCodex> items;
	public InventarioCodex(String nombre, int slots, String title, List<ItemInventarioCodex> items) {
		super();
		this.nombre = nombre;
		this.slots = slots;
		this.items = items;
		this.title = title;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public int getSlots() {
		return slots;
	}
	public void setSlots(int slots) {
		this.slots = slots;
	}
	public List<ItemInventarioCodex> getItems() {
		return items;
	}
	public void setItems(List<ItemInventarioCodex> items) {
		this.items = items;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
}
