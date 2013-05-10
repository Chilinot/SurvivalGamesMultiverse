/**
 *  Name: Commands.java
 *  Date: 16:18:09 - 15 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Copyright 2013 Lucas Arnström
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *  
 *
 *  Filedescription:
 *  
 *  Command executor for the plugin.
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse;

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
				return sginfo(sender, args);
				
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

	private boolean sginfo(CommandSender sender, String[] args) {
		
		if(args.length == 0) {
			sender.sendMessage(ChatColor.GREEN + "SurvivalGamesMultiverse v." + plugin.getDescription().getVersion() + " is up and running!");
			
			if(sender instanceof Player) 
				sender.sendMessage(ChatColor.GREEN + "You are in world: " +((Player) sender).getWorld().getName());
			
			return true;
		}
		else if(args.length == 1) {
			
			String arg = args[0].toLowerCase();
			
			switch(arg) {
				
				case "worlds":
					sender.sendMessage(ChatColor.LIGHT_PURPLE + " -- Registered worlds:");
					
					for(String string : this.plugin.getWorldManager().getRegisteredWorldNames()) {
						sender.sendMessage(" - " + ChatColor.YELLOW + string);
					}
					
					return true;
			}
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
		
		if(!plugin.getPlayerManager().isInGame(player)) {
			player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("sgleaveNotIngame"));
			return true;
		}
		
		plugin.getPlayerManager().removePlayer(player.getWorld().getName(), player);
		
		plugin.getStatsManager().removeScoreboard(player.getName());
		
		plugin.getSignManager().updateSigns();
		
		if(plugin.getStatusManager().getStatusFlag(player.getWorld().getName()) == 1) {
			plugin.gameover(player.getWorld());
		}
		else {
			plugin.getLocationManager().resetLocationStatus(player.getLocation());
		}
		
		plugin.getWorldManager().sendPlayerToLobby(player);
		
		return true;
	}

	private boolean sgplayers(CommandSender sender, String[] args) {
		
		if(!(sender instanceof Player)) {
			sender.sendMessage(ChatColor.RED + "You have to be a player inorder to use this command!");
			return true;
		}
		
		Player player = (Player) sender;
		
		Player[] playerlist = null;
		
		if(args.length == 1) {
			
			World world = Bukkit.getWorld(args[0]);
			
			if(world != null && plugin.getWorldManager().isGameWorld(world))
				playerlist = plugin.getPlayerManager().getPlayerList(world.getName());
		}
		else if(playerlist == null && plugin.getWorldManager().isGameWorld(player.getWorld())) {
			playerlist = plugin.getPlayerManager().getPlayerList(player.getWorld().getName());
		}
		else
			player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("sgplayersIncorrect"));
		
		if(playerlist != null) {
				
			if(playerlist.length > 0) {
				player.sendMessage(ChatColor.LIGHT_PURPLE + " --- " + plugin.getLanguageManager().getString("sgplayersHeading") + " --- ");
				
				for(Player tempplayer : playerlist) {
					player.sendMessage(" - " + ChatColor.GREEN + tempplayer.getName());
				}
			}
			else
				player.sendMessage(ChatColor.LIGHT_PURPLE + plugin.getLanguageManager().getString("sgplayersNoonealive"));
			
			return true;
		}
		
		return false;
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
		
		plugin.resetWorld(world);
		
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
		
		final String worldname = location.getWorld().getName();
		
		String error = ChatColor.RED + "This world is not registered!";
		
		switch(firstarg) {
			
			case "set":
				if(secondarg.equals("main")) {
					
					if(plugin.getLocationManager().addLocation("main", location)) 
						player.sendMessage(ChatColor.GREEN + "Adding main location for this world!");
					else 
						player.sendMessage(error);
					
					return true;
				}
				else if(secondarg.equals("arena")) {
					
					if(plugin.getLocationManager().addLocation("arena", location))
						player.sendMessage(ChatColor.GREEN + "Adding arena location for this world!");
					else 
						player.sendMessage(error);
					
					return true;
				}
				
				break;
				
			case "save":
				if(secondarg.equals("main")) {
					
					if(plugin.getLocationManager().saveLocationList("main", worldname))
						player.sendMessage(ChatColor.GREEN + "Saving main locationlist for this world!");
					else 
						player.sendMessage(error);
					
					return true;
				}
				else if(secondarg.equals("arena")) {
					
					if(plugin.getLocationManager().saveLocationList("arena", worldname)) 
						player.sendMessage(ChatColor.GREEN + "Saving arena locationlist for this world!");
					else
						player.sendMessage(error);
					
					return true;
				}
				
				break;
				
			case "clear":
				if(secondarg.equals("main")) {
					
					if(plugin.getLocationManager().clearLocationList("main", worldname)) {
						player.sendMessage(ChatColor.GREEN + "Clearing main locations for this world!");
						
						plugin.getSignManager().updateSigns();
						
						plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
							public void run() {
								plugin.getSQLiteConnector().clearStartLocations(worldname, "main");
							}
						});
					}
						
					else 
						player.sendMessage(error);
					
					return true;
				}
				else if(secondarg.equals("arena")) {
					
					if(plugin.getLocationManager().clearLocationList("arena", worldname)) {
						player.sendMessage(ChatColor.GREEN + "Clearing arena locations for this world!");
						
						plugin.getServer().getScheduler().runTaskAsynchronously(plugin, new Runnable() {
							public void run() {
								plugin.getSQLiteConnector().clearStartLocations(worldname, "arena");
							}
						});
					}
						
					else
						player.sendMessage(error);
					
					return true;
				}
				
				break;
		}
		
		return false;
	}
}
