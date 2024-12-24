package cx.ajneb97.commands;

import cx.ajneb97.Codex;
import cx.ajneb97.managers.InventoryManager;
import cx.ajneb97.managers.MessagesManager;
import cx.ajneb97.model.inventory.CommonInventory;
import cx.ajneb97.model.inventory.InventoryPlayer;
import cx.ajneb97.model.structure.Category;
import cx.ajneb97.model.structure.Discovery;
import cx.ajneb97.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;


public class MainCommand implements CommandExecutor, TabCompleter {

	private Codex plugin;
	public MainCommand(Codex plugin){
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command c, String label, String[] args){
		MessagesManager msgManager = plugin.getMessagesManager();
		FileConfiguration messagesConfig = plugin.getMessagesConfig();

		if (!(sender instanceof Player)){
		   if(args.length >= 1){
			   String arg = args[0].toLowerCase();
			   switch(arg){
				   case "reload":
					   reload(sender,msgManager,messagesConfig);
					   break;
				   case "resetplayer":
					   resetPlayer(sender,args,msgManager,messagesConfig);
					   break;
				   case "unlock":
					   unlock(sender,args,msgManager,messagesConfig);
					   break;
				   case "open":
					   open(sender,args,msgManager,messagesConfig);
					   break;
				   case "help":
					   help(sender,args,msgManager,messagesConfig);
					   break;
				   default:
					   wrongCommand(sender,msgManager,messagesConfig);
					   break;

			   }
		   }
		   return false;
	   	}

		Player player = (Player) sender;
		if(args.length >= 1){
			String arg = args[0].toLowerCase();
			switch(arg){
				case "reload":
					reload(sender,msgManager,messagesConfig);
					break;
				case "resetplayer":
					resetPlayer(sender,args,msgManager,messagesConfig);
					break;
				case "unlock":
					unlock(sender,args,msgManager,messagesConfig);
					break;
				case "open":
					open(sender,args,msgManager,messagesConfig);
					break;
				case "help":
					help(sender,args,msgManager,messagesConfig);
					break;
				case "verify":
					verify(player,msgManager,messagesConfig);
					break;
				default:
					wrongCommand(sender,msgManager,messagesConfig);
					break;
			}
		}else{
			noArguments(player,msgManager,messagesConfig);
		}

	   	return true;
	}

	public void wrongCommand(CommandSender sender,MessagesManager msgManager,FileConfiguration messagesConfig){
		msgManager.sendMessage(sender,messagesConfig.getString("commandDoesNotExist"),true);
	}

