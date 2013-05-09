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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Location;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.utils.SerializedLocation;

public class ConcurrentSQLiteConnection {
	
	private Main plugin;
	
	private Connection con;
	
	public ConcurrentSQLiteConnection(Main instance) {
		plugin = instance;
		getConnection();
	}
	
	private synchronized void testConnection() {
		try {
			con.getCatalog();
		}
		catch(SQLException e) {
			System.out.println("Connection no longer valid! Trying to re-establish one...");
			getConnection();
		}
	}
	
	private synchronized void getConnection() {
		try {
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + plugin.getDataFolder() + "/data.db");
			
			Statement stmt = con.createStatement();
			
			stmt.execute("CREATE TABLE IF NOT EXISTS signlocations (serial_position VARHCAR(250) NOT NULL PRIMARY KEY, worldname VARCHAR(30) NOT NULL)");
			stmt.execute("CREATE TABLE IF NOT EXISTS startlocations(serial_position VARHCAR(250) NOT NULL PRIMARY KEY, worldname VARCHAR(30) NOT NULL, type VARCHAR(10) NOT NULL)");
			
			stmt.close();
		}
		catch(ClassNotFoundException | SQLException e) {
			System.out.println("WARNING! SEVERE ERROR! Could not connect to SQLite-database in plugin-datafolder! This means it cannot load/store important data!");
			System.out.println("Error message: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public synchronized void closeConnection() {
		try {
			con.close();
		}
		catch (SQLException e) {
			System.out.println("Error while closing connection, data might have been lost! Message: " + e.getMessage());
		}
	}
	
	public synchronized ArrayList<HashSet<Location>> getStartLocations(String worldname) {
		try {
			testConnection();
			
			ArrayList<HashSet<Location>> locations = new ArrayList<HashSet<Location>>();
			
			Statement stmt_main  = con.createStatement();
			Statement stmt_arena = con.createStatement();
			
			ResultSet rs_main  = stmt_main.executeQuery ("SELECT * FROM startlocations WHERE worldname='" + worldname + "' AND type='main'");
			ResultSet rs_arena = stmt_arena.executeQuery("SELECT * FROM startlocations WHERE worldname='" + worldname + "' AND type='arena'");
			
			HashSet<Location> main  = new HashSet<Location>();
			HashSet<Location> arena = new HashSet<Location>();
			
			while(rs_main.next()) {
				main.add(SerializedLocation.deserializeString(rs_main.getString("serial_position")));
			}
			
			while(rs_arena.next()) {
				arena.add(SerializedLocation.deserializeString(rs_arena.getString("serial_position")));
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
			System.out.println("Error while retrieving startlocations for world " + worldname + ". Message: " + e.getMessage());
			return null;
		}
	}
	
	public synchronized void saveStartLocations(String worldname, String type, Set<SerializedLocation> locations) {
		try {
			testConnection();
			
			Statement stmt = con.createStatement();
			
			for(SerializedLocation l : locations) {
				String serial = l.toString();
				stmt.execute("INSERT OR REPLACE INTO startlocations VALUES('" + serial + "', '" + worldname + "', '" + type + "')");
			}
			
			stmt.close();
		}
		catch(SQLException e) {
			System.out.println("Error while saving startlocations for world " + worldname + ". Message: " + e.getMessage());
		}
	}
	
	public synchronized void clearStartLocations(String worldname, String type) {
		try {
			testConnection();
			
			Statement stmt = con.createStatement();
			stmt.execute("DELETE FROM startlocations WHERE worldname='" + worldname + "' AND type='" + type + "'");
			stmt.close();
		}
		catch (SQLException e) {
			System.out.println("Error while clearing startlocations! Message: " + e.getMessage());
		}
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
			System.out.println("Error while retrieving saved signlocations! Message: " + e.getMessage());
			return null;
		}
	}
	
	public synchronized void saveSignLocations(HashMap<SerializedLocation, String> locations) {
		
		testConnection();
		
		Statement stmt = null;
		
		try {
			stmt = con.createStatement();
		}
		catch (SQLException e) {
			System.out.println("Error while saving signs! Message: " + e.getMessage());
			return;
		}
		
		if(stmt != null)  {
			for(Entry<SerializedLocation, String> entry : locations.entrySet()) {
				
				String serial    = entry.getKey().toString();
				String worldname = entry.getValue();
				
				try {
					stmt.execute("INSERT OR REPLACE INTO signlocations VALUES('" + serial + "', '" + worldname + "')");
				}
				catch (SQLException e) {
					System.out.println("Error while inserting signdata to database! Message: " + e.getMessage());
					return;
				}
			}
			
			try {
				stmt.close();
			}
			catch (SQLException e) {
				System.out.println("Could not close statment! Message: " + e.getMessage());
			}
		}
			
	}
	
	public synchronized void removeSign(Location location) {
		
		testConnection();
		
		try {
			Statement stmt = con.createStatement();
			
			String serial = new SerializedLocation(location).toString();
			
			stmt.execute("DELETE FROM signlocations WHERE serial_position='" + serial + "'");
			
			stmt.close();
		}
		catch (SQLException e) {
			System.out.println("Error while removing sign! Message: " + e.getMessage());
		}
	}
}
