/**
 *  Name: Blocks.java
 *  Date: 08:25:43 - 10 sep 2012
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

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class Blocks implements Listener {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	public Blocks(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "BlockListener");
		
		logger.debug("Initiated");
	}
	
	//TODO logg block changes for registered worlds

	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		Block block = event.getBlock();
		Player player = event.getPlayer();
		
		if(plugin.getWorldManager().isWorld(block.getWorld())) {
			
		}
	}
}
