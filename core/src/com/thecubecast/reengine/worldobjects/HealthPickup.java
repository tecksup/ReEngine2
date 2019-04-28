package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.dcputils.TextureAnimation;
import com.thecubecast.reengine.gamestates.GameState;

import static com.thecubecast.reengine.data.GameStateManager.Render;

public class HealthPickup extends WorldObject {

    TextureAnimation<TextureAtlas.AtlasRegion> Heart;

    public HealthPickup(int x, int y) {
        this.setPosition(x,y,0);
        Heart = new TextureAnimation<>(GameStateManager.Render.getTextures("012"), 0.075f);
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {

    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        Heart.update(Gdx.graphics.getDeltaTime());
        batch.draw(Heart.getFrame(), getPosition().x, getPosition().y);
    }
}
