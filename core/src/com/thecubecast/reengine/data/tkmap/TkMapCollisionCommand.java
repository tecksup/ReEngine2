package com.thecubecast.reengine.data.tkmap;

import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.gamestates.EditorState;

import java.util.ArrayList;

public class TkMapCollisionCommand implements TkMapCommand {

    int x;
    int y;

    boolean ID;
    boolean OldID;

    EditorState.BrushSizes size;

    ArrayList<Vector3> Tiles = new ArrayList<>();

    TkMap map;

    public TkMapCollisionCommand(int x, int y, boolean ID, EditorState.BrushSizes size, TkMap map) {
        this.x = x;
        this.y = y;
        this.ID = ID;
        this.map = map;
        this.size = size;
    }

    @Override
    public void Execute() {
        //defines the old tile
        OldID = map.getCollision()[x][y];
        //sets up the new tile
        map.getCollision()[x][y] = ID;
    }

    @Override
    public void UnExecute() {
        map.getCollision()[x][y] = OldID;
    }
}
