package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.data.Cube;
import com.thecubecast.reengine.data.Item;
import com.thecubecast.reengine.data.ParticleHandler;
import com.thecubecast.reengine.gamestates.GameState;

import java.util.List;

import static com.thecubecast.reengine.data.GameStateManager.WorldHeight;
import static com.thecubecast.reengine.data.GameStateManager.WorldWidth;

public abstract class NPC extends WorldObject {

    private float knockbackResistance;
    private long LastDamagedTime;

    private float health;
    private boolean invulnerable = false;
    public String name;

    Item DropOnDeath;

    public float VelDrag = -1;

    private intractability interact;
    private entityState EState = entityState.alive;

    public NPC(String name, int x, int y, int z, Vector3 size, float knockbackResistance, float health) {
        super(x, y, z, size, type.Dynamic);
        this.knockbackResistance = knockbackResistance;
        this.health = health;
        this.name = name;
        this.interact = intractability.Silent;
        init(WorldWidth, WorldHeight);
    }

    public NPC(String name, int x, int y, int z, Vector3 size, float knockbackResistance, float health, boolean invincible) {
        super(x, y, z, size, type.Dynamic);
        this.knockbackResistance = knockbackResistance;
        this.health = health;
        this.name = name;
        this.interact = intractability.Silent;
        this.invulnerable = invincible;
        init(WorldWidth, WorldHeight);
    }

    public NPC(String name, int x, int y, int z, Vector3 size, float knockbackResistance, float health, intractability interact) {
        super(x, y, z, size, type.Dynamic);
        this.knockbackResistance = knockbackResistance;
        this.health = health;
        this.name = name;
        this.interact = interact;
        init(WorldWidth, WorldHeight);
    }

    public NPC(String name, int x, int y, int z, Vector3 size, float knockbackResistance, float health, intractability interact, boolean invincible) {
        super(x, y, z, size, type.Dynamic);
        this.knockbackResistance = knockbackResistance;
        this.health = health;
        this.name = name;
        this.interact = interact;
        this.invulnerable = invincible;
        init(WorldWidth, WorldHeight);
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {

        if (getState().equals(type.Dynamic)) {
            super.setVelocityX((getVelocity().x + getVelocity().x * VelDrag * 0.1f));
            super.setVelocityY((getVelocity().y + getVelocity().y * VelDrag * 0.1f));

            Vector2 pos = new Vector2(getVelocity().x * delta, getVelocity().y * delta);



            if (pos.x < 0) { //Moving left
                if (checkCollision(new Vector3(getPosition().x-1, getPosition().y, getPosition().z), G.Collisions)) {
                    super.setVelocityX(0);
                    super.setPositionX(getPosition().x + 1);
                } else {
                    super.setPositionX((getPosition().x - getVelocity().x*delta*-1));
                }
            } else if (pos.x > 0) { // Moving right
                if (checkCollision(new Vector3(getPosition().x+1, getPosition().y, getPosition().z), G.Collisions)) {
                    super.setVelocityX(0);
                    super.setPositionX(getPosition().x - 1);
                } else {
                    super.setPositionX((getPosition().x + getVelocity().x*delta));
                }
            }

            if (pos.y < 0) { // Moving down
                if (checkCollision(new Vector3(getPosition().x, getPosition().y-1, getPosition().z), G.Collisions)) {
                    super.setVelocityY(0);
                    super.setPositionY(getPosition().y + 1);
                } else {
                    super.setPositionY((getPosition().y - getVelocity().y*delta*-1));
                }
            } else if (pos.y > 0) {
                if (checkCollision(new Vector3(getPosition().x, getPosition().y+1, getPosition().z), G.Collisions)) {
                    super.setVelocityY(0);
                    super.setPositionY(getPosition().y - 1);
                } else {
                    super.setPositionY((getPosition().y + getVelocity().y*delta));
                }
            }

        }
    }

    @Override
    public boolean checkCollision(Vector3 Newposition, List<Cube> Colls) {
        if (Colls == null) {
            return false;
        }

        BoundingBox PrismPla = new BoundingBox(new Vector3(getHitbox().getWidth() / 4 + Newposition.x, Newposition.y, Newposition.z), new Vector3(getHitbox().getWidth() / 4 + Newposition.x, Newposition.y, Newposition.z).add(getSize()));

        for (int i = 0; i < Colls.size(); i++) {
            if (PrismPla.intersects(Colls.get(i).getPrism())) {
                return true; // Dont move
            }
        }
        return false;
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

    }

    public void drawHighlight(SpriteBatch batch, float Time) {
        draw(batch, Time);
    }

    public void drawGui(SpriteBatch batch, float Time) {

    }

    public abstract void interact(GameState G);

    public void heal(int heal) {
        health += heal;
    }

    public void damage(int damage) {
        if (!invulnerable) {
            health -= damage;
            LastDamagedTime = System.nanoTime() / 1000000;
        }

        if (health < 0) {
            Die();
        }
    }

    public void damage(int damage, Vector3 knockback) {
        if (!invulnerable) {
            health -= damage;
            LastDamagedTime = System.nanoTime() / 1000000;
            knockback.x -= knockback.x * knockbackResistance;
            knockback.y -= knockback.y * knockbackResistance;
            knockback.x += getVelocity().x;
            knockback.y += getVelocity().y;
            super.setVelocity(knockback);
        }

        if (health < 0) {
            Die();
        }
    }

    public void Die() {
        //Remove this NPC, or at least set its state to dead
        setEState(entityState.dead);
    }

    public float getHealth() {
        return health;
    }

    public void setHealth(float health) {
        this.health = health;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public intractability getInteract() {
        return interact;
    }

    public void setInteract(intractability interact) {
        this.interact = interact;
    }

    public boolean isAlive() {
        return getEState().equals(entityState.alive);
    }

    public entityState getEState() {
        return EState;
    }

    public void setEState(entityState EState) {
        this.EState = EState;
    }

    public enum intractability {
        Talk, Silent,
    }

    private enum entityState {
        alive, dead
    }

    public boolean isInvulnerable() {
        return invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public float getKnockbackResistance() {
        return knockbackResistance;
    }

    public void setKnockbackResistance(float knockbackResistance) {
        this.knockbackResistance = knockbackResistance;
    }

    public long getLastDamagedTime() {
        return LastDamagedTime;
    }

    public Item getDropOnDeath() {
        return DropOnDeath;
    }

    public void setDropOnDeath(Item dropOnDeath) {
        DropOnDeath = dropOnDeath;
    }

}
