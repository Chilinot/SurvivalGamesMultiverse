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

package se.lucasarnstrom.survivalgamesmultiverse.misc;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.UUID;

import me.desht.dhutils.block.CraftMassBlockUpdate;
import me.desht.dhutils.block.MassBlockUpdate;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;

import se.lucasarnstrom.survivalgamesmultiverse.Main;
import se.lucasarnstrom.survivalgamesmultiverse.utils.ConsoleLogger;

public class GameWorld {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	private final World world;
	
	private boolean health_regen;
	private boolean allow_pvp    = true;
	
	private EnumSet<Material> blockfilter = null;
	
	private HashMap<Location, LoggedBlock> log_block          = new HashMap<Location, LoggedBlock>();
	private HashMap<UUID, LoggedEntity>    log_entity         = new HashMap<UUID, LoggedEntity>();
	private HashSet<Entity>                log_entity_removal = new HashSet<Entity>();
	
	// Entities that shouldn't be removed on world reset
	private static final EnumSet<EntityType> nonremovable = EnumSet.of(
			EntityType.PLAYER, 
			EntityType.PAINTING,
			EntityType.ITEM_FRAME
	);
	
	public GameWorld(Main plugin, ConsoleLogger logger, World w) {
		this.plugin = plugin;
		this.logger = logger;
		this.world  = w;
		
		health_regen = plugin.getConfig().getBoolean("worlds." + world.getName() + ".enable_healthregeneration");
		
		// Load blockfilter
		String materials = plugin.getConfig().getString("worlds." + world.getName() + ".blockfilter");
		
		if(materials != null) {
			String[] list = materials.split(", ");
			
			for(String s : list) {
				
				if(s.equalsIgnoreCase("false")) {
					logger.info("Blockfilter disabled for world " + world.getName());
					blockfilter = null; // Just to make sure it is disabled.
					break;
				}
				
				// Remove any data added by user since it can't handle that right now
				if(s.contains(":"))
					s = s.split(":")[0];
				
				try {
					int id = Integer.parseInt(s);
					addMaterialToFilter(id);
				}
				catch(NumberFormatException e) {
					logger.severe("Incorrectly formatted blockfilter for world \"" + world.getName() + "\" :: ENTRY IS NOT A VALID MATERIAL-ID: ENTRY = \"" + s + "\"");
					continue;
				}
			}
		}
	}
	
	private void addMaterialToFilter(int id) throws NumberFormatException {
		if(blockfilter == null) {
			blockfilter = EnumSet.noneOf(Material.class);
		}
		
		Material m = Material.getMaterial(id);
		
		if(m == null)
			throw new NumberFormatException();
		else {
			logger.debug("Adding material \"" + m + "\" to blockfilter for world \"" + world.getName() + "\"");
			blockfilter.add(m);
		}
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
		
		// Clear all logged entities
		for(Entity e : log_entity_removal) {
			if(e != null)
				e.remove();
		}
		
		log_entity_removal.clear();

		// Clear the remaining basic entities
		for (Entity entity : world.getEntities()) {
			if(nonremovable.contains(entity.getType()))
				continue;
			entity.remove();
		}
	}
	
	public boolean allowHealthRegen() {
		return health_regen;
	}
	
	public boolean allowBlock(Block b) {
		if(blockfilter == null) 
			return true;
		else if(blockfilter.contains(b.getType())) {
			return true;
		}
		return false;
	}
	
	public boolean allowPVP() {
		return allow_pvp;
	}
	
	public void setAllowPVP(boolean value) {
		allow_pvp = value;
	}
	
	public String getWorldname() {
		return world.getName();
	}
}
