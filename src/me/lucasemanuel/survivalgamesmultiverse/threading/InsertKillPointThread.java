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

package me.lucasemanuel.survivalgamesmultiverse.threading;

public class InsertKillPointThread extends Thread {
	
	private final String playername;
	private final int points;
	
	private ConcurrentInsert insertobject;
	
	public InsertKillPointThread(ConcurrentInsert insertobject, String playername, int points) {
		
		this.playername = playername;
		this.points     = points;
		
		this.insertobject = insertobject;
		
		start();
	}
	
	public void run() {
		insertobject.insert(playername, 0, points, 0);
	}
}
