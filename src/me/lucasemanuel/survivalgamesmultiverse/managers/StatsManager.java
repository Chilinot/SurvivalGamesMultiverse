/**
 *  Name: StatsManager.java
 *  Date: 14:53:19 - 12 sep 2012
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
 *  Used by the main-thread to initiate sub-threads that modify the data
 *  in the database.
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.threading.ConcurrentConnection;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class StatsManager {
	
	private ConsoleLogger logger;
	
	private HashMap<String, PlayerScore> playerstats;
	
	private final String username;
	private final String password;
	private final String host;
	private final int    port;
	private final String database;
	private final String tablename;
	
	private ConcurrentConnection insertobject = null;
	
	public StatsManager(Main instance) {
		
		logger = new ConsoleLogger(instance, "StatsManager");
		logger.debug("Loading settings");
		
		playerstats = new HashMap<String, PlayerScore>();
		
		username  = instance.getConfig().getString("database.auth.username");
		password  = instance.getConfig().getString("database.auth.password");
		host      = instance.getConfig().getString("database.settings.host");
		port      = instance.getConfig().getInt   ("database.settings.port");
		database  = instance.getConfig().getString("database.settings.database");
		tablename = instance.getConfig().getString("database.settings.tablename");
		
		if(instance.getConfig().getBoolean("database.enabled")) {
			
			logger.info("Testing connection to database, please wait!");
			
			insertobject = new ConcurrentConnection(username, password, host, port, database, tablename);
			
			if(!insertobject.testConnection()) {
				insertobject = null;
				logger.severe("No connection to database! Stats will not be saved!");
			}
		}
		else {
			logger.info("Database logging disabled! No stats will be saved.");
		}
	}
	
	public void checkScoreboard(String playername) {
		if(!playerstats.containsKey(playername)) {
			Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
			playerstats.put(playername, new PlayerScore(board));
			Bukkit.getPlayerExact(playername).setScoreboard(board);
		}
	}
	
	public boolean removeScoreboard(String playername) {
		Player player = Bukkit.getPlayerExact(playername);
		
		if(player != null && playerstats.containsKey(playername)) {
			
			player.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
			playerstats.remove(playername);
			
			return true;
		}
		else 
			return false;
	}
	
	public void addWinPoints(final String playername, final int points) {
		
		/* 
		 *  - Database
		 */
		
		if(insertobject != null) {
			new Thread() {
				public void run() {
					insertobject.update(playername, points, 0, 0);
				}
			}.start();
		}
	}
	
	public void addKillPoints(final String playername, final int points) {
		
		checkScoreboard(playername);
		
		playerstats.get(playername).addKills(points);
		
		
		/* 
		 *  - Database
		 */
		
		if(insertobject != null) {
			new Thread() {
				public void run() {
					insertobject.update(playername, 0, points, 0);
				}
			}.start();
		}
	}
	
	public void addDeathPoints(final String playername, final int points) {
		
		
		/* 
		 *  - Database
		 */
		
		if(insertobject != null) {
			new Thread() {
				public void run() {
					insertobject.update(playername, 0, 0, points);
				}
			}.start();
		}
	}
}

class PlayerScore {
	
	private Scoreboard board;
	private int        kills;
	
	public PlayerScore(Scoreboard board) {
		
		this.board  = board;
		this.kills  = 0;
		
		Objective o = board.registerNewObjective("stats", "dummy");
		
		o.setDisplayName("Stats:");
		o.setDisplaySlot(DisplaySlot.SIDEBAR);
		
		Score s = o.getScore(Bukkit.getOfflinePlayer("Kills:"));
		s.setScore(kills);
	}
	
	private void updateScoreboard() {
		
		Objective o = board.getObjective(DisplaySlot.SIDEBAR);
		
		Score s = o.getScore(Bukkit.getOfflinePlayer("Kills:"));
		s.setScore(kills);
	}
	
	public void addKills(int amount) {
		this.kills += amount;
		updateScoreboard();
	}
}