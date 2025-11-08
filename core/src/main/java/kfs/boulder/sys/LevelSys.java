package kfs.boulder.sys;

import kfs.boulder.Tile;
import kfs.boulder.World;
import kfs.boulder.comp.*;
import kfs.boulder.ecs.Entity;
import kfs.boulder.ecs.KfsSystem;

import java.util.List;

public class LevelSys implements KfsSystem {

    private final World world;

    public LevelSys(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {

        List<Entity> players = world.getEntitiesWith(PlayerComp.class, PositionComp.class);
        if (players.isEmpty()) {
            world.gameOver(false);
        }

        for (Entity player : players) {
            PositionComp playerPosition = world.getComponent(player, PositionComp.class);
            for (Entity door : world.getEntitiesWith(DoorComp.class, PositionComp.class)) {
                PositionComp doorPosition = world.getComponent(door, PositionComp.class);
                RenderComp doorRender = world.getComponent(door, RenderComp.class);
                if (playerPosition.equals(doorPosition) && doorRender.tile == Tile.DOOR_OPENED) {
                    world.gameOver(true);
                }
            }
        }

        // care of time(out)


        checkDoor();
    }

    private void checkDoor() {
        for (Entity e : world.getEntitiesWith(ItemComp.class)) {
            ItemComp ic = world.getComponent(e, ItemComp.class);
            if (ic.achieve) return;
        }
        for (Entity e : world.getEntitiesWith(DoorComp.class)) {
            RenderComp r = world.getComponent(e, RenderComp.class);
            if (r != null) {
                r.tile = Tile.DOOR_OPENED;
            }
        }
    }
}
