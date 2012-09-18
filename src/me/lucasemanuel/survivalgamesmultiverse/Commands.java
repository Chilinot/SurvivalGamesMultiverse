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

import org.bukkit.ChatColor;
import org.bukkit.Location;
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
			
			case "sglocation":
				return sglocation(sender, args);
				
			case "sgactivate":
				return sgactivate(sender, args);
				
		}
		
		return false;
	}

	private boolean sgactivate(CommandSender sender, String[] args) {
		
		if(args.length != 1) return false;
		
		String worldname = args[0];
		
		if(plugin.getStatusManager().setStatus(worldname, true))
			sender.sendMessage(ChatColor.GREEN + worldname + " is now activated!");
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
		
		Player player     = (Player) sender;
		Location location = player.getLocation();
		
		String firstarg  = args[0].toLowerCase();
		String secondarg = args[1].toLowerCase();
		
		switch(firstarg) {
			
			case "set":
				if(secondarg.equals("main")) {
					player.sendMessage(ChatColor.GREEN + "Adding main location for this world!");
					plugin.getLocationManager().addLocation("main", location);
					return true;
				}
				else if(secondarg.equals("arena")) {
					player.sendMessage(ChatColor.GREEN + "Adding arena location for this world!");
					plugin.getLocationManager().addLocation("arena", location);
					return true;
				}
				return false;
				
			case "save":
				String worldname = location.getWorld().getName();
				
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
				
				return false;
		}
		
		return false;
	}
}
