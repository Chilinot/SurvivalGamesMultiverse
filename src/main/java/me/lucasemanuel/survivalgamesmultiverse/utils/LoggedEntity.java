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
	
	private final SerializedLocation      location;
	private final EntityType              type;
	private final HashMap<String, Object> data = new HashMap<String, Object>();
	
	public LoggedEntity(Entity e) {
		location = new SerializedLocation(e.getLocation());
		type     = e.getType();
		
		if(e instanceof Hanging) {
			Hanging h = (Hanging) e;
			data.put("FacingDirection", h.getFacing());
			
			if(h instanceof Painting) {
				Painting p = (Painting) h;
				data.put("Art", p.getArt());
			}
			else if(h instanceof ItemFrame) {
				ItemFrame i = (ItemFrame) h;
				data.put("ItemStack", i.getItem().clone());
			}
		}
	}
	
	public void reset() {
		Location l = location.deserialize();
		
		switch(type) {
			case ITEM_FRAME:
				ItemFrame i = l.getWorld().spawn(l, ItemFrame.class);
				i.setFacingDirection((BlockFace) data.get("FacingDirection"));
				i.setItem((ItemStack) data.get("ItemStack"));
				break;
			
			case PAINTING:
				Painting p = l.getWorld().spawn(l, Painting.class);
				p.setFacingDirection((BlockFace) data.get("FacingDirection"));
				p.setArt((Art) data.get("Art"));
				break;
				
			default:
		}
	}
}
