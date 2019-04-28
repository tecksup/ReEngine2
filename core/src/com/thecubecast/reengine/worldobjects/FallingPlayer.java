package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.dcputils.TextureAnimation;
import com.thecubecast.reengine.gamestates.GameState;

import static com.thecubecast.reengine.data.GameStateManager.AudioM;

public class FallingPlayer extends WorldObject {

    TextureAnimation<TextureAtlas.AtlasRegion> Walking;
    TextureAnimation<TextureAtlas.AtlasRegion> Jump;
    TextureAnimation<TextureAtlas.AtlasRegion> Fall;

    public boolean Falling = false;

    public boolean HasJump;
    public float JustJumped = 0;
    public float JumpCooling = 0;
    public float LastOnGroundY;
    public float JumpCooldown = 0.25f;

    public int Health = 3;

    public FallingPlayer() {
        this.setPosition(20, 0, 0);
        this.setSize(new Vector3(12, 12, 12));
        this.setState(type.Dynamic);

        Walking = new TextureAnimation<>(GameStateManager.Render.getTextures("WalkingR"), 0.1f);
        Jump = new TextureAnimation<>(GameStateManager.Render.getTextures("JumpR"), 0.05f);
        Fall = new TextureAnimation<>(GameStateManager.Render.getTextures("Falling"), 0.1f);

    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {

        JustJumped += delta;
        JumpCooling += delta;

        if (getState().equals(type.Dynamic)) {

            super.setVelocityX((getVelocity().x + getVelocity().x * -1 * 0.25f));
            super.setVelocityY((getVelocity().y + getVelocity().y * -1 * 0.15f));
            super.setVelocityZ((getVelocity().z + getVelocity().z * -1 * 0.25f) - 1);

            if (JustJumped > JumpCooldown)
                super.setVelocityY(getVelocity().y -8);

            if (getVelocity().y < -8)
                super.setVelocityY(-8);

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
                    if (getPosition().y != LastOnGroundY && getPosition().y - LastOnGroundY < -64) {
                        Health--;
                        AudioM.play("Damage");
                    }
                    super.setVelocityY(0);
                    super.setPositionY((int)(getPosition().y / 16)*16+1);
                    HasJump = true;
                    LastOnGroundY = getPosition().y;
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

        if (getVelocity().y < -2) { //Falling
            Fall.setType(TextureAnimation.Type.STOP_IN_EACH_NEW_FRAME);
            Fall.setStayOnLastFrameAfterFinished(true);
            Fall.update(Gdx.graphics.getDeltaTime());
            batch.draw(Fall.getFrame(), (int) getPosition().x, (int) getPosition().y + (int) getPosition().z / 2, 0f, 0f, (float) Fall.getFrame().getRegionWidth(), (float) Fall.getFrame().getRegionHeight(), 1f, 1f, 0f);
        } else if (getVelocity().y > 1) {
            Jump.setType(TextureAnimation.Type.STOP_IN_EACH_NEW_FRAME);
            Jump.setStayOnLastFrameAfterFinished(true);
            Jump.update(Gdx.graphics.getDeltaTime());
            batch.draw(Jump.getFrame(), (int) getPosition().x, (int) getPosition().y + (int) getPosition().z / 2, 0f, 0f, (float) Jump.getFrame().getRegionWidth(), (float) Jump.getFrame().getRegionHeight(), 1f, 1f, 0f);
        }else {
            if (getVelocity().x > 0.2f) {
                Walking.update(Gdx.graphics.getDeltaTime());
                batch.draw(Walking.getFrame(), (int) getPosition().x, (int) getPosition().y + (int) getPosition().z / 2, 0f, 0f, (float) Walking.getFrame().getRegionWidth(), (float) Walking.getFrame().getRegionHeight(), 1f, 1f, 0f);
            } else if (getVelocity().x < -0.2f) {
                Walking.update(Gdx.graphics.getDeltaTime());
                batch.draw(Walking.getFrame(), (int) getPosition().x + Walking.getFrame().getRegionWidth(), (int) getPosition().y + (int) getPosition().z / 2, 0f, 0f, (float) Walking.getFrame().getRegionWidth(), (float) Walking.getFrame().getRegionHeight(), -1f, 1f, 0f);
            } else {
                Jump.setFrame(0);
                batch.draw(Jump.getFrame(), (int) getPosition().x, (int) getPosition().y + (int) getPosition().z / 2, 0f, 0f, (float) Jump.getFrame().getRegionWidth(), (float) Jump.getFrame().getRegionHeight(), 1f, 1f, 0f);

            }
        }

    }

    public BoundingBox getAttackBox() {

        return getIntereactBox();
    }

    public BoundingBox getIntereactBox() {
        BoundingBox RectPla = new BoundingBox();

        RectPla = new BoundingBox(new Vector3(getPosition().x + (1 * getSize().x), getPosition().y - 12, getPosition().z), new Vector3(getPosition().x + (1 * getSize().x) + getSize().x, getPosition().y + getSize().y + 8, getPosition().z + getSize().z));

        return RectPla;
    }

}
