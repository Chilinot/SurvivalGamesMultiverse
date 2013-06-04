/**
 *  Name:    v1_5_R3.java
 *  Created: 15:51:02 - 3 jun 2013
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

package me.lucasemanuel.survivalgamesmultiverse.reflection;

import org.bukkit.Chunk;
import org.bukkit.block.Block;

public class v1_5_R3 implements NMS {
	@Override
	public boolean setBlockFast(Block b, int typeId, byte data) {
		Chunk c = b.getChunk();
		net.minecraft.server.v1_5_R3.Chunk chunk = ((org.bukkit.craftbukkit.v1_5_R3.CraftChunk) c).getHandle();
		return chunk.a(b.getX() & 15, b.getY(), b.getZ() & 15, typeId, data);
	}
}
