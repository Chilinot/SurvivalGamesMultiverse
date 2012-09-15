SurvivalGamesMultiverse
=======================

##Description:
This plugin allows you to host multiple SurvivalGames in diffrent worlds at the same time.

######Worlds and their templates
Each world has its main world and a templateworld. When the game is over the mainworld gets reset based on the templateworld.
The plugin logs all block changes to the mainworld and then resets those blocks based on what it finds in the template world.
That means that you could do pretty much anything to the mainworld without having to worry if you can reverse the changes, you can always let my plugin reset the changes or just reset it yourself with the templateworld.

Using templateworlds also means that you can edit the template world while a game is ongoing in the mainworld. This means that you can just edit the templateworld and the changes will eventually be transmitted to the main world.

Theoretically this will also reduce the amount of RAM necessary to log all changes since the plugin only have to log what location was changed and not save the material, data etc for each block. It will just get that information from the templateworld.

That also enables some more fun gaming rules. For example:
 - Players can break any block.
 - Players can craft items in the world to use as armor or weapons (all drops/items are removed when the world gets reset).
 - etc

######Chests
All chests in the map gets randomized the first time a player opens them when a new game has started.
You can edit what items are going to spawn in the chests and how often they will spawn in the itemlist.yml.

##Signs:
In progress...

##Commands:
#####/sglocation
 - Description: Manages locations for the worlds.
 - Usage: /sglocation \<set/save/clear/\> \<main/arena\>
 - Example: To set a main location (starting point that players gets teleported to when they join) use this "/sglocation set main". Thats saves your location as one of the main startpoints for that world. The same goes for "/sglocation set arena" except that saves an arena locaiton.