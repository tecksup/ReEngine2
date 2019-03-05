package com.thecubecast.reengine.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.ParticleEffect;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

import java.util.ArrayList;
import java.util.List;

public class ParticleHandler {

    List<ParticleEffect> ParticleEffects = new ArrayList<>();

    public ParticleHandler() {

    }

    public void AddParticleEffect(String ParticleName, Vector2 pos) {
        //SETUP THE PARTICLES
        ParticleEffect temp = new ParticleEffect();
        temp.load(Gdx.files.internal("TkParticles/" + ParticleName + ".p"), Gdx.files.internal("TkParticles"));
        temp.setPosition(pos.x, pos.y);
        temp.start();
        ParticleEffects.add(temp);
    }

    public ParticleEffect AddParticleEffect(String ParticleName, float x, float y) {
        //SETUP THE PARTICLES
        ParticleEffect temp = new ParticleEffect();
        temp.load(Gdx.files.internal("TkParticles/" + ParticleName + ".p"), Gdx.files.internal("TkParticles"));
        temp.setPosition(x, y);
        temp.start();
        ParticleEffects.add(temp);
        return temp;
    }

    public void Update() {
        for (int i = 0; i < ParticleEffects.size(); i++) {
            if (ParticleEffects.get(i).isComplete()) {
                ParticleEffects.get(i).dispose();
                ParticleEffects.remove(i);
            }
        }
    }

    public void Draw(SpriteBatch batch) {
        for (int i = 0; i < ParticleEffects.size(); i++) {
            ParticleEffects.get(i).draw(batch, Gdx.graphics.getDeltaTime());
        }
    }

}
