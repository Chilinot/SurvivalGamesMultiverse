SurvivalGamesMultiverse
=======================

##Description:
This plugin allows you to host multiple SurvivalGames in diffrent worlds at the same time.

######Worlds
Each survivalgames-match takes place in its own world. Anything that happens in these worlds are logged and reset after each match.
This means that you could do pretty much anything to the world and everything will be back to normal when a new round starts.
This opens up for some interesting gameplay, like allowing all players to be able to craft their own armor or weapons, or build bridges, dig tunnels, create walls to hide behind etc.

Each match is timed, and after a configurable time all players in the match are teleported to a predefined arena to fight till the end.
If the players just wont kill each other in the arena the plugin will kill all players left and start a new match if the time runs out.

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

It is of course possible to disable this check in the config.

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
Players join by rightclicking a sign. These signs can be placed anywhere.

To create "joinsign", write [sginfo] on the first line of the sign, and the name of the gameworld on the second line.
Then just rightclick the sign to register it and it will update with the correct information.

This is how it's supposed to look before it is rightclicked.

[<img src="https://dl.dropboxusercontent.com/u/51186702/SGMultiverse/Create%20sign.PNG">](Create sign)

**Example:**

Before rightclick:

[<img src="https://dl.dropboxusercontent.com/u/51186702/SGMultiverse/Create%20sign%20-%20Example1.PNG">](Create sign - example1)

After rightclick:

[<img src="https://dl.dropboxusercontent.com/u/51186702/SGMultiverse/Create%20sign%20-%20Example2.PNG">](Create sign - example2)

##Commands:
#####/sginfo
 - Description: Displays version and worldname.
 - Usage: /sginfo
 - Permission: survivalgames.commands.sginfo
 
#####/sgdebug
 - Description: Manages debug settings.
 - Usage:  /sgdebug \<set/listen\> \<true/false\>
 - Permission: survivalgames.commands.sgdebug
 
#####/sgplayers
 - Description: Lists all remaining players for the world the sender is in.
 - Usage: /sgplayers
 - Permission: survivalgames.commands.sgplayers
 
#####/sgleave
 - Description: Allows a player to leave the game.
 - Usage: /sgleave
 - Permission: survivalgames.commands.sgleave
 
#####/sgreset
 - Description: Resets the game for the given world.
 - Usage: /sgreset \<worldname\>
 - Permission: survivalgames.commands.sgreset

#####/sglocation
 - Description: Manages locations for the worlds.
 - Usage: /sglocation \<set/save/clear/\> \<main/arena\>
 - Example: To set a main location (starting point that players gets teleported to when they join) use this "/sglocation set main". Thats saves your location as one of the main startpoints for that world. The same goes for "/sglocation set arena" except that saves an arena locaiton.
 - Permission: survivalgames.commands.sglocation

#####/sgactivate
 - Description: Activates the game for the given world.
 - Usage: /sgactivate \<worldname\>
 - Permission: survivalgames.commands.sgactivate

##Permissions:
#####survivalgames.ignore.commandfilter:
 - description: Players with this perm will be allowed to use any command while playing SG.
 - default: op

#####survivalgames.ignore.stats:
 - description: Players with this perm will not recieve points in SG.
 - default: false

#####survivalgames.ignore.forcepumpkin:
 - description: Players with this perm can remove their pumpkin helmet even if forcepumpkin is enabled in the config.
 - default: op

#####survivalgames.ignore.blockfilter:
 - description: Allows a player to break/place blocks in a game world that hasnt started.
 - default: op

#####survivalgames.ignore.clearinv:
 - description: Allows player to ignore inventory clearing upon joining SurvivalGames
 - default: op

#####survivalgames.signs.sginfo:
 - description: Allows a player to register an sginfo sign.
 - default: op
