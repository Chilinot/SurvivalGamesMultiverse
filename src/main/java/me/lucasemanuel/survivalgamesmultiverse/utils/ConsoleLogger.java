/**
 *  Name: ConsoleLogger.java
 *  Updated: 2012-03-23 - 02:43
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
 *  Takes care of all the logging to the console.
 * 
 */
package me.lucasemanuel.survivalgamesmultiverse.utils;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

public class ConsoleLogger {

	private static JavaPlugin plugin;
	private Logger logger;

	private static String template;
	private static boolean debug;
	
	private final String name;
	private final String info;
	
	private static Set<String> listeners = Collections.synchronizedSet(new HashSet<String>());

	/**
	 * Constructor for the ConsoleLogger.
	 * 
	 * @param instance - The JavaPlugin instance that initiated this logmanager.
	 * @param logger_name - Name of the logger.
	 */
	public ConsoleLogger(JavaPlugin instance, String logger_name) {
		plugin = instance;
		this.logger = instance.getLogger();
		this.name = logger_name;

		ConsoleLogger.debug    = plugin.getConfig().getBoolean("debug");
		ConsoleLogger.template = "v" + plugin.getDescription().getVersion() + ": ";
		
		this.info = ConsoleLogger.template + "[" + logger_name + "] - ";
	}

	/**
	 * Outputs normal info to the console with a green color.
	 * 
	 * @param msg - Info message
	 */
	public synchronized void info(String msg) {
		this.logger.info(Ansi.ansi().fg(Ansi.Color.GREEN) + this.info + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		
		broadcastToListeners("info", msg);
	}

	/**
	 * Outputs warnings to the console with a yellow color.
	 * 
	 * @param msg - Warning message
	 */
	public synchronized void warning(String msg) {
		this.logger.warning(Ansi.ansi().fg(Ansi.Color.YELLOW) + this.info + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		
		broadcastToListeners("warning", msg);
	}

	/**
	 * Outputs severe messages to the console with a red color.
	 * 
	 * @param msg - Severe message
	 */
	public synchronized void severe(String msg) {
		this.logger.severe(Ansi.ansi().fg(Ansi.Color.RED) + this.info + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		
		broadcastToListeners("severe", msg);
	}

	/**
	 * This will only output if the debug is set to true.
	 * Outputs with a cyan color.
	 * 
	 * @param msg - Debug message
	 */
	public synchronized void debug(String msg) {
		if (debug == true)
			this.logger.info(Ansi.ansi().fg(Ansi.Color.CYAN) + this.info + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		
		broadcastToListeners("debug", msg);
	}
	
	private synchronized void broadcastToListeners(String level, String msg) {
		
		String label = null;
		
		switch(level) {
			case "info":
				label = ChatColor.GREEN + "INFO";
				break;
			case "warning":
				label = ChatColor.YELLOW + "WARNING";
				break;
			case "severe":
				label = ChatColor.DARK_RED + "SEVERE";
				break;
			case "debug":
				label = ChatColor.BLUE + "DEBUG";
				break;
			default:
				label = "Default label";
		}
		
		synchronized(listeners) {
			
			Iterator<String> i = listeners.iterator();
			
			while(i.hasNext()) {
				
				Player player = plugin.getServer().getPlayer(i.next());
				
				if(player != null) {
					player.sendMessage(label + " [" + this.name + "] - " + ChatColor.WHITE + msg);
				}
				else {
					i.remove();
				}
			}
		}
	}
	
	
	
	
	// ---------- Static methods ----------
	
	/**
	 * Set whether or not to output debug information to the console.
	 * 
	 * @param newstate - True to output, otherwise false.
	 */
	public synchronized static void setDebug(boolean newstate) {
		ConsoleLogger.debug = newstate;
		plugin.getConfig().set("debug", newstate);
		plugin.saveConfig();
	}
	
	/**
	 * Add a player to the list of players listening for debug info.
	 * 
	 * @param playername - Name of the player
	 */
	public synchronized static void addListener(String playername) {
		synchronized(listeners) {
			listeners.add(playername);
		}
	}
	
	/**
	 * Remove a listening player from the list of listeners.
	 * 
	 * @param playername - Name of the player to remove
	 */
	public synchronized static void removeListener(String playername) {
		synchronized(listeners) {
			listeners.remove(playername);
		}
	}
}
