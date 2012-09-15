/**
 *  Name: StatusManager.java
 *  Date: 23:58:26 - 15 sep 2012
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

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class StatusManager {
	
	private ConsoleLogger logger;
	
	// Key = worldname, Value = gamestatus / true = started
	private HashMap<String, Boolean> worlds;
	
	public StatusManager(Main instance) {
		logger = new ConsoleLogger(instance, "StatusManager");
		
		worlds = new HashMap<String, Boolean>();
		
		logger.debug("Initiated");
	}
	
	public void addWorld(String worldname) {
		worlds.put(worldname, false);
	}

	public void setStatus(String worldname, boolean value) {
		worlds.put(worldname, value);
	}
	
	public boolean getStatus(String worldname) {
		return worlds.get(worldname);
	}
}
