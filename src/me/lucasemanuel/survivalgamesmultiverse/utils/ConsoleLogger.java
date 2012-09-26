/**
 *  Name: ConsoleLogger.java
 *  Date: 15:16:11
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
 *  
 *  Takes care of all the logging to the console.
 * 
 */
package me.lucasemanuel.survivalgamesmultiverse.utils;

import java.util.HashSet;
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
	private String name;
	
	private static Set<String> listeners = new HashSet<String>();

	/**
	 * Constructor for the ConsoleLogger.
	 * 
	 * @param instance - The JavaPlugin instance that initiated this logmanager.
	 * @param debug - If set to true, it will output debug info to the console.
	 */
	public ConsoleLogger(JavaPlugin instance, String loggerName) {
		plugin = instance;
		this.logger = instance.getLogger();
		this.name = loggerName;

		ConsoleLogger.debug = plugin.getConfig().getBoolean("debug");

		ConsoleLogger.template = "v" + plugin.getDescription().getVersion() + ": ";
	}

	/**
	 * Outputs normal info to the console with a green color.
	 * 
	 * @param msg - Info message
	 */
	public synchronized void info(String msg) {
		this.logger.info(Ansi.ansi().fg(Ansi.Color.GREEN) + ConsoleLogger.template + "[" + this.name + "] - " + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		
		broadcastToListeners("info", msg);
	}

	/**
	 * Outputs warnings to the console with a yellow color.
	 * 
	 * @param msg - Warning message
	 */
	public synchronized void warning(String msg) {
		this.logger.warning(Ansi.ansi().fg(Ansi.Color.YELLOW) + ConsoleLogger.template + "[" + this.name + "] - " + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		
		broadcastToListeners("warning", msg);
	}

	/**
	 * Outputs severe messages to the console with a red color.
	 * 
	 * @param msg - Severe message
	 */
	public synchronized void severe(String msg) {
		this.logger.severe(Ansi.ansi().fg(Ansi.Color.RED) + ConsoleLogger.template + "[" + this.name + "] - " + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		
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
			this.logger.info(Ansi.ansi().fg(Ansi.Color.CYAN) + ConsoleLogger.template + "DEBUG [" + this.name + "] - " + msg + Ansi.ansi().fg(Ansi.Color.WHITE));
		
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
		
		for(String playername : listeners) {
			Player player = plugin.getServer().getPlayer(playername);
			
			if(player != null) {
				player.sendMessage(label + " [" + this.name + "] - " + ChatColor.WHITE + msg);
			}
			else {
				listeners.remove(playername);
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
		listeners.add(playername);
	}
	
	/**
	 * Remove a listening player from the list of listeners.
	 * 
	 * @param playername - Name of the player to remove
	 */
	public synchronized static void removeListener(String playername) {
		listeners.remove(playername);
	}
}
