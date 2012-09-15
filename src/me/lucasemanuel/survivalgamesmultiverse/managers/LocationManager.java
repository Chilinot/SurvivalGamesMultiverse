/**
 *  Name: LocationManager.java
 *  Date: 19:56:01 - 15 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
 *  
 *  Manages all locations for all gameworlds.
 *  
 * 
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.util.HashMap;
import java.util.Map.Entry;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class LocationManager {
	
	private ConsoleLogger logger;
	
	// Key = Worldname, Value = Main/Arena lists, ValueOfValue = key=location, boolean=true means the location is available
	private HashMap<String, HashMap<String, HashMap<Location, Boolean>>> locations;
	
	public LocationManager(Main instance) {
		logger = new ConsoleLogger(instance, "LocationManager");
		
		locations = new HashMap<String, HashMap<String, HashMap<Location, Boolean>>>();
		
		logger.debug("Initiated");
	}
	
	public void addWorld(String worldname) {
		locations.put(worldname, new HashMap<String, HashMap<Location, Boolean>>());
	}
	
	public void resetLocationStatuses(World world) {
		
		String worldname = world.getName();
		
		HashMap<Location, Boolean> main  = locations.get(worldname).get("main");
		HashMap<Location, Boolean> arena = locations.get(worldname).get("arena");
		
		if(main != null) 
			for(Entry<Location, Boolean> entry : main.entrySet()) {
				entry.setValue(true);
			}
		
		if(arena != null)
			for(Entry<Location, Boolean> entry : arena.entrySet()) {
				entry.setValue(true);
			}
	}
	
	public boolean tpToStart(Player player) {
		
		HashMap<Location, Boolean> locationlist = locations.get(player.getWorld().getName()).get("main");
		
		if(locationlist == null) {
			logger.severe("No saved locations for world: " + player.getWorld().getName());
			return false;
		}
		
		for(Entry<Location, Boolean> entry : locationlist.entrySet()) {
			if(entry.getValue()) {
				
				player.teleport(entry.getKey());
				entry.setValue(false);
				
				return true;
			}
		}
		
		logger.debug("No locations left for world: " + player.getWorld().getName());
		return false;
	}
	
	public void addLocation(String type, Location location) {
		
		logger.debug("Adding locationtype: " + type + ", to world: " + location.getWorld().getName());
		
		HashMap<Location, Boolean> map = locations.get(location.getWorld().getName()).get(type);
		
		if(map == null) {
			map = new HashMap<Location, Boolean>();
			locations.get(location.getWorld().getName()).put(type, map);
		}
		
		map.put(location, true);
	}
}
