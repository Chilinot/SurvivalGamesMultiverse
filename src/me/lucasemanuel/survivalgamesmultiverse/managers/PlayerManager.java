/**
 *  Name: PlayerManager.java
 *  Date: 20:44:47 - 8 sep 2012
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

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class PlayerManager {
	
	//TODO freeze players when they join
	//TODO have a countdown unfreeze the players after a configurable time
	//TODO teleport players to arena after configurable time
	
	private Main plugin;
	private ConsoleLogger logger;
	
	private HashMap<String, HashSet<String>> playerlists;
	
	public PlayerManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "PlayerManager");
		
		playerlists = new HashMap<String, HashSet<String>>();
		
		logger.debug("Initiated");
	}
	
	public void addWorld(String worldname) {
		logger.debug("Adding world - " + worldname);
		playerlists.put(worldname, new HashSet<String>());
	}

	public void addPlayer(String worldname, String playername) {
		
		if(playerlists.containsKey(worldname)) {
			HashSet<String> playerlist = playerlists.get(worldname);
			
			playerlist.add(playername);
			
			logger.debug("Added - " + playername + " - to world - " + worldname);
		}
		else
			logger.debug("Error! Tried to add player to non existing worldname! - " + worldname);
	}

	public boolean isInGame(String playername) {
		
		for(HashSet<String> playerlist : playerlists.values()) {
			if(playerlist.contains(playername))
				return true;
		}
		
		return false;
	}
}
