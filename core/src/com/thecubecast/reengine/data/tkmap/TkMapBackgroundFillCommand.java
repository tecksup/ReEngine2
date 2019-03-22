package com.thecubecast.reengine.data.tkmap;

import java.util.Stack;

public class TkMapBackgroundFillCommand implements TkMapCommand {

    int x;
    int y;

    private Stack<TkMapCommand> Undocommands = new Stack<>();
    private Stack<TkMapCommand> Redocommands = new Stack<>();

    int ID;
    int OldID;

    TkMap map;

    public TkMapBackgroundFillCommand(int x, int y, int ID, TkMap map) {
        this.x = x;
        this.y = y;
        this.ID = ID;
        this.map = map;
    }

    @Override
    public void Execute() {
        //defines the old tile
        OldID = map.getGround()[x][y];

        int maxX = map.getGround().length - 1;
        int maxY = map.getGround()[0].length - 1;
        int[][] stack = new int[(maxX+1)*(maxY+1)][2];
        int index = 0;

        stack[0][0] = x;
        stack[0][1] = y;
        TkMapCommand OGcmd = new TkMapBackgroundCommand(x, y, ID, map);
        OGcmd.Execute();
        Undocommands.push(OGcmd);
        Redocommands.clear();

        while (index >= 0){
            x = stack[index][0];
            y = stack[index][1];
            index--;

            if ((x > 0) && (map.getGround()[x-1][y] == OldID)){
                //map.getGround()[x-1][y] = ID;
                TkMapCommand cmd = new TkMapBackgroundCommand(x-1, y, ID, map);
                cmd.Execute();
                Undocommands.push(cmd);
                Redocommands.clear();
                index++;
                stack[index][0] = x-1;
                stack[index][1] = y;
            }

            if ((x < maxX) && (map.getGround()[x+1][y] == OldID)){
                //map.getGround()[x+1][y] = ID;
                TkMapCommand cmd = new TkMapBackgroundCommand(x+1, y, ID, map);
                cmd.Execute();
                Undocommands.push(cmd);
                Redocommands.clear();
                index++;
                stack[index][0] = x+1;
                stack[index][1] = y;
            }

            if ((y > 0) && (map.getGround()[x][y-1] == OldID)){
                //map.getGround()[x][y-1] = ID;
                TkMapCommand cmd = new TkMapBackgroundCommand(x, y-1, ID, map);
                cmd.Execute();
                Undocommands.push(cmd);
                Redocommands.clear();
                index++;
                stack[index][0] = x;
                stack[index][1] = y-1;
            }

            if ((y < maxY) && (map.getGround()[x][y+1] == OldID)){
                //map.getGround()[x][y+1] = ID;
                TkMapCommand cmd = new TkMapBackgroundCommand(x, y+1, ID, map);
                cmd.Execute();
                Undocommands.push(cmd);
                Redocommands.clear();
                index++;
                stack[index][0] = x;
                stack[index][1] = y+1;
            }
        }

    }

    @Override
    public void UnExecute() {
        for (int i = 0; i < Undocommands.size(); i++) {
            if (Undocommands.size() != 0)
            {
                TkMapCommand command = Undocommands.get(i);
                command.UnExecute();
            }
        }
        Undocommands.clear();
    }
}
