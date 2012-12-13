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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

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

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		Block block = event.getBlock();
		
		if(plugin.getWorldManager().isGameWorld(block.getWorld())) {
			if(plugin.getStatusManager().getStatusFlag(block.getWorld().getName()) == 1 || event.getPlayer().hasPermission("survivalgames.ignore.blockfilter")) {
				if(plugin.getWorldManager().isGameWorld(block.getWorld())) {
					
					plugin.getWorldManager().logBlock(block.getLocation());
					
					if(block.getType().equals(Material.CHEST))
						plugin.getChestManager().addChestToLog(block.getLocation());
				}
			}
			else {
				event.getPlayer().sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("gameHasNotStartedYet"));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onBlockBreak(BlockBreakEvent event) {
		
		Block block = event.getBlock();
		
		if(plugin.getWorldManager().isGameWorld(block.getWorld())) {
			if(plugin.getStatusManager().getStatusFlag(block.getWorld().getName()) == 1 || event.getPlayer().hasPermission("survivalgames.ignore.blockfilter")) {
				if(plugin.getWorldManager().isGameWorld(block.getWorld())) {
					plugin.getWorldManager().logBlock(block.getLocation());
				}
			}
			else {
				event.getPlayer().sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("gameHasNotStartedYet"));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		if(plugin.getWorldManager().isGameWorld(event.getLocation().getWorld())) {
			for(Block block : event.blockList()) {
				plugin.getWorldManager().logBlock(block.getLocation());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onLeavesDecay(LeavesDecayEvent event) {
		
		Block block = event.getBlock();
		
		if(plugin.getWorldManager().isGameWorld(block.getWorld())) {
			plugin.getWorldManager().logBlock(block.getLocation());
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onBlockBurn(BlockBurnEvent event) {
		
		Block block = event.getBlock();
		
		if(plugin.getWorldManager().isGameWorld(block.getWorld())) {
			plugin.getWorldManager().logBlock(block.getLocation());
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onBlockFade(BlockFadeEvent event) {

		Block block = event.getBlock();
		
		if(plugin.getWorldManager().isGameWorld(block.getWorld())) {
			plugin.getWorldManager().logBlock(block.getLocation());
		}
	}
}