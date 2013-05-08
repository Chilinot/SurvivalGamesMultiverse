/**
 *  Name:    ConcurrentSQLiteConnection.java
 *  Created: 17:37:33 - 8 maj 2013
 * 
 *  Author:  Lucas Arnstr�m - LucasEmanuel @ Bukkit forums
 *  Contact: lucasarnstrom(at)gmail(dot)com
 *  
 *
 *  Copyright 2013 Lucas Arnstr�m
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;
import me.lucasemanuel.survivalgamesmultiverse.utils.SerializedLocation;

public class ConcurrentSQLiteConnection {
	
	private ConsoleLogger logger;
	private Main plugin;
	
	private Connection con;
	
	public ConcurrentSQLiteConnection(Main instance) {
		logger = new ConsoleLogger(instance, "SQLiteConnection");
		plugin = instance;
		
		getConnection();
		
		logger.debug("Initiated");
	}
	
	public synchronized HashMap<Location, String> getSignlocations() {
		try {
			HashMap<Location, String> map = new HashMap<Location, String>();
			
			testConnection();
			
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM signlocations");
			
			while(rs.next()) {
				
				String serial    = rs.getString("serial_position");
				String worldname = rs.getString("worldname");
				
				map.put(SerializedLocation.deserializeString(serial), worldname);
			}
			
			rs.close();
			stmt.close();
			
			return map;
		}
		catch (SQLException e) {
			logger.severe("Error while retrieving saved signlocations! Message: " + e.getMessage());
			return null;
		}
	}
	
	public synchronized void saveSignLocations(HashMap<Location, String> locations) {
		
		testConnection();
		
		Statement stmt = null;
		
		try {
			stmt = con.createStatement();
		}
		catch (SQLException e) {
			logger.severe("Error while saving signs! Message: " + e.getMessage());
			return;
		}
		
		if(stmt != null)  {
			for(Entry<Location, String> entry : locations.entrySet()) {
				
				String serial    = new SerializedLocation(entry.getKey()).toString();
				String worldname = entry.getValue();
				
				try {
					stmt.execute("INSERT OR REPLACE INTO signlocations VALUES('" + serial + "', '" + worldname + "')");
				}
				catch (SQLException e) {
					logger.severe("Error while inserting signdata to database! Message: " + e.getMessage());
					return;
				}
			}
			
			try {
				stmt.close();
			}
			catch (SQLException e) {
				logger.severe("Could not close statment! Message: " + e.getMessage());
			}
		}
			
	}
	
	public synchronized void removeSign(Location location) {
		
		testConnection();
		
		try {
			Statement stmt = con.createStatement();
			
			String serial = new SerializedLocation(location).toString();
			
			stmt.execute("DELETE FROM signlocations WHERE serial_position='" + serial + "'");
			
			logger.debug("Removed sign at position: " + serial);
			
			stmt.close();
		}
		catch (SQLException e) {
			logger.severe("Error while removing sign! Message: " + e.getMessage());
		}
	}
	
	private synchronized void testConnection() {
		try {
			con.getCatalog();
		}
		catch(SQLException e) {
			logger.warning("Connection no longer valid! Trying to re-establish one...");
			getConnection();
		}
	}
	
	private synchronized void getConnection() {
		try {
			logger.debug("Getting connection...");
			
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/data.db");
			
			Statement stmt = con.createStatement();
			
			stmt.execute("CREATE TABLE IF NOT EXISTS signlocations (serial_position VARHCAR(250) NOT NULL PRIMARY KEY, worldname VARCHAR(30) NOT NULL)");
			stmt.execute("CREATE TABLE IF NOT EXISTS spawnlocations(serial_position VARHCAR(250) NOT NULL PRIMARY KEY, worldname VARCHAR(30) NOT NULL)");
			
			stmt.close();
			
			logger.debug("Connected!");
		}
		catch(ClassNotFoundException | SQLException e) {
			logger.severe("WARNING! SEVERE ERROR! Could not connect to SQLite-database in plugin-datafolder! This means it cannot load/store important data!");
			logger.severe("Error message: " + e.getMessage());
		}
	}

	public synchronized void closeConnection() {
		try {
			con.close();
		}
		catch (SQLException e) {
			logger.severe("Error while closing connection, data might have been lost! Message: " + e.getMessage());
		}
	}
}