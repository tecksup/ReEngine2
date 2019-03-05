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
import com.badlogic.gdx.utils.Array;

/**
 * A node for a {@link TiledGraph}.
 *
 * @param <N> Type of node, either flat or hierarchical, extending the {@link TiledNode} class
 * @author davebaol
 */
public abstract class TiledNode<N extends TiledNode<N>> {

    /**
     * A constant representing a walkable tile
     */
    public static final int GROUND = 7;

    /**
     * A constant representing a wall
     */
    public static final int COLLIDABLE = 8;

    /**
     * The x coordinate of this tile
     */
    public final int x;

    /**
     * The y coordinate of this tile
     */
    public final int y;

    /**
     * The type of this tile, see {@link #GROUND}, {@link #COLLIDABLE}
     */
    public final int type;

    protected Array<Connection<N>> connections;

    public TiledNode(int x, int y, int type, Array<Connection<N>> connections) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.connections = connections;
    }

    public abstract int getIndex();

    public Array<Connection<N>> getConnections() {
        return this.connections;
    }

}