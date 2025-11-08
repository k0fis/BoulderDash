package kfs.boulder.sys;

import com.badlogic.gdx.graphics.Camera;
import kfs.boulder.World;
import kfs.boulder.comp.PlayerComp;
import kfs.boulder.comp.PositionComp;
import kfs.boulder.ecs.Entity;
import kfs.boulder.ecs.KfsSystem;

public class CameraUpdateSys implements KfsSystem {

    private final Camera camera;
    private final World world;


    public CameraUpdateSys(Camera camera, World world) {
        this.camera = camera;
        this.world = world;
    }

    @Override
    public void update(float delta) {

        for (Entity e : world.getEntitiesWith(PlayerComp.class, PositionComp.class)) {
            PositionComp pos = world.getComponent(e, PositionComp.class);

            float targetX = pos.x * World.TILE_SIZE + World.TILE_SIZE / 2f;
            float targetY = pos.y * World.TILE_SIZE + World.TILE_SIZE / 2f;

            camera.position.x += (targetX - camera.position.x) * World.CAMERA_SMOOTH;
            camera.position.y += (targetY - camera.position.y) * World.CAMERA_SMOOTH;
            camera.update();
        }
    }
}
