package com.thecubecast.reengine.data.tkmap;

import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.gamestates.EditorState;

import java.util.ArrayList;

public class TkMapBackgroundCommand implements TkMapCommand {

    int x;
    int y;

    EditorState.BrushSizes size;

    ArrayList<Vector3> Tiles = new ArrayList<>();

    int ID;
    int OldID;

    TkMap map;

    public TkMapBackgroundCommand(int x, int y, int ID, EditorState.BrushSizes size, TkMap map) {
        this.x = x;
        this.y = y;
        this.ID = ID;
        this.map = map;
        this.size = size;
    }

    @Override
    public void Execute() {
        switch (size) {
            case small:
                //defines the old tile
                OldID = map.getGround()[x][y];
                //sets up the new tile
                map.getGround()[x][y] = ID;
                Tiles.add(new Vector3(x,y,OldID));
                break;
            case medium:
                break;
            case large:
                break;
        }

    }

    @Override
    public void UnExecute() {
        for (int i = 0; i < Tiles.size(); i++) {
            map.getGround()[(int)Tiles.get(i).x][(int)Tiles.get(i).y] = (int)Tiles.get(i).z;
        }
    }
}
