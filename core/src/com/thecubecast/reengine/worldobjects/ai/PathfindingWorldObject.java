package com.thecubecast.reengine.worldobjects.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.data.Item;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.worldobjects.NPC;
import com.thecubecast.reengine.worldobjects.ai.Smart;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledGraph;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledNode;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.TiledSmoothableGraphPath;

public class PathfindingWorldObject extends NPC {

    public Smart AI;

    public PathfindingWorldObject(String name, int x, int y, int z, Vector3 size, float knockbackResistance, float health, intractability interact, boolean invincible, FlatTiledGraph map, State<Smart> AItype) {
        super(name, x, y, z, size, knockbackResistance, health, interact, invincible);
        AI = new Smart(this, map, AItype);
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {
        super.update(delta, G);
        getAI().update(G);
    }

    @Override
    public void interact(GameState G) {
    }

    public Vector3 getDestination() {
        return getAI().getDestination();
    }

    public void setDestination(Vector3 destination) {
        AI.setDestination(destination);
    }

    public void setDestination(int destinationX, int destinationY, int destinationZ) {
        AI.setDestination(new Vector3(destinationX, destinationY, destinationZ));
    }

    public TiledSmoothableGraphPath<FlatTiledNode> getPath() {
        return AI.getPath();
    }

    public Smart getAI() {
        return AI;
    }
}