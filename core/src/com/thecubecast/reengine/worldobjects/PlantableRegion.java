package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thecubecast.reengine.gamestates.GameState;

public class PlantableRegion extends WorldObject{

    boolean InSunlight;

    public PlantableRegion(boolean InSunlight) {
        this.InSunlight = InSunlight;
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {

    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

    }

    public void setInSunlight(boolean inSunlight) {
        InSunlight = inSunlight;
    }

    public boolean CanPlantGoHere() {
        return InSunlight;
    }
}
