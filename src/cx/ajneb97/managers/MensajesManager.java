package cx.ajneb97.managers;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import cx.ajneb97.libs.centeredmessages.DefaultFontInfo;
import cx.ajneb97.utilidades.UtilidadesOtros;
import net.md_5.bungee.api.ChatColor;

public class MensajesManager {
	
	private String prefix;
	public MensajesManager(String prefix) {
		this.prefix = prefix;
	}

	public void enviarMensaje(CommandSender jugador,String mensaje,boolean prefix) {
		if(!mensaje.isEmpty()) {
			if(prefix) {
				jugador.sendMessage(getMensajeColor(this.prefix+mensaje));
			}else {
				jugador.sendMessage(getMensajeColor(mensaje));
			}
		}
	}
	
	public static String getMensajeColor(String texto) {
		if(UtilidadesOtros.esNew()) {
			Pattern pattern = Pattern.compile("#[a-fA-F0-9]{6}");
			Matcher match = pattern.matcher(texto);
			
			while(match.find()) {
				String color = texto.substring(match.start(),match.end());
				texto = texto.replace(color, ChatColor.of(color)+"");
				
				match = pattern.matcher(texto);
			}
		}
		
		texto = ChatColor.translateAlternateColorCodes('&', texto);
		
		if(texto.startsWith("{centered}")) {
			texto = texto.replace("{centered}", "");
			texto = getMensajeCentrado(texto);
		}
		
		return texto;
	}
	
	public static String getMensajeCentrado(String message){
		int CENTER_PX = 154;
		int messagePxSize = 0;
        boolean previousCode = false;
        boolean isBold = false;
       
        for(char c : message.toCharArray()){
                if(c == '§'){
                        previousCode = true;
                        continue;
                }else if(previousCode == true){
                        previousCode = false;
                        if(c == 'l' || c == 'L'){
                                isBold = true;
                                continue;
                        }else isBold = false;
                }else{
                        DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
                        messagePxSize += isBold ? dFI.getBoldLength() : dFI.getLength();
                        messagePxSize++;
                }
        }
       
        int halvedMessageSize = messagePxSize / 2;
        int toCompensate = CENTER_PX - halvedMessageSize;
        int spaceLength = DefaultFontInfo.SPACE.getLength() + 1;
        int compensated = 0;
        StringBuilder sb = new StringBuilder();
        while(compensated < toCompensate){
                sb.append(" ");
                compensated += spaceLength;
        }
        return (sb.toString() + message);       
     }
}
