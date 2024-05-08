package cx.ajneb97.libs.worldguard;

import java.util.List;

import org.bukkit.entity.Player;

public class JugadorWorldGuardAPI {

	private List<String> regionesActuales;
	private Player jugador;
	public JugadorWorldGuardAPI(List<String> regionesActuales, Player jugador) {
		super();
		this.regionesActuales = regionesActuales;
		this.jugador = jugador;
	}
	public List<String> getRegionesActuales() {
		return regionesActuales;
	}
	public void setRegionesActuales(List<String> regionesActuales) {
		this.regionesActuales = regionesActuales;
	}
	public Player getJugador() {
		return jugador;
	}
	public void setJugador(Player jugador) {
		this.jugador = jugador;
	}
	
	
}
