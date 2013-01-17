/**
 *  Name: WorldManager.java
 *  Date: 17:28:06 - 10 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
 *  
 *  Manages all worlds, this includes: logging and resetting.
 *  
 * 
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class WorldManager {
	
	
	private Main plugin;
	private ConsoleLogger logger;
	
	// Key = Worldname, Value = logged blocks for that world
	// The logged blocks are separated into several HashSet's so the resetWorld() method will have
	// less locations to loop over per world, and so speed up performance.
	private HashMap<String, HashMap<String, LoggedBlock>> logged_blocks;
	
	// Entities that shouldn't be removed on world reset
	private final EntityType[] nonremovable = new EntityType[] { 
			EntityType.PLAYER, 
			EntityType.PAINTING 
	};
	
	public WorldManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "WorldManager");
		
		logged_blocks = new HashMap<String, HashMap<String, LoggedBlock>>();
	}
	
	public synchronized void addWorld(String worldname) {
		logged_blocks.put(worldname, new HashMap<String, LoggedBlock>());
	}
	
	public synchronized boolean isGameWorld(World world) {
		
		if(logged_blocks.containsKey(world.getName()))
			return true;
		else
			return false;
	}
	
	public synchronized void broadcast(World world, String msg) {
		if(isGameWorld(world)) {
			
			logger.debug("Broadcasting message to '" + world.getName() + "': " + msg);
			
			for(Player player : world.getPlayers()) {
				player.sendMessage(ChatColor.GREEN + "[SurvivalGames] - " + ChatColor.WHITE + msg);
			}
			
		}
		else
			logger.debug("Tried to broadcast message '" + msg + "' to non game-world - " + world.getName());
	}

	public synchronized void logBlock(Location location, boolean placed) {
		
		String key = new String(location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());
		
		if(logged_blocks.containsKey(location.getWorld().getName()) && logged_blocks.get(location.getWorld().getName()).containsKey(key) == false) {
			
			Material material = placed ? Material.AIR : location.getBlock().getType();
			
			String[] sign_lines = null;
			
			if(location.getBlock().getType() == Material.WALL_SIGN || location.getBlock().getType() == Material.SIGN_POST) {
				sign_lines = ((Sign) location.getBlock().getState()).getLines();
			}
			
			logged_blocks.get(location.getWorld().getName()).put(
					key, new LoggedBlock(
							location.getWorld().getName(), 
							location.getBlockX(), location.getBlockY(), location.getBlockZ(),
							material, location.getBlock().getData(), 
							sign_lines)
			);
			
//			logger.debug("Logged block in world: " + location.getWorld().getName());
		}
		else
			logger.debug("Dupe log!");
	}

	public synchronized void resetWorld(final World world) {
		
		logger.debug("Resetting world: " + world.getName());
		
		if(isGameWorld(world)) {
			
			HashMap<String, LoggedBlock> blocks_to_reset = logged_blocks.get(world.getName());
			
			for(LoggedBlock block : blocks_to_reset.values()) {
				block.reset();
			}
			
			// Schedule removal of all world entities to make sure they are all removed
			
			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					
					for(Entity entity : world.getEntities()) {
						
						boolean remove = true;
						
						for(EntityType type : nonremovable) {
							if(entity.getType().equals(type)) {
								remove = false;
								break;
							}
						}
						
						if(remove)
							entity.remove();
					}
				}
			});
			
			// Reset logs
			
			blocks_to_reset.clear();
		}
		else
			logger.debug("Tried to reset non registered world!");
	}

	public synchronized void sendPlayerToLobby(Player player) {
		player.teleport(Bukkit.getWorld(plugin.getConfig().getString("lobbyworld")).getSpawnLocation());
	}

	public synchronized String[] getRegisteredWorldNames() {
		
		String[] worlds = new String[logged_blocks.size()];
		
		int i = 0;
		for(String string : logged_blocks.keySet()) {
			worlds[i] = string;
			i++;
		}
		
		return worlds;
	}
}

class LoggedBlock {
	
	private final String   WORLDNAME;
	private final int      X, Y, Z;
	
	private final Material MATERIAL;
	private final byte     DATA;
	
	private final String[] SIGN_LINES;
	
	public LoggedBlock(String worldname, int x, int y, int z, Material material, byte data, String[] sign_lines) {
		
		WORLDNAME = worldname;
		X = x; Y = y; Z = z;
		
		MATERIAL = material;
		DATA     = data;
		
		SIGN_LINES = sign_lines;
	}
	
	public synchronized void reset() {
		
		Block block_to_restore = Bukkit.getWorld(WORLDNAME).getBlockAt(X, Y, Z);
		
		block_to_restore.setType(MATERIAL);
		block_to_restore.setData(DATA);
		
		if(SIGN_LINES != null && (MATERIAL == Material.SIGN_POST || MATERIAL ==  Material.WALL_SIGN)) {
			
			Sign sign = (Sign) block_to_restore.getState();
			
			for(int i = 0 ; i < 4 ; i++) {
				sign.setLine(i, SIGN_LINES[i]);
			}
			
			sign.update();
		}
	}
}



