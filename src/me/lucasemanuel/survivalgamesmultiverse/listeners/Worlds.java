/**
 *  Name: Worlds.java
 *  Date: 21:18:38 - 8 nov 2012
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

package me.lucasemanuel.survivalgamesmultiverse.listeners;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

public class Worlds implements Listener {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	public Worlds(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "WorldListener");
		
		logger.debug("Initiated");
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onWorldUnload(WorldUnloadEvent event) {
		logger.debug("World unload event captured!");
		
		if(plugin.getWorldManager().isGameWorld(event.getWorld())) {
			logger.debug("Blocking unload!");
			event.setCancelled(true);
		}
		else {
			logger.debug("Allowing!");
		}
	}
}
