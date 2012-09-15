/**
 *  Name: LanguageManager.java
 *  Date: 23:22:06 - 13 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
 *  
 *  Manages all of the configurable broadcasts etc.
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class LanguageManager {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	private HashMap<String, String> language;
	
	public LanguageManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "LanguageManager");
		
		language = new HashMap<String, String>();
		
		loadLanguage();
	}
	
	private void loadLanguage() {
		
		FileConfiguration config = YamlConfiguration.loadConfiguration(new File(plugin.getDataFolder(), "language.yml"));
		
		checkDefaults(config);
		
		for(String key : config.getKeys(false)) {
			language.put(key, config.getString(key));
		}
	}

	private void checkDefaults(FileConfiguration config) {
		
		boolean save = false;
		
		if(!config.contains("gameover")) {
			config.set("gameover", "Game over!");
			save = true;
		}
		
		if(!config.contains("wonTheGame")) {
			config.set("wonTheGame", "won the game!");
			save = true;
		}
		
		if(!config.contains("isOutOfTheGame")) {
			config.set("isOutOfTheGame", "is out of the game!");
			save = true;
		}
		
		if(!config.contains("wasKilledBy")) {
			config.set("wasKilledBy", "was killed by");
			save = true;
		}
		
		if(!config.contains("youJoinedTheGame")) {
			config.set("youJoinedTheGame", "You joined the game!");
			save = true;
		}
		
		if(!config.contains("playerJoinedGame")) {
			config.set("playerJoinedGame", "joined the game!");
			save = true;
		}
		
		if(!config.contains("alreadyPlaying")) {
			config.set("alreadyPlaying", "You are already playing!");
			save = true;
		}
		
		if(!config.contains("anticheatRemoval")) {
			config.set("anticheatRemoval", "was removed due to disconnect!");
			save = true;
		}
		
		if(!config.contains("gameIsFull")) {
			config.set("gameIsFull", "Game is full!");
			save = true;
		}
		
		if(!config.contains("blockedMovement")) {
			config.set("blockedMovement", "You are not allowed to move yet!");
			save = true;
		}
		
		if(save) {
			try {
	            config.save(this.plugin.getDataFolder() + File.separator + "language.yml");
	        } catch (IOException e) {
	        	this.logger.severe("Could not save language.yml!");
	        }
		}
	}
	
	public String getString(String key) {
		
		if(language.containsKey(key)) {
			return language.get(key);
		}
		
		return null;
	}
}
