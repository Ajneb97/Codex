package cx.ajneb97.model;

import org.bukkit.entity.Player;

public class JugadorInventario {

	private Player jugador;
	private String inventario;
	public JugadorInventario(Player jugador, String inventario) {
		super();
		this.jugador = jugador;
		this.inventario = inventario;
	}
	public Player getJugador() {
		return jugador;
	}
	public void setJugador(Player jugador) {
		this.jugador = jugador;
	}
	public String getInventario() {
		return inventario;
	}
	public void setInventario(String inventario) {
		this.inventario = inventario;
	}
	
}
