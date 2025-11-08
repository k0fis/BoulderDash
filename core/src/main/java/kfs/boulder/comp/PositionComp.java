package kfs.boulder.comp;

import kfs.boulder.ecs.KfsComp;

public class PositionComp implements KfsComp {

    public int x;
    public int y;

    public PositionComp(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public final boolean equals(Object o) {
        if (!(o instanceof PositionComp)) return false;

        PositionComp that = (PositionComp) o;
        return x == that.x && y == that.y;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        return result;
    }
}
