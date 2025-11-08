package kfs.boulder.comp;

import kfs.boulder.Tile;
import kfs.boulder.ecs.KfsComp;

public class RenderComp implements KfsComp {

    public Tile tile;
    public int dx = 1;

    public RenderComp(Tile tile) {
        this.tile = tile;
    }
}