	public void help(CommandSender sender,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig){
		// /codex help
		// /codex help <page>
		if(!PlayerUtils.isCodexAdmin(sender)){
			msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
			return;
		}

		int page = 1;
		if (args.length > 1) {
			try {
				page = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				msgManager.sendMessage(sender,messagesConfig.getString("commandHelpNoValidPage"),true);
				return;
			}
		}

		if(page == 1) {
			sender.sendMessage(MessagesManager.getColoredMessage("&f&l- - - - - &6&lCODEX COMMANDS &8(&e1&8/&e2&8) &f&l- - - - -"));
			sender.sendMessage(MessagesManager.getColoredMessage(""));
			sender.sendMessage(MessagesManager.getColoredMessage("&7Opens the main menu."));
			sender.sendMessage(MessagesManager.getColoredMessage("&b/codex"));
			sender.sendMessage(MessagesManager.getColoredMessage(""));
			sender.sendMessage(MessagesManager.getColoredMessage("&7Shows this message."));
			sender.sendMessage(MessagesManager.getColoredMessage("&b/codex help"));
			sender.sendMessage(MessagesManager.getColoredMessage(""));
			sender.sendMessage(MessagesManager.getColoredMessage("&7Unlocks a discovery for a player."));
			sender.sendMessage(MessagesManager.getColoredMessage("&b/codex unlock <player> <category> <discovery> (optional, send message)<true/false>"));
			sender.sendMessage(MessagesManager.getColoredMessage(""));
			sender.sendMessage(MessagesManager.getColoredMessage("&7Resets a player discovery."));
			sender.sendMessage(MessagesManager.getColoredMessage("&b/codex resetplayer <player>/* (optional)<category> (optional)<discovery>"));
			sender.sendMessage(MessagesManager.getColoredMessage(""));
			sender.sendMessage(MessagesManager.getColoredMessage("&7Reloads the config"));
			sender.sendMessage(MessagesManager.getColoredMessage("&b/codex reload"));
			sender.sendMessage(MessagesManager.getColoredMessage(""));
			sender.sendMessage(MessagesManager.getColoredMessage("&f&l- - - - - &6&lCODEX COMMANDS &8(&e1&8/&e2&8) &f&l- - - - -"));
		}else if(page == 2) {
			sender.sendMessage(MessagesManager.getColoredMessage("&f&l- - - - - &6&lCODEX COMMANDS &8(&e2&8/&e2&8) &f&l- - - - -"));
			sender.sendMessage(MessagesManager.getColoredMessage(""));
			sender.sendMessage(MessagesManager.getColoredMessage("&7Opens an inventory for the player."));
			sender.sendMessage(MessagesManager.getColoredMessage("&b/codex open <player> <inventory>"));
			sender.sendMessage(MessagesManager.getColoredMessage(""));
			sender.sendMessage(MessagesManager.getColoredMessage("&7Checks the plugin for errors."));
			sender.sendMessage(MessagesManager.getColoredMessage("&b/codex verify"));
			sender.sendMessage(MessagesManager.getColoredMessage(""));
			sender.sendMessage(MessagesManager.getColoredMessage("&f&l- - - - - &6&lCODEX COMMANDS &8(&e2&8/&e2&8) &f&l- - - - -"));
		}else {
			msgManager.sendMessage(sender,messagesConfig.getString("commandHelpNoValidPage"),true);
		}
	}

	public void reload(CommandSender sender,MessagesManager msgManager,FileConfiguration messagesConfig){
		if(!PlayerUtils.isCodexAdmin(sender)){
			msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
			return;
		}

		if(!plugin.getConfigsManager().reload()){
			sender.sendMessage(Codex.prefix+MessagesManager.getColoredMessage("&cThere was an error reloading the config, check the console."));
			return;
		}
		msgManager.sendMessage(sender,messagesConfig.getString("configReloaded"),true);
	}

	public void resetPlayer(CommandSender sender,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig){
		// /codex resetplayer <player> (opt)<category> (opt)<discovery>
		if(!PlayerUtils.isCodexAdmin(sender)){
			msgManager.sendMessage(sender,messagesConfig.getString("noPermissions"),true);
			return;
		}

		if(args.length <= 1){
			msgManager.sendMessage(sender,messagesConfig.getString("commandResetPlayerError"),true);
			return;
		}

		String playerName = args[1];
		String category = null;
		String discovery = null;
		if(args.length > 2){
			category = args[2];
		}
		if(args.length > 3){
			discovery = args[3];
		}

		String result = plugin.getPlayerDataManager().resetDataPlayer(playerName,category,discovery,messagesConfig);
		if(result != null){
			msgManager.sendMessage(sender,result,true);
		}
	}

	public void noArguments(Player player,MessagesManager msgManager,FileConfiguration messagesConfig){
		if(plugin.getVerifyManager().isCriticalErrors()){
			msgManager.sendMessage(player,messagesConfig.getString("pluginCriticalErrors"),true);
			return;
		}

		plugin.getInventoryManager().openInventory(new InventoryPlayer(player,"main_inventory"));
	}

	public void verify(Player player,MessagesManager msgManager,FileConfiguration messagesConfig){
		// /codex verify
		if(!PlayerUtils.isCodexAdmin(player)){
			msgManager.sendMessage(player,messagesConfig.getString("noPermissions"),true);
			return;
		}
		plugin.getVerifyManager().sendVerification(player);
	}

