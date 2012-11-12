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

import java.util.HashMap;
import java.util.HashSet;

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
	
	private HashMap<String, HashSet<String>> playerlists;
	
	public PlayerManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "PlayerManager");
		
		playerlists = new HashMap<String, HashSet<String>>();
		
		logger.debug("Initiated");
	}
	
	public void addWorld(String worldname) {
		logger.debug("Adding world - " + worldname);
		playerlists.put(worldname, new HashSet<String>());
	}

	public void addPlayer(String worldname, final Player player) {
		
		if(playerlists.containsKey(worldname)) {
			HashSet<String> playerlist = playerlists.get(worldname);
			
			playerlist.add(player.getName());
			
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
		
		for(HashSet<String> playerlist : playerlists.values()) {
			if(playerlist.contains(playername))
				return true;
		}
		
		return false;
	}

	public void removePlayer(String worldname, String name) {
		
		if(playerlists.containsKey(worldname)) {
			if(playerlists.get(worldname).remove(name) == false)
				logger.debug("Tried to remove player from world where he was not listed! Worldname = " + worldname + " - Playername = " + name);
		}
		else
			logger.warning("Tried to remove player '" + name + "' from incorrect world '" + worldname + "'!");
	}

	public boolean isGameOver(World world) {
		
		HashSet<String> playerlist = playerlists.get(world.getName());
		
		if(playerlist != null && playerlist.size() <= 1) {
			return true;
		}
		
		return false;
	}

	public String getWinner(World world) {
		
		if(isGameOver(world)) {
			
			HashSet<String> playerlist = playerlists.get(world.getName());
			
			if(playerlist != null && playerlist.isEmpty() == false) {
				return (String) playerlist.toArray()[0];
			}
		}
		
		return null;
	}

	public int getPlayerAmount(String worldname) {
		
		if(playerlists.containsKey(worldname)) {
			return playerlists.get(worldname).size();
		}
		
		return 0;
	}

	private void clearList(String worldname) {

		HashSet<String> playerlist = playerlists.get(worldname);
		
		if(playerlist != null)
			playerlist.clear();
		
	}

	public void killAndClear(String worldname) {
		
		HashSet<String> playerlist = playerlists.get(worldname);
		
		for(String playername : playerlist) {
			Bukkit.getPlayerExact(playername).setHealth(0);
		}
		
		clearList(worldname);
	}

	public HashSet<String> getPlayerList(String worldname) {
		return playerlists.get(worldname);
	}
}
