/**
 *  Name: InsertDeathPointThread.java
 *  Date: 16:27:53 - 12 sep 2012
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

public class InsertDeathPointThread extends Thread {
	
	private final String playername;
	private final int points;
	
	private ConcurrentInsert insertobject;
	
	public InsertDeathPointThread(ConcurrentInsert insertobject, String playername, int points) {
		
		this.playername = playername;
		this.points     = points;
		
		this.insertobject = insertobject;
		
		start();
	}
	
	public void run() {
		insertobject.insert(playername, 0, 0, points);
	}
}
