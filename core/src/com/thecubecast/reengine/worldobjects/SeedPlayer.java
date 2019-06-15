package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.data.Item;
import com.thecubecast.reengine.data.dcputils.TextureAnimation;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.gamestates.GameState;

public class SeedPlayer extends WorldObject {

    public int Health = 100;

    public Item[] Inventory = new Item[30];
    public Item[] Equipment = new Item[4];

    public enum AttackPhases {first,second,third;

        public boolean Attacking = false;

        public boolean GoodCombo = false;

        public boolean AttackOnNextHit = false;

        public boolean BrokeCombo = false;

        public AttackPhases moveUpCombo() { //AKA the combo length
            switch (this) {
                case first:
                    return second;
                case second:
                    return third;
                case third:
                    return first;
                default:
                    return first;
            }
        };
    };

    public AttackPhases AttackPhase= AttackPhases.first; //AKA the combo length

    //True is left, False is right
    boolean Facing = true;

    public float FacingAngle;

    public float RollingTime;
    public boolean Rolling;

    TextureAnimation<TextureAtlas.AtlasRegion> Walking;
    TextureAnimation<TextureAtlas.AtlasRegion> Roll;
    TextureAnimation<TextureAtlas.AtlasRegion> Attack1;
    TextureAnimation<TextureAtlas.AtlasRegion> Attack2;
    TextureAnimation<TextureAtlas.AtlasRegion> Attack3;
    TextureAnimation<TextureAtlas.AtlasRegion> Idle;

    TextureRegion Shadow;

    GameState G;

    public SeedPlayer(int x, int y, int z, GameState G) {
        super(x,y,0, new Vector3(16,16,2));
        this.setState(type.Dynamic);
        this.G = G;
        Walking = new TextureAnimation<>(GameStateManager.Render.getTextures("adventurer-run"), 0.1f);
        Roll = new TextureAnimation<>(GameStateManager.Render.getTextures("adventurer-smrslt"), 0.05f);
        Idle = new TextureAnimation<>(GameStateManager.Render.getTextures("adventurer-idle"), 0.1f);
        Attack1 = new TextureAnimation<>(GameStateManager.Render.getTextures("adventurer-attack1"), 0.1f);
        Attack2 = new TextureAnimation<>(GameStateManager.Render.getTextures("adventurer-attack2"), 0.1f);
        Attack3 = new TextureAnimation<>(GameStateManager.Render.getTextures("adventurer-attack3"), 0.1f);
        Shadow = GameStateManager.Render.getTexture("Shadow");
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {

        if (Attack1.hasFinishedOneLoop() || Attack2.hasFinishedOneLoop() || Attack3.hasFinishedOneLoop()) {
            Attack1.reset();
            Attack2.reset();
            Attack3.reset();
            if (!AttackPhase.BrokeCombo && AttackPhase.GoodCombo) {
                AttackPhase = AttackPhase.moveUpCombo();
                AttackPhase.Attacking = true;
                AttackPhase.AttackOnNextHit = true;
            } else {
                AttackPhase.Attacking = false;
            }
            AttackPhase.GoodCombo = false;
            AttackPhase.BrokeCombo = false;
        }

        if (RollingTime - delta > 0)
            RollingTime -= delta;

        if (Rolling) {
            getVelocity().clamp(-10, 10);
        } else {

        }

        if (getState().equals(type.Dynamic)) {

            if (AttackPhase.Attacking) {
                setVelocity(0,0,0);
            } else {
                if (getVelocity().x > 0.1f ) {
                    Facing = false;
                } else if (getVelocity().x < -0.1f) {
                    Facing = true;
                }
            }

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
                Roll.update(Gdx.graphics.getDeltaTime());
                TextureRegion frame = Roll.getFrame();
                // batch.draw(frame, Facing ? (int)getPosition().x + (frame.getRegionWidth()) : (int)getPosition().x, (int)getPosition().y + (int)getPosition().z / 2, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);

                if (getVelocity().x < 0)
                    batch.draw(frame,  Facing ? (int) getPosition().x+34: (int) getPosition().x-(37/2)+4, getPosition().y-6, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);
                else
                    batch.draw(frame,  Facing ? (int) getPosition().x+34: (int) getPosition().x-(37/2)+4, getPosition().y-6, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);
                if (Roll.hasFinishedOneLoop()) {
                    Rolling = false;
                    Roll.reset();
                    Roll.pause();
                }
            } else {
                if (AttackPhase.Attacking) {
                    batch.draw(Shadow, Facing ? (int) getPosition().x + 1 : (int) getPosition().x + 3, (int) getPosition().y - 2 + (int) getZFloor() / 2);
                    //Attacking animation
                    TextureRegion frame = Attack1.getFrame();
                    switch (AttackPhase) {
                        case first:
                            Attack1.update(Gdx.graphics.getDeltaTime());
                            frame = Attack1.getFrame();
                            break;
                        case second:
                            Attack2.update(Gdx.graphics.getDeltaTime());
                            frame = Attack2.getFrame();
                            break;
                        case third:
                            Attack3.update(Gdx.graphics.getDeltaTime());
                            frame = Attack3.getFrame();
                            break;
                    }

                    batch.draw(frame, Facing ? (int) getPosition().x+34: (int) getPosition().x-(37/2)+4, getPosition().y, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);

                } else {
                    if (Math.abs(this.getVelocity().y) >= 0.5f || Math.abs(this.getVelocity().x) >= 0.5f) {
                        batch.draw(Shadow, Facing ? (int) getPosition().x + 1 : (int) getPosition().x + 3, (int) getPosition().y - 2 + (int) getZFloor() / 2);
                        //running animation
                        Walking.update(Gdx.graphics.getDeltaTime());
                        TextureRegion frame = Walking.getFrame();
                        batch.draw(frame,  Facing ? (int) getPosition().x+34: (int) getPosition().x-(37/2)+4, getPosition().y, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);
                    } else if (this.getVelocity().y < 0.5f || this.getVelocity().x < 0.5f) {
                        batch.draw(Shadow, Facing ? (int) getPosition().x + 1 : (int) getPosition().x + 3, (int) getPosition().y - 2 + (int) getZFloor() / 2);
                        //Idle animation
                        Idle.update(Gdx.graphics.getDeltaTime());
                        TextureRegion frame = Idle.getFrame();
                        batch.draw(frame,  Facing ? (int) getPosition().x+34: (int) getPosition().x-(37/2)+4, getPosition().y, 0f, 0f, (float) frame.getRegionWidth(), (float) frame.getRegionHeight(), Facing ? -1f : 1f, 1f, 0f);
                    }
                }
            }
        }

    }

