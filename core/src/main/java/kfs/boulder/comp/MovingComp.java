package kfs.boulder.comp;

import kfs.boulder.ecs.KfsComp;

public class MovingComp implements KfsComp {

    public final float targetX;
    public final float targetY;
    public final float moveSpeed;
    public float posX;
    public float posY;

    public MovingComp(float posX, float posY, float targetX, float targetY, float moveSpeed) {
        this.posX = posX;
        this.posY = posY;
        this.targetX = targetX;
        this.targetY = targetY;
        this.moveSpeed = moveSpeed;
    }

}
