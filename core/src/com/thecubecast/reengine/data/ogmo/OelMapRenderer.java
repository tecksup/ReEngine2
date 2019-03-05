package com.thecubecast.reengine.data.ogmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;
import java.util.List;

public class OelMapRenderer {

    //The Tilesets
    private List<OelTileset> Tilesets = new ArrayList<>();

    //Grid image
    Texture pixel;

    //The camera to bind to, and calculate the frustum
    OrthographicCamera cam;

    //Just the XML reader
    private XmlReader reader = new XmlReader();

    public OelMapRenderer(String OelProject) {

        pixel = new Texture(Gdx.files.internal("white-pixel.png"));

        XmlReader.Element root = reader.parse(Gdx.files.internal(OelProject));

        //String Tileset = root.getAttribute("width");

        for (int i = 0; i < root.getChildCount(); i++) {
            //This if statement is for Tiles and Grids
            if (root.getChild(i).getName().equals("Tilesets")) {
                for (int j = 0; j < root.getChild(i).getChildCount(); j++) {
                    String Name = root.getChild(i).getChild(j).getChildByName("Name").getText();
                    String FilePath = root.getChild(i).getChild(j).getChildByName("FilePath").getText();
                    int TileSizeW = Integer.parseInt(root.getChild(i).getChild(j).getChildByName("TileSize").getChildByName("Width").getText());
                    int TileSizeH = Integer.parseInt(root.getChild(i).getChild(j).getChildByName("TileSize").getChildByName("Height").getText());
                    int TileSep = Integer.parseInt(root.getChild(i).getChild(j).getChildByName("TileSep").getText());

                    OelTileset temp = new OelTileset(Name, FilePath, TileSizeW, TileSizeH, TileSep);

                    Tilesets.add(temp);
                }
            }
        }

    }

    public void setView(OrthographicCamera cam) {
        this.cam = cam;
    }

    public void render(SpriteBatch batch, OelMap Map) {
        //batch.draw();
        for (int i = 0; i < Map.getLayers().size(); i++) {
            renderLayer(batch, Map, i);
        }
    }

    public void renderLayer(SpriteBatch batch, OelMap Map, int layerIndex) {
        Rectangle drawView;
        if (cam != null) {
            drawView = new Rectangle(cam.position.x - cam.viewportWidth, cam.position.y - cam.viewportHeight, cam.viewportWidth + cam.viewportWidth, cam.viewportHeight + cam.viewportHeight);
        } else {
            drawView = new Rectangle(0, 0, Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
        }

        for (int i = 0; i < Tilesets.size(); i++) {

            if (Map.getLayers().get(layerIndex) instanceof OelGridLayer) { // Grid black or white

                OelGridLayer GridLayer = ((OelGridLayer) Map.getLayers().get(layerIndex));

                for (int y = Map.getHeight() / 16; y >= 0; y--) {
                    for (int x = 0; x < Map.getWidth() / 16; x++) {

                        if (GridLayer.getCell(x, y) == 0) {
                            if (drawView.overlaps(new Rectangle(x * 16, y * 16, 16, 16))) {
                                //batch.draw(pixel, x * 16, y *16, 16, 16);
                            }
                        } else {
                            if (drawView.overlaps(new Rectangle(x * 16, y * 16, 16, 16))) {
                                batch.draw(pixel, x * 16, y * 16, 16, 16);
                            }
                        }
                    }
                }

            } else if (Map.getLayers().get(layerIndex) instanceof OelTilesLayer) { // draws from the tileset

                OelTilesLayer TileLayer = ((OelTilesLayer) Map.getLayers().get(layerIndex));

                if (TileLayer.getTileset().equals(Tilesets.get(i).Name)) {

                    for (int y = Map.getHeight() / 16; y >= 0; y--) {
                        for (int x = 0; x < Map.getWidth() / 16; x++) {
                            if (TileLayer.getCell(x, y) != -1) {
                                if (drawView.overlaps(new Rectangle(x * 16, y * 16, 16, 16))) {
                                    batch.draw(Tilesets.get(i).Tiles[TileLayer.getCell(x, y)], x * 16, y * 16);
                                }
                            }
                        }
                    }

                } else {
                    //Break the loop, we don't know what colors to draw this
                    continue;
                }

            }

        }
    }

    public void renderLayer(SpriteBatch batch, OelMap Map, String layername) {
        Rectangle drawView;
        if (cam != null) {
            drawView = new Rectangle(cam.position.x - cam.viewportWidth, cam.position.y - cam.viewportHeight, cam.viewportWidth + cam.viewportWidth, cam.viewportHeight + cam.viewportHeight);
        } else {
            drawView = new Rectangle(0, 0, Gdx.graphics.getWidth() / 4, Gdx.graphics.getHeight() / 4);
        }

        int layerIndex = 0;
        for (int i = 0; i < Map.getLayers().size(); i++) {
            if (Map.getLayers().get(i).getName().equals(layername)) {
                layerIndex = i;
                break;
            } else if (i == Map.getLayers().size() - 1) {
                System.out.println("Error in OemMapRendering \n Layer \"" + layername + "\" not found");
                return;
            }
        }

        for (int i = 0; i < Tilesets.size(); i++) {

            if (Map.getLayers().get(layerIndex) instanceof OelGridLayer) { // Grid black or white

                OelGridLayer GridLayer = ((OelGridLayer) Map.getLayers().get(layerIndex));

                for (int y = Map.getHeight() / 16; y >= 0; y--) {
                    for (int x = 0; x < Map.getWidth() / 16; x++) {

                        if (GridLayer.getCell(x, y) == 0) {
                            if (drawView.overlaps(new Rectangle(x * 16, y * 16, 16, 16))) {
                                //batch.draw(pixel, x * 16, y *16, 16, 16);
                            }
                        } else {
                            if (drawView.overlaps(new Rectangle(x * 16, y * 16, 16, 16))) {
                                batch.draw(pixel, x * 16, y * 16, 16, 16);
                            }
                        }
                    }
                }

            } else if (Map.getLayers().get(layerIndex) instanceof OelTilesLayer) { // draws from the tileset

                OelTilesLayer TileLayer = ((OelTilesLayer) Map.getLayers().get(layerIndex));

                if (TileLayer.getTileset().equals(Tilesets.get(i).Name)) {

                    for (int y = Map.getHeight() / 16; y >= 0; y--) {
                        for (int x = 0; x < Map.getWidth() / 16; x++) {
                            if (TileLayer.getCell(x, y) != -1) {
                                if (drawView.overlaps(new Rectangle(x * 16, y * 16, 16, 16))) {
                                    batch.draw(Tilesets.get(i).Tiles[TileLayer.getCell(x, y)], x * 16, y * 16);
                                }
                            }
                        }
                    }

                } else {
                    //Break the loop, we don't know what colors to draw this
                    continue;
                }

            }

        }


    }

}
