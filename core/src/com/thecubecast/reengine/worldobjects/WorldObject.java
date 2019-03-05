package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.data.Cube;
import com.thecubecast.reengine.gamestates.GameState;

import java.util.List;

public abstract class WorldObject {
    private Vector3 position;
    private Vector3 Size; // The x and y are the entities footprint, z is height.
    private Vector3 HitboxOffset;
    private Vector3 velocity;

    private float ZFloor = 0;

    public int CollisionHashID;

    private type State;

    private boolean DebugView = false;

    public float FocusStrength = 0;

    public enum type {
        Static, Dynamic
    }

    private boolean Collidable = false;

    /**
     * Creates a blank WorldObject at (0,0)
     * Has a default size of 16 pixels
     * Is Static
     **/
    public WorldObject() {
        this.position = new Vector3(0, 0, 0);
        this.velocity = new Vector3(0, 0, 0);
        this.HitboxOffset = new Vector3(0, 0, 0);

        this.Size = new Vector3(16, 16, 4);

        this.State = type.Static;
    }

    /**
     * Creates a blank WorldObject
     *
     * @param x    the x pos
     * @param y    the y pos
     * @param size the size of the hitbox, x and y, ignore z
     **/
    public WorldObject(int x, int y, int z, Vector3 size) {
        this.position = new Vector3(x, y, z);
        this.velocity = new Vector3(0, 0, 0);
        this.HitboxOffset = new Vector3(0, 0, 0);

        this.Size = size;

        this.State = type.Static;
    }

    /**
     * Creates a blank WorldObject
     *
     * @param x     the x pos
     * @param y     the y pos
     * @param size  the size of the hitbox, x and y, ignore z
     * @param State is whether or not it gets it's movement updated based on it's velocity
     **/
    public WorldObject(int x, int y, int z, Vector3 size, type State) {
        this.position = new Vector3(x, y, z);
        this.velocity = new Vector3(0, 0, 0);
        this.HitboxOffset = new Vector3(0, 0, 0);

        this.Size = size;

        this.State = State;
    }

    /**
     * Creates a blank WorldObject
     *
     * @param x         the x pos
     * @param y         the y pos
     * @param size      the size of the hitbox
     * @param State     is whether or not it gets it's movement updated based on it's velocity
     * @param collision if it is true, then it keeps track of it's hashid so that it can update the rectangle for collision
     *                  during its update method
     **/
    public WorldObject(int x, int y, int z, Vector3 size, type State, boolean collision) {
        this.position = new Vector3(x, y, z);
        this.velocity = new Vector3(0, 0, 0);
        this.HitboxOffset = new Vector3(0, 0, 0);

        this.Size = size;

        this.State = State;

        this.Collidable = collision;
    }

    public BoundingBox getHitbox() {
        BoundingBox PrismPla = new BoundingBox(new Vector3(getPosition().x + HitboxOffset.x, getPosition().y + HitboxOffset.y, getPosition().z + +HitboxOffset.z), new Vector3(getPosition()).add(getHitboxOffset()).add(getSize()));
        return PrismPla;
    }

    public boolean checkCollision(Vector3 Newposition, List<Cube> Colls) {
        if (Colls == null) {
            return false;
        }

        BoundingBox PrismPla = new BoundingBox(Newposition, new Vector3(Newposition).add(getSize()));
        for (int i = 0; i < Colls.size(); i++) {
            if (PrismPla.intersects(Colls.get(i).getPrism())) {
                return true; // Dont move
            }
        }
        return false;
    }

    public boolean checkCollision(Vector3 Newposition, List<Cube> Colls, boolean FloorSearch) {
        if (Colls == null) {
            return false;
        }

        BoundingBox PrismPla = new BoundingBox(Newposition, new Vector3(Newposition).add(getSize()));
        for (int i = 0; i < Colls.size(); i++) {
            if (PrismPla.intersects(Colls.get(i).getPrism())) {
                if (FloorSearch) {
                    ZFloor = Colls.get(i).getPrism().max.z;
                }
                return true; // Dont move
            }
        }
        return false;
    }

    public boolean ifColliding(Rectangle coll) {
        Rectangle temp = new Rectangle(getPosition().x, getPosition().y, getSize().x, getSize().y);
        // Dont move
        return temp.overlaps(coll);
    }

    public abstract void init(int Width, int Height);

    public abstract void update(float delta, GameState G);

    public abstract void draw(SpriteBatch batch, float Time);

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public void setPosition(float x, float y, float z) {
        this.position = new Vector3(x, y, z);
    }

    public void setPositionX(float x) {
        this.position.x = x;
    }

    public void setPositionY(float y) {
        this.position.y = y;
    }

    public void setPositionZ(float z) {
        this.position.z = z;
    }

    public Vector3 getVelocity() {
        return velocity;
    }

    public void setVelocity(Vector3 velocity) {
        this.velocity = velocity;
    }

    public void setVelocity(float x, float y, float z) {
        this.velocity = new Vector3(x, y, z);
    }

    public void setVelocityX(float x) {
        this.velocity.x = x;
    }

    public void setVelocityY(float y) {
        this.velocity.y = y;
    }

    public void setVelocityZ(float z) {
        this.velocity.z = z;
    }

    public type getState() {
        return State;
    }

    public void setState(type state) {
        State = state;
    }

    public Vector3 getSize() {
        return Size;
    }

    public void setSize(Vector3 size) {
        Size = size;
    }

    public boolean isCollidable() {
        return Collidable;
    }

    public void setCollidable(boolean collidable) {
        Collidable = collidable;
    }

    public float getZFloor() {
        return ZFloor;
    }

    public void setZFloor(float ZFloor) {
        this.ZFloor = ZFloor;
    }

    public Vector3 getHitboxOffset() {
        return HitboxOffset;
    }

    public void setHitboxOffset(Vector3 hitboxOffset) {
        HitboxOffset = hitboxOffset;
    }

    public boolean isDebugView() {
        return DebugView;
    }

    public void setDebugView(boolean debugView) {
        DebugView = debugView;
    }

    public void dispose() {

    }
}
