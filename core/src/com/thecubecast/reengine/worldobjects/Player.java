package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.data.Cube;
import com.thecubecast.reengine.gamestates.GameState;

import java.util.List;

import static com.thecubecast.reengine.graphics.Draw.loadAnim;

public class Player extends WorldObject {

    Texture Shadow = new Texture(Gdx.files.internal("Sprites/Shadow.png"));
    int imageWidth = 16;
    int imageHeight = 16;
    public float ShadowZ = 0;

    public float AttackTime;

    public float MaxVelocity = 3;

    public Direction playerDirection = Direction.South;
    public AnimationState AnimState = AnimationState.Standing;

    private Animation<TextureRegion> PengAnim0, PengAnim1, PengAnim2, PengAnim3, PengAnim4, PengAnim5, PengAnim6, PengAnim7;
    Texture penguin0, penguin1, penguin2, penguin3, penguin4, penguin5, penguin6, penguin7;

    public enum Direction {
        Direction() {

        },
        South, SouthEast, East, NorthEast, North, NorthWest, West, SouthWest
    }

    public boolean Facing = false; //true is left direction

    public enum AnimationState {
        Standing, Walking, Running, Jumping, Falling
    }

    public Player(int x, int y, int z) {
        super(x, y, z, new Vector3(10, 10, 12), type.Dynamic);

        PengAnim0 = new Animation<TextureRegion>(0.1f, loadAnim(penguin0, "Sprites/8direct/south.png", 4, 1));
        PengAnim1 = new Animation<TextureRegion>(0.1f, loadAnim(penguin1, "Sprites/8direct/southEast.png", 4, 1));
        PengAnim2 = new Animation<TextureRegion>(0.1f, loadAnim(penguin2, "Sprites/8direct/east.png", 4, 1));
        PengAnim3 = new Animation<TextureRegion>(0.1f, loadAnim(penguin3, "Sprites/8direct/northEast.png", 4, 1));
        PengAnim4 = new Animation<TextureRegion>(0.1f, loadAnim(penguin4, "Sprites/8direct/north.png", 4, 1));
        PengAnim5 = new Animation<TextureRegion>(0.1f, loadAnim(penguin5, "Sprites/8direct/northWest.png", 4, 1));
        PengAnim6 = new Animation<TextureRegion>(0.1f, loadAnim(penguin6, "Sprites/8direct/west.png", 4, 1));
        PengAnim7 = new Animation<TextureRegion>(0.1f, loadAnim(penguin7, "Sprites/8direct/southWest.png", 4, 1));

    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {

        //Debug.println("Player", "" + delta);
        if (AttackTime - delta > 0)
            AttackTime -= delta;

        // Debug.println("Player", "" + AttackTime);

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
                    //TODO SHADOW VECTOR MATH, determine the z of a collisionbox below the player, 0 if not found.


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

        //Calculate the AnimState
        if (Math.abs(getVelocity().x) > 0) {
            AnimState = AnimationState.Walking;
        } else {
            AnimState = AnimationState.Standing;
        }

    }

    public BoundingBox getAttackBox() {

        return getIntereactBox();
    }

    public BoundingBox getIntereactBox() {
        BoundingBox RectPla = new BoundingBox();

        switch (playerDirection) {
            case South:
                RectPla = new BoundingBox(new Vector3(imageWidth / 4 + getPosition().x, getPosition().y - (1 * getSize().y), getPosition().z), new Vector3(imageWidth / 4 + getPosition().x + getSize().x, getPosition().y - (1 * getSize().y) + getSize().y, getPosition().z + getSize().z));
                break;
            case SouthEast:
                RectPla = new BoundingBox(new Vector3(imageWidth / 4 + getPosition().x + (1 * getSize().x), getPosition().y - (1 * getSize().y), getPosition().z), new Vector3(imageWidth / 4 + getPosition().x + (1 * getSize().x) + getSize().x, getPosition().y - (1 * getSize().y) + getSize().y, getPosition().z + getSize().z));
                break;
            case East:
                RectPla = new BoundingBox(new Vector3(imageWidth / 4 + getPosition().x + (1 * getSize().x), getPosition().y, getPosition().z), new Vector3(imageWidth / 4 + getPosition().x + (1 * getSize().x) + getSize().x, getPosition().y + getSize().y, getPosition().z + getSize().z));
                break;
            case NorthEast:
                RectPla = new BoundingBox(new Vector3(imageWidth / 4 + getPosition().x + (1 * getSize().x), getPosition().y + (1 * getSize().y), getPosition().z), new Vector3(imageWidth / 4 + getPosition().x + (1 * getSize().x) + getSize().x, getPosition().y + (1 * getSize().y) + getSize().y, getPosition().z + getSize().z));
                break;
            case North:
                RectPla = new BoundingBox(new Vector3(imageWidth / 4 + getPosition().x, getPosition().y + (1 * getSize().y), getPosition().z), new Vector3(imageWidth / 4 + getPosition().x + getSize().x, getPosition().y + (1 * getSize().y) + getSize().y, getPosition().z + getSize().z));
                break;
            case NorthWest:
                RectPla = new BoundingBox(new Vector3(imageWidth / 4 + getPosition().x - (1 * getSize().x), getPosition().y + (1 * getSize().y), getPosition().z), new Vector3(imageWidth / 4 + getPosition().x - (1 * getSize().x) + getSize().x, getPosition().y + (1 * getSize().y) + getSize().y, getPosition().z + getSize().z));
                break;
            case West:
                RectPla = new BoundingBox(new Vector3(imageWidth / 4 + getPosition().x - (1 * getSize().x), getPosition().y, getPosition().z), new Vector3(imageWidth / 4 + getPosition().x - (1 * getSize().x) + getSize().x, getPosition().y + getSize().y, getPosition().z + getSize().z));
                break;
            case SouthWest:
                RectPla = new BoundingBox(new Vector3(imageWidth / 4 + getPosition().x - (1 * getSize().x), getPosition().y - (1 * getSize().y), getPosition().z), new Vector3(imageWidth / 4 + getPosition().x - (1 * getSize().x) + getSize().x, getPosition().y - (1 * getSize().y) + getSize().y, getPosition().z + getSize().z));
                break;
        }
        return RectPla;
    }

    @Override
    public void draw(SpriteBatch batch, float time) {

        batch.draw(Shadow, getPosition().x, getPosition().y + getZFloor() / 2);

        switch (playerDirection) {
            case South:
                TextureRegion tempFrame0 = PengAnim0.getKeyFrame(time, true);
                batch.draw(tempFrame0, Facing ? getPosition().x + (imageWidth) : getPosition().x, getPosition().y + getPosition().z / 2, Facing ? -(imageHeight) : (imageHeight), (imageHeight));
                break;
            case SouthEast:
                TextureRegion tempFrame1 = PengAnim1.getKeyFrame(time, true);
                batch.draw(tempFrame1, Facing ? getPosition().x + (imageWidth) : getPosition().x, getPosition().y + getPosition().z / 2, Facing ? -(imageHeight) : (imageHeight), (imageHeight));
                break;
            case East:
                TextureRegion tempFrame2 = PengAnim2.getKeyFrame(time, true);
                batch.draw(tempFrame2, Facing ? getPosition().x + (imageWidth) : getPosition().x, getPosition().y + getPosition().z / 2, Facing ? -(imageHeight) : (imageHeight), (imageHeight));
                break;
            case NorthEast:
                TextureRegion tempFrame3 = PengAnim3.getKeyFrame(time, true);
                batch.draw(tempFrame3, Facing ? getPosition().x + (imageWidth) : getPosition().x, getPosition().y + getPosition().z / 2, Facing ? -(imageHeight) : (imageHeight), (imageHeight));
                break;
            case North:
                TextureRegion tempFrame4 = PengAnim4.getKeyFrame(time, true);
                batch.draw(tempFrame4, Facing ? getPosition().x + (imageWidth) : getPosition().x, getPosition().y + getPosition().z / 2, Facing ? -(imageHeight) : (imageHeight), (imageHeight));
                break;
            case NorthWest:
                TextureRegion tempFrame5 = PengAnim5.getKeyFrame(time, true);
                batch.draw(tempFrame5, Facing ? getPosition().x + (imageWidth) : getPosition().x, getPosition().y + getPosition().z / 2, Facing ? -(imageHeight) : (imageHeight), (imageHeight));
                break;
            case West:
                TextureRegion tempFrame6 = PengAnim6.getKeyFrame(time, true);
                batch.draw(tempFrame6, Facing ? getPosition().x + (imageWidth) : getPosition().x, getPosition().y + getPosition().z / 2, Facing ? -(imageHeight) : (imageHeight), (imageHeight));
                break;
            case SouthWest:
                TextureRegion tempFrame7 = PengAnim7.getKeyFrame(time, true);
                batch.draw(tempFrame7, Facing ? getPosition().x + (imageWidth) : getPosition().x, getPosition().y + getPosition().z / 2, Facing ? -(imageHeight) : (imageHeight), (imageHeight));
                break;
        }

        switch (AnimState) {
            case Standing:

                break;

            case Falling:
                //Do nothing
                //batch.draw(tempFrame, Facing ? getPosition().x*(Size) + (Size) : getPosition().x*Size, getPosition().y*Size, Facing ? -(Size) : (Size), (Size*2));
                break;

            case Walking:

                break;

            case Running:

                break;
        }
    }

    public void MovePlayerVelocity(Direction direction, float speed) {

        switch (direction) {
            case South:
                super.setVelocityY((getVelocity().y - speed));
                break;
            case SouthEast:
                super.setVelocityX((getVelocity().x + speed));
                super.setVelocityY((getVelocity().y - speed));
                break;
            case East:
                super.setVelocityX((getVelocity().x + speed));
                break;
            case NorthEast:
                super.setVelocityX((getVelocity().x + speed));
                super.setVelocityY((getVelocity().y + speed));
                break;
            case North:
                super.setVelocityY((getVelocity().y + speed));
                break;
            case NorthWest:
                super.setVelocityX((getVelocity().x - speed));
                super.setVelocityY((getVelocity().y + speed));
                break;
            case West:
                super.setVelocityX((getVelocity().x - speed));
                break;
            case SouthWest:
                super.setVelocityX((getVelocity().x - speed));
                super.setVelocityY((getVelocity().y - speed));
                break;
        }
        playerDirection = direction;

        if (Math.abs(getVelocity().x) > MaxVelocity) {
            if (getVelocity().x > 0) {
                setVelocityX(MaxVelocity);
            } else {
                setVelocityX(-MaxVelocity);
            }
        }

        if (Math.abs(getVelocity().y) > MaxVelocity) {
            if (getVelocity().y > 0) {
                setVelocityY(MaxVelocity);
            } else {
                setVelocityY(-MaxVelocity);
            }
        }

    }

    public Vector2 VecDirction() {
        switch (playerDirection) {
            case South:
                return new Vector2(0, -1);
            case SouthEast:
                return new Vector2(1, -1);
            case East:
                return new Vector2(1, 0);
            case NorthEast:
                return new Vector2(1, 1);
            case North:
                return new Vector2(0, 1);
            case NorthWest:
                return new Vector2(-1, 1);
            case West:
                return new Vector2(-1, 0);
            case SouthWest:
                return new Vector2(-1, -1);
        }
        return null;
    }

    public Direction getPlayerDirection() {
        return playerDirection;
    }

    public void setPlayerDirection(Direction playerDirection) {
        this.playerDirection = playerDirection;
    }

    @Override
    public BoundingBox getHitbox() {
        BoundingBox PrismPla = new BoundingBox(new Vector3(imageWidth / 4 + getPosition().x, getPosition().y, getPosition().z), new Vector3(imageWidth / 4 + getPosition().x, getPosition().y, getPosition().z).add(getSize()));
        return PrismPla;
    }

    @Override
    public boolean checkCollision(Vector3 Newposition, List<Cube> Colls) {
        if (Colls == null) {
            return false;
        }

        BoundingBox PrismPla = new BoundingBox(new Vector3(imageWidth / 4 + Newposition.x, Newposition.y, Newposition.z), new Vector3(imageWidth / 4 + Newposition.x, Newposition.y, Newposition.z).add(getSize()));

        for (int i = 0; i < Colls.size(); i++) {
            if (PrismPla.intersects(Colls.get(i).getPrism())) {
                return true; // Dont move
            }
        }
        return false;
    }

    @Override
    public boolean checkCollision(Vector3 Newposition, List<Cube> Colls, boolean FloorSearch) {
        if (Colls == null) {
            return false;
        }

        BoundingBox PrismPla = new BoundingBox(new Vector3(imageWidth / 4 + Newposition.x, Newposition.y, Newposition.z), new Vector3(imageWidth / 4 + Newposition.x, Newposition.y, Newposition.z).add(getSize()));
        for (int i = 0; i < Colls.size(); i++) {
            if (PrismPla.intersects(Colls.get(i).getPrism())) {
                if (FloorSearch) {
                    setZFloor(Colls.get(i).getPrism().max.z);
                }
                return true; // Dont move
            }
        }
        return false;
    }
}
