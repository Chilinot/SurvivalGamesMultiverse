/**
 *  Name: SignManager.java
 *  Date: 21:35:06 - 9 nov 2012
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
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;
import me.lucasemanuel.survivalgamesmultiverse.utils.SLAPI;
import me.lucasemanuel.survivalgamesmultiverse.utils.SerializedLocation;

public class SignManager {
	
	private final Main plugin;
	private final ConsoleLogger logger;
	
	private HashMap<String, Location> signs;
	
	public SignManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "SignManager");
		
		loadsigns();
		
		logger.debug("Initiated");
	}

	@SuppressWarnings("unchecked")
	private void loadsigns() {
		
		logger.debug("Loading signlocations...");
		
		signs = new HashMap<String, Location>();
		
		HashMap<String, SerializedLocation> tempmap = null;
		
		try {
			tempmap = (HashMap<String, SerializedLocation>) SLAPI.load(plugin.getDataFolder() + "/" + "signlocations.dat");
		}
		catch(Exception e) {
			logger.severe("Error while loading signlocations! Message: " + e.getMessage());
		}
		
		if(tempmap != null)
			for(Entry<String, SerializedLocation> entry : tempmap.entrySet()) {
				signs.put(entry.getKey(), entry.getValue().deserialize());
			}
	}
	
	private void saveSigns() {
		
		logger.debug("Saving signlocations...");
		
		final HashMap<String, SerializedLocation> tempmap = new HashMap<String, SerializedLocation>();
		
		for(Entry<String, Location> entry : signs.entrySet()) {
			tempmap.put(entry.getKey(), new SerializedLocation(entry.getValue()));
		}
		
		Thread thread = new Thread() {
			public void run() {
				
				try {
					SLAPI.save(tempmap, plugin.getDataFolder() + "/" + "signlocations.dat");
				}
				catch(Exception e) {
					logger.severe("Error while saving signlocations! Message: " + e.getMessage());
				}
				
				logger.debug("Finished");
			}
		};
		
		thread.start();
	}

	public void updateSigns(String worldname) {
		updateInfoSign(worldname);
	}

	private void updateInfoSign(String worldname) {
		
		Location location = signs.get(worldname);
		
		if(location != null) {
			
			Sign sign = (Sign) location.getBlock().getState();
			
			String status = plugin.getStatusManager().getStatus(worldname) ? 
					ChatColor.BLUE  + plugin.getLanguageManager().getString("signs.started") : 
					ChatColor.GREEN + plugin.getLanguageManager().getString("signs.waiting");
			
			sign.setLine(0, ChatColor.GOLD + worldname);
			sign.setLine(1, status);
			sign.setLine(2, ChatColor.LIGHT_PURPLE + plugin.getLanguageManager().getString("signs.playersIngame"));
			sign.setLine(3, plugin.getPlayerManager().getPlayerList(worldname).size() + "/" + plugin.getLocationManager().getLocationAmount(worldname));
			
			sign.update();
		}
		else
			logger.warning("No info-sign for world " + worldname);
	}

	public String getGameworldName(Block block) {
		
		Location location = block.getLocation();
		
		for(Entry<String, Location> entry : signs.entrySet()) {
			if(entry.getValue().equals(location))
				return entry.getKey();
		}
		
		return null;
	}

	public void registerSign(Block block) {
		
		Sign sign = (Sign) block.getState();
		
		String firstline  = sign.getLine(0);
		String secondline = sign.getLine(1);
		
		if(firstline.equalsIgnoreCase("[sginfo]") && secondline != null) {
			signs.put(secondline, block.getLocation());
			
			updateInfoSign(secondline);
			
			saveSigns();
		}
	}
}