	public void unlock(CommandSender sender,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig) {
		// /codex unlock <player> <category> <discovery> (opt)<send message>
		if (!PlayerUtils.isCodexAdmin(sender)) {
			msgManager.sendMessage(sender, messagesConfig.getString("noPermissions"), true);
			return;
		}

		if (args.length <= 3) {
			msgManager.sendMessage(sender, messagesConfig.getString("commandUnlockErrorUse"), true);
			return;
		}

		Player player = Bukkit.getPlayer(args[1]);
		if(player == null){
			msgManager.sendMessage(sender, messagesConfig.getString("playerNotOnline"), true);
			return;
		}

		String categoryName = args[2];
		String discoveryName = args[3];
		Category category = plugin.getCategoryManager().getCategory(categoryName);
		if(category == null){
			msgManager.sendMessage(sender, messagesConfig.getString("categoryDoesNotExist"), true);
			return;
		}
		Discovery discovery = category.getDiscovery(discoveryName);
		if(discovery == null){
			msgManager.sendMessage(sender, messagesConfig.getString("discoveryDoesNotExist")
					.replace("%category%",categoryName), true);
			return;
		}

		boolean sendMessage = true;
		if(args.length >= 5){
			try{
				sendMessage = Boolean.parseBoolean(args[4]);
			}catch(Exception ignore){}
		}

		boolean canDiscover = plugin.getDiscoveryManager().onDiscover(player,categoryName,discoveryName);
		if(!canDiscover){
			if(sendMessage){
				msgManager.sendMessage(sender, messagesConfig.getString("playerAlreadyHasDiscovery")
						.replace("%category%",categoryName)
						.replace("%discovery%",discoveryName)
						.replace("%player%",player.getName()), true);
			}
		}else{
			if(sendMessage){
				msgManager.sendMessage(sender, messagesConfig.getString("playerUnlockDiscovery")
						.replace("%category%",categoryName)
						.replace("%discovery%",discoveryName)
						.replace("%player%",player.getName()), true);
			}
		}
	}

	public void open(CommandSender sender,String[] args,MessagesManager msgManager,FileConfiguration messagesConfig) {
		// /codex open <player> <inventory>
		if (!PlayerUtils.isCodexAdmin(sender)) {
			msgManager.sendMessage(sender, messagesConfig.getString("noPermissions"), true);
			return;
		}

		if (args.length <= 2) {
			msgManager.sendMessage(sender, messagesConfig.getString("commandUnlockErrorUse"), true);
			return;
		}

		Player player = Bukkit.getPlayer(args[1]);
		if(player == null){
			msgManager.sendMessage(sender, messagesConfig.getString("playerNotOnline"), true);
			return;
		}

		InventoryManager inventoryManager = plugin.getInventoryManager();
		CommonInventory commonInventory = inventoryManager.getInventory(args[2]);
		if(commonInventory == null){
			msgManager.sendMessage(sender, messagesConfig.getString("inventoryDoesNotExists").replace("%inventory%",args[2]), true);
			return;
		}

		inventoryManager.openInventory(new InventoryPlayer(player,commonInventory.getName()));
		msgManager.sendMessage(sender, messagesConfig.getString("playerOpenInventory").replace("%inventory%",args[2])
				.replace("%player%",player.getName()), true);
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		List<String> completions = new ArrayList<>();
		List<String> commands = new ArrayList<>();

		switch(args.length){
			case 1:
				if(PlayerUtils.isCodexAdmin(sender)){
					commands.add("reload");commands.add("resetplayer");commands.add("unlock");
					commands.add("open");commands.add("help");commands.add("verify");
				}
				for(String c : commands) {
					if(args[0].isEmpty() || c.startsWith(args[0].toLowerCase())) {
						completions.add(c);
					}
				}
				return completions;
			case 2:
				if(PlayerUtils.isCodexAdmin(sender)){
					commands.add("resetplayer");commands.add("unlock");commands.add("open");
				}
				for(String c : commands) {
					if(args[0].equalsIgnoreCase(c)){
						if(c.equals("resetplayer")){
							completions = new ArrayList<>();
							addPlayers(completions,args[1]);
							addAllWord(completions,args[1]);
							return completions;
						}else if(c.equals("unlock") || c.equals("open")){
							completions = new ArrayList<>();
							addPlayers(completions,args[1]);
							return completions;
						}
					}
				}
				break;
			case 3:
				if(PlayerUtils.isCodexAdmin(sender)){
					commands.add("resetplayer");commands.add("unlock");commands.add("open");
				}
				for(String c : commands) {
					if(args[0].equalsIgnoreCase(c)){
						if(c.equals("resetplayer") || c.equals("unlock")){
							return getCategoryCompletions(args,2);
						}else if(c.equals("open")){
							return getInventoryCompletions(args,2);
						}
					}
				}
				break;
			case 4:
				if(PlayerUtils.isCodexAdmin(sender)){
					commands.add("resetplayer");commands.add("unlock");
				}
				for(String c : commands) {
					if(args[0].equalsIgnoreCase(c)){
						if(c.equals("resetplayer") || c.equals("unlock")){
							return getDiscoveryCompletions(args,3);
						}
					}
				}
				break;
			case 5:
				if(PlayerUtils.isCodexAdmin(sender)){
					commands.add("unlock");
				}
				for(String c : commands) {
					if(args[0].equalsIgnoreCase(c)){
						if(c.equals("unlock")){
							completions = new ArrayList<>();
							completions.add("true");
							completions.add("false");
							return completions;
						}
					}
				}
				break;
		}

		return null;
	}

