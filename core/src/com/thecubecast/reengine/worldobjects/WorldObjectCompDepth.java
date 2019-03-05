package com.thecubecast.reengine.worldobjects;

import java.util.Comparator;

public class WorldObjectCompDepth implements Comparator<WorldObject> {
    @Override
    public int compare(WorldObject o1, WorldObject o2) {
        // entities ordered based on y-position
        return (Float.compare(o1.getHitbox().min.z, o2.getHitbox().min.z));
    }
}
