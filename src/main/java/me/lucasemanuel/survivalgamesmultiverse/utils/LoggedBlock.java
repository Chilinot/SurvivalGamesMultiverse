/**
 *  Name:    LoggedBlock.java
 *  Created: 19:19:39 - 8 jun 2013
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

import me.desht.dhutils.block.MassBlockUpdate;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Sign;

public class LoggedBlock {

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

	public void reset(MassBlockUpdate mbu) {
		
		mbu.setBlock(X, Y, Z, MATERIAL, DATA);
		
		if (SIGN_LINES != null && (MATERIAL == Material.SIGN_POST.getId() || MATERIAL == Material.WALL_SIGN.getId())) {

			Sign sign = (Sign) Bukkit.getWorld(WORLDNAME).getBlockAt(X, Y, Z).getState();

			for (int i = 0; i < 4; i++) {
				sign.setLine(i, SIGN_LINES[i]);
			}

			sign.update();
		}
	}
}
