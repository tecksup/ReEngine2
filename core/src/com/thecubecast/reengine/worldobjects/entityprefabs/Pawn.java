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
import com.thecubecast.reengine.data.dcputils.TextureAnimation;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.graphics.scene2d.TkLabel;
import com.thecubecast.reengine.worldobjects.PathfindingWorldObject;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledGraph;
import com.thecubecast.reengine.worldobjects.ai.PawnStates;
import com.thecubecast.reengine.worldobjects.HackSlashPlayer;

public class Pawn extends PathfindingWorldObject {

    //True is left, False is right
    public boolean Facing = true;

    public float degrees;

    Vector3 Size = new Vector3(10,8,16);

    TextureAnimation<TextureAtlas.AtlasRegion> Walking;
    TextureAnimation<TextureAtlas.AtlasRegion> Idle;

    TextureAnimation<TextureAtlas.AtlasRegion> Sword;

    TextureRegion Shadow;

    TkLabel NameLabel;
    Group stage;
    ProgressBar HealthBar;

    public Pawn(String name, int x, int y, int z, Vector3 size, float knockbackResistance, float health, intractability interact, boolean invincible, FlatTiledGraph map, GameStateManager gsm) {
        super(name,x,y,z, size, knockbackResistance,health, interact, invincible, map, PawnStates.IDLE);
        setSize(Size);
        setHitboxOffset(new Vector3(6,0,0));

        Walking = new TextureAnimation<>(gsm.Render.getTextures("pawn"), 0.1f);
        Idle = new TextureAnimation<>(gsm.Render.getTextures("pawn_idle"), 0.1f);
        Sword = new TextureAnimation<>(gsm.Render.getTextures("pawn_pistol"), 0.05f);
        Shadow = gsm.Render.getTexture("Shadow");

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

        Sword.resume();
        TextureRegion frameS = Sword.getFrame(Gdx.graphics.getDeltaTime());
        batch.draw(frameS, Facing ? (int)getPosition().x - 4 + (frameS.getRegionWidth()) : (int)getPosition().x + 10, (int)getPosition().y + (int)getPosition().z / 2, 0f, 0f, (float) frameS.getRegionWidth(), (float) frameS.getRegionHeight(), Facing ? -1f : 1f, 1f, degrees);


        if(Math.abs(this.getVelocity().y) >= 0.5f || Math.abs(this.getVelocity().x) >= 0.5f) {
            batch.draw(Shadow, Facing ? (int)getPosition().x + 3: (int)getPosition().x + 3, (int)getPosition().y - 2 + (int)getZFloor() / 2);
            //running animation
            TextureRegion frame = Walking.getFrame(Gdx.graphics.getDeltaTime());
            batch.draw(frame, Facing ? (int)getPosition().x + 2 + (frame.getRegionWidth()) : (int)getPosition().x, (int)getPosition().y + (int)getPosition().z / 2, Facing ? -(frame.getRegionHeight()) : (frame.getRegionHeight()), (frame.getRegionHeight()));
        } else if(this.getVelocity().y < 0.5f || this.getVelocity().x < 0.5f) {
            batch.draw(Shadow, Facing ? (int)getPosition().x +3 : (int)getPosition().x + 3, (int)getPosition().y - 2 + (int)getZFloor() / 2);
            //Idle animation
            TextureRegion frame = Idle.getFrame(Gdx.graphics.getDeltaTime());
            batch.draw(frame, Facing ? (int)getPosition().x + 2 + (frame.getRegionWidth()) : (int)getPosition().x, (int)getPosition().y + (int)getPosition().z / 2, Facing ? -(frame.getRegionHeight()) : (frame.getRegionHeight()), (frame.getRegionHeight()));
        }

    }

    @Override
    public void drawHighlight(SpriteBatch batch, float Time) {
        //TextureRegion currentFrame = idle.getKeyFrame(Time, true);

        //setOutlineShaderColor(Color.YELLOW, 0.8f);

        //batch.setShader(OutlineShader);
        //batch.draw(currentFrame, getPosition().x-6, getPosition().y-4);
        //batch.setShader(null);

    }

    @Override
    public void drawGui(SpriteBatch batch, float Time) {
        stage.draw(batch, 1);
        //batch.draw(Exclamation, (int) getPosition().x + 6, (int) getPosition().y + 63 + (float) (Math.sin(Time) * 2));
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
