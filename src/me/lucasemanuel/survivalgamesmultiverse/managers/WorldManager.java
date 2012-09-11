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
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class WorldManager {
	
	//TODO add method for resetting the worlds based on their templates
	//TODO add way of setting and saving spawnpoints for gameworlds
	
	private Main plugin;
	private ConsoleLogger logger;
	
	private HashMap<World, World> worldlist;
	private HashMap<String, HashSet<Location>> loggedblocks;
	
	public WorldManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "WorldManager");
		
		worldlist    = new HashMap<World, World>();
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

	public void logBlock(Location location) {
		if(loggedblocks.containsKey(location.getWorld().getName()) && loggedblocks.get(location.getWorld().getName()).contains(location) == false)
			loggedblocks.get(location.getWorld().getName()).add(location);
	}

	public void resetWorld(World world) {
		
		if(worldlist.containsKey(world)) {
			
			World template = worldlist.get(world);
			
			HashSet<Location> blocksToReset = loggedblocks.get(world.getName());
			for(Location location : blocksToReset) {
				
				int blockX = location.getBlockX();
				int blockY = location.getBlockY();
				int blockZ = location.getBlockZ();
				
				Block templateblock  = template.getBlockAt(blockX, blockY, blockZ);
				Block blockToRestore = location.getBlock();
				
				blockToRestore.setType(templateblock.getType());
				blockToRestore.setData(templateblock.getData());
				
				if(templateblock.getType() == Material.WALL_SIGN || templateblock.getType() == Material.SIGN_POST) {
					
					// Temprorary bugfix - hopefully
					if(blockToRestore.getType() != templateblock.getType()) {
						logger.warning("blockToRestore and templateblock not a match! Sign!");
						continue;
					}
					
					Sign templatesign = (Sign) templateblock.getState();
					Sign restoredsign = (Sign) blockToRestore.getState();
					
					for(int i = 0 ; i < 4 ; i++) {
						restoredsign.setLine(i, templatesign.getLine(i));
					}
					
					restoredsign.update();
				}
			}
			
			plugin.getChestManager().clearLogs(world.getName());
		}
		else
			logger.debug("Tried to reset non registered world!");
	}
}
