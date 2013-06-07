/**
 *  Name:    StatusManager.java
 *  Created: 23:59:55 - 7 jun 2013
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

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.util.HashMap;

import org.bukkit.scheduler.BukkitTask;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class StatusManager2 {
	
	private Main plugin;
	private ConsoleLogger logger;
	
	private HashMap<String, Game> games;
	
	public StatusManager2(Main instance) {
		plugin = instance;
		logger = new ConsoleLogger(instance, "StatusManager");
		
		logger.debug("Loading configured times.");
		Game.countdown_first = plugin.getConfig().getInt("timeoutTillStart");
		Game.countdown_arena = plugin.getConfig().getInt("timeoutTillArenaInSeconds");
		Game.countdown_end   = plugin.getConfig().getInt("timeoutAfterArena");
		
		logger.debug("Initiated");
	}
	
	public void addWorld(String worldname) {
		int ptwf = plugin.getConfig().getInt("worldnames." + worldname + ".players_to_wait_for");
		games.put(worldname, new Game(plugin, worldname, ptwf));
	}
	
	public boolean startCountdown(final String worldname) {
		if(games.containsKey(worldname)) {
			
			final Game game = games.get(worldname);
			
			game.setTask(plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable() {
				public void run() {
					game.startCounter();
				}
			}, 0L, 20L));
			return true;
		}
		else 
			return false;
	}
}

class Game {
	
	private Main plugin;
	
	private final String worldname;
	private BukkitTask task = null;
	private int flag = 0;
	
	private long time_of_initiation = 0L;
	
	private int players_to_wait_for;
	
	// Configured times
	public static int countdown_first;
	public static int countdown_arena;
	public static int countdown_end;
	
	public Game(Main plugin, String worldname, int ptwf) {
		this.worldname = worldname;
		players_to_wait_for = ptwf;
	}
	
	public void startCounter() {
		if(time_of_initiation == 0L) {
			time_of_initiation = System.currentTimeMillis();
			return;
		}
			
		int timepassed = (int) (System.currentTimeMillis() - time_of_initiation);
		
		if(timepassed >= countdown_first) {
			activate();
			plugin.getWorldManager().broadcast(worldname, plugin.getLanguageManager().getString("gamestarted"));
		}
		else
			plugin.getWorldManager().broadcast(worldname, (countdown_first - timepassed) + plugin.getLanguageManager().getString("timeleft"));
	}
	
	public void activate() {
		cancelTask();
		flag = 1;
		
		
	}
	
	public int getFlag() {
		return flag;
	}
	
	public void setTask(BukkitTask task) {
		this.task = task;
	}
	
	public void cancelTask() {
		if(task != null) {
			task.cancel();
			task = null;
		}
	}
}














