/**
 *  Name:    AbilityManager.java
 *  Created: 16:57:57 - 6 aug 2013
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

package se.lucasarnstrom.survivalgamesmultiverse.managers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import se.lucasarnstrom.survivalgamesmultiverse.Main;
import se.lucasarnstrom.survivalgamesmultiverse.utils.ConsoleLogger;

public class AbilityManager {
	
	public enum Abilities {
		COMPASS, INVISIBILITY, FORCE, HEALING;
	}
	
	private final Main plugin;
	private ConsoleLogger logger;
	
	private HashMap<String, Ability> active_abilities = new HashMap<String, Ability>();
	
	public AbilityManager(Main instance) {
		logger = new ConsoleLogger("AbilityManager");
		plugin = instance;
		
		logger.debug("Initiated!");
	}
	
	public void giveAbility(String playername, Abilities type) {
		
		if(plugin.getConfig().getBoolean("abilities.enabled")) {
			Ability ability = null;
			
			switch(type) {
				
				case COMPASS:
					if(plugin.getConfig().getBoolean("abilities.compass.enabled"))
						ability = new Compass(playername);
					
					break;
					
				case INVISIBILITY:
					break;
					
				case FORCE:
					break;
					
				case HEALING:
					break;
			}
			
			if(ability != null) {
				active_abilities.put(playername, ability);
				Bukkit.getPlayerExact(playername).sendMessage("You have been given the ability " + ability.getType());
			}
			else
				logger.debug("Tried to give ability \"" + type + "\" to player \"" + playername + "\" but it was disabled!");
		}
		else
			logger.debug("giveAbility() aborted - Abilities are disabled!");
	}
	
	/* =================================
	 * =          ABILITIES            =
	 * ================================= */
	private abstract class Ability {
		protected final String PLAYERNAME;
		protected final Abilities TYPE;
		
		public Ability(String playername, Abilities type) {
			PLAYERNAME = playername;
			TYPE = type;
		}
		
		public abstract void activate();
		
		public Abilities getType() {
			return TYPE;
		}
	}
	
	
	private class Compass extends Ability {

		public Compass(String playername) {
			super(playername, Abilities.COMPASS);
		}

		public void activate() {
			final Player p = Bukkit.getPlayerExact(PLAYERNAME);
			
			p.sendMessage("You have activated the compass ability!");
			p.sendMessage("You can use the compass in your inventory to track players for 30 seconds.");
			
			final ItemStack compass = new ItemStack(Material.COMPASS, 1);
			p.getInventory().addItem(compass);
			
			final int id = new BukkitRunnable() {
				public void run() {
					
					Location origin  = p.getLocation();
					Location closest = null;
					
					for(Player temp : plugin.getPlayerManager().getPlayerList(p.getWorld().getName())) {
						
						// Skip the holder.
						if(temp == p) continue;
						
						if(closest == null) {
							closest = temp.getLocation();
							continue;
						}
						
						if(closest.distanceSquared(origin) >= temp.getLocation().distanceSquared(origin)) {
							closest = temp.getLocation();
						}
					}
					
					if(closest != null) {
						p.setCompassTarget(closest);
					}
				}
			}.runTaskTimer(plugin, 0L, 10L).getTaskId();
			
			new BukkitRunnable() {
				public void run() {
					plugin.getServer().getScheduler().cancelTask(id);
					p.getInventory().remove(compass);
				}
			}.runTaskLater(plugin, plugin.getConfig().getLong("abilities.compass.duration_in_seconds") * 20);
		}
	}
}





