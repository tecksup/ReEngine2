package com.thecubecast.reengine.worldobjects;

import java.util.Comparator;

public class WorldObjectComp implements Comparator<WorldObject> {
    @Override
    public int compare(WorldObject o1, WorldObject o2) {
        // entities ordered based on y-position
        return (Float.compare(o2.getHitbox().min.y, o1.getHitbox().min.y));
    }
}
