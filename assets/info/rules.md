# Boulder Dash : Rules and Mechanics

## Objective

Collect all gems (*) and then reach the exit (E) to complete the level.

## Player

The player is represented by @.

The player can move up, down, left, right (usually with arrow keys or WASD).

The player can dig through dirt (.) or move into empty space ( ).

The player cannot move through walls (#).

The player cannot push boulders (O) upward, but can sometimes push them sideways if space allows.


## Gems

Gems (*) can be collected by moving onto their tile.

Gems fall like boulders when unsupported — gravity applies.

When a gem falls on the player, it causes death (same as a boulder).

Collecting all gems may unlock the exit (if your game design includes that).

## Boulders

Boulders (O) are affected by gravity:

If there’s empty space below them, they fall straight down.

If they land on another boulder or gem, they roll off to the side if there’s space.

If a boulder falls onto the player, the player dies.

A player can push a boulder left or right if:

The tile beyond the boulder in that direction is empty.

The player moves horizontally into the boulder.

## Walls

Walls (#) are indestructible and immovable.

They define the boundaries of the level.

## Dirt

Dirt (.) can be dug through by moving into it.

Once dug, it becomes empty space ( ).

## Exit

The exit (E) is the goal.

The player can enter it only after collecting all gems (optional rule).

Reaching the exit completes the level.

## Death Conditions

Crushed by a falling boulder or gem.

Trapped (no possible moves).

Optional: Timer runs out (for time-limited levels).

## Optional Advanced Rules

You can add difficulty or realism with:

Explosions: Falling objects hitting certain tiles cause an explosion.

Enemies: Moving hazards that kill the player on contact.

Unlockable exits: Exit appears only after all gems are collected.

Time limit: Player must finish before time runs out.

Scoring: Each gem collected gives points.

## Turn Logic (for coding or simulation)

Each “tick” or turn:

Process gravity:

Every boulder/gem checks the tile below.

If it’s empty, it moves down.

Process player input.

Process collisions (death, gem collection, exit reached).

# Credits
Music: https://patrickdearteaga.com/en/arcade-music/?utm_source=chatgpt.com
Font: https://fonts.google.com/specimen/Press+Start+2P
