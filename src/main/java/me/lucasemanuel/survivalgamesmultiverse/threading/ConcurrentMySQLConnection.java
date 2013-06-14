/**
 *  Name: ConcurrentConnection.java
 *  Date: 16:00:42 - 15 sep 2012
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
 *  Filedescription:
 *  
 *  Used to insert/update data in the database.
 *  
 *  It will only allow one thread at a time to talk to the database,
 *  this will remove problems concurrent modifications of the data.
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.threading;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConcurrentMySQLConnection {
	
	private final String url;
	private final String username;
	private final String password;
	
	private final String select_s;
	private final String update_s;
	private final String insert_s;

	public ConcurrentMySQLConnection(String username, String password, String host, int port, String database, String tablename) {
		
		// Load the driver
		try {
			Class.forName("com.mysql.jdbc.Driver");
		}
		catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		
		this.url      = "jdbc:mysql://" + host + ":" + port + "/" + database;
		this.username = username;
		this.password = password;
		
		this.select_s = "SELECT * " +
						"FROM " + tablename + " " +
						"WHERE playernames = ?";
		
		this.update_s = "UPDATE " + tablename + " " +
						"SET wins = ? , kills = ? , deaths = ? " +
						"WHERE playernames = ?";
		
		this.insert_s = "INSERT INTO " + tablename + " " +
						"VALUES( ? , ? , ? , ? )";
	}
	
	public synchronized void update(String playername, int[] s) {
		PreparedStatement select = null;
		PreparedStatement update = null;
		PreparedStatement insert = null;
		
		try {
			Connection con = DriverManager.getConnection(url, username, password);

			select = con.prepareStatement(select_s);
			select.setString(1, playername);
			ResultSet rs = select.executeQuery();
			
			if(rs.next() && rs.getString(1) != null) {
				update = con.prepareStatement(update_s);
				update.setInt(1, s[0]);
				update.setInt(2, s[1]);
				update.setInt(3, s[2]);
				update.setString(4, playername);
				update.executeUpdate();
				update.close();
			}
			else {
				insert = con.prepareStatement(insert_s);
				insert.setString(1, playername);
				insert.setInt(2, s[0]);
				insert.setInt(3, s[1]);
				insert.setInt(4, s[2]);
				insert.executeUpdate();
				insert.close();
			}
			
			rs.close();
			select.close();
			con.close();
		}
		catch(SQLException e) {
			System.out.println("Error while inserting killpoint! Message: " + e.getMessage());
		}
	}
	
	public synchronized void testConnection() throws SQLException, ClassNotFoundException {
		Connection con = DriverManager.getConnection(url, username, password);
		con.close();
	}
}
