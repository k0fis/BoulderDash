package kfs.boulder.sys;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kfs.boulder.World;
import kfs.boulder.comp.MovingComp;
import kfs.boulder.comp.PositionComp;
import kfs.boulder.comp.RenderComp;
import kfs.boulder.ecs.Entity;
import kfs.boulder.ecs.KfsSystem;

public class RenderMoving implements KfsSystem {

    private final World world;

    public RenderMoving(World world) {
        this.world = world;
    }

    @Override
    public void render(SpriteBatch batch) {
        for (Entity e : world.getEntitiesWith(RenderComp.class, PositionComp.class)) {
            RenderComp rc = world.getComponent(e, RenderComp.class);
            MovingComp mc = world.getComponent(e, MovingComp.class);
            if (mc != null) {
                draw(batch, world.getTexture(rc.tile), mc.posX, mc.posY, rc.dx == -1);
            } else {
                PositionComp pc = world.getComponent(e, PositionComp.class);
                draw(batch, world.getTexture(rc.tile), pc.x * World.TILE_SIZE, pc.y * World.TILE_SIZE, rc.dx == -1);
            }
        }
    }

    private void draw(SpriteBatch batch, Texture texture, float posX, float posY, boolean flipX) {
        batch.draw(texture, posX, posY, 0,0, texture.getWidth(), texture.getHeight(),
            1, 1, 0, 0,0, texture.getWidth(), texture.getHeight(), flipX, false);
    }
}
