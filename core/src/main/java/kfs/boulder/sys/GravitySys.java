package kfs.boulder.sys;

import kfs.boulder.Tile;
import kfs.boulder.World;
import kfs.boulder.comp.*;
import kfs.boulder.ecs.Entity;
import kfs.boulder.ecs.KfsSystem;

public class GravitySys implements KfsSystem {

    private final World world;

    public GravitySys(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        for (Entity e : world.getEntitiesWith(ItemComp.class, PositionComp.class, RenderComp.class)) {
            MovingComp mc = world.getComponent(e, MovingComp.class);
            if (mc != null) continue;
            PositionComp pc = world.getComponent(e, PositionComp.class);
            if (pc.y > 0 && world.getTileType(pc.x, pc.y-1) == Tile.EMPTY) {
                move(e, pc, pc.x, pc.y-1);
            } else if (pc.y > 0 // right
                && world.getTileType(pc.x+1, pc.y) == Tile.EMPTY
                && world.getTileType(pc.x+1, pc.y-1) == Tile.EMPTY
            ) {
                move(e, pc, pc.x+1, pc.y-1);
            } else if (pc.y > 0 // left
                && world.getTileType(pc.x-1, pc.y) == Tile.EMPTY
                && world.getTileType(pc.x-1, pc.y-1) == Tile.EMPTY
            ) {
                move(e, pc, pc.x-1, pc.y-1);
            } else if (pc.y > 0 && world.getTileType(pc.x, pc.y-1) == Tile.PLAYER) {
                move(e, pc, pc.x, pc.y-1);
                // kill
                for (Entity player: world.getEntitiesWith(PlayerComp.class, PositionComp.class)) {
                    PositionComp playerpc = world.getComponent(player, PositionComp.class);
                    if (playerpc.x == pc.x && playerpc.y == pc.y) {
                        world.addComponent(player, new ExplosionComp(playerpc.x * World.TILE_SIZE, playerpc.y * World.TILE_SIZE,  2, Tile.PLAYER));
                        world.removeComponent(player, RenderComp.class);
                    }
                }
            }
        }
    }

    private void move(Entity e, PositionComp pc, int newX, int newY) {
        world.addComponent(e, new MovingComp(pc.x * World.TILE_SIZE, pc.y * World.TILE_SIZE,
            newX * World.TILE_SIZE, newY * World.TILE_SIZE, World.STONE_ROLLING_TIME));
        world.setTile(pc.x, pc.y, Tile.EMPTY);
        pc.x = newX;
        pc.y = newY;
        world.setTile(pc.x, pc.y, world.getComponent(e, RenderComp.class).tile);
    }
}

