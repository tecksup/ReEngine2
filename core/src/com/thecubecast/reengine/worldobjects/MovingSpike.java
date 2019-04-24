package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.Gdx;
import com.thecubecast.reengine.gamestates.GameState;

public class MovingSpike extends Spike {

    public MovingSpike(int X, int Y) {
        super(X, Y);
    }

    @Override
    public void update(float delta, GameState G) {
        super.update(delta, G);
        //We'll come back to that...
        this.setPositionX((float) (getPosition().x + (Math.sin(System.nanoTime()*100)*10)));
    }
}
