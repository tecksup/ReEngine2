package com.thecubecast.reengine.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.thecubecast.reengine.data.dcputils.TextureAnimation;
import javafx.geometry.Pos;

import java.util.Random;

import static com.thecubecast.reengine.data.Common.FormatterObject;
import static com.thecubecast.reengine.data.Common.stringBuilderObject;
import static com.thecubecast.reengine.data.GameStateManager.Render;

public class FallingLayer {
    //This class will hold the 1 tile layer that the player is falling from
    //There will be multiple classes extended from this one that allows me to make several different types that i can generate
    //When the player first jumps in the well

    private int size; //This is how wide the section is

    //This stores the tiles contained in the row/layer in order from left to right
    // -1 is empty space
    private int[] Tiles;

    public int Prefab = -1;


    public FallingLayer(int size) {
        this.size = size;
        Tiles = new int[size];
        Random tempRand = new Random();
        for (int i = 0; i < size; i++) { //I REALLY NEED A BETTER WAY TO CHOOSE THESE TILES
            if (i == 0) { // Left Most Tile
                Tiles[i] = 6;
            } else if (i == size-1) { // Right Most Tile
                Tiles[i] = 7;
            } else {
                Tiles[i] = -1;
                if (tempRand.nextInt(10) == 1) {
                    Tiles[i] = 2;
                } else if (tempRand.nextInt(3) == 1) {
                    Tiles[i] = 1;
                }
            }
        }
    }

    public FallingLayer(int size, int Prefab) {
        this.size = size;
        this.Prefab = Prefab;
        Tiles = new int[size];
        switch (Prefab) {
            default:
                Tiles = new int[] {6,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,7};
                break;
            case 0:
                Tiles = new int[] {6,-1,-1,-1,-1,-1,-1,-1,0,0,0,0,0,1};
                break;
            case 1:
                Tiles = new int[] {1,0,0,0,0,0,-1,-1,-1,-1,-1,-1,-1,7};
                break;
            case 2:
                Tiles = new int[] {6,-1,-1,-1,-1,0,0,0,0,0,-1,-1,-1,7};
                break;
            case 3:
                Tiles = new int[] {1,0,0,0,-1,0,0,0,0,-1,-1,-1,-1,7};
                break;
        }
    }

    public void setTile(int Position, int ID) {
        Tiles[Position] = ID;
    }

    public int getTile(int Position) {
        return Tiles[Position];
    }

    public int getSize() {
        return size;
    }

    public void Draw(SpriteBatch batch, OrthographicCamera cam, GameStateManager gsm, int StartingX, int y) {

        //Boom now where culling
        Rectangle drawView;
        if (cam != null) {
            drawView = new Rectangle(cam.position.x - cam.viewportWidth, cam.position.y - cam.viewportHeight, cam.viewportWidth + cam.viewportWidth, cam.viewportHeight + cam.viewportHeight);
        } else {
            drawView = new Rectangle(0, 0, Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
        }

        for (int i = 0; i < Tiles.length; i++) {
            if (drawView.overlaps(new Rectangle(StartingX + i * 16, y, 16, 16))) {
                if (i != Tiles.length-1)
                    batch.draw(gsm.Render.getTexture("008"), StartingX + i * 16 + 4, y);
                if (Tiles[i] != -1) {
                    String temp = FormatterObject.format("%03d", Tiles[i]).toString();
                    stringBuilderObject.setLength(0);
                    batch.draw(gsm.Render.getTexture(temp), StartingX + i * 16, y);
                }
            }
        }
    }

}
