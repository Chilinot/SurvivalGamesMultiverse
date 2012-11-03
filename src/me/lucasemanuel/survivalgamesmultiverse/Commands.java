/**
 *  Name: Commands.java
 *  Date: 16:18:09 - 15 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
 *  
 *  Command executor for the plugin.
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class Commands implements CommandExecutor {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	public Commands(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "CommandExecutor");
		
		logger.debug("Initiated");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		logger.debug(sender.getName() + " issued command: " + cmd.getName());
		
		String command = cmd.getName().toLowerCase();
		
		switch(command) {
			
			case "sginfo":
				sender.sendMessage(ChatColor.GREEN + "SurvivalGamesMultiverse v." + plugin.getDescription().getVersion() + " is up and running!");
				if(sender instanceof Player) sender.sendMessage(ChatColor.GREEN + "You are in world: " +((Player) sender).getWorld().getName());
				return true;
				
			case "sgdebug":
				return sgdebug(sender, args);
			
			case "sglocation":
				return sglocation(sender, args);
				
			case "sgactivate":
				return sgactivate(sender, args);
				
			case "sgreset":
				return sgreset(sender, args);
				
			case "sgplayers":
				return sgplayers(sender, args);
			
			case "sgleave":
				return sgleave(sender, args);
				
		}
		
		return false;
	}

	private boolean sgdebug(CommandSender sender, String[] args) {
		
		if(args.length != 2) {
			sender.sendMessage(ChatColor.RED + "Incorrect usage!");
			return false;
		}
		
		String firstarg  = args[0].toLowerCase();

		boolean secondarg = false;
		
		switch(args[1].toLowerCase()) {
			
			case "true":
				secondarg = true;
				break;
			
			case "false":
				secondarg = false;
				break;
				
			default:
				sender.sendMessage(ChatColor.RED + "You need to provide a boolean!");
				return false;
		}
		
		switch(firstarg) {
			
			case "set":
				ConsoleLogger.setDebug(secondarg);
				break;
				
			case "listen":
				
				if(!(sender instanceof Player)) {
					sender.sendMessage(ChatColor.RED + "You need to be a player to use this command!");
					break;
				}
				
				Player player = (Player) sender;
				
				if(secondarg == true) {
					ConsoleLogger.addListener(player.getName());
					sender.sendMessage(ChatColor.GREEN + "You are now listening to messages from SurvivalGames!");
				}
				else if(secondarg == false) {
					ConsoleLogger.removeListener(player.getName());
					sender.sendMessage(ChatColor.GREEN + "You are no longer listening to messages from SurvivalGames!");
				}
				
				break;
		}
		
		return true;
	}

	private boolean sgleave(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You have to be a player inorder to use this command!");
			return true;
		}
		
		Player player = (Player) sender;
		
		if(!plugin.getPlayerManager().isInGame(player.getName())) {
			player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("sgleaveNotIngame"));
			return true;
		}
		
		plugin.getPlayerManager().removePlayer(player.getWorld().getName(), player.getName());
		
		if(plugin.getStatusManager().getStatus(player.getWorld().getName())) {
			plugin.gameover(player.getWorld());
		}
		else {
			plugin.getLocationManager().resetLocationStatus(player.getLocation());
		}
		
		plugin.getWorldManager().sendPlayerToSpawn(player);
		
		return true;
	}

	private boolean sgplayers(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You have to be a player inorder to use this command!");
			return true;
		}
		
		Player player = (Player) sender;
		
		HashSet<String> playerlist = plugin.getPlayerManager().getPlayerList(player.getWorld().getName());
		
		player.sendMessage(ChatColor.LIGHT_PURPLE + " --- " + plugin.getLanguageManager().getString("sgplayersHeading") + " --- ");
		
		for(String playername : playerlist) {
			player.sendMessage(" - " + ChatColor.GREEN + playername);
		}
		
		return true;
	}

	private boolean sgreset(CommandSender sender, String[] args) {
		
		if(args.length != 1) { 
			sender.sendMessage(ChatColor.RED + "You need to provide a worldname!");
			return false;
		}
		
		World world = Bukkit.getWorld(args[0]);
		
		if(world == null) {
			sender.sendMessage(ChatColor.RED + "That world doesnt exist!");
			return true;
		}
		
		sender.sendMessage(ChatColor.GREEN + "Resetting world: " + world.getName());
		
		plugin.resetWorld(Bukkit.getWorld(world.getName()));
		
		return true;
	}

	private boolean sgactivate(CommandSender sender, String[] args) {
		
		if(args.length != 1) return false;
		
		String worldname = args[0];
		
		if(plugin.getStatusManager().activate(worldname)) {
			sender.sendMessage(ChatColor.GREEN + worldname + " is now activated!");
		}
		else
			sender.sendMessage(ChatColor.RED + worldname + " could not be activated!");
		
		return true;
	}

	private boolean sglocation(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You have to be a player to use this command!");
			return true;
		}
		if(args.length != 2)
			return false;
		
		Player player = (Player) sender;
		Location location = player.getLocation();
		
		String firstarg  = args[0].toLowerCase();
		String secondarg = args[1].toLowerCase();
		
		String worldname = location.getWorld().getName();
		
		switch(firstarg) {
			
			case "set":
				if(secondarg.equals("main")) {
					if(plugin.getLocationManager().addLocation("main", location)) player.sendMessage(ChatColor.GREEN + "Adding main location for this world!");
					else player.sendMessage(ChatColor.RED + "This world is not registered!");
					return true;
				}
				else if(secondarg.equals("arena")) {
					if(plugin.getLocationManager().addLocation("arena", location)) player.sendMessage(ChatColor.GREEN + "Adding arena location for this world!");
					else player.sendMessage(ChatColor.RED + "This world is not registered!");
					return true;
				}
				break;
				
			case "save":
				if(secondarg.equals("main")) {
					player.sendMessage(ChatColor.GREEN + "Saving main locationlist for this world!");
					plugin.getLocationManager().saveLocationList("main", worldname);
					return true;
				}
				else if(secondarg.equals("arena")) {
					player.sendMessage(ChatColor.GREEN + "Saving arena locationlist for this world!");
					plugin.getLocationManager().saveLocationList("arena", worldname);
					return true;
				}
				break;
				
			case "clear":
				if(secondarg.equals("main")) {
					player.sendMessage(ChatColor.GREEN + "Clearing main locations for this world! Remember to save!");
					plugin.getLocationManager().clearLocationList("main", worldname);
					return true;
				}
				else if(secondarg.equals("arena")) {
					player.sendMessage(ChatColor.GREEN + "Clearing arena locations for this world! Remember to save!");
					plugin.getLocationManager().clearLocationList("arena", worldname);
					return true;
				}
				break;
		}
		
		return false;
	}
}
