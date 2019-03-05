package com.thecubecast.reengine.worldobjects.ai.pawn;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledGraph;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledNode;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.TiledSmoothableGraphPath;
import com.thecubecast.reengine.worldobjects.HackSlashPlayer;
import com.thecubecast.reengine.worldobjects.NPC;

public class Pawn_Enemy extends NPC {

    private Vector3 Destination;
    private Smart AI;
    HackSlashPlayer player;

    public GameStateManager gsm;

    public Pawn_Enemy(String name, int x, int y, int z, Vector3 size, float knockbackResistance, float health, intractability interact, boolean invincible, FlatTiledGraph map, GameStateManager gsm) {
        super(name,x,y,z, size, knockbackResistance,health, interact, invincible);
        this.gsm = gsm;
        AI = new Smart(this, map);
    }

    public void update(float delta, GameState G, HackSlashPlayer player) {
        this.player = player;
        super.update(delta, G);
        AI.update(G);
        //System.out.println(ai.getStateMachine().getCurrentState().name());
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        batch.draw(gsm.Render.getTexture("BlackEnemyTemp"), getPosition().x,getPosition().y);
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

    public void setDestination(Vector3 destination) {
        Destination = destination;
        AI.setDestination(Destination);
        AI.updatePath(true);
    }

    public TiledSmoothableGraphPath<FlatTiledNode> getPath() {
        return AI.getPath();
    }

    public Smart getAI() {
        return AI;
    }
}
