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

package se.lucasarnstrom.survivalgamesmultiverse.managers;

import java.util.HashSet;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import se.lucasarnstrom.lucasutils.ConsoleLogger;
import se.lucasarnstrom.survivalgamesmultiverse.Main;
import se.lucasarnstrom.survivalgamesmultiverse.misc.GameWorld;

public class WorldManager {

	private Main plugin;
	private ConsoleLogger logger;
	
	// Logging
	private HashSet<GameWorld> worlds = new HashSet<GameWorld>();

	public WorldManager(final Main instance) {
		plugin = instance;
		logger = new ConsoleLogger("WorldManager");

		logger.debug("Initiated");
	}

	public void addWorld(World world) {
		worlds.add(new GameWorld(plugin, logger, world));
	}

	public boolean isGameWorld(String name) {
		if(getGameWorld(name) != null)
			return true;
		else
			return false;
	}
	
	public void broadcast(String worldname, String msg) {
		broadcast(Bukkit.getWorld(worldname), msg);
	}

	public void broadcast(World world, String msg) {
		GameWorld game = getGameWorld(world.getName());
		if(game != null) {

			logger.debug("Broadcasting message to '" + world.getName() + "': " + msg);

			for (Player player : world.getPlayers()) {
				player.sendMessage("[" + ChatColor.GOLD + "SGM" + ChatColor.WHITE + "] - " + msg);
			}

		}
		else
			logger.debug("Tried to broadcast message '" + msg + "' to non game-world - " + world.getName());
	}

	public void logBlock(Block b, boolean placed) {
		getGameWorld(b.getWorld().getName()).logBlock(b, placed);
	}
	
	public void logEntity(Entity e, boolean remove) {
		getGameWorld(e.getWorld().getName()).logEntity(e, remove);
	}

	public void resetWorld(final World world) {

		logger.debug("Resetting world: " + world.getName());
		
		GameWorld game = getGameWorld(world.getName());
		if(game != null) {
			game.resetWorld();
		}
		else
			logger.debug("Tried to reset non registered world!");
	}
	
	public void clearEntities(World world) {
		GameWorld game = getGameWorld(world.getName());
		if(game != null)
			game.clearEntities();
	}

	public void sendPlayerToLobby(Player player) {
		player.teleport(Bukkit.getWorld(plugin.getConfig().getString("lobbyworld")).getSpawnLocation());
	}

	public String[] getRegisteredWorldNames() {
		String[] names = new String[worlds.size()];
		
		int i = 0;
		for(GameWorld world : worlds) {
			names[i] = world.getWorldname();
			i++;
		}
		
		return names;
	}
	
	public GameWorld getGameWorld(String worldname) {
		for(GameWorld world : worlds) {
			if(world.getWorldname().equals(worldname))
				return world;
		}
		return null;
	}

	public boolean allowHealthRegen(World world) {
		GameWorld game = getGameWorld(world.getName());
		if(game != null)
			return game.allowHealthRegen();
		else
			return true;
	}
	
	public boolean allowBlock(Block b) {
		GameWorld game = getGameWorld(b.getWorld().getName());
		if(game != null) {
			return game.allowBlock(b);
		}
		return true;
	}
	
	public boolean allowPVP(World w) {
		GameWorld game = getGameWorld(w.getName());
		if(game != null) {
			return game.allowPVP();
		}
		return true;
	}
	
	public void setAllowPVP(String worldname, boolean value) {
		GameWorld game = getGameWorld(worldname);
		if(game != null) {
			game.setAllowPVP(value);
		}
	}
}