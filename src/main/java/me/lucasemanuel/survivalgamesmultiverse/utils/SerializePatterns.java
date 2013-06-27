/**
 *  Name:    SerializePatterns.java
 *  Created: 15:49:37 - 20 jun 2013
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
 *  The basic patterns used to deserialize the strings from Serialize.java.
 * 
 */

package me.lucasemanuel.survivalgamesmultiverse.utils;

import java.util.regex.Pattern;

public enum SerializePatterns {
	
	INV_POSITION           (1, "([0-9]{1,2});@"),
	
	STACK_AMOUNT           (1, "@a:([0-9]{1,2})!"),
	STACK_TYPE             (1, "t:([0-9]{1,})!"),
	STACK_DATA             (1, "D:([0-9]{1,2})!"),
	STACK_DURABILITY       (1, "d:([0-9]{1,2})!"),
	STACK_ENCHANTMENTS     (1, "([0-9]{1,2}-[0-9]{1,2})\\+"),
	STACK_META_DISPLAYNAME (1, "mn:(\\[([0-9]*,)*\\])&"),
	STACK_META_LORE        (1, "(\\[([0-9]*,)*\\])/"),
	
	ENCHANTMENT_ID         (1, "(\\d{1,2})-(\\d{1,2})"),
	ENCHANTMENT_LEVEL      (2, "(\\d{1,2})-(\\d{1,2})"),
	
	ASCII                  (1, "([0-9]{1,3}),");
	
	public Pattern pattern;
	public int groupID;
	
	private SerializePatterns(int groupID, String regex) {
		this.pattern = Pattern.compile(regex);
		this.groupID = groupID;
	}
}
