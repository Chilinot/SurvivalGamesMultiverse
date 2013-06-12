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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class ConcurrentMySQLConnection {
	
	private final String username;
	private final String password;
	private final String host;
	private final int    port;
	private final String database;
	private final String tablename;

	public ConcurrentMySQLConnection(String username, String password, String host, int port, String database, String tablename) {
		
		this.username   = username;
		this.password   = password;
		this.host       = host;
		this.port       = port;
		this.database   = database;
		this.tablename  = tablename;
	}
	
	public synchronized void update(String playername, int[] s) {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
			Connection con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename + " WHERE playernames='" + playername + "'");
			
			if(rs.next() && rs.getString("playernames") != null) {
				stmt.executeUpdate("UPDATE " + tablename + " SET wins=" + s[0] + ", kills=" + s[1] + ", deaths=" + s[2] + " WHERE playernames='" + playername + "'");
			}
			else {
				stmt.executeUpdate("INSERT INTO " + tablename + " VALUES('" + playername + "', " + s[0] + ", " + s[1] + ", " + s[2] + ")");
			}
			
			rs.close();
			stmt.close();
			con.close();
		}
		catch(SQLException | ClassNotFoundException e) {
			System.out.println("Error while inserting killpoint! Message: " + e.getMessage());
		}
	}
	
	public synchronized void testConnection() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
		Connection con = DriverManager.getConnection(url, username, password);
		con.close();
	}
}
