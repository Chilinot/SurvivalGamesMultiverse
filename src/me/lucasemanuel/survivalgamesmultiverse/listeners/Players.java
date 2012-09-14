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
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

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
						player.sendMessage(ChatColor.GOLD + plugin.getLanguageManager().getString("youJoinedTheGame"));
						
						plugin.getWorldManager().broadcast(block.getWorld(), ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.WHITE + " " + plugin.getLanguageManager().getString("playerJoinedGame"));
					}
					else
						player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("alreadyPlaying"));
				}
			}
			
			else if(block.getType().equals(Material.CHEST)) {
				plugin.getChestManager().randomizeChest((Chest)block.getState());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerRespawn(PlayerRespawnEvent event) {
		
		if(plugin.getWorldManager().isWorld(event.getRespawnLocation().getWorld())) {
			event.setRespawnLocation(event.getRespawnLocation().getWorld().getSpawnLocation());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		if(plugin.getWorldManager().isWorld(event.getPlayer().getWorld())) {
			event.getPlayer().teleport(event.getPlayer().getWorld().getSpawnLocation());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		if(plugin.getWorldManager().isWorld(player.getWorld()) && plugin.getPlayerManager().isInGame(player.getName())) {
			
			String message = ChatColor.RED + "[SGAnti-Cheat]" + ChatColor.WHITE + " :: " + ChatColor.BLUE + player.getName() + ChatColor.WHITE + " - " + plugin.getLanguageManager().getString("anticheatRemoval");
			
			plugin.getPlayerManager().removePlayer(player.getWorld().getName(), player.getName());
			plugin.getWorldManager().broadcast(player.getWorld(), message);
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		Player victim = event.getEntity();
		
		if(plugin.getWorldManager().isWorld(victim.getWorld())) {
			
			PlayerManager playermanager = plugin.getPlayerManager();
			
			// Block all deathmessages in the SG worlds
			event.setDeathMessage(null);
			
			if(playermanager.isInGame(victim.getName())) {
				
				WorldManager worldmanager = plugin.getWorldManager();
				StatsManager statsmanager = plugin.getStatsManager();
				
				// Was this player killed by another player?
				Player killer = event.getEntity().getKiller();
				
				if(killer != null) {
					worldmanager.broadcast(victim.getWorld(), ChatColor.LIGHT_PURPLE + victim.getName() + ChatColor.RED + " " + plugin.getLanguageManager().getString("wasKilledBy") + " " + ChatColor.BLUE + killer.getName());
					statsmanager.addKillPoints(killer.getName(), 1);
				}
				else
					worldmanager.broadcast(victim.getWorld(), ChatColor.LIGHT_PURPLE + victim.getName() + ChatColor.RED + " " + plugin.getLanguageManager().getString("isOutOfTheGame"));
				
				// Remove the player and give him one deathpoint
				playermanager.removePlayer(victim.getWorld().getName(), victim.getName());
				worldmanager.sendPlayerToSpawn(victim);
				statsmanager.addDeathPoints(victim.getName(), 1);
				
				// Is the game over?
				if(playermanager.isGameOver(victim.getWorld())) {
					
					// Broadcast a message to all players in that world that the game is over.
					worldmanager.broadcast(victim.getWorld(), plugin.getLanguageManager().getString("gameover"));
					
					// Do we have a winner?
					String winner = playermanager.getWinner(victim.getWorld());
					if(winner != null) {
						worldmanager.broadcast(victim.getWorld(), ChatColor.LIGHT_PURPLE + winner + ChatColor.WHITE + " " + plugin.getLanguageManager().getString("wonTheGame"));
						statsmanager.addWinPoints(winner, 1);
					}
					
					// Reset the world
					worldmanager.resetWorld(victim.getWorld());
				}
			}
		}
	}
}
