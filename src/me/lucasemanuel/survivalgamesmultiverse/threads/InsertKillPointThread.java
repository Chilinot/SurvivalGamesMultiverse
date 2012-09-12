/**
 *  Name: InsertKillPointThread.java
 *  Date: 15:04:21 - 12 sep 2012
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

package me.lucasemanuel.survivalgamesmultiverse.threads;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class InsertKillPointThread extends Thread {
	
	private final String username;
	private final String password;
	private final String host;
	private final int    port;
	private final String database;
	private final String tablename;
	
	private final String playername;
	private final int points;
	
	public InsertKillPointThread(String username, String password, String host, int port, String database, String tablename, String playername, int points) {
		
		this.username   = username;
		this.password   = password;
		this.host       = host;
		this.port       = port;
		this.database   = database;
		this.tablename  = tablename;
		
		this.playername = playername;
		this.points     = points;
		
		start();
	}
	
	public void run() {
		try {
			
			Class.forName("com.mysql.jdbc.Driver");
			
			String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
			
			Connection con = DriverManager.getConnection(url, username, password);
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM " + tablename + " WHERE playernames='" + playername + "'");
			
			int kills = points;
			
			if(rs.next() && rs.getString("playernames") != null) {
				kills = kills + rs.getInt("kills");
				
				stmt.executeUpdate("UPDATE " + tablename + " SET kills=" + kills + " WHERE playernames='" + playername + "'");
			}
			else {
				stmt.executeUpdate("INSERT INTO " + tablename + " VALUES('" + playername + "', 0, " + kills + ", 0)");
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
