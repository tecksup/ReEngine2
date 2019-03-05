package com.thecubecast.reengine.data.ogmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.XmlReader;

import java.util.ArrayList;
import java.util.List;

public class OelMap {

    private String MapLocation;

    private List<OelLayer> Layers = new ArrayList<>();

    private int width;
    private int height;

    private XmlReader reader = new XmlReader();

    public OelMap(String MapLocation) {
        this.MapLocation = MapLocation;
        XmlReader.Element root = reader.parse(Gdx.files.internal(MapLocation));

        width = Integer.parseInt(root.getAttribute("width"));
        height = Integer.parseInt(root.getAttribute("height"));

        //Load layers
        for (int i = 0; i < root.getChildCount(); i++) {
            //This if statement is for Tiles and Grids
            if (root.getChild(i).hasAttribute("exportMode")) {
                if (root.getChild(i).getAttribute("exportMode").equals("CSV")) {

                    OelTilesLayer temp = new OelTilesLayer(width, height, root.getChild(i).getName(), root.getChild(i).getAttribute("tileset"), root.getChild(i).getText());
                    //System.out.println(temp.toString());
                    Layers.add(temp);


                } else if (root.getChild(i).getAttribute("exportMode").equals("Bitstring")) {

                    OelGridLayer temp = new OelGridLayer(width, height, root.getChild(i).getName(), root.getChild(i).getText());
                    //System.out.println(temp.toString());
                    Layers.add(temp);

                }
            } else { //This else statement is for Entitiy Layers
                OelEntitiesLayer temp = new OelEntitiesLayer(root.getChild(i).getName(), root.getChild(i));
                temp.setHeight(height);
                temp.setWidth(width);


                Layers.add(temp);
            }
        }


    }

    public List<OelLayer> getLayers() {
        return Layers;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }
}
