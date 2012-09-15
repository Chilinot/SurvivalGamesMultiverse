/**
 *  Name: StatsManager.java
 *  Date: 14:53:19 - 12 sep 2012
 * 
 *  Author: LucasEmanuel @ bukkit forums
 *  
 *  
 *  Description:
 *  
 *  Used by the main-thread to initiate sub-threads that modify the data
 *  in the database.
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.managers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import me.lucasemanuel.survivalgamesmultiverse.Main;
import me.lucasemanuel.survivalgamesmultiverse.threading.ConcurrentConnection;
import me.lucasemanuel.survivalgamesmultiverse.threading.InsertDeathPointThread;
import me.lucasemanuel.survivalgamesmultiverse.threading.InsertKillPointThread;
import me.lucasemanuel.survivalgamesmultiverse.threading.InsertWinPointThread;
import me.lucasemanuel.survivalgamesmultiverse.utils.ConsoleLogger;

public class StatsManager {
	
	private ConsoleLogger logger;
	
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
		
		username  = instance.getConfig().getString("database.auth.username");
		password  = instance.getConfig().getString("database.auth.password");
		host      = instance.getConfig().getString("database.settings.host");
		port      = instance.getConfig().getInt   ("database.settings.port");
		database  = instance.getConfig().getString("database.settings.database");
		tablename = instance.getConfig().getString("database.settings.tablename");
		
		logger.info("Testing connection to database, please wait!");
		
		Connection con = null;
		
		try {
			Class.forName("com.mysql.jdbc.Driver");
			
			String url = "jdbc:mysql://" + host + ":" + port + "/" + database;
			
			con = DriverManager.getConnection(url, username, password);
		}
		catch(SQLException | ClassNotFoundException e) {
			logger.severe("Error while testing connection! Message: " + e.getMessage());
		}
		
		if(con != null) {
			
			logger.debug("Initiated");
			logger.info("Connected!");
			
			insertobject = new ConcurrentConnection(username, password, host, port, database, tablename);
			
			try {
				con.close();
			}
			catch (SQLException e) {
				logger.severe("Error while closing test connection! Message: " + e.getMessage());
			}
		}
		else
			logger.severe("No connection to database! This plugin will probably not work!");
	}
	
	public void addWinPoints(String playername, int points) {
		if(insertobject != null)
			new InsertWinPointThread(insertobject, playername, points);
	}
	
	public void addKillPoints(String playername, int points) {
		if(insertobject != null)
			new InsertKillPointThread(insertobject, playername, points);
	}
	
	public void addDeathPoints(String playername, int points) {
		if(insertobject != null)
			new InsertDeathPointThread(insertobject, playername, points);
	}
}
