SurvivalGamesMultiverse
=======================

##Description:
This plugin allows you to host multiple SurvivalGames in diffrent worlds at the same time.

######Worlds and their templates
Each world has its main world and a templateworld. When the game is over the mainworld gets reset based on the templateworld.
The plugin logs all block changes to the mainworld and then resets those blocks based on what it finds in the template world.
That means that you could do pretty much anything to the mainworld without having to worry if you can reverse the changes, you can always let my plugin reset the changes or just reset it yourself with the templateworld.

Using templateworlds also means that you can edit the templateworld while a game is ongoing in the mainworld. This means that you can just edit the templateworld and the changes will eventually be transmitted to the main world.

Theoretically this will also reduce the amount of RAM necessary to log all changes since the plugin only have to log what location was changed and not save the material, data etc for each block. It will just get that information from the templateworld.

That also enables some more fun gaming rules. For example:
 - Players can break any block.
 - Players can craft items in the world to use as armor or weapons (all drops/items are removed when the world gets reset).
 - etc

######Chests
All chests in the map gets randomized the first time a player opens them when a new game has started.
You can edit what items are going to spawn in the chests and how often they will spawn in the _itemlist.yml_.

If a player crafts a chest in the game it will automatically be added to the log of chests to not randomize, this means that players can not craft a chest and it will be randomized when they open it. It will function just as a regular chest, and when the game resets the chest gets removed.
Chests that already where placed in the world will not be added to the log of chests to not randomize, this means that all chests that where allready in the world gets randomized when a player first opens it.

Chests will only get randomized once every game. Opening and closing a gamechest (a chest that allready was in the world and not crafted by a player) will not randomize it again when opened.

######Multithreaded I/O operations
Everytime the plugin needs to talk to the database or log something in a file it happens in a seperate thread.
This will prevent server lag/freezes when the plugin needs to store some information, like kills, wins, deaths or positions.

######Stats & MySQL
When the plugin gets loaded the first time, for example when the server starts. It automatically tries to connect to the database configured in the config.yml file.
If it cant connect, no stats will be logged. But if a connection is established, it will log whenever a player wins, kills or dies and sends this information to the database.

The table containing the data has to have the following layout in the following order:


Column Name  | Type    | Primary Key | NotNull
:------------|:-------:|:-----------:|:---------:|
playernames  | VARCHAR |     YES     |    YES    |
wins         |   INT   |     NO      |    NO     |
kills        |   INT   |     NO      |    NO     |
deaths       |   INT   |     NO      |    NO     |

######Multilanguage support
Every message/broadcast in the game (except error messages for commands or in the console) can be customized.

You can edit/translate the messages in _language.yml_. 

This allows people from all nations to play the game in their native language without me having to do all the translations myself.

For example, the community im the head developer for is using this plugin in swedish. All they had to do was translate the strings in the _language.yml_ file.

##Signs:
In progress...

##Commands:
#####/sglocation
 - Description: Manages locations for the worlds.
 - Usage: /sglocation \<set/save/clear/\> \<main/arena\>
 - Example: To set a main location (starting point that players gets teleported to when they join) use this "/sglocation set main". Thats saves your location as one of the main startpoints for that world. The same goes for "/sglocation set arena" except that saves an arena locaiton.
 - Permission: multisurvival.commands.sglocation

#####/sgactivate
 - Description: Activates the game for the given world.
 - Usage: /sgactivate \<worldname\>
 - Permission: multisurvival.commands.sgactivate