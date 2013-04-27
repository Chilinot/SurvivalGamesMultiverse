/**
 *  Name: SerializedLocation.java
 *  Date: 21:01:38 - 6 aug 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
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