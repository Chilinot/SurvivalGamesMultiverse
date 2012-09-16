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

	private void startCountDown(String worldname) {
		
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
		
		
		
	}
	
	private void countDown(CountDown info) {
		
		String worldname = info.getWorldname();
		long timeOfInitiation = info.getStartTime();
		
		logger.debug("CountDown() called for world: " + worldname + " :: TaskID - " + info.getTaskID());
		
		int timeToWait = plugin.getConfig().getInt("timeoutTillStart");
		
		int timeleft = (int) ((System.currentTimeMillis() - timeOfInitiation) / 1000);
		
		if(timeleft >= timeToWait) {
			setStatus(worldname, true);
			plugin.getWorldManager().broadcast(Bukkit.getWorld(worldname), ChatColor.GOLD + plugin.getLanguageManager().getString("gamestarted"));
			plugin.getServer().getScheduler().cancelTask(info.getTaskID());
		}
		else
			plugin.getWorldManager().broadcast(Bukkit.getWorld(worldname), (timeToWait - timeleft) + " " + plugin.getLanguageManager().getString("timeleft"));
	}
}

class CountDown {
	
	private final String worldname;
	private final long timeOfInitiation;
	
	private int taskID = 0;
	
	public CountDown(String worldname) {
		this.worldname = worldname;
		this.timeOfInitiation = System.currentTimeMillis();
	}
	
	public synchronized String getWorldname() {
		return worldname;
	}
	
	public synchronized long getStartTime() {
		return timeOfInitiation;
	}
	
	public synchronized void setTaskID(int newID) {
		this.taskID = newID;
	}
	
	public synchronized int getTaskID() {
		return taskID;
	}
}