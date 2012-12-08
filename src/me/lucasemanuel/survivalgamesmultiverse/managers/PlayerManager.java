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

import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;

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
	
	// The second concurrent map in the values is only there to prevent CME's we where getting with the HashSet
	private ConcurrentHashMap<String, ConcurrentHashMap<Player, Boolean>> playerlists;
	
	public PlayerManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "PlayerManager");
		
		playerlists = new ConcurrentHashMap<String, ConcurrentHashMap<Player, Boolean>>();
		
		logger.debug("Initiated");
	}
	
	public synchronized void addWorld(String worldname) {
		logger.debug("Adding world - " + worldname);
		playerlists.put(worldname, new ConcurrentHashMap<Player, Boolean>());
	}

	public synchronized void addPlayer(String worldname, final Player player) {
		
		if(playerlists.containsKey(worldname)) {
			ConcurrentHashMap<Player, Boolean> playerlist = playerlists.get(worldname);
			
			playerlist.put(player, true);
			
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					resetPlayer(player);
				}
			}, 20L);
			
			logger.debug("Added - " + player.getName() + " - to world - " + worldname);
		}
		else
			logger.warning("Error! Tried to add player to non existing worldname! - " + worldname);
	}

	@SuppressWarnings("deprecation")
	private synchronized void resetPlayer(Player player) {
		
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

	public synchronized boolean isInGame(Player player) {
		
		for(ConcurrentHashMap<Player, Boolean> playerlist : playerlists.values()) {
			if(playerlist.containsKey(player))
				return true;
		}
		
		return false;
	}

	public synchronized void removePlayer(String worldname, Player player) {
		
		if(playerlists.containsKey(worldname)) {
			
			ConcurrentHashMap<Player, Boolean> playerlist = playerlists.get(worldname);
			
			if(playerlist.remove(player) == false)
				logger.debug("Tried to remove player from world where he was not listed! Worldname = " + worldname + " - Playername = " + player.getName());
		}
		else
			logger.warning("Tried to remove player '" + player.getName() + "' from incorrect world '" + worldname + "'!");
	}

	public synchronized boolean isGameOver(World world) {
		
		Player[] playerlist = getPlayerList(world.getName());
		
		if(playerlist != null && playerlist.length <= 1) {
			return true;
		}
		
		return false;
	}

	public synchronized Player getWinner(World world) {
		
		if(isGameOver(world)) {
			
			Player[] playerlist = getPlayerList(world.getName());
			
			if(playerlist != null && playerlist.length == 1) {
				return playerlist[0];
			}
		}
		
		return null;
	}

	public synchronized int getPlayerAmount(String worldname) {
		
		if(playerlists.containsKey(worldname)) {
			return playerlists.get(worldname).keySet().size();
		}
		
		return 0;
	}

	private synchronized void clearList(String worldname) {
		playerlists.get(worldname).clear();
	}

	public synchronized void killAndClear(String worldname) {
		
		logger.debug("Initated killAndClear on world: " + worldname);
		
		Iterator<Player> playerlist = playerlists.get(worldname).keySet().iterator();
			
		while(playerlist.hasNext()) {
			
			Player player = playerlist.next();
			
			if(player != null) {
				resetPlayer(player);
				player.setHealth(0);
			}
			else
				logger.warning("Tried to reset/kill null player!");
		}
		
		clearList(worldname);
	}

	public synchronized Player[] getPlayerList(String worldname) {
		if(playerlists.contains(worldname))
			return (Player[]) playerlists.get(worldname).keySet().toArray();
		else
			return null;
	}
}
