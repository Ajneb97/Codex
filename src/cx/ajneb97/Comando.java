package cx.ajneb97;




import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import cx.ajneb97.data.AgregarEntradaCallback;
import cx.ajneb97.data.JugadorCodex;
import cx.ajneb97.data.JugadorCodexCallback;
import cx.ajneb97.managers.InventarioManager;
import cx.ajneb97.managers.MensajesManager;
import cx.ajneb97.model.CategoriaCodex;
import cx.ajneb97.model.EntradaCodex;




public class Comando implements CommandExecutor {
	
	private Codex plugin;
	public Comando(Codex plugin) {
		this.plugin = plugin;
	}
	
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
		FileConfiguration messages = plugin.getMessages();
		   MensajesManager msgManager = plugin.getMensajesManager();
		if (!(sender instanceof Player)){
		   if(args.length >= 1) {
			   if(args[0].equalsIgnoreCase("unlock")) {
				   unlock(sender,messages,msgManager,args);
			   }else if(args[0].equalsIgnoreCase("reload")) {
				   reload(sender,messages,msgManager);
			   }else if(args[0].equalsIgnoreCase("reset")) {
				   reset(sender,messages,msgManager,args);
			   }else if(args[0].equalsIgnoreCase("help")) {
				   help(sender,msgManager,messages,args);
			   }else if(args[0].equalsIgnoreCase("open")) {
				   open(sender,messages,msgManager,args);
			   }
		   }
		   return true;
	   }
	   Player jugador = (Player)sender;
	   if(args.length == 0) {
		   if(plugin.isErroresEnItems()) {
			   msgManager.enviarMensaje(sender, messages.getString("commandCodexError"), true);
			   return true;
		   }
		   plugin.getInventarioManager().crearInventario("main", jugador);
	   }else {
		   if(jugador.isOp() || jugador.hasPermission("codex.admin")) {
			   if(args[0].equalsIgnoreCase("unlock")) {
				   unlock(sender,messages,msgManager,args);
			   }else if(args[0].equalsIgnoreCase("reload")) {
				   reload(sender,messages,msgManager);
			   }else if(args[0].equalsIgnoreCase("help")) {
				   help(sender,msgManager,messages,args);
			   }else if(args[0].equalsIgnoreCase("reset")) {
				   reset(sender,messages,msgManager,args);
			   }else if(args[0].equalsIgnoreCase("open")) {
				   open(sender,messages,msgManager,args);
			   }
			   else {
				   //Comando no existe
				   msgManager.enviarMensaje(sender, messages.getString("commandNotExists"), true);
			   }
		   }else {
			   //No tiene permisos
			   msgManager.enviarMensaje(sender, messages.getString("noPermissions"), true);
		   }
	   }
	   return true;
	   
	}
	
	public void help(CommandSender sender,MensajesManager msgManager,FileConfiguration messages,String[] args) {
		// /codex help
		// /codex help <page>
		if (args.length > 1) {
			try {
				int pagina = Integer.valueOf(args[1]);
				mensajeAyuda(sender, pagina, msgManager, messages);
			} catch (NumberFormatException e) {
				msgManager.enviarMensaje(sender, messages.getString("commandHelpNoValidPage"), true);
			}
		} else {
			mensajeAyuda(sender, 1, msgManager, messages);
		}
	}
	
	public void mensajeAyuda(CommandSender sender,int pagina,MensajesManager msgManager,FileConfiguration messages) {
		if(pagina == 1) {
			sender.sendMessage(MensajesManager.getMensajeColor("&f&l- - - - - &c&lCODEX COMMANDS &8(&e1&8/&e2&8) &f&l- - - - -"));
			sender.sendMessage(MensajesManager.getMensajeColor(""));
			sender.sendMessage(MensajesManager.getMensajeColor("&7Opens the main menu."));
			sender.sendMessage(MensajesManager.getMensajeColor("&b/codex"));
			sender.sendMessage(MensajesManager.getMensajeColor(""));
			sender.sendMessage(MensajesManager.getMensajeColor("&7Shows this message."));
			sender.sendMessage(MensajesManager.getMensajeColor("&b/codex help"));
			sender.sendMessage(MensajesManager.getMensajeColor(""));
			sender.sendMessage(MensajesManager.getMensajeColor("&7Unlocks a discovery for a player."));
			sender.sendMessage(MensajesManager.getMensajeColor("&b/codex unlock <player> <category> <discovery> (optional, send message)<true/false>"));
			sender.sendMessage(MensajesManager.getMensajeColor(""));
			sender.sendMessage(MensajesManager.getMensajeColor("&7Resets a player discovery."));
			sender.sendMessage(MensajesManager.getMensajeColor("&b/codex reset <player> <category> <discovery>"));
			sender.sendMessage(MensajesManager.getMensajeColor(""));
			sender.sendMessage(MensajesManager.getMensajeColor("&7Reloads the config"));
			sender.sendMessage(MensajesManager.getMensajeColor("&b/codex reload"));
			sender.sendMessage(MensajesManager.getMensajeColor(""));
			sender.sendMessage(MensajesManager.getMensajeColor("&f&l- - - - - &c&lCODEX COMMANDS &8(&e1&8/&e2&8) &f&l- - - - -"));
		}else if(pagina == 2) {
			sender.sendMessage(MensajesManager.getMensajeColor("&f&l- - - - - &c&lCODEX COMMANDS &8(&e2&8/&e2&8) &f&l- - - - -"));
			sender.sendMessage(MensajesManager.getMensajeColor(""));
			sender.sendMessage(MensajesManager.getMensajeColor("&7Opens an inventory for the player."));
			sender.sendMessage(MensajesManager.getMensajeColor("&b/codex open <player> <inventory>"));
			sender.sendMessage(MensajesManager.getMensajeColor(""));
			sender.sendMessage(MensajesManager.getMensajeColor("&f&l- - - - - &c&lCODEX COMMANDS &8(&e2&8/&e2&8) &f&l- - - - -"));
		}else {
			msgManager.enviarMensaje(sender, messages.getString("commandHelpNoValidPage"), true); 
		}
	}
	
	public void reset(CommandSender sender,FileConfiguration messages,MensajesManager msgManager,String[] args) {
		   // /codex reset <player> <category> <discovery>
		   // /codex reset <player> all
		   if(args.length >= 4) {
			   String categoria = args[2];
			   String discovery = args[3];
			   
			   plugin.getJugadorDataManager().getJugador(args[1], new JugadorCodexCallback() {
				   @Override
				   public void onDone(JugadorCodex j) {
					   if(j == null) {
						   msgManager.enviarMensaje(sender, messages.getString("playerNotUnlockedDiscovery"), true);
						   return;
					   }
					   boolean tieneEntrada = j.tieneEntrada(categoria, discovery);
					   if(!tieneEntrada) {
						   msgManager.enviarMensaje(sender, messages.getString("playerNotUnlockedDiscovery"), true);
						   return;
					   }
					   
					   plugin.getJugadorDataManager().resetearEntrada(j, categoria, discovery, false);

					   msgManager.enviarMensaje(sender, messages.getString("playerResetDiscovery")
							   .replace("%category%", categoria).replace("%discovery%", discovery)
							   .replace("%player%", args[1]), true);
				   }   
			   });
		   }else {
			   if(args.length >= 3 && args[2].equalsIgnoreCase("all")) {
				   plugin.getJugadorDataManager().getJugador(args[1], new JugadorCodexCallback() {
					@Override
					public void onDone(JugadorCodex j) {
						if(j == null) {
							   msgManager.enviarMensaje(sender, messages.getString("playerNotUnlockedDiscovery"), true);
							   return;
						   }
						   
						   plugin.getJugadorDataManager().resetearEntrada(j, null, null, true);
						   msgManager.enviarMensaje(sender, messages.getString("playerResetAll")
								   .replace("%player%", args[1]), true);
					}   
				   });
			   }else {
				   msgManager.enviarMensaje(sender, messages.getString("commandResetErrorUse"), true); 
			   }
		   }
	}
	
	public void unlock(CommandSender sender,FileConfiguration messages,MensajesManager msgManager,String[] args) {
		   // /codex unlock <player> <category> <discovery> (opcional) true/false
		   if(args.length >= 4) {
			   Player player = Bukkit.getPlayer(args[1]);
			   if(player == null || !player.isOnline()) {
				   msgManager.enviarMensaje(sender, messages.getString("notOnline"), true);
				   return;
			   }
			   String categoria = args[2];
			   String discovery = args[3];
			   CategoriaCodex categoriaCodex = plugin.getCategoriasManager().getCategoria(categoria);
			   if(categoriaCodex == null) {
				   msgManager.enviarMensaje(sender, messages.getString("categoryNotExists")
						   .replace("%category%", categoria), true);
				   return;
			   }
			   EntradaCodex entradaCodex = categoriaCodex.getEntrada(discovery);
			   if(entradaCodex == null) {
				   msgManager.enviarMensaje(sender, messages.getString("discoveryNotExists")
						   .replace("%category%", categoria).replace("%discovery%", discovery), true);
				   return;
			   }
			   
			   plugin.getJugadorDataManager().agregarEntrada(player, categoria, discovery, new AgregarEntradaCallback() {

				@Override
				public void onDone(boolean agrega) {
					   boolean enviaMensaje = true;
					   if(args.length >= 5) {
						   try {
							   enviaMensaje = Boolean.valueOf(args[4]);
						   }catch(Exception e) {
							  
						   }
					   }
					   if(!agrega) {
						   if(enviaMensaje) {
							   msgManager.enviarMensaje(sender, messages.getString("playerAlreadyHasDiscovery")
									   .replace("%category%", categoria).replace("%discovery%", discovery)
									   .replace("%player%", args[1]), true);
						   }
						   return;
					   }
					   if(enviaMensaje) {
						   msgManager.enviarMensaje(sender, messages.getString("playerUnlockDiscovery")
								   .replace("%category%", categoria).replace("%discovery%", discovery)
								   .replace("%player%", args[1]), true);
					   }
					   plugin.getCodexManager().desbloquearEntrada(player, categoriaCodex, entradaCodex);
				}
				   
			   });
		   }else {
			   msgManager.enviarMensaje(sender, messages.getString("commandUnlockErrorUse"), true);
		   }
	}
	
	public void open(CommandSender sender,FileConfiguration messages,MensajesManager msgManager,String[] args) {
		   // /codex open <player> <inventory>
		if(args.length >= 3) {
			Player player = Bukkit.getPlayer(args[1]);
			if(player == null || !player.isOnline()) {
				 msgManager.enviarMensaje(sender, messages.getString("notOnline"), true);
				 return;
			}
			
			String inventory = args[2];
			
			InventarioManager invManager = plugin.getInventarioManager();
			if(invManager.getInventario(inventory) == null) {
				msgManager.enviarMensaje(sender, messages.getString("inventoryDoesNotExists")
						.replace("%inventory%", inventory), true);
				return;
			}
			
			plugin.getInventarioManager().crearInventario(inventory, player);
			msgManager.enviarMensaje(sender, messages.getString("playerOpenInventory")
					.replace("%player%", player.getName()).replace("%inventory%", inventory), true);
		}else {
			 msgManager.enviarMensaje(sender, messages.getString("commandOpenErrorUse"), true);
		}
	}
	
	public void reload(CommandSender sender,FileConfiguration messages,MensajesManager msgManager) {
		plugin.getConfigsManager().getMensajesConfigManager().reloadMessages();
		plugin.getConfigsManager().getCategoriesConfigsManager().reloadCategories();
		plugin.getConfigsManager().getInventoryConfigManager().recargar();
		plugin.reloadConfig();
		plugin.restartSavePlayersTask();
		msgManager.enviarMensaje(sender, messages.getString("configReloaded"), true);
	}
}
