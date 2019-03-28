package com.thecubecast.reengine.data.tkmap;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.thecubecast.reengine.data.GameStateManager.Render;

public class TkTileset {

    String Name;
    String FilePath;
    int TileSizeW;
    int TileSizeH;
    int TileSep;

    int TilesetWidth;

    TextureRegion TilesetImage;
    TextureRegion[] Tiles;

    public TkTileset(String name, String Path, int tileSizeW, int tileSizeH, int tileSep) {
        Name = name;
        FilePath = Path;
        TileSizeW = tileSizeW;
        TileSizeH = tileSizeH;
        TileSep = tileSep;
        if (Path.contains(".png")) {
            TilesetImage = new TextureRegion(new Texture(Gdx.files.internal(Path)));
        } else {
            TilesetImage = Render.getTexture(Path);
        }

        int cols = TilesetImage.getRegionWidth() / TileSizeW;
        int rows = TilesetImage.getRegionHeight() / TileSizeH;

        TilesetWidth = cols;

        Tiles = new TextureRegion[rows * cols];

        TextureRegion[][] tmp = TilesetImage.split(
                TileSizeW,
                TileSizeH);

        int index = 0;
        for (int l = 0; l < TilesetImage.getRegionHeight() / TileSizeH; l++) {
            for (int j = 0; j < TilesetImage.getRegionWidth() / TileSizeW; j++) {
                Tiles[index++] = tmp[l][j];
            }
        }

    }

    public TextureRegion[] getTiles() {
        return Tiles;
    }
}
