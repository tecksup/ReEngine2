package com.thecubecast.reengine.worldobjects.entityprefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.Item;
import com.thecubecast.reengine.data.dcputils.TextureAnimation;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.graphics.scene2d.TkLabel;
import com.thecubecast.reengine.worldobjects.ai.PathfindingWorldObject;
import com.thecubecast.reengine.worldobjects.ai.aistates.Grass_Tuft_States;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledGraph;

public class Grass_Tuft extends PathfindingWorldObject {
    //True is left, False is right
    public boolean Facing = true;

    TextureAnimation<TextureAtlas.AtlasRegion> Walking;
    TextureAnimation<TextureAtlas.AtlasRegion> Idle;

    TextureRegion Shadow;

    TkLabel NameLabel;
    Group stage;
    ProgressBar HealthBar;

    public Grass_Tuft(int x, int y, int z, float knockbackResistance, float health, intractability interact, boolean invincible, FlatTiledGraph map, GameStateManager gsm) {
        super("Grass Tuft", x, y, z, new Vector3(4,4,4), knockbackResistance, health, interact, invincible, map, Grass_Tuft_States.IDLE);
        setHitboxOffset(new Vector3(6,0,0));

        setDropOnDeath(new Item(0, 2));

        Walking = new TextureAnimation<>(GameStateManager.Render.getTextures("Enemy_Grass_Tuft_Moving"), 0.06f);
        Idle = new TextureAnimation<>(GameStateManager.Render.getTextures("Enemy_Grass_Tuft_Idle"), 0.06f);
        Shadow = GameStateManager.Render.getTexture("Shadow");

        FocusStrength = 0.15f;

        Skin skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
        skin.getFont("Mecha").getData().markupEnabled = true;
        skin.getFont("Pixel").getData().markupEnabled = true;

        stage = new Group();

        NameLabel = new TkLabel(getName(), skin);
        HealthBar = new ProgressBar(0f, 10f, 0.1f, false, skin, "Health_npc");
        HealthBar.setValue(getHealth() / 5);
        HealthBar.setWidth(40);
        stage.addActor(NameLabel);
        stage.addActor(HealthBar);
    }

    @Override
    public BoundingBox getHitbox() {
        return super.getHitbox();
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

        if(Math.abs(this.getVelocity().y) >= 0.5f || Math.abs(this.getVelocity().x) >= 0.5f) {
            batch.draw(Shadow, Facing ? (int)getPosition().x + 3: (int)getPosition().x + 3, (int)getPosition().y - 2 + (int)getZFloor() / 2);

            Walking.update(Gdx.graphics.getDeltaTime());

            //running animation
            batch.draw(Walking.getFrame(), Facing ? (int)getPosition().x + 2 + (Walking.getFrame().getRegionWidth()) : (int)getPosition().x, (int)getPosition().y + (int)getPosition().z / 2, Facing ? -(Walking.getFrame().getRegionHeight()) : (Walking.getFrame().getRegionHeight()), (Walking.getFrame().getRegionHeight()));
        } else if(this.getVelocity().y < 0.5f || this.getVelocity().x < 0.5f) {
            batch.draw(Shadow, Facing ? (int)getPosition().x +3 : (int)getPosition().x + 3, (int)getPosition().y - 2 + (int)getZFloor() / 2);
            //Idle animation
            batch.draw(Idle.getFrame(), Facing ? (int)getPosition().x + 2 + (Idle.getFrame().getRegionWidth()) : (int)getPosition().x, (int)getPosition().y + (int)getPosition().z / 2, Facing ? -(Idle.getFrame().getRegionHeight()) : (Idle.getFrame().getRegionHeight()), (Idle.getFrame().getRegionHeight()));
        }

    }

    @Override
    public void drawGui(SpriteBatch batch, float Time) {
        stage.draw(batch, 1);
    }

    public void update(float delta, GameState G) {
        if (G.Collisions == null) {
            return;
        }
        for (int i = 0; i < G.Collisions.size(); i++) {
            if (G.Collisions.get(i).getHash() == this.hashCode()) {
                //Rectangle hankbox = new Rectangle();
                //G.Collisions.get(i).setRect(hankbox);
            }
        }
        super.update(delta, G);
        stage.act(Gdx.graphics.getDeltaTime());
        NameLabel.setText(getName());
        NameLabel.setPosition((int) getPosition().x - 2, (int) getPosition().y + 24);
        HealthBar.setValue(getHealth() / 5);
        HealthBar.setPosition((int) getPosition().x + 10 - (HealthBar.getWidth() / 2), (int) getPosition().y + 18);
    }
}
