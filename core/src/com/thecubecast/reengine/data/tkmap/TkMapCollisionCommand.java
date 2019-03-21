package com.thecubecast.reengine.data.tkmap;

public class TkMapCollisionCommand implements TkMapCommand {

    int x;
    int y;

    boolean ID;
    boolean OldID;

    TkMap map;

    public TkMapCollisionCommand(int x, int y, boolean ID, TkMap map) {
        this.x = x;
        this.y = y;
        this.ID = ID;
        this.map = map;
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
