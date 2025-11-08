package kfs.boulder.sys;

import kfs.boulder.World;
import kfs.boulder.comp.MovingComp;
import kfs.boulder.ecs.Entity;
import kfs.boulder.ecs.KfsSystem;

public class MovingSys implements KfsSystem {

    private final World world;

    public MovingSys(World world) {
        this.world = world;
    }

    @Override
    public void update(float delta) {
        for (Entity e: world.getEntitiesWith(MovingComp.class)) {
            MovingComp mc = world.getComponent(e, MovingComp.class);

            float step = mc.moveSpeed * World.TILE_SIZE * delta;
            mc.posX = approach(mc.posX, mc.targetX, step);
            mc.posY = approach(mc.posY, mc.targetY, step);

            if (Math.abs(mc.posX - mc.targetX) < 1f && Math.abs(mc.posY - mc.targetY) < 1f) {
                world.removeComponent(e, MovingComp.class);


            }
        }
    }

    private float approach(float current, float target, float step) {
        if (current < target) {
            return Math.min(current + step, target);
        } else {
            return Math.max(current - step, target);
        }
    }
}
