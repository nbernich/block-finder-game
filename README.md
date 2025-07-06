# Block Finder Minigame

A plugin for playing block scavenger hunt in Minecraft.

This plugin currently supports **Paper 1.21.4**.

## Gameplay

Players begin by joining one of several opposing teams (entirely configurable). When the game starts, all players will be told 3 (configurable, up to 8) "target blocks" that they need to search for. The target blocks are selected randomly from a list of all blocks in the game.

Each team will race to locate _any_ of the target blocks and crouch on top of one of them. The first player to crouch on a target block will win the round for their team, and their team will earn one point. Then, _all_ target blocks will refresh. The players now need to search for the new target blocks. The game continues until stopped by an operator, at which point the final scores will be displayed.

## Features

- Easy command interface for joining teams, managing the game, and viewing scores (tab suggestions and help menu included)
- Custom team names/colors
- Optional sidebar scoreboard display
- Configurable target block count and prevention of repeat block selection

## Commands

- `/bfinder` - show current target blocks, if the game is running
- `/bfinder help` - show help message for using commands
- `/bfinder start` - start the game (op only)
- `/bfinder stop` - stop the game (op only)
- `/bfinder skip` - skip the current round, refreshing target blocks (op only)


- `/bhteams` - list current team memberships
- `/bhteams join <team name>` - join a team
- `/bhteams leave` - leave your current team


- `/bfscores` - list the scores of each team
- `/bfscores set <team name> <score>` - set the score for a team (op only)

## Installation

- Download the minigame plugin `.jar` from the Releases tab
- Move the `.jar` into the `plugins` folder of your Paper server
- After running the server once with the plugin enabled, a `BlockFinderPlugin` directory will be created in the plugins folder. It will contain the `config.yml` file that you can edit to your liking. This includes tweaking game settings and customizing your own teams.

Enjoy!
