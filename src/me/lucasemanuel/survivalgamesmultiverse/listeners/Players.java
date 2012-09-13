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
import me.lucasemanuel.survivalgamesmultiverse.managers.PlayerManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.StatsManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.WorldManager;
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
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
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
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		Player victim = event.getEntity();
		
		if(plugin.getWorldManager().isWorld(victim.getWorld())) {
			
			PlayerManager playermanager = plugin.getPlayerManager();
			
			// Blocka alla meddelanden i SG världarna
			event.setDeathMessage(null);
			
			if(playermanager.isInGame(victim.getName())) {
				
				WorldManager worldmanager = plugin.getWorldManager();
				StatsManager statsmanager = plugin.getStatsManager();
				
				// Dödades spelaren utav en annan spelare eller dog han naturligt?
				Player killer = event.getEntity().getKiller();
				
				if(killer != null) {
					worldmanager.broadcast(victim.getWorld(), ChatColor.LIGHT_PURPLE + victim.getName() + ChatColor.RED + " dödades utav " + ChatColor.BLUE + killer.getName());
					statsmanager.addKillPoints(killer.getName(), 1);
				}
				else
					worldmanager.broadcast(victim.getWorld(), ChatColor.LIGHT_PURPLE + victim.getName() + ChatColor.RED + " är ute ur spelet!");
				
				// Ta bort spelaren och ge honom en dödspoäng
				playermanager.removePlayer(victim.getWorld().getName(), victim.getName());
				worldmanager.sendPlayerToSpawn(victim);
				statsmanager.addDeathPoints(victim.getName(), 1);
				
				// Är spelet slut?
				if(playermanager.isGameOver(victim.getWorld())) {
					
					// Har vi en vinnare?
					String winner = playermanager.getWinner(victim.getWorld());
					if(winner != null) {
						worldmanager.broadcast(victim.getWorld(), ChatColor.LIGHT_PURPLE + winner + ChatColor.WHITE + " vann spelet!");
						statsmanager.addWinPoints(winner, 1);
					}
					
					// Skicka ut ett meddelande till alla spelare i världen att spelet är slut.
					worldmanager.broadcast(victim.getWorld(), "Spelet är över!");
					
					// Återställ världen
					worldmanager.resetWorld(victim.getWorld());
				}
			}
		}
	}
}
