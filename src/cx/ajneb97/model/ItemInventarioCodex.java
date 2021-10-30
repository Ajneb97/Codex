package cx.ajneb97.model;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemInventarioCodex {

	private List<Integer> slots;
	private String tipo; //Indica la categoria y el discovery
	private ItemStack item;
	private String nombre;
	private List<String> lore;
	private String abreInventario;
	private List<String> comandos;
	public ItemInventarioCodex(List<Integer> slots, String tipo, ItemStack item, String nombre, List<String> lore,
			String abreInventario,List<String> comandos) {
		super();
		this.slots = slots;
		this.tipo = tipo;
		this.item = item;
		this.nombre = nombre;
		this.lore = lore;
		this.abreInventario = abreInventario;
		this.comandos = comandos;
	}
	public List<Integer> getSlots() {
		return slots;
	}
	public void setSlots(List<Integer> slots) {
		this.slots = slots;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public ItemStack getItem() {
		return item;
	}
	public void setItem(ItemStack item) {
		this.item = item;
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
	public String getAbreInventario() {
		return abreInventario;
	}
	public void setAbreInventario(String abreInventario) {
		this.abreInventario = abreInventario;
	}
	public List<String> getComandos() {
		return comandos;
	}
	public void setComandos(List<String> comandos) {
		this.comandos = comandos;
	}
	
	
}
