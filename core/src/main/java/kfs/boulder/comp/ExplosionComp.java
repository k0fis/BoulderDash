package kfs.boulder.comp;

import kfs.boulder.Tile;
import kfs.boulder.ecs.KfsComp;

public class ExplosionComp implements KfsComp {

    public final float x;
    public final float y;
    public final float timer;
    public float stateTime;
    public final Tile tile;

    public ExplosionComp(float x, float y, float timer, Tile tile) {
        this.x = x;
        this.y = y;
        this.timer = timer;
        this.stateTime = 0;
        this.tile = tile;
    }


}
