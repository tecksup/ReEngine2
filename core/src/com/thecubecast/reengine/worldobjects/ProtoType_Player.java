package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.dcputils.TextureAnimation;
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

    TextureAnimation<TextureAtlas.AtlasRegion> Walking;

    GameState G;

    public ProtoType_Player(int x, int y, int z, GameState G) {
        super(x,y,z, new Vector3(16,16,2));
        this.setState(type.Dynamic);
        this.G = G;
        Walking = new TextureAnimation<>(GameStateManager.Render.getTextures("adventurer-run"), 0.1f);
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

        Walking.update(Gdx.graphics.getDeltaTime());

        batch.draw(Walking.getFrame(), getPosition().x, getPosition().y);
        /*
        if (FacingAngle >= 0 && FacingAngle < 22.5) {
            System.out.println("1");
            batch.draw(Walking.getFrame(), getPosition().x, getPosition().y);
        } else if (FacingAngle >= 22.5 && FacingAngle < 67.5) {
            System.out.println("2");
            batch.draw(Render.getTexture("player_right"), getPosition().x, getPosition().y);
        } else if (FacingAngle >= 67.5 && FacingAngle < 112.5) {
            System.out.println("3");
            batch.draw(Render.getTexture("player_up"), getPosition().x, getPosition().y);
        } else if (FacingAngle >= 112.5 && FacingAngle < 157.5) {
            System.out.println("4");
            batch.draw(Render.getTexture("player_left"), getPosition().x, getPosition().y);
        } else if (FacingAngle >= 157.5 && FacingAngle < 202.5) {
            System.out.println("5");
            batch.draw(Render.getTexture("player_left"), getPosition().x, getPosition().y);
        } else if (FacingAngle >= 202.5 && FacingAngle < 247.5) {
            System.out.println("6");
            batch.draw(Render.getTexture("player_left"), getPosition().x, getPosition().y);
        } else if (FacingAngle >= 247.5 && FacingAngle < 292.5) {
            System.out.println("7");
            batch.draw(Render.getTexture("player_down"), getPosition().x, getPosition().y);
        } else if (FacingAngle >= 292.5 && FacingAngle < 337.5) {
            System.out.println("8");
            batch.draw(Render.getTexture("player_right"), getPosition().x, getPosition().y);
        } else if (FacingAngle >= 337.5 && FacingAngle <= 360) {
            System.out.println("9");
            batch.draw(Render.getTexture("player_right"), getPosition().x, getPosition().y);
        }

        batch.draw(Render.getTexture("sword"), getPosition().x + getSize().x/2, getPosition().y, 0,0,32,32,1,1,FacingAngle);
        */
    }

    public BoundingBox getAttackBox() {

        return getIntereactBox();
    }

    public BoundingBox getIntereactBox() {
        BoundingBox RectPla;

        if (FacingAngle >= 0 && FacingAngle < 22.5) {
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x+8,
                            getPosition().y-4,
                            getPosition().z),
                    new Vector3(getPosition().x+32,
                            getPosition().y + getSize().y+4,
                            getPosition().z + getSize().z));
            return RectPla;
        } else if (FacingAngle >= 22.5 && FacingAngle < 67.5) {
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x+8,
                            getPosition().y+8,
                            getPosition().z),
                    new Vector3(getPosition().x+32,
                            getPosition().y + getSize().y+16,
                            getPosition().z + getSize().z));
            return RectPla;
        } else if (FacingAngle >= 67.5 && FacingAngle < 112.5) {
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x-4,
                            getPosition().y+8,
                            getPosition().z),
                    new Vector3(getPosition().x+20,
                            getPosition().y + getSize().y+14,
                            getPosition().z + getSize().z));
            return RectPla;
        } else if (FacingAngle >= 112.5 && FacingAngle < 157.5) {
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x - 16,
                            getPosition().y+8,
                            getPosition().z),
                    new Vector3(getPosition().x+8,
                            getPosition().y + getSize().y+16,
                            getPosition().z + getSize().z));
            return RectPla;
        } else if (FacingAngle >= 157.5 && FacingAngle < 202.5) {
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x - 16,
                            getPosition().y-4,
                            getPosition().z),
                    new Vector3(getPosition().x+8,
                            getPosition().y + getSize().y+6,
                            getPosition().z + getSize().z));
            return RectPla;
        } else if (FacingAngle >= 202.5 && FacingAngle < 247.5) {
            System.out.println(":5");
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x - 16,
                            getPosition().y-16,
                            getPosition().z),
                    new Vector3(getPosition().x+8,
                            getPosition().y + getSize().y-8,
                            getPosition().z + getSize().z));
            return RectPla;
        } else if (FacingAngle >= 247.5 && FacingAngle < 292.5) {
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x-4,
                            getPosition().y-16,
                            getPosition().z),
                    new Vector3(getPosition().x+20,
                            getPosition().y + getSize().y-8,
                            getPosition().z + getSize().z));
            return RectPla;
        } else if (FacingAngle >= 292.5 && FacingAngle < 337.5) {
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x+8,
                            getPosition().y-16,
                            getPosition().z),
                    new Vector3(getPosition().x+32,
                            getPosition().y + getSize().y-8,
                            getPosition().z + getSize().z));
            return RectPla;
        } else if (FacingAngle >= 337.5 && FacingAngle <= 360) {
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x+8,
                            getPosition().y-4,
                            getPosition().z),
                    new Vector3(getPosition().x+32,
                            getPosition().y + getSize().y+4,
                            getPosition().z + getSize().z));
            return RectPla;
        }

        RectPla = new BoundingBox(
                new Vector3(
                    getPosition().x + (1 * getSize().x),
                    getPosition().y - 12,
                        getPosition().z),
                new Vector3(
                        getPosition().x + (1 * getSize().x) + getSize().x,
                        getPosition().y + getSize().y + 8,
                        getPosition().z + getSize().z));

        return RectPla;
    }

}