    public BoundingBox getAttackBox() {

        return getIntereactBox();
    }

    public BoundingBox getIntereactBox() {
        BoundingBox RectPla;

        if (Facing) {
            //Left
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x - 16,
                            getPosition().y,
                            getPosition().z),
                    new Vector3(getPosition().x+8,
                            getPosition().y + getSize().y+18,
                            getPosition().z + getSize().z));
            return RectPla;
        } else {
            //Right
            RectPla = new BoundingBox(
                    new Vector3(getPosition().x+8,
                            getPosition().y,
                            getPosition().z),
                    new Vector3(getPosition().x+32,
                            getPosition().y + getSize().y+16,
                            getPosition().z + getSize().z));
            return RectPla;
        }

        /*
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
        */
    }

    public void Attack() {

        switch (AttackPhase) {
            case first:
                if (AttackPhase.Attacking) {
                    if (Attack1.getFrameIndex() >= Attack1.getNumberOfFrames()-2) {
                        AttackPhase.GoodCombo = true;
                    } else {
                        AttackPhase.BrokeCombo = true;
                        AttackPhase.GoodCombo = false;
                    }
                } else {
                    AttackPhase.AttackOnNextHit = true;
                }
                break;
            case second:
                if (AttackPhase.Attacking) {
                    if (Attack2.getFrameIndex() >= Attack2.getNumberOfFrames() - 2) {
                        AttackPhase.GoodCombo = true;
                    } else {
                        AttackPhase.BrokeCombo = true;
                        AttackPhase.GoodCombo = false;
                    }
                } else {
                    AttackPhase = AttackPhases.first;
                    AttackPhase.AttackOnNextHit = true;
                    AttackPhase.GoodCombo = false;
                    AttackPhase.BrokeCombo = false;
                }
                break;
            case third:
                if (!AttackPhase.Attacking) {
                    AttackPhase = AttackPhases.first;
                    AttackPhase.AttackOnNextHit = true;
                    AttackPhase.GoodCombo = false;
                    AttackPhase.BrokeCombo = false;
                }
                break;

        }

        AttackPhase.Attacking = true;

    }

    public int getItemQuant(int ItemId) {
        int StoredResource = 0;

        for (int j = 0; j < Inventory.length; j++) {
            if (Inventory[j] != null) {
                if (Inventory[j].getID() == ItemId) {
                    //Found matching item
                    StoredResource += Inventory[j].getQuantity();
                }
            }
        }

        return StoredResource;
    }

    public boolean AddToInventory(Item item) {

        boolean found = false;

        //Finds first Matching spot
        for (int j = 0; j < Inventory.length; j++) {
            if (Inventory[j] != null) {
                if (Inventory[j].getID() == item.getID()) {
                    Inventory[j].setQuantity(Inventory[j].getQuantity() + item.getQuantity());
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            for (int j = 0; j < Inventory.length; j++) {
                if (Inventory[j] == null) {
                    Item tempItem = new Item(item);
                    Inventory[j] = tempItem;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean DeductFromInventory(int ItemID, int Quant) {
        boolean Success = false;

        if (getItemQuant(ItemID) >= Quant) {

            int ResourceRemaining = Quant;

            for (int j = 0; j < Inventory.length; j++) {
                if (Inventory[j] != null) {
                    if (Inventory[j].getID() == ItemID) { //Found matching item
                        if (Inventory[j].getQuantity() < ResourceRemaining) { //if that item Quant is less then needed
                            ResourceRemaining -= Inventory[j].getQuantity();
                            Inventory[j] = null;
                        } else if (Inventory[j].getQuantity() == ResourceRemaining) {
                            ResourceRemaining = 0;
                            Inventory[j] = null;
                            break;
                        } else {
                            Inventory[j].setQuantity(Inventory[j].getQuantity() - ResourceRemaining);
                            break;
                        }
                    }
                }
            }

            if (ResourceRemaining == 0) {
                Success = true;
            }

        } else {
            return false;
        }

        return Success;
    }
}
