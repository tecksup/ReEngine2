package com.thecubecast.reengine.data.tkmap;

import com.thecubecast.reengine.worldobjects.WorldObject;

/**
 * This class is for an object that stores changes, delted objects, Tile Changes, etc. All for Undo/Redo
 */
public interface TkMapCommand {

    void Execute();
    void UnExecute();

}
