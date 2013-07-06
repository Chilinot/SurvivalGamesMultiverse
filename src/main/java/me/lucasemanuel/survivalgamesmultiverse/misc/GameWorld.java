/**
 *  Name:    GameWorld.java
 *  Created: 04:35:36 - 6 jul 2013
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

package me.lucasemanuel.survivalgamesmultiverse.misc;

import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import me.desht.dhutils.block.CraftMassBlockUpdate;
import me.desht.dhutils.block.MassBlockUpdate;
import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

public class GameWorld {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	private final World world;
	private boolean health_regen;
	
	private HashMap<Location, LoggedBlock> log_block          = new HashMap<Location, LoggedBlock>();
	private HashMap<UUID, LoggedEntity>    log_entity         = new HashMap<UUID, LoggedEntity>();
	private HashSet<Entity>                log_entity_removal = new HashSet<Entity>();
	
	// Entities that shouldn't be removed on world reset
	private static final EntityType[] nonremovable = new EntityType[] { 
			EntityType.PLAYER, 
			EntityType.PAINTING,
			EntityType.ITEM_FRAME
	};
	
	public GameWorld(Main plugin, ConsoleLogger logger, World w) {
		this.plugin = plugin;
		this.logger = logger;
		this.world  = w;
		
		health_regen = plugin.getConfig().getBoolean("worlds." + world.getName() + ".enable_healthregeneration");
	}
	
	public void logBlock(Block b, boolean placed) {
		
		Location l = b.getLocation();
		
		if (!log_block.containsKey(l)) {

			Material material = placed ? Material.AIR : b.getType();

			String[] sign_lines = null;

			if (b.getType() == Material.WALL_SIGN || b.getType() == Material.SIGN_POST) {
				sign_lines = ((Sign) b.getState()).getLines();
			}

			log_block.put(l, new LoggedBlock(b.getWorld().getName(), b.getX(), b.getY(), b.getZ(), material, b.getData(), sign_lines));
			logger.debug("Logging block :: " + b.getWorld().getName() + " " + b.getX() + " " + b.getY() + " " + b.getZ() + " " + material + " " + b.getData() + " " + sign_lines);
		}
	}
	
	public void logEntity(Entity e, boolean remove) {
		if(!log_entity.containsKey(e.getUniqueId())) {
			if(remove) {
				log_entity_removal.add(e);
				log_entity.put(e.getUniqueId(), null);
			}
			else {
				log_entity.put(e.getUniqueId(), new LoggedEntity(e));
			}
			logger.debug("Logged entity " + e.getType() + " " + e.getLocation());
		}
	}
	
	public void resetWorld() {
		
		logger.debug("Resetting world: " + world.getName());
		
		MassBlockUpdate mbu = CraftMassBlockUpdate.createMassBlockUpdater(plugin, world);

		for(LoggedBlock block : log_block.values()) {
			block.reset(mbu);
		}
		
		for(LoggedEntity entity : log_entity.values()) {
			if(entity != null)
				entity.reset();
		}
		
		mbu.notifyClients();

		plugin.getServer().getScheduler().runTask(plugin, new Runnable() {
			public void run() {
				clearEntities();
			}
		});

		log_block.clear();
		log_entity.clear();
	}
	
	public void clearEntities() {
		Set<EntityType> skip = EnumSet.noneOf(EntityType.class);
		Collections.addAll(skip, nonremovable);
		
		// Clear all logged entities
		for(Entity e : log_entity_removal) {
			if(e != null)
				e.remove();
		}
		
		log_entity_removal.clear();

		// Clear the remaining basic entities
		for (Entity entity : world.getEntities()) {
			
			if(skip.contains(entity.getType()))
				continue;

			entity.remove();
		}
	}
	
	public boolean allowHealthRegen() {
		return health_regen;
	}
	
	public String getWorldname() {
		return world.getName();
	}
}
