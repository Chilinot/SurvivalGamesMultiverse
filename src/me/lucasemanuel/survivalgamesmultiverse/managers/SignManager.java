/**
 *  Name: SignManager.java
 *  Date: 21:35:06 - 9 nov 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Copyright 2013 Lucas Arnström
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *  
 *
 *  Filedescription:
 *  
 *  
 *  
 * 
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class SignManager {
	
	private final Main plugin;
	private final ConsoleLogger logger;
	
	private HashMap<Sign, String> signs;
	
	public SignManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "SignManager");
		
		logger.debug("Initiated");
	}

	public synchronized void loadsigns() {
		
		logger.debug("Loading signlocations...");
		
		this.signs = new HashMap<Sign, String>();
		
		HashMap<Location, String> locations = plugin.getSQLiteConnector().getSignlocations();
		
		if(locations != null && locations.size() > 0) {
			for(Entry<Location, String> entry : locations.entrySet()) {
				
				Block block = entry.getKey().getBlock();
				
				if(block != null) {
					if(block.getType().equals(Material.SIGN_POST) || block.getType().equals(Material.WALL_SIGN)) {
						signs.put((Sign) block.getState(), entry.getValue());
					}
					else {
						logger.warning("Loaded block not a sign! Material: " + block.getType());
						
						final Location location = entry.getKey();
						
						new Thread() {
							public void run() {
								plugin.getSQLiteConnector().removeSign(location);
							}
						}.start();
					}
				}
				else
					logger.warning("Loaded block is null!");
			}
		}
		else {
			logger.debug("No saved signs!");
		}
	}
	
	private synchronized void saveSigns() {
		
		logger.debug("Saving signlocations...");
		
		final HashMap<Location, String> locations = new HashMap<Location, String>();
		
		for(Entry<Sign, String> entry : signs.entrySet()) {
			locations.put(entry.getKey().getLocation(), entry.getValue());
		}
		
		new Thread() {
			public void run() {
				plugin.getSQLiteConnector().saveSignLocations(locations);
			}
		}.start();
	}

	public synchronized void updateSigns() {
		for(String worldname : signs.values()) {
			updateInfoSign(worldname);
		}
	}

	private synchronized void updateInfoSign(String worldname) {
		
		Sign sign = getSign(worldname);
		
		if(sign != null) {
			
			String output = "";
			
			int status = plugin.getStatusManager().getStatusFlag(worldname);
			switch(status) {
				case 0:  output = ChatColor.GREEN       + plugin.getLanguageManager().getString("signs.waiting"); break;
				case 1:  output = ChatColor.DARK_GREEN  + plugin.getLanguageManager().getString("signs.started"); break;
				case 2:  output = ChatColor.RED         + plugin.getLanguageManager().getString("signs.frozen");  break;
				default: output = ChatColor.RED         + "ERROR"; break;
			}
					
			Player[] playerlist = plugin.getPlayerManager().getPlayerList(worldname);
				
			sign.setLine(0, ChatColor.DARK_GREEN + worldname);
			sign.setLine(1, output);
			sign.setLine(2, plugin.getLanguageManager().getString("signs.playersIngame"));
			sign.setLine(3, "" + ChatColor.WHITE + playerlist.length + 
					"/" + plugin.getLocationManager().getLocationAmount(worldname));
			
			sign.update();
		}
		else
			logger.warning("Sign is null! Worldname: " + worldname);
	}

	private synchronized Sign getSign(String worldname) {
		
		for(Entry<Sign, String> entry : signs.entrySet()) {
			if(entry.getValue().equals(worldname))
				return entry.getKey();
		}
		
		return null;
	}

	public synchronized String getGameworldName(Sign sign) {
		
		for(Entry<Sign, String> entry : signs.entrySet()) {
			if(entry.getKey().equals(sign))
				return entry.getValue();
		}
		
		return null;
	}

	public synchronized void registerSign(Sign sign) {
		
		if(sign != null) {
			
			String firstline  = sign.getLine(0);
			String secondline = sign.getLine(1);
			
			if(firstline.equalsIgnoreCase("[sginfo]") && secondline != null) {
				
				World world = Bukkit.getWorld(secondline);
				
				if(world != null) {
					signs.put(sign, world.getName());
					updateInfoSign(world.getName());
					saveSigns();
				}
				else
					logger.warning("Tried to register sign for null world! Worldname used: " + secondline);
			}
		}
	}
}
