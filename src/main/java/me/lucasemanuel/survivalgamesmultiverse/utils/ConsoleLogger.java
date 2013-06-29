/**
 *  Name: ConsoleLogger.java
 *  Updated: 2013-06-10 - 14:44
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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.fusesource.jansi.Ansi;

public class ConsoleLogger {

	private static JavaPlugin plugin = null;
	private static Logger logger     = null;

	private static String template;
	private static boolean debug;
	
	private final String name;
	private final String info;
	
	private static Set<String> listeners = new HashSet<String>();
	private static Set<ConsoleLogger> loggers = new HashSet<ConsoleLogger>();

	/**
	 * Constructor for the ConsoleLogger.
	 * 
	 * @param instance - The JavaPlugin instance that initiated this logmanager.
	 * @param logger_name - Name of the logger.
	 */
	public ConsoleLogger(JavaPlugin instance, String logger_name) {
		ConsoleLogger.plugin   = instance;
		ConsoleLogger.logger   = instance.getLogger();
		ConsoleLogger.debug    = plugin.getConfig().getBoolean("debug");
		ConsoleLogger.template = "v" + plugin.getDescription().getVersion() + ": ";
		
		this.name = logger_name;
		this.info = ConsoleLogger.template + "[" + logger_name + "] - ";
		
		loggers.add(this);
	}
	
	/**
	 * Returns the name of this object.
	 * 
	 * @return name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Outputs normal info to the console with a green color.
	 * 
	 * @param msg - Info message
	 */
	public void info(String msg) {
		ConsoleLogger.logger.info(Ansi.ansi().fg(Ansi.Color.GREEN) + this.info + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		broadcastToListeners("info", msg);
	}

	/**
	 * Outputs warnings to the console with a yellow color.
	 * 
	 * @param msg - Warning message
	 */
	public void warning(String msg) {
		ConsoleLogger.logger.warning(Ansi.ansi().fg(Ansi.Color.YELLOW) + this.info + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		broadcastToListeners("warning", msg);
	}

	/**
	 * Outputs severe messages to the console with a red color.
	 * 
	 * @param msg - Severe message
	 */
	public void severe(String msg) {
		ConsoleLogger.logger.severe(Ansi.ansi().fg(Ansi.Color.RED) + this.info + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		broadcastToListeners("severe", msg);
	}

	/**
	 * This will only output if the debug is set to true.
	 * Outputs with a cyan color.
	 * 
	 * @param msg - Debug message
	 */
	public void debug(String msg) {
		if (debug == true) {
			ConsoleLogger.logger.info(Ansi.ansi().fg(Ansi.Color.CYAN) + this.info + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
			broadcastToListeners("debug", msg);
		}
	}
	
	private void broadcastToListeners(String level, String msg) {
		
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
			
		Iterator<String> i = listeners.iterator();
		
		while(i.hasNext()) {
			
			Player player = plugin.getServer().getPlayerExact(i.next());
			
			if(player != null && player.isOnline()) {
				player.sendMessage(label + " [" + this.name + "] - " + ChatColor.WHITE + msg);
			}
			else {
				i.remove();
			}
		}
	}
	
	
	
	
	// ---------- Static methods ----------
	
	/**
	 * Set whether or not to output debug information to the console.
	 * 
	 * @param newstate - True to output, otherwise false.
	 */
	public static void setDebug(boolean newstate) {
		ConsoleLogger.debug = newstate;
		ConsoleLogger.plugin.getConfig().set("debug", newstate);
		ConsoleLogger.plugin.saveConfig();
	}
	
	/**
	 * Add a player to the list of players listening for debug info.
	 * 
	 * @param playername - Name of the player
	 */
	public static void addListener(String playername) {
		ConsoleLogger.listeners.add(playername);
	}
	
	/**
	 * Remove a listening player from the list of listeners.
	 * 
	 * @param playername - Name of the player to remove
	 */
	public static void removeListener(String playername) {
		ConsoleLogger.listeners.remove(playername);
	}
	
	/**
	 * Retrieve the logger with the given name.
	 * 
	 * @param name - Name of the ConsoleLogger to retrieve.
	 * @return ConsoleLogger with given name, returns null if none was found.
	 */
	public static ConsoleLogger getLogger(String name) {
		for(ConsoleLogger logger : ConsoleLogger.loggers) {
			if(logger.getName().equals(name))
				return logger;
		}
		return null;
	}
}
