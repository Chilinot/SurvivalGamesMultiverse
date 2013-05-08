/**
 *  Name: Config.java
 *  Date: 14:27:53 - 8 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Copyright 2013 Lucas Arnstr�m
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

package me.lucasemanuel.survivalgamesmultiverse;

import java.util.ArrayList;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public class Config {

	@SuppressWarnings("serial")
	public static void load(JavaPlugin plugin) {
		
		FileConfiguration config = plugin.getConfig();
		boolean save = false;
		
		// General
		
		if(!config.contains("debug")) {
			config.set("debug", false);
			save = true;
		}
		
		if(!config.contains("lobbyworld")) {
			config.set("lobbyworld", "world");
			save = true;
		}
		
		if(!config.contains("halloween.enabled")) {
			config.set("halloween.enabled", false);
			save = true;
		}
		
		if(!config.contains("halloween.forcepumpkin")) {
			config.set("halloween.forcepumpkin", false);
			save = true;
		}
		
		if(!config.contains("worldnames")) {
			
			ArrayList<String> worlds = new ArrayList<String>() {{ 
				add("survivalgames1");
				add("survivalgames2");
			}};
			
			config.set("worldnames", worlds);
			save = true;
		}
		
		if(!config.contains("timeoutTillStart")) {
			config.set("timeoutTillStart", 120);
			save = true;
		}
		
		if(!config.contains("timeoutTillArenaInSeconds")) {
			config.set("timeoutTillArenaInSeconds", 180);
			save = true;
		}
		
		if(!config.contains("timeoutAfterArena")) {
			config.set("timeoutAfterArena", 60);
			save = true;
		}
		
		if(!config.contains("allowedCommandsInGame")) {
			
			ArrayList<String> allowedcommands = new ArrayList<String>() {{ 
				add("/sgplayers");
				add("/sgleave");
			}};
			
			config.set("allowedCommandsInGame", allowedcommands);
			save = true;
		}
		
		// Database
		
		if(!config.contains("database.enabled")) {
			config.set("database.enabled", false);
			save = true;
		}
		
		if(!config.contains("database.auth.username")) {
			config.set("database.auth.username", "username");
			save = true;
		}
		
		if(!config.contains("database.auth.password")) {
			config.set("database.auth.password", "password");
			save = true;
		}
		
		if(!config.contains("database.settings.host")) {
			config.set("database.settings.host", "localhost");
			save = true;
		}
		
		if(!config.contains("database.settings.port")) {
			config.set("database.settings.port", 3306);
			save = true;
		}
				
		if(!config.contains("database.settings.database")) {
			config.set("database.settings.database", "survivalgames");
			save = true;
		}
		
		if(!config.contains("database.settings.tablename")) {
			config.set("database.settings.tablename", "sg_stats");
			save = true;
		}
		
		if(save) {
			plugin.saveConfig();
		}
	}
}
