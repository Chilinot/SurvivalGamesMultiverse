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
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import me.desht.dhutils.block.CraftMassBlockUpdate;
import me.desht.dhutils.block.MassBlockUpdate;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;
import me.lucasemanuel.survivalgamesmultiverse.utils.LoggedBlock;

public class WorldManager {

	private Main plugin;
	private ConsoleLogger logger;
	
	// Logging
	private HashMap<String, GameWorld> worlds = new HashMap<String, GameWorld>();

	public WorldManager(final Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "WorldManager");

		logger.debug("Initiated");
	}

	public void addWorld(World world) {
		worlds.put(world.getName(), new GameWorld(plugin, world));
	}

	public boolean isGameWorld(World world) {
		return worlds.containsKey(world.getName());
	}
	
	public void broadcast(String worldname, String msg) {
		broadcast(Bukkit.getWorld(worldname), msg);
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
		worlds.get(location.getWorld().getName()).logBlock(location, placed);
	}

	public void resetWorld(final World world) {

		logger.debug("Resetting world: " + world.getName());

		if (isGameWorld(world)) {
			worlds.get(world.getName()).resetWorld();
		}
		else
			logger.debug("Tried to reset non registered world!");
	}
	
	public void clearEntities(World world) {
		if(isGameWorld(world))
			worlds.get(world.getName()).clearEntities();
	}

	public void sendPlayerToLobby(Player player) {
		player.teleport(Bukkit.getWorld(plugin.getConfig().getString("lobbyworld")).getSpawnLocation());
	}

	public String[] getRegisteredWorldNames() {
		return (String[]) worlds.keySet().toArray(new String[worlds.keySet().size()]);
	}
	

	public boolean allowHealthRegen(World world) {
		if(isGameWorld(world))
			return worlds.get(world.getName()).allowHealthRegen();
		else
			return true;
	}
}

class GameWorld {
	
	private Main plugin;
	
	private final World world;
	private boolean health_regen;
	
	private HashMap<String, LoggedBlock> log = new HashMap<String, LoggedBlock>();
	
	// Entities that shouldn't be removed on world reset
	private static final EntityType[] nonremovable = new EntityType[] { 
			EntityType.PLAYER, 
			EntityType.PAINTING,
			EntityType.ITEM_FRAME
	};
	
	public GameWorld(Main plugin, World w) {
		this.plugin = plugin;
		this.world = w;
		
		health_regen = plugin.getConfig().getBoolean("worlds." + world.getName() + ".enable_healthregeneration");
	}
	
	public void logBlock(Location location, boolean placed) {
		String key = location.getBlockX() + " " + location.getBlockY() + " " + location.getBlockZ();
		
		if (!log.containsKey(key)) {

			Material material = placed ? Material.AIR : location.getBlock().getType();

			String[] sign_lines = null;

			if (location.getBlock().getType() == Material.WALL_SIGN || location.getBlock().getType() == Material.SIGN_POST) {
				sign_lines = ((Sign) location.getBlock().getState()).getLines();
			}

			log.put(key, new LoggedBlock(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), material, location.getBlock().getData(), sign_lines));
		}
	}
	
	public void resetWorld() {
		
		MassBlockUpdate mbu = CraftMassBlockUpdate.createMassBlockUpdater(plugin, world);

		for (LoggedBlock block : log.values()) {
			block.reset(mbu);
		}
		
		mbu.notifyClients();

		plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
			public void run() {
				clearEntities();
			}
		});

		log.clear();
	}
	
	public void clearEntities() {
		Set<EntityType> skip = EnumSet.noneOf(EntityType.class);
		Collections.addAll(skip, nonremovable);

		for (Entity entity : world.getEntities()) {
			
			if(skip.contains(entity.getType()))
				continue;

			entity.remove();
		}
	}
	
	public boolean allowHealthRegen() {
		return health_regen;
	}
}