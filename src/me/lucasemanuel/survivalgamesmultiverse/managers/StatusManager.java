/**
 *  Name: StatusManager.java
 *  Date: 23:58:26 - 15 sep 2012
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

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class StatusManager {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	// Key = worldname, Value = gamestatus / true = started
	private HashMap<String, Boolean> worlds;
	
	public StatusManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "StatusManager");
		
		worlds = new HashMap<String, Boolean>();
		
		logger.debug("Initiated");
	}
	
	public void addWorld(String worldname) {
		worlds.put(worldname, false);
	}

	public boolean setStatus(String worldname, boolean value) {
		if(worlds.containsKey(worldname)) {
			worlds.put(worldname, value);
			return true;
		}
		else
			return false;
	}
	
	public boolean getStatus(String worldname) {
		return worlds.get(worldname);
	}

	public void startCountDown(String worldname) {
		
		if(worlds.containsKey(worldname)) {
			
			final CountDown info = new CountDown(worldname);
			
			info.setTaskID(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
				public void run() {
					countDown(info);
				}
			}, 20L, 200L));
		}
	}
	
	public void startPlayerCheck(String worldname) {
		
		if(worlds.containsKey(worldname)) {
			
			final PlayerCheck info = new PlayerCheck(worldname);
			
			info.setTaskID(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					playerCheck(info);
				}
			}, 20L, 200L));
		}
	}
	
	private void playerCheck(PlayerCheck info) {
		
		String worldname = info.getWorldname();
		int taskID = info.getTaskID();
		
		logger.debug("PlayerCheck() called for world: " + worldname + " :: taskID - " + taskID);
		
		if(plugin.getPlayerManager().getPlayerAmount(worldname) >= 2) {
			startCountDown(worldname);
			plugin.getServer().getScheduler().cancelTask(taskID);
		}
		else
			plugin.getWorldManager().broadcast(Bukkit.getWorld(worldname), ChatColor.LIGHT_PURPLE + plugin.getLanguageManager().getString("waitingForPlayers"));
	}
	
	private void countDown(final CountDown info) {
		
		String worldname = info.getWorldname();
		long timeOfInitiation = info.getStartTime();
		
		int taskID = info.getTaskID();
		
		logger.debug("CountDown() called for world: " + worldname + " :: TaskID - " + taskID);
		
		int timeToWait = plugin.getConfig().getInt("timeoutTillStart");
		
		int timepassed = (int) ((System.currentTimeMillis() - timeOfInitiation) / 1000);
		
		if(timepassed >= (timeToWait - 12)) {
			if(timepassed >= timeToWait && info.getStarted10() == true) {
				setStatus(worldname, true);
				plugin.getWorldManager().broadcast(Bukkit.getWorld(worldname), ChatColor.GOLD + plugin.getLanguageManager().getString("gamestarted"));
				plugin.getServer().getScheduler().cancelTask(taskID);
			}
			else if(info.getStarted10() == false) {
				plugin.getServer().getScheduler().cancelTask(taskID);
				
				info.setStarted10();
				
				info.setTaskID(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
					public void run() {
						countDown(info);
					}
				}, 20L, 20L));
			}
		}
		
		if((timeToWait - timepassed) > 0)
			plugin.getWorldManager().broadcast(Bukkit.getWorld(worldname), (timeToWait - timepassed) + " " + plugin.getLanguageManager().getString("timeleft"));
	}
}

// Some small objects to keep track of task id's and what worlds they are working with.

class PlayerCheck {
	
	private final String worldname;
	private int taskID = -1;
	
	public PlayerCheck(String worldname) {
		this.worldname = worldname;
	}
	
	public synchronized String getWorldname() {
		return this.worldname;
	}
	
	public synchronized void setTaskID(int newID) {
		this.taskID = newID;
	}
	
	public synchronized int getTaskID() {
		return this.taskID;
	}
}

class CountDown {
	
	private final String worldname;
	private final long timeOfInitiation;
	
	private boolean started10;
	
	private int taskID = -1;
	
	public CountDown(String worldname) {
		this.worldname = worldname;
		this.timeOfInitiation = System.currentTimeMillis();
		
		started10 = false;
	}
	
	public synchronized String getWorldname() {
		return this.worldname;
	}
	
	public synchronized long getStartTime() {
		return this.timeOfInitiation;
	}
	
	public synchronized void setTaskID(int newID) {
		this.taskID = newID;
	}
	
	public synchronized int getTaskID() {
		return this.taskID;
	}
	
	public synchronized void setStarted10() {
		this.started10 = true;
	}
	
	public synchronized boolean getStarted10() {
		return this.started10;
	}
}