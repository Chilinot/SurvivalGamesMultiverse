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
	
	// Key = Worldname , Value = TaskID || There should only be one task per world
	private HashMap<String, Integer> tasks;
	
	public StatusManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "StatusManager");
		
		worlds = new HashMap<String, Boolean>();
		tasks  = new HashMap<String, Integer>();
		
		logger.debug("Initiated");
	}
	
	public void addWorld(String worldname) {
		worlds.put(worldname, false);
		tasks.put(worldname, -1); // -1 means no task
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
		
		if(worlds.containsKey(worldname) && tasks.get(worldname) == -1) {
			
			final CountDown info = new CountDown(worldname);
			
			info.setTaskID(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
				public void run() {
					countDown(info);
				}
			}, 20L, 200L));
			
			tasks.put(worldname, info.getTaskID());
			
			logger.debug("Started task for startCountDown in world: " + worldname + " :: taskID - " + info.getTaskID());
		}
	}
	
	public void startPlayerCheck(String worldname) {
		
		if(worlds.containsKey(worldname) && tasks.get(worldname) == -1) {
			
			final PlayerCheck info = new PlayerCheck(worldname);
			
			info.setTaskID(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
				public void run() {
					playerCheck(info);
				}
			}, 20L, 200L));
			
			tasks.put(worldname, info.getTaskID());
			
			logger.debug("Started task for startPlayerCheck in world: " + worldname + " :: taskID - " + info.getTaskID());
		}
	}
	
	private void playerCheck(PlayerCheck info) {
		
		String worldname = info.getWorldname();
		int taskID = info.getTaskID();
		
		int playeramount = plugin.getPlayerManager().getPlayerAmount(worldname);
		
		logger.debug("PlayerCheck() called for world: " + worldname + " :: taskID - " + taskID + " :: Playeramount - " + playeramount);
		
		if(playeramount >= 2) {
			plugin.getServer().getScheduler().cancelTask(taskID);
			tasks.put(worldname, -1);
			startCountDown(worldname);
		}
		else if(playeramount == 0) {
			logger.debug("Cancelling task: " + taskID);
			plugin.getServer().getScheduler().cancelTask(taskID);
			tasks.put(worldname, -1);
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
				tasks.put(worldname, -1);
			}
			
			else if(info.getStarted10() == false) {
				
				logger.debug("Starting 1s countdown for world: " + worldname);
				
				plugin.getServer().getScheduler().cancelTask(taskID);
				tasks.put(worldname, -1);
				
				info.setStarted10();
				
				info.setTaskID(plugin.getServer().getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable() {
					public void run() {
						countDown(info);
					}
				}, 20L, 20L));
				
				tasks.put(worldname, info.getTaskID());
			}
		}
		
		if((timeToWait - timepassed) > 0)
			plugin.getWorldManager().broadcast(Bukkit.getWorld(worldname), (timeToWait - timepassed) + " " + plugin.getLanguageManager().getString("timeleft"));
	}

	public void reset(String worldname) {
		
		logger.debug("Resetting world: " + worldname);
		
		if(tasks.get(worldname) != -1) {
			plugin.getServer().getScheduler().cancelTask(tasks.get(worldname));
			tasks.put(worldname, -1);
		}
		
		setStatus(worldname, false);
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