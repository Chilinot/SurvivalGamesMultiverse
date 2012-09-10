/**
 *  Name: WorldManager.java
 *  Date: 17:28:06 - 10 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
 *  
 *  
 *  
 * 
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class WorldManager {
	
	//TODO add method for resetting the worlds based on their templates
	//TODO add method for logging blocks, world specific or just a regular HashSet?
	//TODO add way of setting and saving spawnpoints for gameworlds
	
	private Main plugin;
	private ConsoleLogger logger;
	
	private HashMap<World, World> worldlist;
	private HashMap<String, HashSet<Location>> loggedblocks;
	
	public WorldManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "WorldManager");
		
		worldlist = new HashMap<World, World>();
		loggedblocks = new HashMap<String, HashSet<Location>>();
	}
	
	public void addWorld(World world, World template) {
		worldlist.put(world, template);
	}
	
	public boolean isWorld(World world) {
		if(worldlist.containsKey(world))
			return true;
		else
			return false;
	}
	
	public void broadcast(World world, String msg) {
		if(worldlist.containsKey(world)) {
			
			for(Player player : Bukkit.getOnlinePlayers()) {
				if(player.getWorld().equals(world)) {
					player.sendMessage(ChatColor.GREEN + "[SurvivalGames] - " + ChatColor.WHITE + msg);
				}
			}
			
		}
		else
			logger.debug("Tried to broadcast message '" + msg + "' to non registered world - " + world.getName());
	}
}
