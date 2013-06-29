/**
 *  Name:    Misc.java
 *  Created: 15:45:17 - 29 jun 2013
 * 
 *  Author:  Lucas Arnström - LucasEmanuel @ Bukkit forums
 *  Contact: lucasarnstrom(at)gmail(dot)com
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
 *
 *  Filedescription:
 *
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.listeners;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.managers.WorldManager;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.hanging.HangingBreakEvent;

public class Misc implements Listener {
	
	private ConsoleLogger logger;
	private WorldManager worldmanager;
	
	public Misc(Main instance) {
		logger = new ConsoleLogger("MiscListener");
		
		worldmanager = instance.getWorldManager();
		
		logger.debug("Inititated");
	}
	
	@EventHandler(priority=EventPriority.MONITOR, ignoreCancelled=true)
	public void onHangingBreak(HangingBreakEvent event) {
		if(worldmanager.isGameWorld(event.getEntity().getWorld()))
			worldmanager.logEntity(event.getEntity());
	}
}
