/**
 *  Name: WorldGuardHook.java
 *  Date: 00:31:35 - 23 sep 2012
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

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.plugin.Plugin;

import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class WorldGuardHook {

	public static boolean isInRegion(Location playerlocation, String regionname) {

		if (regionname == null) {
			return true;
		}
		ApplicableRegionSet set = getWGSet(playerlocation);
		if (set == null) {
			return false;
		}
		for (ProtectedRegion r : set) {
			if (r.getId().equalsIgnoreCase(regionname)) {
				return true;
			}
		}
		return false;
	}

	private static ApplicableRegionSet getWGSet(Location loc) {
		WorldGuardPlugin wg = getWorldGuard();
		if (wg == null) {
			return null;
		}
		RegionManager rm = wg.getRegionManager(loc.getWorld());
		if (rm == null) {
			return null;
		}
		return rm.getApplicableRegions(com.sk89q.worldguard.bukkit.BukkitUtil.toVector(loc));
	}

	public static WorldGuardPlugin getWorldGuard() {
		Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("WorldGuard");

		// WorldGuard may not be loaded
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			return null;
		}
		return (WorldGuardPlugin) plugin;
	}

}
