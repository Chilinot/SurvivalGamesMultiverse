/**
 *  Name: PlayerManager.java
 *  Date: 20:44:47 - 8 sep 2012
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

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.util.HashMap;
import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Player;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class PlayerManager {
	
	//TODO freeze players when they join
	//TODO have a countdown unfreeze the players after a configurable time
	//TODO teleport players to arena after configurable time
	
	private ConsoleLogger logger;
	
	private HashMap<String, HashSet<String>> playerlists;
	
	public PlayerManager(Main instance) {
		logger = new ConsoleLogger(instance, "PlayerManager");
		
		playerlists = new HashMap<String, HashSet<String>>();
		
		logger.debug("Initiated");
	}
	
	public void addWorld(String worldname) {
		logger.debug("Adding world - " + worldname);
		playerlists.put(worldname, new HashSet<String>());
	}

	public void addPlayer(String worldname, String playername) {
		
		if(playerlists.containsKey(worldname)) {
			HashSet<String> playerlist = playerlists.get(worldname);
			
			playerlist.add(playername);
			
			logger.debug("Added - " + playername + " - to world - " + worldname);
		}
		else
			logger.debug("Error! Tried to add player to non existing worldname! - " + worldname);
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
			logger.debug("Tried to remove player '" + name + "' from incorrect world '" + worldname + "'!");
	}

	public Player getWinner(World world) {
		
		if(playerlists.containsKey(world.getName())) {
			
			HashSet<String> playerlist = playerlists.get(world.getName());
			if(playerlist.size() == 1) {
				return Bukkit.getPlayer((String)playerlist.toArray()[0]);
			}
		}
		
		return null;
	}
}
