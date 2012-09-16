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
	
	private ConcurrentConnection insertobject;
	
	public InsertDeathPointThread(ConcurrentConnection insertobject, String playername, int points) {
		
		this.playername = playername;
		this.points     = points;
		
		this.insertobject = insertobject;
		
		start();
	}
	
	public void run() {
		insertobject.update(playername, 0, 0, points);
	}
}