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

import se.lucasarnstrom.survivalgamesmultiverse.Main;
import se.lucasarnstrom.survivalgamesmultiverse.utils.ConsoleLogger;

public class AbilityManager {
	
	public enum Abilites {
		COMPASS;
	}
	
	private Main          plugin;
	private ConsoleLogger logger;
	
	private HashMap<String, Ability> current_abilities = new HashMap<String, Ability>();
	
	public AbilityManager(Main instance) {
		logger = new ConsoleLogger("AbilityManager");
		plugin = instance;
		
		logger.debug("Initiated!");
	}
	
	public boolean hasAbilityAlready(String player_name) {
		return current_abilities.containsKey(player_name);
	}
	
	/*
	 *  -- Abstract ability
	 */
	private abstract class Ability {
		
		public final String ABILITY_NAME;
		public final String PLAYER_NAME;
		
		public Ability(String name, String player_name) {
			this.ABILITY_NAME = name;
			this.PLAYER_NAME = player_name;
		}
		
		public abstract void run();
	}

	/*
	 *  -- Abilities
	 */
	
	private class Compass extends Ability {

		public Compass(String player_name) {
			super("Compass", player_name);
		}

		@Override
		public void run() {
			
		}
	}
}
