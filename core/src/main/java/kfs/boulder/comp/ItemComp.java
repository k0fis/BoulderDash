package kfs.boulder.comp;

import kfs.boulder.ecs.KfsComp;

public class ItemComp implements KfsComp {

    public boolean achieve;

    public ItemComp(boolean achieve) {
        this.achieve = achieve;
    }
}
