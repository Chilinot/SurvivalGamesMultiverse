/**
 *  Name: Worlds.java
 *  Date: 21:18:38 - 8 nov 2012
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

package se.lucasarnstrom.survivalgamesmultiverse.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.WorldUnloadEvent;

import se.lucasarnstrom.survivalgamesmultiverse.Main;
import se.lucasarnstrom.survivalgamesmultiverse.utils.ConsoleLogger;

public class Worlds implements Listener {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	public Worlds(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger("WorldListener");
		
		logger.debug("Initiated");
	}
	
	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled=true)
	public void onWorldUnload(WorldUnloadEvent event) {
		logger.debug("World unload event captured!");
		
		if(plugin.getWorldManager().isGameWorld(event.getWorld().getName())) {
			logger.debug("Blocking unload!");
			event.setCancelled(true);
		}
		else {
			logger.debug("Allowing!");
		}
	}
}
