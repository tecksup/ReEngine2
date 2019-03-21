package com.thecubecast.reengine.data.tkmap;

public class TkMapForegroundFillCommand implements TkMapCommand {

    int x;
    int y;

    int ID;
    int OldID;

    TkMap map;

    public TkMapForegroundFillCommand(int x, int y, int ID, TkMap map) {
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

        int maxX = map.getForeground().length - 1;
        int maxY = map.getForeground()[0].length - 1;
        int[][] stack = new int[(maxX+1)*(maxY+1)][2];
        int index = 0;

        stack[0][0] = x;
        stack[0][1] = y;
        map.getForeground()[x][y] = ID;

        while (index >= 0){
            x = stack[index][0];
            y = stack[index][1];
            index--;

            if ((x > 0) && (map.getForeground()[x-1][y] == OldID)){
                map.getForeground()[x-1][y] = ID;
                index++;
                stack[index][0] = x-1;
                stack[index][1] = y;
            }

            if ((x < maxX) && (map.getForeground()[x+1][y] == OldID)){
                map.getForeground()[x+1][y] = ID;
                index++;
                stack[index][0] = x+1;
                stack[index][1] = y;
            }

            if ((y > 0) && (map.getForeground()[x][y-1] == OldID)){
                map.getForeground()[x][y-1] = ID;
                index++;
                stack[index][0] = x;
                stack[index][1] = y-1;
            }

            if ((y < maxY) && (map.getForeground()[x][y+1] == OldID)){
                map.getForeground()[x][y+1] = ID;
                index++;
                stack[index][0] = x;
                stack[index][1] = y+1;
            }
        }
        
    }

    @Override
    public void UnExecute() {
        int maxX = map.getForeground().length - 1;
        int maxY = map.getForeground()[0].length - 1;
        int[][] stack = new int[(maxX+1)*(maxY+1)][2];
        int index = 0;

        stack[0][0] = x;
        stack[0][1] = y;
        map.getForeground()[x][y] = ID;

        while (index >= 0){
            x = stack[index][0];
            y = stack[index][1];
            index--;

            if ((x > 0) && (map.getForeground()[x-1][y] == ID)){
                map.getForeground()[x-1][y] = OldID;
                index++;
                stack[index][0] = x-1;
                stack[index][1] = y;
            }

            if ((x < maxX) && (map.getForeground()[x+1][y] == ID)){
                map.getForeground()[x+1][y] = OldID;
                index++;
                stack[index][0] = x+1;
                stack[index][1] = y;
            }

            if ((y > 0) && (map.getForeground()[x][y-1] == ID)){
                map.getForeground()[x][y-1] = OldID;
                index++;
                stack[index][0] = x;
                stack[index][1] = y-1;
            }

            if ((y < maxY) && (map.getForeground()[x][y+1] == ID)){
                map.getForeground()[x][y+1] = OldID;
                index++;
                stack[index][0] = x;
                stack[index][1] = y+1;
            }
        }
    }
}
