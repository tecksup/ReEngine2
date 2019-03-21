package com.thecubecast.reengine.data.tkmap;

public class TkMapBackgroundCommand implements TkMapCommand {

    int x;
    int y;

    int ID;
    int OldID;

    TkMap map;

    public TkMapBackgroundCommand(int x, int y, int ID, TkMap map) {
        this.x = x;
        this.y = y;
        this.ID = ID;
        this.map = map;
    }

    @Override
    public void Execute() {
        //defines the old tile
        OldID = map.getGround()[x][y];
        //sets up the new tile
        map.getGround()[x][y] = ID;
    }

    @Override
    public void UnExecute() {
        map.getGround()[x][y] = OldID;
    }
}
