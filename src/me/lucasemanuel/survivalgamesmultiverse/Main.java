/**
 *  Name: Main.java
 *  Date: 10:39:48 - 8 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
 *  
 *  This is the object that gets initialized by the server.
 *  
 *  The onEnable() method initializes all managers, loads all worlds, registers all commands etc.
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse;

import me.lucasemanuel.survivalgamesmultiverse.listeners.Blocks;
import me.lucasemanuel.survivalgamesmultiverse.listeners.Players;
import me.lucasemanuel.survivalgamesmultiverse.listeners.Worlds;
import me.lucasemanuel.survivalgamesmultiverse.managers.ChestManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.LanguageManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.LocationManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.PlayerManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.StatsManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.StatusManager;
import me.lucasemanuel.survivalgamesmultiverse.managers.WorldManager;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
	
	//TODO add command to freeze the game
	//TODO add command to block players from entering, for maintenance purpose
	
	private ConsoleLogger logger;
	
	private PlayerManager   playermanager;
	private WorldManager    worldmanager;
	private ChestManager    chestmanager;
	private StatsManager    statsmanager;
	private LanguageManager languagemanager;
	private LocationManager locationmanager;
	private StatusManager   statusmanager;
	
	public void onEnable() {
		
		logger = new ConsoleLogger(this, "Main");
		logger.debug("Initiating startup sequence...");
		
		Config.load(this);
		
		logger.debug("Initiating managers...");
		
		playermanager   = new PlayerManager(this);
		worldmanager    = new WorldManager(this);
		chestmanager    = new ChestManager(this);
		statsmanager    = new StatsManager(this);
		languagemanager = new LanguageManager(this);
		locationmanager = new LocationManager(this);
		statusmanager   = new StatusManager(this);
		
		logger.debug("Finished! Moving on to event listeners...");
		
		this.getServer().getPluginManager().registerEvents(new Players(this), this);
		this.getServer().getPluginManager().registerEvents(new Blocks(this), this);
		this.getServer().getPluginManager().registerEvents(new Worlds(this), this);
		
		logger.debug("Finished! Registering commands...");
		
		Commands commands = new Commands(this);
		
		this.getCommand("sginfo").setExecutor(commands);
		this.getCommand("sgdebug").setExecutor(commands);
		this.getCommand("sglocation").setExecutor(commands);
		this.getCommand("sgactivate").setExecutor(commands);
		this.getCommand("sgreset").setExecutor(commands);
		this.getCommand("sgplayers").setExecutor(commands);
		this.getCommand("sgleave").setExecutor(commands);
		
		logger.debug("Finished! Lets load some worlds...");
		
		for(String key : getConfig().getConfigurationSection("worldnames").getKeys(false)) {
			
			worldmanager.addWorld(Bukkit.createWorld(new WorldCreator(key)), Bukkit.createWorld(new WorldCreator(getConfig().getString("worldnames." + key))));
			playermanager.addWorld(key);
			locationmanager.addWorld(key);
			statusmanager.addWorld(key);
			
			logger.debug("Loading world - " + key + " :: template - " + getConfig().getString("worldnames." + key));
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
	
	public StatsManager getStatsManager() {
		return statsmanager;
	}
	
	public LanguageManager getLanguageManager() {
		return languagemanager;
	}

	public LocationManager getLocationManager() {
		return locationmanager;
	}
	
	public StatusManager getStatusManager() {
		return statusmanager;
	}
	
	public void gameover(World world) {
		
		if(playermanager.isGameOver(world)) {
			
			if(statusmanager.getStatus(world.getName())) {
				
				// Broadcast a message to all players in that world that the game is over.
				worldmanager.broadcast(world, languagemanager.getString("gameover"));
				
				// Do we have a winner?
				String winnername = playermanager.getWinner(world);
				Player winner = null;
				
				if(winnername != null && (winner = Bukkit.getPlayerExact(winnername)) != null) {
					
					worldmanager.broadcast(world, ChatColor.LIGHT_PURPLE + winner.getName() + ChatColor.WHITE + " " + languagemanager.getString("wonTheGame"));
					
					if(!winner.hasPermission("survivalgames.ignore.stats")) statsmanager.addWinPoints(winner.getName(), 1);
					
					worldmanager.sendPlayerToSpawn(winner);
					playermanager.removePlayer(winner.getWorld().getName(), winner.getName());
				}
			}
			
			// Resets
			resetWorld(world);
		}
	}
	
	public void resetWorld(World world) {
		playermanager.killAndClear(world.getName());
		worldmanager.resetWorld(world);
		locationmanager.resetLocationStatuses(world);
		chestmanager.clearLogs(world.getName());
		statusmanager.reset(world.getName());
	}
}
