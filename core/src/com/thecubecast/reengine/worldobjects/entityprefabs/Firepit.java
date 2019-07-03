package com.thecubecast.reengine.worldobjects.entityprefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.worldobjects.Interactable;
import com.thecubecast.reengine.worldobjects.WorldObject;

public class Firepit extends Interactable {
    ParticleEffect Particles;
    int particle_offset = 8;
    Texture Firepit;

    public Firepit(int x, int y, int z) {
        super(x,y,z, new Vector3(4,4,4), type.Static, false);
        this.Name = "Firepit";
        Particles = new ParticleEffect();
        Particles.load(Gdx.files.internal("TkParticles/" + "Fire_big" + ".p"), Gdx.files.internal("TkParticles"));
        Particles.setPosition(getPosition().x + particle_offset, getPosition().y + getPosition().z/2 + particle_offset);
        Particles.start();
        Firepit = new Texture(Gdx.files.internal("Sprites/Firepit.png"));
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {
        Particles.setPosition(getPosition().x + particle_offset, getPosition().y + getPosition().z/2 + particle_offset);
        if (Particles.isComplete()){
            Particles.reset();
        }
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        batch.draw(Firepit, getPosition().x, getPosition().y);
        Particles.draw(batch, Gdx.graphics.getDeltaTime());
    }

    @Override
    public Interactable CreateNew() {
        return this;
    }
}
