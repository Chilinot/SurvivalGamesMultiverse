/**
 *  Name: Config.java
 *  Date: 14:27:53 - 8 sep 2012
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

package se.lucasarnstrom.survivalgamesmultiverse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {

	@SuppressWarnings("serial")
	public static void load(JavaPlugin plugin) {
		
		FileConfiguration config = plugin.getConfig();
		boolean save = false;
		
		HashMap<String, Object> world_defaults = new HashMap<String, Object>() {{
			put("players_to_wait_for", 2);
			put("enable_healthregeneration", true);
			put("blockfilter", false);
			
			put("grace.enabled", true);
			put("grace.time_in_seconds", 15);
		}};
		
		// -- Fix old entries
		if(config.contains("worldnames")) {
			for(String name : config.getStringList("worldnames")) {
				for(Entry<String, Object> entry : world_defaults.entrySet()) {
					config.set("worlds." + name + "." + entry.getKey(), entry.getValue());
				}
			}
			
			// Remove the now obsolete entry
			config.set("worldnames", null);
			
			save = true;
		}
		
		if(!config.contains("worlds")) {
			// This doesn't need to be any more specific.
			config.set("worlds.survivalgames1.players_to_wait_for", 2);
			config.set("worlds.survivalgames2.players_to_wait_for", 2);
			save = true;
		}
		
		// Add missing settings to existing worlds.
		if(config.contains("worlds")) {
			for(String name : config.getConfigurationSection("worlds").getKeys(false)) {
				
				for(Entry<String, Object> entry : world_defaults.entrySet()) {
					String key = "worlds." + name + "." + entry.getKey();
					if(!config.contains(key))
						config.set(key, entry.getValue());
				}
				
				save = true;
			}
		}
		
		// -- Default entries
		
		HashMap<String, Object> defaults = new HashMap<String, Object>() {{
			
			// General
			put("debug", false);
			put("lobbyworld", "world");
			
			put("allowedCommandsInGame", new ArrayList<String>() {{ 
				add("/sgplayers");
				add("/sgleave");
			}});
			
			// Update
			put("auto-update", true);
			
			// Backup
			put("backup.inventories", true);
			
			// Halloween
			put("halloween.enabled", false);
			put("halloween.forcepumpkin", false);
			
			// Time
			put("timeoutTillStart", 120);
			put("timeoutTillArenaInSeconds", 180);
			put("timeoutAfterArena", 60);
			
			// Database
			put("database.enabled", false);
			put("database.auth.username", "username");
			put("database.auth.password", "password");
			put("database.settings.host", "localhost");
			put("database.settings.port", 3306);
			put("database.settings.database", "survivalgames");
			put("database.settings.tablename", "sg_stats");
			
			// Abilities
			put("abilities.enabled", true);
			put("abilities.compass.enabled", true);
			put("abilities.compass.duration_in_seconds", 30);
		}};
		
		for(Entry<String, Object> entry : defaults.entrySet()) {
			if(!config.contains(entry.getKey())) {
				config.set(entry.getKey(), entry.getValue());
				save = true;
			}
		}
		
		if(save) {
			plugin.saveConfig();
		}
	}
}
