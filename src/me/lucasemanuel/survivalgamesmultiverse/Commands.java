/**
 *  Name: Commands.java
 *  Date: 16:18:09 - 15 sep 2012
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

package me.lucasemanuel.survivalgamesmultiverse;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class Commands implements CommandExecutor {
	
	private ConsoleLogger logger;
	
	public Commands(Main instance) {
		logger = new ConsoleLogger(instance, "CommandExecutor");
		
		logger.debug("Initiated");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		
		
		
		return false;
	}
}
