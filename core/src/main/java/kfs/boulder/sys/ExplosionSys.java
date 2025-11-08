package kfs.boulder.sys;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import kfs.boulder.World;
import kfs.boulder.comp.ExplosionComp;
import kfs.boulder.comp.PlayerComp;
import kfs.boulder.comp.RenderComp;
import kfs.boulder.ecs.Entity;
import kfs.boulder.ecs.KfsSystem;

public class ExplosionSys implements KfsSystem {

    private final World world;

    public ExplosionSys(World world) {
        this.world = world;
    }


    @Override
    public void update(float delta) {
        for (Entity e : world.getEntitiesWith(ExplosionComp.class)) {
            ExplosionComp g = world.getComponent(e, ExplosionComp.class);
            g.stateTime += delta;
            if (g.stateTime > g.timer) {
                if (world.getComponent(e, PlayerComp.class) != null) {
                    world.gameOver(false);
                }
                world.deleteEntity(e);
            }
        }
    }

    @Override
    public void render(SpriteBatch batch) {
        for (Entity e : world.getEntitiesWith(ExplosionComp.class)) {
            ExplosionComp g = world.getComponent(e, ExplosionComp.class);

            // Calculate progress (0 → 1)
            float progress = g.stateTime / g.timer;

            // Scale from 1.0 → 0.1 (shrinking)
            float scale = 1.0f - progress * 0.9f;

            // Fade out over time (alpha 1 → 0)
            float alpha = 1.0f - progress;

            // Save previous color
            float oldAlpha = batch.getColor().a;

            // Apply alpha fade
            batch.setColor(1, 1, 1, alpha);

            Texture t = world.getTexture(g.tile);
            // Compute scaled size
            float width = t.getWidth() * scale;
            float height = t.getHeight() * scale;

            // Draw centered at (x, y)
            batch.draw(t, g.x, g.y, width, height);

            // Restore batch color
            batch.setColor(1, 1, 1, oldAlpha);
        }
    }

}
