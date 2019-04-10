package com.thecubecast.reengine.data.tkmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.thecubecast.reengine.data.dcputils.TextureAnimation;

import java.util.Formatter;

import static com.thecubecast.reengine.data.Common.FormatterObject;
import static com.thecubecast.reengine.data.Common.stringBuilderObject;
import static com.thecubecast.reengine.data.GameStateManager.Render;

public class TkTileset {

    String Name;
    int TileSizeW;
    int TileSizeH;
    int TileSep;
    float TileSpeed;

    int TilesetWidth;

    private TextureAnimation<TextureAtlas.AtlasRegion>[] Tiles;

    public TkTileset(String name, int tileSizeW, int tileSizeH, int tileSep, float tileSpeed) {
        Name = name;
        TileSizeW = tileSizeW;
        TileSizeH = tileSizeH;
        TileSep = tileSep;
        TileSpeed = tileSpeed;


        TilesetWidth = 0;
        while (hasTile(TilesetWidth)) {
            TilesetWidth++;
        }

        Tiles = new TextureAnimation[TilesetWidth];

        for (int i = 0; i < TilesetWidth; i++) {
            String temp = FormatterObject.format("%03d", i).toString();
            stringBuilderObject.setLength(0);
            Tiles[i] = new TextureAnimation(Render.getTextures(temp), TileSpeed);
        }

    }

    public TextureAnimation<TextureAtlas.AtlasRegion> getTile(int ID) {
        return Tiles[ID];
    }

    private boolean hasTile(int ID) {
        String temp = FormatterObject.format("%03d", ID).toString();
        stringBuilderObject.setLength(0);

        if (Render.hasTextures(temp)) {
            return true;
        }
        return false;
    }

    public int getTilesSize() {
        return TilesetWidth;
    }

    public void Update(float Time) {
        for (int i = 0; i < Tiles.length; i++) {
            Tiles[i].update(Time);
        }
    }

    public void RefreshAtlas() {
        Tiles = new TextureAnimation[TilesetWidth];

        for (int i = 0; i < TilesetWidth; i++) {
            String temp = FormatterObject.format("%03d", i).toString();
            stringBuilderObject.setLength(0);
            Tiles[i] = new TextureAnimation(Render.getTextures(temp), TileSpeed);
        }
    }
}
