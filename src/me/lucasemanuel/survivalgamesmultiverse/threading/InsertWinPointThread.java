/**
 *  Name: InsertWinPointThread.java
 *  Date: 16:25:27 - 12 sep 2012
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

public class InsertWinPointThread extends Thread {
	
	private final String playername;
	private final int points;
	
	private ConcurrentInsert insertobject;
	
	public InsertWinPointThread(ConcurrentInsert insertobject, String playername, int points) {
		
		this.playername = playername;
		this.points     = points;
		
		this.insertobject = insertobject;
		
		start();
	}

	public void run() {
		insertobject.insert(playername, points, 0, 0);
	}
}
