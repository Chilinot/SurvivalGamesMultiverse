/**
 *  Name: Blocks.java
 *  Date: 08:25:43 - 10 sep 2012
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
 *  
 *  
 * 
 * 
 */

package se.lucasarnstrom.survivalgamesmultiverse.listeners;

import java.util.EnumSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.block.LeavesDecayEvent;
import org.bukkit.event.entity.EntityExplodeEvent;

import se.lucasarnstrom.survivalgamesmultiverse.Main;
import se.lucasarnstrom.survivalgamesmultiverse.managers.WorldManager;
import se.lucasarnstrom.survivalgamesmultiverse.managers.StatusManager.StatusFlag;
import se.lucasarnstrom.survivalgamesmultiverse.utils.ConsoleLogger;

public class Blocks implements Listener {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	// Materials to log in the physics event.
	private Set<Material> physics = EnumSet.of(
			Material.TORCH,
			Material.LADDER,
			Material.REDSTONE_COMPARATOR_OFF,
			Material.REDSTONE_COMPARATOR_ON,
			Material.REDSTONE_TORCH_OFF,
			Material.REDSTONE_TORCH_ON,
			Material.REDSTONE_WIRE,
			Material.WALL_SIGN,
			Material.SIGN_POST
		);
	
	public Blocks(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger("BlockListener");
		
		logger.debug("Initiated");
	}

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		
		WorldManager wm = plugin.getWorldManager();
		
		Block block = event.getBlock();
		
		if(wm.isGameWorld(block.getWorld().getName())) {
			if(plugin.getStatusManager().getStatusFlag(block.getWorld().getName()) == StatusFlag.STARTED 
					|| event.getPlayer().hasPermission("survivalgames.ignore.blockfilter")) {
				if(wm.allowBlock(block)) {
					wm.logBlock(block, true);
					if(block.getType().equals(Material.CHEST))
						plugin.getChestManager().addChestToLog(block.getLocation());
				}
				else {
					event.getPlayer().sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("nonAllowedBlock"));
					event.setCancelled(true);
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
		
		WorldManager wm = plugin.getWorldManager();
		
		Player player = event.getPlayer();
		Block block   = event.getBlock();
		
		if(wm.isGameWorld(block.getWorld().getName())) {
			if(plugin.getStatusManager().getStatusFlag(block.getWorld().getName()) == StatusFlag.STARTED 
					|| player.hasPermission("survivalgames.ignore.blockfilter")) {
				if(wm.allowBlock(block)) {
					wm.logBlock(block, false);
				}
				else {
					player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("nonAllowedBlock"));
					event.setCancelled(true);
				}
			}
			else {
				player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("gameHasNotStartedYet"));
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onEntityExplode(EntityExplodeEvent event) {
		
		if(plugin.getWorldManager().isGameWorld(event.getLocation().getWorld().getName())) {
			for(Block block : event.blockList()) {
				plugin.getWorldManager().logBlock(block, false);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onLeavesDecay(LeavesDecayEvent event) {
		
		Block block = event.getBlock();
		
		if(plugin.getWorldManager().isGameWorld(block.getWorld().getName())) {
			plugin.getWorldManager().logBlock(block, false);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onBlockBurn(BlockBurnEvent event) {
		
		Block block = event.getBlock();
		
		if(plugin.getWorldManager().isGameWorld(block.getWorld().getName())) {
			plugin.getWorldManager().logBlock(block, false);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onBlockFade(BlockFadeEvent event) {

		Block block = event.getBlock();
		
		if(plugin.getWorldManager().isGameWorld(block.getWorld().getName())) {
			plugin.getWorldManager().logBlock(block, false);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onBlockSpread(BlockSpreadEvent event) {
		
		Block block = event.getBlock();
		
		if(plugin.getWorldManager().isGameWorld(block.getWorld().getName())) {
			plugin.getWorldManager().logBlock(block, false);
		}
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onBlockPhysics(BlockPhysicsEvent event) {
		
		//TODO need to do something about this!
		
		Block b = event.getBlock();
		
		if(plugin.getWorldManager().isGameWorld(b.getWorld().getName())
				&& physics.contains(b.getType())) {
			plugin.getWorldManager().logBlock(b, false);
		}
	}
}