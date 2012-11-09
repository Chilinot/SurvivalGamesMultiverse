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

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class SignManager {
	
	private ConsoleLogger logger;
	
	public SignManager(Main instance) {
		logger = new ConsoleLogger(instance, "SignManager");
		
		logger.debug("Initiated");
	}

}
