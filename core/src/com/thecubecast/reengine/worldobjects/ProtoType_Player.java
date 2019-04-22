package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.gamestates.GameState;

import static com.thecubecast.reengine.data.GameStateManager.Render;

public class ProtoType_Player extends WorldObject {

    public int Health = 100;

    public float AttackTime;

    public float FacingAngle;

    //True is left, False is right
    boolean Facing = true;

    public float RollingTime;
    public boolean Rolling;

    GameState G;

    public ProtoType_Player(int x, int y, int z, GameState G) {
        super(x,y,z, new Vector3(16,16,2));
        this.setState(type.Dynamic);
        this.G = G;
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {

        if (AttackTime - delta > 0)
            AttackTime -= delta;
        if (RollingTime - delta > 0)
            RollingTime -= delta;

        if (Rolling) {
            getVelocity().clamp(-5, 5);
        } else {

        }

        if (getState().equals(type.Dynamic)) {

            super.setVelocityX((getVelocity().x + getVelocity().x * -1 * 0.25f));
            super.setVelocityY((getVelocity().y + getVelocity().y * -1 * 0.25f));
            super.setVelocityZ((getVelocity().z + getVelocity().z * -1 * 0.25f) - 1);

            Vector3 pos = new Vector3(getVelocity().x * delta, getVelocity().y * delta, getVelocity().z * delta);
            Vector3 newpos = new Vector3(getPosition()).add(getVelocity());
            if (pos.x < 0) { //Moving left
                if (checkCollision(new Vector3(newpos.x, getPosition().y, getPosition().z), G.Collisions)) {
                    super.setVelocityX(0);
                } else {
                    super.setPositionX((getPosition().x - getVelocity().x * -1));
                }
            } else if (pos.x > 0) { // Moving right
                if (checkCollision(new Vector3(newpos.x, getPosition().y, getPosition().z), G.Collisions)) {
                    super.setVelocityX(0);
                } else {
                    super.setPositionX((getPosition().x + getVelocity().x));
                }
            }

            if (pos.y < 0) { // Moving down
                if (checkCollision(new Vector3(getPosition().x, newpos.y, getPosition().z), G.Collisions)) {
                    super.setVelocityY(0);
                } else {
                    super.setPositionY((getPosition().y - getVelocity().y * -1));
                }
            } else if (pos.y > 0) {
                if (checkCollision(new Vector3(getPosition().x, newpos.y, getPosition().z), G.Collisions)) {
                    super.setVelocityY(0);
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
        }
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        batch.draw(Render.getTexture("Eggplant"), getPosition().x, getPosition().y, 0,0,16,16,1,1,FacingAngle);
    }

    public BoundingBox getAttackBox() {

        return getIntereactBox();
    }

    public BoundingBox getIntereactBox() {
        BoundingBox RectPla;

        if (FacingAngle >= 0 && FacingAngle <= 45) {
            System.out.println("1");
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x - 16,
                                 getPosition().y+8,
                                   getPosition().z),
                    new Vector3(getPosition().x+8,
                               getPosition().y + getSize().y+16,
                               getPosition().z + getSize().z));
            return RectPla;
        }

        else if (FacingAngle >= 45 && FacingAngle <= 90) {
            System.out.println("2");
        }

        else if (FacingAngle >= 90 && FacingAngle <= 135) {
            System.out.println("3");
        }

        else if (FacingAngle >= 135 && FacingAngle <= 180) {
            System.out.println("4");
        }



        else if (FacingAngle < 0 && FacingAngle >= -45) {
            System.out.println("5");
        }

        else if (FacingAngle < -45 && FacingAngle >= -90) {
            System.out.println("6");
        }

        else if (FacingAngle < -90 && FacingAngle >= -135) {
            System.out.println("7");
        }

        else if (FacingAngle < -135 && FacingAngle >= -180) {
            System.out.println("8");
        }

        RectPla = new BoundingBox(new Vector3(getPosition().x + (1 * getSize().x), getPosition().y - 12, getPosition().z), new Vector3(getPosition().x + (1 * getSize().x) + getSize().x, getPosition().y + getSize().y + 8, getPosition().z + getSize().z));

        return RectPla;
    }

}
