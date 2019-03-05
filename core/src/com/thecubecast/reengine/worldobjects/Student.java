package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledGraph;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledNode;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.TiledSmoothableGraphPath;
import com.thecubecast.reengine.worldobjects.ai.pawn.EnemyState;
import com.thecubecast.reengine.worldobjects.ai.pawn.Smart;

public class Student extends NPC {

    private Vector3 Destination;
    private Smart AI;

    public Student(String name, int x, int y, int z, Vector3 size, float knockbackResistance, float health, intractability interact, FlatTiledGraph worldMap) {
        super(name, x, y, z, size, knockbackResistance, health, interact);

        //ai = new Smart(this, worldMap);

    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {
        super.update(delta, G);
        AI.setDestination(Destination);
        AI.update(G);

    }


    @Override
    public void interact() {
        if (!AI.getStateMachine().getCurrentState().equals(EnemyState.HUNTING))
            AI.getStateMachine().changeState(EnemyState.HUNTING);

        if (AI.getPath().nodes.size > 1) {
            setPosition(AI.getPath().get(1).x * 16, AI.getPath().get(1).y * 16, 0);
            AI.updatePath(true);
        }

    }

    public Vector3 getDestination() {
        return Destination;
    }

    public void setDestination(Vector3 destination) {
        Destination = destination;
        AI.setDestination(Destination);
    }

    public void setDestination(int destinationX, int destinationY, int destinationZ) {
        Destination = new Vector3(destinationX, destinationY, destinationZ);
        AI.setDestination(Destination);
    }

    public TiledSmoothableGraphPath<FlatTiledNode> getPath() {
        return AI.getPath();
    }

    public Smart getAI() {
        return AI;
    }
}