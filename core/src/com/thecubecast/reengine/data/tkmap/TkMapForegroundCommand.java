package com.thecubecast.reengine.data.tkmap;

public class TkMapForegroundCommand implements TkMapCommand {

    int x;
    int y;

    int ID;
    int OldID;

    TkMap map;

    public TkMapForegroundCommand(int x, int y, int ID, TkMap map) {
        this.x = x;
        this.y = y;
        this.ID = ID;
        this.map = map;
    }

    @Override
    public void Execute() {
        //defines the old tile
        OldID = map.getForeground()[x][y];
        //sets up the new tile
        map.getForeground()[x][y] = ID;
    }

    @Override
    public void UnExecute() {
        map.getForeground()[x][y] = OldID;
    }
}
