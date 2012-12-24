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

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.potion.PotionEffect;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class PlayerManager {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	private ConcurrentHashMap<String, PlayerList> playerlists;
	
	public PlayerManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "PlayerManager");
		
		playerlists = new ConcurrentHashMap<String, PlayerList>();
		
		logger.debug("Initiated");
	}
	
	public synchronized void addWorld(String worldname) {
		logger.debug("Adding world - " + worldname);
		playerlists.put(worldname, new PlayerList());
	}

	public synchronized void addPlayer(String worldname, final Player player) {
		
		if(playerlists.containsKey(worldname)) {
			PlayerList playerlist = playerlists.get(worldname);
			
			playerlist.addPlayer(player);
			
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					resetPlayer(player);
				}
			}, 5L);
			
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
			
			// Doesn't work without this!
			player.updateInventory();
		}
		
		for(PotionEffect potion : player.getActivePotionEffects()) {
			player.removePotionEffect(potion.getType());
		}
		
		player.setHealth(20);
		player.setFoodLevel(20);
		player.setLevel(0);
		player.setTotalExperience(0);
	}

	public synchronized boolean isInGame(Player player) {
		
		for(PlayerList playerlist : playerlists.values()) {
			if(playerlist.containsPlayer(player))
				return true;
		}
		
		return false;
	}

	public synchronized void removePlayer(String worldname, Player player) {
		
		if(playerlists.containsKey(worldname)) {
			
			PlayerList playerlist = playerlists.get(worldname);
			
			if(playerlist.removePlayer(player) == false)
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
			return playerlists.get(worldname).getAmountOfPlayers();
		}
		
		return 0;
	}

	private synchronized void clearList(String worldname) {
		playerlists.get(worldname).clear();
	}

	public synchronized void killAndClear(String worldname) {
		
		logger.debug("Initated killAndClear on world: " + worldname);
		
		Player[] playerlist = playerlists.get(worldname).toArray();
		
		for(Player player : playerlist) {
			
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
		
		logger.debug("getPlayerList called for world: " + worldname);
		
		if(playerlists.containsKey(worldname)) {
			logger.debug("Returning player array.");
			return playerlists.get(worldname).toArray();
		}
		else {
			logger.debug("Returning null!");
			return null;
		}
	}
}

class PlayerList {
	
	private Set<Player> players;
	
	public PlayerList() {
		players = new HashSet<Player>();
	}
	
	public synchronized boolean containsPlayer(Player player) {
		return players.contains(player);
	}

	public synchronized boolean addPlayer(Player player) {
		return players.add(player);
	}
	
	public synchronized boolean removePlayer(Player player) {
		return players.remove(player);
	}
	
	public synchronized int getAmountOfPlayers() {
		return players.size();
	}
	
	public synchronized void clear() {
		players.clear();
	}
	
	// It didn't let me use the set.toArray() and parse like (Player[]) set.toArray()
	public synchronized Player[] toArray() {
		
		Player[] array = new Player[players.size()];
		
		int i = 0;
		for(Player player : players) {
			array[i] = player;
			i++;
		}
		
		return array;
	}
}
