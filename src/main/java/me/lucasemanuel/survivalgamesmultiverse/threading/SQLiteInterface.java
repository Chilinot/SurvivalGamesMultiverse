/**
 *  Name:    ConcurrentSQLiteConnection.java
 *  Created: 17:37:33 - 8 maj 2013
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

package me.lucasemanuel.survivalgamesmultiverse.threading;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.SerializedLocation;

public class SQLiteInterface {
	
	private Main plugin;
	private final String folder;
	
	private Connection con;
	
	// Dummy object for locking purpose
	private Object lock = new Object();
	
	public SQLiteInterface(Main instance) {
		plugin = instance;
		
		folder = plugin.getDataFolder().getAbsolutePath();
		
		getConnection();
	}
	
	private void testConnection() {
		synchronized(lock) {
			try {
				con.getCatalog();
			}
			catch(SQLException e) {
				System.out.println("Connection no longer valid! Trying to re-establish one...");
				getConnection();
			}
		}
	}
	
	private void getConnection() {
		synchronized(lock) {
			try {
				Class.forName("org.sqlite.JDBC");
				con = DriverManager.getConnection("jdbc:sqlite:" + folder + "/data.db");
				
				Statement stmt = con.createStatement();
				
				stmt.execute("CREATE TABLE IF NOT EXISTS signlocations (serial_position VARHCAR(250) NOT NULL PRIMARY KEY, worldname VARCHAR(30) NOT NULL)");
				stmt.execute("CREATE TABLE IF NOT EXISTS startlocations(serial_position VARHCAR(250) NOT NULL PRIMARY KEY, worldname VARCHAR(30) NOT NULL, type VARCHAR(10) NOT NULL)");
				stmt.execute("CREATE TABLE IF NOT EXISTS playerstats   (playername VARHCAR(250)      NOT NULL PRIMARY KEY, wins INT(10), kills INT(10), deaths INT(10))");
				
				stmt.close();
			}
			catch(ClassNotFoundException | SQLException e) {
				System.out.println("WARNING! SEVERE ERROR! Could not connect to SQLite-database in plugin-datafolder! This means it cannot load/store important data!");
				System.out.println("Error message: " + e.getMessage());
			}
		}
	}

	public void closeConnection() {
		synchronized(lock) {
			try {
				con.close();
			}
			catch (SQLException e) {
				System.out.println("Error while closing connection, data might have been lost! " +
						"Message: " + e.getMessage());
			}
		}
	}
	
	public ArrayList<HashSet<String>> getStartLocations(String worldname) {
		synchronized(lock) {
			
			String select = "SELECT * " +
							"FROM startlocations " +
							"WHERE worldname = ? " +
							"AND type = ?";
			try {
				testConnection();
				
				ArrayList<HashSet<String>> locations = new ArrayList<HashSet<String>>();
				
				PreparedStatement stmt_main  = con.prepareStatement(select);
				PreparedStatement stmt_arena = con.prepareStatement(select);
				
				stmt_main.setString(1, worldname); stmt_main.setString(2, "main");
				ResultSet rs_main = stmt_main.executeQuery();
				
				stmt_arena.setString(1, worldname); stmt_arena.setString(2, "arena");
				ResultSet rs_arena = stmt_arena.executeQuery();
				
				HashSet<String> main  = new HashSet<String>();
				HashSet<String> arena = new HashSet<String>();
				
				while(rs_main.next()) {
					main.add(rs_main.getString(1));
				}
				
				while(rs_arena.next()) {
					arena.add(rs_arena.getString(1));
				}
				
				locations.add(main);
				locations.add(arena);
				
				rs_main.close();
				rs_arena.close();
				stmt_main.close();
				stmt_arena.close();
				
				return locations;
			}
			catch(SQLException e) {
				System.out.println("Error while retrieving startlocations for world " + worldname + ". " +
						"Message: " + e.getMessage());
				return null;
			}
		}
	}
	
	public void saveStartLocations(String worldname, String type, Set<SerializedLocation> locations) {
		synchronized(lock) {
			testConnection();
			
			String insert_s = "INSERT OR REPLACE INTO startlocations " +
							  "VALUES( ? , ? , ? )";
			
			try {
				PreparedStatement stmt = con.prepareStatement(insert_s);
				
				for(SerializedLocation l : locations) {
					stmt.setString(1, l.toString());
					stmt.setString(2, worldname);
					stmt.setString(3, type);
					stmt.addBatch();
				}
				
				stmt.executeBatch();
				stmt.close();
			}
			catch(SQLException e) {
				System.out.println("Error while saving startlocations for world " + worldname + ". " +
						"Message: " + e.getMessage());
			}
		}
	}
	
	public void clearStartLocations(String worldname, String type) {
		synchronized(lock) {
			testConnection();
			
			String delete_s = "DELETE FROM startlocations " +
							  "WHERE worldname = ? " +
							  "AND type = ?";
			try {
				PreparedStatement stmt = con.prepareStatement(delete_s);
				stmt.setString(1, worldname);
				stmt.setString(2, type);
				stmt.execute();
				stmt.close();
			}
			catch (SQLException e) {
				System.out.println("Error while clearing startlocations! " +
						"Message: " + e.getMessage());
			}
		}
	}
	
	public HashMap<String, String> getSignlocations() {
		synchronized(lock) {
			try {
				HashMap<String, String> map = new HashMap<String, String>();
				
				testConnection();
				
				Statement stmt = con.createStatement();
				ResultSet rs = stmt.executeQuery("SELECT * FROM signlocations");
				
				while(rs.next()) {
					
					String serial    = rs.getString("serial_position");
					String worldname = rs.getString("worldname");
					
					map.put(serial, worldname);
				}
				
				rs.close();
				stmt.close();
				
				return map;
			}
			catch (SQLException e) {
				System.out.println("Error while retrieving saved signlocations! Message: " + e.getMessage());
				return null;
			}
		}
	}
	
	public void saveSignLocations(HashMap<SerializedLocation, String> locations) {
		synchronized(lock) {
			testConnection();
			
			String insert_s = "INSERT OR REPLACE INTO signlocations " +
							  "VALUES( ? , ? )";
			
			try {
				PreparedStatement stmt = con.prepareStatement(insert_s);
				
				for(Entry<SerializedLocation, String> entry : locations.entrySet()) {
					stmt.setString(1, entry.getKey().toString());
					stmt.setString(2, entry.getValue());
					stmt.addBatch();
				}
				
				stmt.executeBatch();
				stmt.close();
			}
			catch (SQLException e) {
				System.out.println("Error while saving signs! Message: " + e.getMessage());
			}
		}
	}
	
	public void removeSign(SerializedLocation l) {
		synchronized(lock) {
			testConnection();
			
			String delete = "DELETE FROM signlocations " +
							"WHERE serial_position = ?";
			
			try {
				PreparedStatement stmt = con.prepareStatement(delete);
				stmt.setString(1, l.toString());
				stmt.execute();
				stmt.close();
			}
			catch (SQLException e) {
				System.out.println("Error while removing sign! Message: " + e.getMessage());
			}
		}
	}
	
	public void loadPlayerStats(final String playername) {
		
		final int[] stats = new int[3];
		
		synchronized(lock) {
			testConnection();
			
			String select = "SELECT * " +
							"FROM playerstats " +
							"WHERE playername = ?";
			try {
				PreparedStatement stmt = con.prepareStatement(select);
				
				stmt.setString(1, playername);
				
				ResultSet rs = stmt.executeQuery();
				
				if(rs.next()) {
					stats[0] = rs.getInt("wins");
					stats[1] = rs.getInt("kills");
					stats[2] = rs.getInt("deaths");
				}
				
				rs.close();
				stmt.close();
			}
			catch (SQLException e) {
				System.out.println("Error while loading stats for player= " + playername + "! " +
						"Message: " + e.getMessage());
			}
		}
		
		plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable() {
			public void run() {
				plugin.getStatsManager().addWinPoints(  playername, stats[0], false);
				plugin.getStatsManager().addKillPoints( playername, stats[1], false);
				plugin.getStatsManager().addDeathPoints(playername, stats[2], false);
			}
		});
	}

	public void addScore(String playername, int wins, int kills, int deaths) {
		synchronized(lock) {
			testConnection();
			
			String select_s = "SELECT * FROM playerstats WHERE playername = ?";
			
			String update_s = "UPDATE playerstats " +
							  "SET wins = wins + ? , kills = kills + ? , deaths = deaths + ? " +
							  "WHERE playername = ?";
			
			String insert_s = "INSERT INTO playerstats " +
							  "VALUES( ? , ? , ? , ? )";
			
			PreparedStatement select = null;
			PreparedStatement update = null;
			PreparedStatement insert = null;
			
			try {
				select = con.prepareStatement(select_s);
				select.setString(1, playername);
				
				ResultSet rs = select.executeQuery();
				
				if(rs.next()) {
					update = con.prepareStatement(update_s);
					update.setInt(1, wins);
					update.setInt(2, kills);
					update.setInt(3, deaths);
					update.setString(4, playername);
					update.execute();
					update.close();
				}
				else {
					insert = con.prepareStatement(insert_s);
					insert.setString(1, playername);
					insert.setInt(2, wins);
					insert.setInt(3, kills);
					insert.setInt(4, deaths);
					insert.execute();
					insert.close();
				}
				
				rs.close();
				select.close();
			}
			catch (SQLException e) {
				System.out.println("Error while saving stats for player= " + playername + "! " +
						"Message: " + e.getMessage());
			}
		}
	}
}
