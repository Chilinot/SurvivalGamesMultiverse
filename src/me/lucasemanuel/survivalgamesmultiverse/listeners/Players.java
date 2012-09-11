/**
 *  Name: Players.java
 *  Date: 08:16:34 - 10 sep 2012
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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class Players implements Listener {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	public Players(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "PlayerListener");
		
		logger.debug("Initiated");
	}
	
	//TODO teleport players to spawnpoint on join
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		Block block = event.getClickedBlock();
		Player player = event.getPlayer();
		
		if(plugin.getWorldManager().isWorld(block.getWorld())) {
		
			if(block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN)) {
				
				Sign sign = (Sign) block.getState();
				String firstline = sign.getLine(0).toLowerCase();
				
				if(firstline.equals("survivalgames")) {
					
					if(plugin.getPlayerManager().isInGame(player.getName()) == false) {
						
						plugin.getPlayerManager().addPlayer(block.getWorld().getName(), player.getName());
						player.sendMessage(ChatColor.GREEN + "Du har hoppat med i spelet!");
						
						plugin.getWorldManager().broadcast(block.getWorld(), ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.WHITE + " har hoppat med i spelet!");
					}
					else
						player.sendMessage(ChatColor.RED + "Du verkar redan spela!");
				}
			}
			
			else if(block.getType().equals(Material.CHEST)) {
				plugin.getChestManager().randomizeChest((Chest)block.getState());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.NORMAL, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		Player victim = event.getEntity();
		
		if(plugin.getWorldManager().isWorld(victim.getWorld())) {
			
			// Blocka alla meddelanden i SG världarna
			event.setDeathMessage(null);
			
			if(plugin.getPlayerManager().isInGame(victim.getName())) {
				Player killer = event.getEntity().getKiller();
				
				// Dödades spelaren utav en annan spelare eller dog han naturligt?
				if(killer != null) {
					plugin.getWorldManager().broadcast(victim.getWorld(), ChatColor.LIGHT_PURPLE + victim.getName() + ChatColor.RED + " dödades utav " + ChatColor.BLUE + killer.getName());
					plugin.getStatsManager().addKillPoints(killer.getName(), 1);
				}
				else
					plugin.getWorldManager().broadcast(victim.getWorld(), ChatColor.LIGHT_PURPLE + victim.getName() + ChatColor.RED + " är ute ur spelet!");
				
				// Ta bort spelaren och ge honom en dödspoäng
				plugin.getPlayerManager().removePlayer(victim.getWorld().getName(), victim.getName());
				plugin.getStatsManager().addDeathPoints(victim.getName(), 1);
				
				// Finns det en vinnare?
				Player winner = plugin.getPlayerManager().getWinner(victim.getWorld());
				if(winner != null) {
					
					plugin.getWorldManager().broadcast(winner.getWorld(), "Spelet är över!");
					plugin.getWorldManager().resetWorld(winner.getWorld());
					
					plugin.getStatsManager().addWinPoints(winner.getName(), 1);
				}
			}
		}
	}
}
