package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.data.dcputils.TextureAnimation;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.gamestates.GameState;

public class HackSlashPlayer extends WorldObject {

    public int Health = 10000;

    public int Energy = 100;

    public float AttackTime;

    //True is left, False is right
    boolean Facing = true;

    private boolean Soundthing = true;
    private float JustPlayed = 0;

    public float RollingTime;
    public boolean Rolling;

    int Degrees = 0;

    TextureAnimation<TextureAtlas.AtlasRegion> Walking;
    TextureAnimation<TextureAtlas.AtlasRegion> Roll;
    TextureAnimation<TextureAtlas.AtlasRegion> Idle;

    TextureAnimation<TextureAtlas.AtlasRegion> Sword;

    TextureRegion Shadow;

    GameStateManager gsm;
    public HackSlashPlayer(int x, int y, GameStateManager gsm) {
        super(x,y,0, new Vector3(16,16,2));
        this.setState(type.Dynamic);
        this.gsm = gsm;
        Walking = new TextureAnimation<>(gsm.Render.getTextures("player"), 0.1f);
        Roll = new TextureAnimation<>(gsm.Render.getTextures("player_roll"), 0.05f);
        Idle = new TextureAnimation<>(gsm.Render.getTextures("player_idle"), 0.1f);
        Sword = new TextureAnimation<>(gsm.Render.getTextures("sword"), 0.05f);
        Shadow = gsm.Render.getTexture("Shadow");
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {

        JustPlayed += delta;

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
            //setPosition(getPosition().x + getVelocity().x, getPosition().y + getVelocity().y, getPosition().z + getVelocity().z);
        }

    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

        if (Health > 0) {

            if (Rolling) {
                Roll.resume();
                batch.draw(Shadow, Facing ? (int) getPosition().x + 1 : (int) getPosition().x + 3, (int) getPosition().y - 2 + (int) getZFloor() / 2);
                //running animation
                TextureRegion frame = Roll.getFrame(Gdx.graphics.getDeltaTime());
                // batch.draw(frame, Facing ? (int)getPosition().x + (frame.getRegionWidth()) : (int)getPosition().x, (int)getPosition().y + (int)getPosition().z / 2, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);

                if (getVelocity().x < 0)
                    batch.draw(frame, (int) getPosition().x + (frame.getRegionWidth()), (int) getPosition().y + (int) getPosition().z / 2, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), -1f, 1f, 0f);
                else
                    batch.draw(frame, (int) getPosition().x, (int) getPosition().y + (int) getPosition().z / 2, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), 1f, 1f, 0f);
                if (Roll.hasFinishedOneLoop()) {
                    Rolling = false;
                    Roll.reset();
                    Roll.pause();
                }
            } else {
                if (AttackTime > 0.1f) {
                    Sword.resume();
                    TextureRegion frameS = Sword.getFrame(Gdx.graphics.getDeltaTime());
                    batch.draw(frameS, Facing ? (int) getPosition().x - 33 + (frameS.getRegionWidth()) : (int) getPosition().x + 9, (int) getPosition().y - 22 + (int) getPosition().z / 2, 0f, 0f, (float) frameS.getRegionWidth(), (float) frameS.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);

                } else {
                    Sword.pause();
                    Sword.setFrame(0);
                    TextureRegion frameS = Sword.getFrame(Gdx.graphics.getDeltaTime());

                    //Degrees

                    batch.draw(frameS, Facing ? (int) getPosition().x - 33 + (frameS.getRegionWidth()) : (int) getPosition().x + 9, (int) getPosition().y - 22 + (int) getPosition().z / 2, 0f, 0f, (float) frameS.getRegionWidth(), (float) frameS.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);

                }

                if (Math.abs(this.getVelocity().y) >= 0.5f || Math.abs(this.getVelocity().x) >= 0.5f) {
                    batch.draw(Shadow, Facing ? (int) getPosition().x + 1 : (int) getPosition().x + 3, (int) getPosition().y - 2 + (int) getZFloor() / 2);
                    //running animation
                    TextureRegion frame = Walking.getFrame(Gdx.graphics.getDeltaTime());
                    batch.draw(frame, Facing ? (int) getPosition().x + (frame.getRegionWidth()) : (int) getPosition().x, (int) getPosition().y + (int) getPosition().z / 2, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);
                    if (JustPlayed > 0.25f) {
                        //if (Soundthing)
                        //AudioM.playS("feet1.wav");
                        //else
                        //AudioM.playS("feet2.wav");
                        JustPlayed = 0;
                        Soundthing = !Soundthing;
                    }
                } else if (this.getVelocity().y < 0.5f || this.getVelocity().x < 0.5f) {
                    batch.draw(Shadow, Facing ? (int) getPosition().x + 1 : (int) getPosition().x + 3, (int) getPosition().y - 2 + (int) getZFloor() / 2);
                    //Idle animation
                    TextureRegion frame = Idle.getFrame(Gdx.graphics.getDeltaTime());
                    batch.draw(frame, Facing ? (int) getPosition().x + (frame.getRegionWidth()) : (int) getPosition().x, (int) getPosition().y + (int) getPosition().z / 2, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);
                }


                if (this.getVelocity().x + this.getVelocity().y >= 1) {
                    //Attack animation
                }
            }
        }

    }

    public BoundingBox getAttackBox() {

        return getIntereactBox();
    }

    public BoundingBox getIntereactBox() {
        BoundingBox RectPla = new BoundingBox();

        if (isFacing()) {
            RectPla = new BoundingBox(new Vector3(getPosition().x + (-1 * getSize().x), getPosition().y - 12, getPosition().z), new Vector3(getPosition().x + (-1 * getSize().x) + getSize().x, getPosition().y + getSize().y + 8, getPosition().z + getSize().z));
        } else {
            RectPla = new BoundingBox(new Vector3(getPosition().x + (1 * getSize().x), getPosition().y - 12, getPosition().z), new Vector3(getPosition().x + (1 * getSize().x) + getSize().x, getPosition().y + getSize().y + 8, getPosition().z + getSize().z));
        }

        return RectPla;
    }

    public void setFacing(boolean facing) {
        Facing = facing;
    }

    public boolean isFacing() {
        return Facing;
    }
}
