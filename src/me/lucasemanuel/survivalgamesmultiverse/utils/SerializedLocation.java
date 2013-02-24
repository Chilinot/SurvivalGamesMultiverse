/**
 *  Name: SerializedLocation.java
 *  Date: 21:01:38 - 6 aug 2012
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
 *
 *  Filedescription:
 *  
 *  
 *  
 * 
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.utils;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class SerializedLocation implements Serializable {
	private static final long serialVersionUID = -9094035533656633605L;
	
	private final String WORLDNAME;
	private final double X;
	private final double Y;
	private final double Z;
	private final float YAW;
	private final float PITCH;
	
	public SerializedLocation(Location location) {
		this.WORLDNAME = location.getWorld().getName();
		this.X         = location.getX();
		this.Y         = location.getY();
		this.Z         = location.getZ();
		this.YAW       = location.getYaw();
		this.PITCH     = location.getPitch();
	}
	
	public Location deserialize() {
		return new Location(Bukkit.getWorld(this.WORLDNAME), this.X, this.Y, this.Z, this.YAW, this.PITCH);
	}
}