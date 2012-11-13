/**
 *  Name: SerialSign.java
 *  Date: 18:42:47 - 13 nov 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
 *  
 *  
 *  
 * 
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.utils;

import java.io.Serializable;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class SerialSign implements Serializable {
	private static final long serialVersionUID = 8162439091234189617L;

	private final String gameworld;
	
	private final String worldname;
	private final int x;
	private final int y;
	private final int z;
	
	public SerialSign(Block block, String gameworld) {
		
		this.gameworld = gameworld;
		
		this.worldname = block.getWorld().getName();
		this.x = block.getX();
		this.y = block.getY();
		this.z = block.getZ();
	}
	
	public String getGameworld() {
		return this.gameworld;
	}
	
	public Sign getSign() {
		
		Block block = Bukkit.getWorld(worldname).getBlockAt(x, y, z);
		
		if(block.getType().equals(Material.WALL_SIGN) || block.getType().equals(Material.SIGN_POST)) {
			return (Sign) block.getState();
		}
		
		return null;
	}
}