	private List<String> getInventoryCompletions(String[] args,int argInventoryPos){
		List<String> completions = new ArrayList<>();
		String argInventory = args[argInventoryPos];

		ArrayList<CommonInventory> inventories = plugin.getInventoryManager().getInventories();
		for(CommonInventory inventory : inventories) {
			if(argInventory.isEmpty() || inventory.getName().toLowerCase().startsWith(argInventory.toLowerCase())) {
				completions.add(inventory.getName());
			}
		}

		if(completions.isEmpty()){
			return null;
		}
		return completions;
	}

	private List<String> getCategoryCompletions(String[] args,int argCategoryPos){
		List<String> completions = new ArrayList<>();
		String argCategory = args[argCategoryPos];

		ArrayList<Category> categories = plugin.getCategoryManager().getCategories();
		for(Category category : categories) {
			if(argCategory.isEmpty() || category.getName().toLowerCase().startsWith(argCategory.toLowerCase())) {
				completions.add(category.getName());
			}
		}

		if(completions.isEmpty()){
			return null;
		}
		return completions;
	}

	private List<String> getDiscoveryCompletions(String[] args,int argDiscoveryPos){
		List<String> completions = new ArrayList<>();
		String argDiscovery = args[argDiscoveryPos];
		String argCategory = args[argDiscoveryPos-1];

		Category category = plugin.getCategoryManager().getCategory(argCategory);
		if(category == null){
			return null;
		}

		ArrayList<Discovery> discoveries = category.getDiscoveries();
		for(Discovery discovery : discoveries) {
			if(argDiscovery.isEmpty() || discovery.getId().toLowerCase().startsWith(argDiscovery.toLowerCase())) {
				completions.add(discovery.getId());
			}
		}

		if(completions.isEmpty()){
			return null;
		}
		return completions;
	}

	private void addAllWord(List<String> completions,String arg){
		if(arg.isEmpty() || "*".startsWith(arg.toLowerCase())) {
			completions.add("*");
		}
	}

	private void addPlayers(List<String> completions,String arg){
		for(Player p : Bukkit.getOnlinePlayers()) {
			if(arg.isEmpty() || p.getName().toLowerCase().startsWith(arg.toLowerCase())){
				completions.add(p.getName());
			}
		}
	}
}
