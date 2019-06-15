package com.thecubecast.reengine.worldobjects.ai.pathfinding;

/*******************************************************************************
 * Copyright 2014 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

import com.badlogic.gdx.ai.pfa.Connection;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapTileLayer;
import com.badlogic.gdx.utils.Array;
import com.thecubecast.reengine.data.tkmap.TkMap;

import static com.thecubecast.reengine.worldobjects.ai.pathfinding.TiledNode.COLLIDABLE;
import static com.thecubecast.reengine.worldobjects.ai.pathfinding.TiledNode.GROUND;

/**
 * A random generated graph representing a flat tiled map.
 *
 * @author davebaol
 */
public class FlatTiledGraph implements TiledGraph<FlatTiledNode> {

    static int sizeX, sizeY;

    protected Array<FlatTiledNode> nodes;

    public boolean diagonal;
    public FlatTiledNode startNode;

    public FlatTiledGraph(TiledMap map) {
        sizeX = map.getProperties().get("width", Integer.class);
        sizeY = map.getProperties().get("height", Integer.class);
        this.nodes = new Array<FlatTiledNode>(sizeX * sizeY);
        this.diagonal = false;
        this.startNode = null;
    }

    public FlatTiledGraph(TkMap map) {
        sizeX = map.getWidth();
        sizeY = map.getHeight();
        this.nodes = new Array<FlatTiledNode>(sizeX * sizeY);
        this.diagonal = false;
        this.startNode = null;
        init(map);
    }

    public void init(TiledMap map) {
        TiledMapTileLayer CollisionLayer = (TiledMapTileLayer) map.getLayers().get("Collision");
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                nodes.add(new FlatTiledNode(x, y, CollisionLayer.getCell(x, y).getTile().getId(), 8));
            }
        }

        // Each node has up to 4 neighbors, therefore no diagonal movement is possible
        for (int x = 0; x < sizeX; x++) {
            int colOffset = x * sizeY;
            for (int y = 0; y < sizeY; y++) {
                if (x > 0) addConnection(nodes.get(colOffset + y), -1, 0);
                if (y > 0) addConnection(nodes.get(colOffset + y), 0, -1);
                if (x < sizeX - 1) addConnection(nodes.get(colOffset + y), 1, 0);
                if (y < sizeY - 1) addConnection(nodes.get(colOffset + y), 0, 1);
            }
        }
    }

    public void init(TkMap map) {
        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                if (map.getCollision()[x][y]) {
                    nodes.add(new FlatTiledNode(x, y, COLLIDABLE, 8));
                } else {
                    nodes.add(new FlatTiledNode(x, y, GROUND, 8));
                }
            }
        }

        // Each node has up to 4 neighbors, therefore no diagonal movement is possible
        for (int x = 0; x < sizeX; x++) {
            int colOffset = x * sizeY;
            for (int y = 0; y < sizeY; y++) {
                if (x > 0) addConnection(nodes.get(colOffset + y), -1, 0);
                if (y > 0) addConnection(nodes.get(colOffset + y), 0, -1);
                if (x < sizeX - 1) addConnection(nodes.get(colOffset + y), 1, 0);
                if (y < sizeY - 1) addConnection(nodes.get(colOffset + y), 0, 1);
            }
        }

        return;
    }

    @Override
    public FlatTiledNode getNode(int x, int y) {
        return nodes.get(x * sizeY + y);
    }

    @Override
    public FlatTiledNode getNode(int index) {
        return nodes.get(index);
    }

    @Override
    public int getIndex(FlatTiledNode node) {
        return node.getIndex();
    }

    @Override
    public int getNodeCount() {
        return nodes.size;
    }

    @Override
    public Array<Connection<FlatTiledNode>> getConnections(FlatTiledNode fromNode) {
        return fromNode.getConnections();
    }

    private void addConnection(FlatTiledNode n, int xOffset, int yOffset) {
        FlatTiledNode target = getNode(n.x + xOffset, n.y + yOffset);
        if (target.type != COLLIDABLE)
            n.getConnections().add(new FlatTiledConnection(this, n, target));
    }

}