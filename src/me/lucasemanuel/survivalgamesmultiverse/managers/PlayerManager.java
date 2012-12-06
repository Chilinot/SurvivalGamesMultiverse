/**
 *  Name: PlayerManager.java
 *  Date: 20:44:47 - 8 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
 *  
 *  Manages playerlists etc.
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class PlayerManager {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	private ConcurrentHashMap<String, Set<String>> playerlists;
	
	public PlayerManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "PlayerManager");
		
		playerlists = new ConcurrentHashMap<String, Set<String>>();
		
		logger.debug("Initiated");
	}
	
	public void addWorld(String worldname) {
		logger.debug("Adding world - " + worldname);
		playerlists.put(worldname, Collections.synchronizedSet(new HashSet<String>()));
	}

	public void addPlayer(String worldname, final Player player) {
		
		if(playerlists.containsKey(worldname)) {
			Set<String> playerlist = playerlists.get(worldname);
			
			synchronized(playerlist) {
				playerlist.add(player.getName());
			}
			
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					resetPlayer(player);
				}
			}, 60L);
			
			logger.debug("Added - " + player.getName() + " - to world - " + worldname);
		}
		else
			logger.warning("Error! Tried to add player to non existing worldname! - " + worldname);
	}

	@SuppressWarnings("deprecation")
	private void resetPlayer(Player player) {
		
		logger.debug("Resetting player: " + player.getName());
		
		if(!player.hasPermission("survivalgames.ignore.clearinv")){
			PlayerInventory inventory = player.getInventory();
			
			inventory.clear();
			
			inventory.setHelmet(null);
			inventory.setChestplate(null);
			inventory.setLeggings(null);
			inventory.setBoots(null);
		
			
			if(plugin.getConfig().getBoolean("halloween.enabled"))
				player.getInventory().setHelmet(new ItemStack(Material.PUMPKIN));
		}
		
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setTotalExperience(0);
		// Doesn't work without this!
		player.updateInventory();
	}

	public boolean isInGame(String playername) {
		
		for(Set<String> playerlist : playerlists.values()) {
			synchronized(playerlist) {
				if(playerlist.contains(playername))
					return true;
			}
		}
		
		return false;
	}

	public void removePlayer(String worldname, String name) {
		
		if(playerlists.containsKey(worldname)) {
			
			Set<String> playerlist = playerlists.get(worldname);
			
			synchronized(playerlist) {
				if(playerlist.remove(name) == false)
					logger.debug("Tried to remove player from world where he was not listed! Worldname = " + worldname + " - Playername = " + name);
			}
		}
		else
			logger.warning("Tried to remove player '" + name + "' from incorrect world '" + worldname + "'!");
	}

	public boolean isGameOver(World world) {
		
		Set<String> playerlist = playerlists.get(world.getName());
		
		synchronized(playerlist) {
			if(playerlist != null && playerlist.size() <= 1) {
				return true;
			}
			
		}
		
		return false;
	}

	public String getWinner(World world) {
		
		if(isGameOver(world)) {
			
			Set<String> playerlist = playerlists.get(world.getName());
			
			synchronized(playerlist) {
				if(playerlist != null && playerlist.isEmpty() == false) {
					return (String) playerlist.toArray()[0];
				}
			}
		}
		
		return null;
	}

	public int getPlayerAmount(String worldname) {
		
		if(playerlists.containsKey(worldname)) {
			
			Set<String> playerlist = playerlists.get(worldname);
			
			synchronized(playerlist) {
				return playerlist.size();
			}
		}
		
		return 0;
	}

	private void clearList(String worldname) {

		Set<String> playerlist = playerlists.get(worldname);
		
		synchronized(playerlist) {
			playerlist.clear();
		}
	}

	public void killAndClear(String worldname) {
		
		Set<String> playerlist = playerlists.get(worldname);
		
		synchronized(playerlist) {
			
			Iterator<String> i = playerlist.iterator();
			
			while(i.hasNext()) {
				Bukkit.getPlayerExact(i.next()).setHealth(0);
			}
		}
		
		clearList(worldname);
	}

	public Set<String> getPlayerList(String worldname) {
		return playerlists.get(worldname);
	}
}
