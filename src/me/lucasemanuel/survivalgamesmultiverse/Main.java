/**
 *  Name: Main.java
 *  Date: 10:39:48 - 8 sep 2012
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

import me.lucasemanuel.survivalgamesmultiverse.listeners.Blocks;
import me.lucasemanuel.survivalgamesmultiverse.listeners.Players;
import me.lucasemanuel.survivalgamesmultiverse.managers.ChestManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.PlayerManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.WorldManager;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

import org.bukkit.Bukkit;
import org.bukkit.WorldCreator;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	//TODO add command to register spawnpoints
	//TODO add command to leave the game
	//TODO add command to force reset the game
	//TODO add command to freeze the game
	//TODO add command to block players from entering, for maintenance purpose
	
	//TODO add statsmanager with connection to MySQL database for stats logging
	//TODO add leaderboardsigns
	
	private ConsoleLogger logger;
	
	private PlayerManager playermanager;
	private WorldManager worldmanager;
	private ChestManager chestmanager;
	
	public void onEnable() {
		logger = new ConsoleLogger(this, "Main");
		logger.debug("Initiating startup sequence...");
		
		Config.load(this);
		
		logger.debug("Initiating managers...");
		
		playermanager = new PlayerManager(this);
		worldmanager  = new WorldManager(this);
		chestmanager  = new ChestManager(this);
		
		logger.debug("Finished! Moving on to event listeners...");
		
		this.getServer().getPluginManager().registerEvents(new Players(this), this);
		this.getServer().getPluginManager().registerEvents(new Blocks(this), this);
		
		logger.debug("Finished! Lets load some worlds...");
		
		for(String key : getConfig().getConfigurationSection("worldnames").getKeys(false)) {
			
			worldmanager.addWorld(Bukkit.createWorld(new WorldCreator(key)), Bukkit.createWorld(new WorldCreator(getConfig().getString("worldnames." + key))));
			playermanager.addWorld(key);
			
			logger.debug("Creating world - " + key + " :: template - " + getConfig().getString("worldnames." + key));
		}
		
		logger.debug("Startup sequence finished!");
	}
	
	public PlayerManager getPlayerManager() {
		return playermanager;
	}
	
	public WorldManager getWorldManager() {
		return worldmanager;
	}
	
	public ChestManager getChestManager() {
		return chestmanager;
	}
}
