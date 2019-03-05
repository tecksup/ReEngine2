package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.gamestates.GameState;

import static com.thecubecast.reengine.data.GameStateManager.Render;

public class Bullet extends WorldObject {

    public WorldObject Parrent;

    public float Lifespan = 1f;
    public float timeAlive = 0;

    public Bullet(int x, int y, int z, Vector3 Velocity, WorldObject parrent) {
        super(x, y, z, new Vector3(4,4,4));
        setState(type.Dynamic);
        setVelocity(Velocity);
        this.Parrent = parrent;
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {
        if (getState().equals(type.Dynamic)) {

            Vector3 pos = new Vector3(getVelocity().x * delta, getVelocity().y * delta, getVelocity().z * delta);
            Vector3 newpos = new Vector3(getPosition()).add(getVelocity());
            if (pos.x < 0) { //Moving left
                if (checkCollision(new Vector3(newpos.x, getPosition().y, getPosition().z), G.Collisions)) {
                    super.setVelocityX(0);
                    timeAlive = 100;
                } else {
                    super.setPositionX((getPosition().x - getVelocity().x * -1));
                }
            } else if (pos.x > 0) { // Moving right
                if (checkCollision(new Vector3(newpos.x, getPosition().y, getPosition().z), G.Collisions)) {
                    super.setVelocityX(0);
                    timeAlive = 100;
                } else {
                    super.setPositionX((getPosition().x + getVelocity().x));
                }
            }

            if (pos.y < 0) { // Moving down
                if (checkCollision(new Vector3(getPosition().x, newpos.y, getPosition().z), G.Collisions)) {
                    super.setVelocityY(0);
                    timeAlive = 100;
                } else {
                    super.setPositionY((getPosition().y - getVelocity().y * -1));
                }
            } else if (pos.y > 0) {
                if (checkCollision(new Vector3(getPosition().x, newpos.y, getPosition().z), G.Collisions)) {
                    super.setVelocityY(0);
                    timeAlive = 100;
                } else {
                    super.setPositionY((getPosition().y + getVelocity().y));
                }
            }

            if (pos.z < 0) { // Moving Vertical
                if (checkCollision(new Vector3(getPosition().x, getPosition().y, newpos.z), G.Collisions, true) || newpos.z <= 0) {
                    if (newpos.z <= 0) {
                        super.setPositionZ(0);
                        setZFloor(0);
                    }
                    super.setVelocityZ(0);

                } else {
                    super.setPositionZ((getPosition().z - getVelocity().z * -1));
                }
            } else if (pos.z > 0) {
                if (checkCollision(new Vector3(getPosition().x, getPosition().y, newpos.z), G.Collisions)) {
                    super.setVelocityZ(0);
                } else {
                    super.setPositionZ((getPosition().z + getVelocity().z));
                }
            }
            //setPosition(getPosition().x + getVelocity().x, getPosition().y + getVelocity().y, getPosition().z + getVelocity().z);
        }
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

        batch.draw(Render.getTexture("bullet"), getPosition().x, getPosition().y);

    }
}
