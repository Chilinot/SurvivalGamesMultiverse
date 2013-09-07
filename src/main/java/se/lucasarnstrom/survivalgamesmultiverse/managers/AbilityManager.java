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
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import se.lucasarnstrom.survivalgamesmultiverse.Main;
import se.lucasarnstrom.survivalgamesmultiverse.utils.ConsoleLogger;

public class AbilityManager {
	
	public enum Abilitytype {
		COMPASS, INVISIBILITY, FORCE, HEALING;
	}
	
	private final Main plugin;
	private ConsoleLogger logger;
	
	private HashMap<String, Ability> active_abilities = new HashMap<String, Ability>();
	
	private final Random random = new Random();
	
	public AbilityManager(Main instance) {
		logger = new ConsoleLogger("AbilityManager");
		plugin = instance;
		
		logger.debug("Initiated!");
	}
	
	public boolean giveAbility(String playername, Abilitytype type) {
		
		if(plugin.getConfig().getBoolean("abilities.enabled") 
				&& plugin.getConfig().getBoolean("worlds." + Bukkit.getPlayerExact(playername).getWorld().getName() + ".enable_abilities")
				&& !active_abilities.containsKey(playername)) {
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
				
				return true;
			}
			else {
				logger.debug("Tried to give ability \"" + type + "\" to player \"" + playername + "\" but it was disabled!");
				return false;
			}
		}
		
		logger.debug("giveAbility() aborted - Abilities are disabled!");
		
		return false;
	}
	
	public boolean giveRandomAbility(String playername) {
		
		Abilitytype[] values = Abilitytype.values();
		
		return giveAbility(playername, values[random.nextInt(values.length)]);
	}
	
	/* =================================
	 * =          ABILITIES            =
	 * ================================= */
	private abstract class Ability {
		protected final String PLAYERNAME;
		protected final Abilitytype TYPE;
		
		public Ability(String playername, Abilitytype type) {
			PLAYERNAME = playername;
			TYPE = type;
		}
		
		public abstract void activate();
		
		public Abilitytype getType() {
			return TYPE;
		}
	}
	
	
	private class Compass extends Ability {

		public Compass(String playername) {
			super(playername, Abilitytype.COMPASS);
		}

		public void activate() {
			final Player p = Bukkit.getPlayerExact(PLAYERNAME);
			
			//TODO remove debug
			p.sendMessage("You have activated the compass ability!");
			p.sendMessage("You can use the compass in your inventory to track players for " + plugin.getConfig().getLong("abilities.compass.duration_in_seconds") + " seconds.");
			
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





