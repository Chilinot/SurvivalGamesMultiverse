/**
 *  Name:    LoggedEntity.java
 *  Created: 16:18:50 - 29 jun 2013
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

package me.lucasemanuel.survivalgamesmultiverse.utils;

import java.util.HashMap;

import org.bukkit.Art;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Hanging;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Painting;
import org.bukkit.inventory.ItemStack;

public class LoggedEntity {
	
	private final Location                location;
	private final EntityType              type;
	private final HashMap<String, Object> data = new HashMap<String, Object>();
	
	public LoggedEntity(Entity e) {
		type = e.getType();
		
		if(e instanceof Hanging) {
			Hanging h = (Hanging) e;
			data.put("FacingDirection", h.getFacing());
			
			// For hanging entities I have to respawn them inside the block they are
			// hanging on and then teleport them to their old location.
			location = e.getLocation().getBlock().getRelative(h.getAttachedFace()).getLocation();
			
			if(h instanceof Painting) {
				Painting p = (Painting) h;
				data.put("Art", p.getArt());
			}
			else if(h instanceof ItemFrame) {
				ItemFrame i = (ItemFrame) h;
				data.put("ItemStack", i.getItem().clone());
			}
		}
		else
			location = e.getLocation();
	}
	
	public void reset() {
		
		BlockFace face = (BlockFace) data.get("FacingDirection");
		
		switch(type) {
			case ITEM_FRAME:
				ItemFrame i = location.getWorld().spawn(location, ItemFrame.class);
				i.teleport(location.getBlock().getRelative(face).getLocation());
				i.setFacingDirection(face);
				i.setItem((ItemStack) data.get("ItemStack"));
				break;
			
			case PAINTING:
				Art art = (Art) data.get("Art");
				Painting p = location.getWorld().spawn(location, Painting.class);
				p.teleport(calculatePainting(art, face, location.getBlock().getRelative(face).getLocation()));
				p.setFacingDirection(face);
				p.setArt(art, true);
				break;
				
			default:
		}
	}
	
	private Location calculatePainting(Art art, BlockFace facing, Location loc) {
		switch(art) {
			
			// 1x1
			case ALBAN:
			case AZTEC:
			case AZTEC2:
			case BOMB:
			case KEBAB:
			case PLANT:
			case WASTELAND:
				return loc; // No calculation needed.
				
			// 1x2
			case GRAHAM:
			case WANDERER:
				return loc.getBlock().getRelative(BlockFace.DOWN).getLocation();
			
			// 2x1
			case CREEBET:
			case COURBET:
			case POOL:
			case SEA:
			case SUNSET:
				return loc; // Needs work
				
			// 2x2
			case BUST:
			case MATCH:
			case SKULL_AND_ROSES:
			case STAGE:
			case VOID:
			case WITHER:
				return loc; // Needs work
				
			// 4x2
			case FIGHTERS:
				return loc; // Needs work
				
			// 4x3
			case DONKEYKONG:
			case SKELETON:
				if(facing == BlockFace.SOUTH || facing == BlockFace.NORTH)
					return loc.getBlock().getRelative(BlockFace.WEST).getLocation();
				else
					return loc.getBlock().getRelative(BlockFace.NORTH).getLocation();
				
			// 4x4
			case BURNINGSKULL:
			case PIGSCENE:
			case POINTER:
				return loc; // Needs work
				
			// Unsupported artwork.
			default:
				return null;
		}
	}
}
