/**
 *  Name: ConcurrentConnection.java
 *  Date: 16:00:42 - 15 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
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

public class ConcurrentConnection {
	
	private final String username;
	private final String password;
	private final String host;
	private final int    port;
	private final String database;
	private final String tablename;

	public ConcurrentConnection(String username, String password, String host, int port, String database, String tablename) {
		
		this.username   = username;
		this.password   = password;
		this.host       = host;
		this.port       = port;
		this.database   = database;
		this.tablename  = tablename;
	}
	
	public synchronized void update(String playername, int wins, int kills, int deaths) {
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			
			String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
			
			Connection con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename + " WHERE playernames='" + playername + "'");
			
			if(rs.next() && rs.getString("playernames") != null) {
				
				wins   = wins   + rs.getInt("wins");
				kills  = kills  + rs.getInt("kills");
				deaths = deaths + rs.getInt("deaths");
				
				stmt.executeUpdate("UPDATE " + tablename + " SET wins=" + wins + ", kills=" + kills + ", deaths=" + deaths + " WHERE playernames='" + playername + "'");
			}
			else {
				stmt.executeUpdate("INSERT INTO " + tablename + " VALUES('" + playername + "', " + wins + ", " + kills + ", " + deaths + ")");
			}
			
			rs.close();
			stmt.close();
			con.close();
		}
		catch(SQLException | ClassNotFoundException e) {
			System.out.println("Error while inserting killpoint! Message: " + e.getMessage());
		}
	}
}
