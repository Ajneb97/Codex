package cx.ajneb97.managers;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import cx.ajneb97.Codex;
import cx.ajneb97.libs.titleapi.TitleAPI;
import cx.ajneb97.model.CategoriaCodex;
import cx.ajneb97.model.CategoriaCodexOpcionesSonido;
import cx.ajneb97.model.CategoriaCodexOpcionesTitle;
import cx.ajneb97.model.EntradaCodex;

public class CodexManager {

	private Codex plugin;
	public CodexManager(Codex plugin) {
		this.plugin = plugin;
	}
	
	public void desbloquearEntrada(Player jugador,CategoriaCodex categoria,EntradaCodex entrada) {
		CategoriaCodexOpcionesTitle opcionesTitle = categoria.getOpcionesTitle();
		List<String> mensajeDiscover = categoria.getMensajeDiscover();
		CategoriaCodexOpcionesSonido opcionesSonido = categoria.getOpcionesSonido();
		MensajesManager msgManager = plugin.getMensajesManager();
		if(opcionesTitle != null) {
			String titulo = opcionesTitle.getTitle().replace("%name%", entrada.getNombre());
			String subtitulo = opcionesTitle.getSubtitle().replace("%name%", entrada.getNombre());
			TitleAPI.sendTitle(jugador, opcionesTitle.getFadeIn(), opcionesTitle.getStay(),
					opcionesTitle.getFadeOut(), titulo, subtitulo);
		}
		if(opcionesSonido != null) {
			try {
				jugador.playSound(jugador.getLocation(), Sound.valueOf(opcionesSonido.getName()),
						opcionesSonido.getVolumen(), opcionesSonido.getPitch());
			}catch(Exception e) {
				Bukkit.getConsoleSender().sendMessage(Codex.nombrePlugin+ChatColor.translateAlternateColorCodes('&', 
						"&7Sound Name: &c"+opcionesSonido.getName()+" &7is not valid. Change it in the config!"));
			}
		}
		if(mensajeDiscover != null) {
			for(String linea : mensajeDiscover) {
				linea = linea.replace("%name%", entrada.getNombre());
				msgManager.enviarMensaje(jugador, linea, false);
			}
		}
		
		List<String> comandos = entrada.getComandos();
		ConsoleCommandSender console = Bukkit.getConsoleSender();
		for(String comando : comandos) {
			if(comando.startsWith("msg %player%")) {
				String mensaje = comando.replace("msg %player% ", "");
				msgManager.enviarMensaje(jugador, mensaje, false);
			}else {
				String comandoAEnviar = comando.replace("%player%", jugador.getName()); 
				Bukkit.dispatchCommand(console, comandoAEnviar);	
			}
		}
	}
}
