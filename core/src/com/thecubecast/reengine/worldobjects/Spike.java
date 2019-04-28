package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thecubecast.reengine.gamestates.GameState;

import static com.thecubecast.reengine.data.GameStateManager.Render;

public class Spike extends WorldObject {

    public Spike(int X, int Y) {
        setPosition(X,Y, 0);
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {

    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        batch.draw(Render.getTexture("011"), getPosition().x, getPosition().y);
    }
}
