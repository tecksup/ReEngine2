package com.thecubecast.reengine.worldobjects.entityprefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.worldobjects.Interactable;
import com.thecubecast.reengine.worldobjects.WorldObject;

public class Firepit extends Interactable {
    ParticleEffect Particles;

    public Firepit(int x, int y, int z) {
        super(x,y,z, new Vector3(4,4,4), type.Static, false);
        this.Name = "Firepit";
        Particles = new ParticleEffect();
        Particles.load(Gdx.files.internal("TkParticles/" + "Fire" + ".p"), Gdx.files.internal("TkParticles"));
        Particles.setPosition(getPosition().x, getPosition().y);
        Particles.start();
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {
        if (Particles.isComplete()){
            Particles.reset();
        }
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        Particles.draw(batch, Gdx.graphics.getDeltaTime());
    }
}
