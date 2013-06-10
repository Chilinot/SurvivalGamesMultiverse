/**
 *  Name: Players.java
 *  Date: 08:16:34 - 10 sep 2012
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
 *  Eventlistener concerning player related events.
 *  
 */

package me.lucasemanuel.survivalgamesmultiverse.listeners;

import java.util.ArrayList;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.managers.PlayerManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.StatsManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.WorldManager;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent;
import org.bukkit.event.entity.EntityRegainHealthEvent.RegainReason;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType.SlotType;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class Players implements Listener {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	public Players(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "PlayerListener");
		
		logger.debug("Initiated");
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onInventoryClick(InventoryClickEvent event) {
		
		Player player = (Player) event.getWhoClicked();
		
		if(plugin.getWorldManager().isGameWorld(player.getWorld())
				&& plugin.getPlayerManager().isInGame(player)
				&& plugin.getConfig().getBoolean("halloween.forcepumpkin")
				&& !player.hasPermission("survivalgames.ignore.forcepumpkin")
				&& event.getSlotType().equals(SlotType.ARMOR)
				&& event.getCurrentItem().getType().equals(Material.PUMPKIN)) {
			
			player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("forcedPumpkin"));
			event.setCancelled(true);
		}
	}
	
	@SuppressWarnings("unchecked")
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
		
		ArrayList<String> allowedcommands = (ArrayList<String>) plugin.getConfig().getList("allowedCommandsInGame");
		
		Player player  = event.getPlayer();
		String command = event.getMessage();
		
		if(plugin.getPlayerManager().isInGame(player)
				&& plugin.getWorldManager().isGameWorld(player.getWorld())
				&& !allowedcommands.contains(command)
				&& !player.hasPermission("survivalgames.ignore.commandfilter")) {
			
			event.setCancelled(true);
			event.getPlayer().sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("blockedCommand"));
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		
		Player   player = event.getPlayer();
		Location from   = event.getFrom();
		Location to     = event.getTo();
		
		if(plugin.getWorldManager().isGameWorld(from.getWorld()) && !plugin.getWorldManager().isGameWorld(to.getWorld()) && plugin.getPlayerManager().isInGame(player)) {
			
			logger.debug("Removing player " + player.getName() + " due to teleportation!");
			
			plugin.getPlayerManager().removePlayer(from.getWorld().getName(), player);
			
			String message = ChatColor.RED + "[SGAnti-Cheat]"
					+ ChatColor.WHITE + " :: " 
					+ ChatColor.BLUE + player.getName() 
					+ ChatColor.WHITE + " - " 
					+ plugin.getLanguageManager().getString("anticheat.teleported");
			
			plugin.getWorldManager().broadcast(from.getWorld(), message);
			
			plugin.getSignManager().updateSigns();
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerInteract(PlayerInteractEvent event) {
		
		Block  block  = event.getClickedBlock();
		Player player = event.getPlayer();
		
		if(event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
			if(block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN)) {
				
				Sign sign = (Sign) block.getState();
				
				/* 
				 *  ---- Join sign
				 */
				
				String worldname = plugin.getSignManager().getGameworldName(sign);
				
				if(worldname != null && plugin.getWorldManager().isGameWorld(Bukkit.getWorld(worldname))) {
					if(plugin.getStatusManager().getStatusFlag(worldname) == 0) {
						if(plugin.getPlayerManager().isInGame(player) == false) {
							if(plugin.getLocationManager().tpToStart(player, worldname)) {
								
								plugin.getPlayerManager().addPlayer(worldname, player);
								
								plugin.getStatsManager().checkAndAddScoreboard(player.getName());
								
								player.sendMessage(ChatColor.GOLD + plugin.getLanguageManager().getString("youJoinedTheGame"));
								plugin.getWorldManager().broadcast(Bukkit.getWorld(worldname), ChatColor.LIGHT_PURPLE + player.getName() + ChatColor.WHITE + " " + plugin.getLanguageManager().getString("playerJoinedGame"));
								
								plugin.getSignManager().updateSigns();
								
								// Now we have to wait for more players!
								if(plugin.getPlayerManager().getPlayerAmount(worldname) == 1)
									plugin.getStatusManager().startPlayerCheck(worldname);
							}
							else
								player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("gameIsFull"));
						}
						else
							player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("alreadyPlaying"));
					}
					else if(plugin.getStatusManager().getStatusFlag(worldname) == 1)
						player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("gameHasAlreadyStarted"));
					else if(plugin.getStatusManager().getStatusFlag(worldname) == 2)
						player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("Join_Blocked_Frozen"));
				}
				
				/*
				 *  ---- Sign Registration
				 */
				
				else if(sign.getLine(0).equalsIgnoreCase("[sginfo]") && player.hasPermission("survivalgames.signs.sginfo")) {
					plugin.getSignManager().registerSign(sign);
				}
			}
			
			/*
			 *  ---- Chest logging
			 */
			
			else if(block.getType().equals(Material.CHEST) && plugin.getWorldManager().isGameWorld(block.getWorld())) {
				plugin.getChestManager().randomizeChest((Chest)block.getState());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerRespawn(final PlayerRespawnEvent event) {
		
		if(plugin.getWorldManager().isGameWorld(event.getRespawnLocation().getWorld())) {
			
			// The players should be teleported directly after respawn instead of resetting the respawnlocation
			// to the desired value. This is to make sure that the player shifts worlds correctly and ensure
			// that any multiverse monitor like MultiInv catches the world change correctly.
			
			final Location lobby = Bukkit.getWorld(plugin.getConfig().getString("lobbyworld")).getSpawnLocation();
			
			this.plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					event.getPlayer().teleport(lobby);
				}
			});
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerJoin(PlayerJoinEvent event) {
		
		if(plugin.getWorldManager().isGameWorld(event.getPlayer().getWorld())) {
			event.getPlayer().teleport(Bukkit.getWorld(plugin.getConfig().getString("lobbyworld")).getSpawnLocation());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerQuit(PlayerQuitEvent event) {
		
		Player player = event.getPlayer();
		
		if(plugin.getWorldManager().isGameWorld(player.getWorld()) && plugin.getPlayerManager().isInGame(player)) {
			
			String message = ChatColor.RED + "[SGAnti-Cheat]" 
					+ ChatColor.WHITE + " :: " 
					+ ChatColor.BLUE + player.getName() 
					+ ChatColor.WHITE + " - " 
					+ plugin.getLanguageManager().getString("anticheat.disconnect");
			
			plugin.getPlayerManager().removePlayer(player.getWorld().getName(), player);
			plugin.getWorldManager().broadcast(player.getWorld(), message);
			
			plugin.getSignManager().updateSigns();
			
			// Is the game over?
			plugin.gameover(player.getWorld());
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerDeath(PlayerDeathEvent event) {
		
		Player victim = event.getEntity();
		
		if(plugin.getWorldManager().isGameWorld(victim.getWorld())) {
			
			PlayerManager playermanager = plugin.getPlayerManager();
			
			// Block all deathmessages in the SG worlds
			event.setDeathMessage(null);
			
			if(playermanager.isInGame(victim)) {
				
				WorldManager worldmanager = plugin.getWorldManager();
				StatsManager statsmanager = plugin.getStatsManager();
				
				// Was this player killed by another player?
				Player killer = event.getEntity().getKiller();
				
				if(killer != null) {
					worldmanager.broadcast(victim.getWorld(), ChatColor.LIGHT_PURPLE + victim.getName() + ChatColor.RED + " " + plugin.getLanguageManager().getString("wasKilledBy") + " " + ChatColor.BLUE + killer.getName());
					if(!killer.hasPermission("survivalgames.ignore.stats")) statsmanager.addKillPoints(killer.getName(), 1, true);
				}
				else
					worldmanager.broadcast(victim.getWorld(), ChatColor.LIGHT_PURPLE + victim.getName() + ChatColor.RED + " " + plugin.getLanguageManager().getString("isOutOfTheGame"));
				
				// Remove the player and give him one deathpoint
				playermanager.removePlayer(victim.getWorld().getName(), victim);
				
				if(!victim.hasPermission("survivalgames.ignore.stats")) statsmanager.addDeathPoints(victim.getName(), 1, true);
				
				plugin.getStatsManager().removeScoreboard(victim.getName());
				
				plugin.getSignManager().updateSigns();
				
				// Is the game over?
				plugin.gameover(victim.getWorld());
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onPlayerMove(PlayerMoveEvent event) {
		
		Player player = event.getPlayer();
		
		// If it is a SG world and the game hasnt started and the player is in the game
		if(plugin.getWorldManager().isGameWorld(player.getWorld())) {
			
			if(plugin.getPlayerManager().isInGame(player)) {
				if(plugin.getStatusManager().getStatusFlag(player.getWorld().getName()) == 0) {
					
					double fromX = event.getFrom().getX();
					double fromZ = event.getFrom().getZ();
					
					double toX   = event.getTo().getX();
					double toZ   = event.getTo().getZ();
					
					if(fromX != toX && fromZ != toZ) {
						player.sendMessage(ChatColor.RED + plugin.getLanguageManager().getString("blockedMovement"));
						player.teleport(event.getFrom());
					}
				}
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onEntityDamage(EntityDamageEvent event) {
		
		if(event.getEntity() instanceof Player) {
			
			Player player = (Player) event.getEntity();
			
			if(plugin.getWorldManager().isGameWorld(player.getWorld())
					&& plugin.getPlayerManager().isInGame(player)
					&& plugin.getStatusManager().getStatusFlag(player.getWorld().getName()) == 0) {
				
				event.setCancelled(true);
			}
		}
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onEntityRegainHealth(EntityRegainHealthEvent event) {
		if(event.getEntity() instanceof Player) {
			Player p = (Player) event.getEntity();
			
			if(plugin.getWorldManager().isGameWorld(p.getWorld()) 
					&& event.getRegainReason().equals(RegainReason.SATIATED)
					&& !plugin.getWorldManager().allowHealthRegen(p.getWorld())) {
				event.setCancelled(true);
			}
		}
	}
}
