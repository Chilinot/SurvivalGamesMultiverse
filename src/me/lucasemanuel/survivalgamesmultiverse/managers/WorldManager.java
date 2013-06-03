/**
 *  Name: WorldManager.java
 *  Date: 17:28:06 - 10 sep 2012
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
 *  Manages all worlds, this includes: logging and resetting.
 *  
 * 
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Set;

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
import me.lucasemanuel.survivalgamesmultiverse.reflection.NMS;
import me.lucasemanuel.survivalgamesmultiverse.reflection.NMSRetriever;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class WorldManager {

	private Main plugin;
	private ConsoleLogger logger;

	// Reflection
	private static NMS nms;
	
	// Logging
	private HashMap<World, HashMap<String, LoggedBlock>> logged_blocks;

	// Entities that shouldn't be removed on world reset
	private final EntityType[] nonremovable = new EntityType[] { 
			EntityType.PLAYER, 
			EntityType.PAINTING,
			EntityType.ITEM_FRAME
	};

	public WorldManager(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "WorldManager");

		logged_blocks = new HashMap<World, HashMap<String, LoggedBlock>>();
		
		// Runs directly on next tick to prevent errors that could occur if the plugin is disabled during its setup.
		plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
			public void run() {
				setupNMS();
			}
		});
	}

	public void addWorld(World world) {
		logged_blocks.put(world, new HashMap<String, LoggedBlock>());
	}

	public boolean isGameWorld(World world) {

		if (logged_blocks.containsKey(world))
			return true;
		else
			return false;
	}

	public void broadcast(World world, String msg) {
		if (isGameWorld(world)) {

			logger.debug("Broadcasting message to '" + world.getName() + "': " + msg);

			for (Player player : world.getPlayers()) {
				player.sendMessage(ChatColor.GREEN + "[SurvivalGames] - " + ChatColor.WHITE + msg);
			}

		}
		else
			logger.debug("Tried to broadcast message '" + msg + "' to non game-world - " + world.getName());
	}

	public void logBlock(Location location, boolean placed) {

		String key = new String(location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ());

		if (logged_blocks.containsKey(location.getWorld()) && logged_blocks.get(location.getWorld()).containsKey(key) == false) {

			Material material = placed ? Material.AIR : location.getBlock().getType();

			String[] sign_lines = null;

			if (location.getBlock().getType() == Material.WALL_SIGN || location.getBlock().getType() == Material.SIGN_POST) {
				sign_lines = ((Sign) location.getBlock().getState()).getLines();
			}

			logged_blocks.get(location.getWorld()).put(key, new LoggedBlock(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), material, location.getBlock().getData(), sign_lines));
		}
	}

	public void resetWorld(final World world) {

		logger.debug("Resetting world: " + world.getName());

		if (isGameWorld(world)) {

			HashMap<String, LoggedBlock> blocks_to_reset = logged_blocks.get(world);

			for (LoggedBlock block : blocks_to_reset.values()) {
				block.reset();
			}

			plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
				public void run() {
					
					Set<EntityType> skip = EnumSet.noneOf(EntityType.class);
					Collections.addAll(skip, nonremovable);

					for (Entity entity : world.getEntities()) {
						
						if(skip.contains(entity.getType()))
							continue;

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

	public void sendPlayerToLobby(Player player) {
		player.teleport(Bukkit.getWorld(plugin.getConfig().getString("lobbyworld")).getSpawnLocation());
	}

	public String[] getRegisteredWorldNames() {

		String[] worlds = new String[logged_blocks.size()];

		int i = 0;
		for (World world : logged_blocks.keySet()) {
			worlds[i] = world.getName();
			i++;
		}

		return worlds;
	}
	
	private void setupNMS() {
		NMS nms = NMSRetriever.getNMS(plugin);
		if(nms != null) {
			WorldManager.nms = nms;
		}
		else {
			logger.severe("Unsupported server version! Disabling plugin!");
			plugin.disable();
		}
	}
	
	public static boolean setBlockFast(Block b, int typeId, byte data) {
		return nms.setBlockFast(b, typeId, data);
	}
}

class LoggedBlock {

	private final String	WORLDNAME;
	private final int		X, Y, Z;

	private final int		MATERIAL;
	private final byte		DATA;

	private final String[]	SIGN_LINES;

	public LoggedBlock(String worldname, int x, int y, int z, Material material, byte data, String[] sign_lines) {

		WORLDNAME = worldname;
		X = x;
		Y = y;
		Z = z;

		MATERIAL = material.getId();
		DATA = data;

		SIGN_LINES = sign_lines;
	}

	public void reset() {

		Block block_to_restore = Bukkit.getWorld(WORLDNAME).getBlockAt(X, Y, Z);

		WorldManager.setBlockFast(block_to_restore, MATERIAL, DATA);

		if (SIGN_LINES != null && (MATERIAL == Material.SIGN_POST.getId() || MATERIAL == Material.WALL_SIGN.getId())) {

			Sign sign = (Sign) block_to_restore.getState();

			for (int i = 0; i < 4; i++) {
				sign.setLine(i, SIGN_LINES[i]);
			}

			sign.update();
		}
	}
}