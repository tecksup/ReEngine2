package com.thecubecast.reengine.worldobjects.entityprefabs;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.gamestates.PlayState;
import com.thecubecast.reengine.graphics.Draw;
import com.thecubecast.reengine.graphics.scene2d.TkLabel;
import com.thecubecast.reengine.worldobjects.NPC;

import static com.thecubecast.reengine.graphics.Draw.loadAnim;

public class Hank extends NPC {

    Texture sprite;
    TextureRegion Exclamation = GameStateManager.Render.getTexture("Yellow_Marker");

    private Animation<TextureRegion> idle;
    TkLabel NameLabel;
    Group stage;
    ProgressBar HealthBar;

    public Hank(int x, int y, int z) {
        super("[YELLOW]H[GREEN]a[BLUE]n[RED]k", x, y, z, new Vector3(32, 32, 4), .1f, 100);

        setInteract(intractability.Talk);

        FocusStrength = 0.15f;

        idle = new Animation<TextureRegion>(0.1f, loadAnim(sprite, "Sprites/8direct/south.png", 4, 1));
        Skin skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
        skin.getFont("Mecha").getData().markupEnabled = true;
        skin.getFont("Pixel").getData().markupEnabled = true;

        stage = new Group();

        NameLabel = new TkLabel(getName(), skin);
        HealthBar = new ProgressBar(0f, 10f, 0.1f, false, skin, "Health_npc");
        HealthBar.setValue(getHealth() / 10);
        HealthBar.setWidth(40);
        stage.addActor(NameLabel);
        stage.addActor(HealthBar);
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

        TextureRegion currentFrame = idle.getKeyFrame(Time, true);

        if (System.nanoTime() / 1000000 - getLastDamagedTime() < 1000) {

            batch.draw(new TextureRegion(currentFrame), getPosition().x - 6, getPosition().y - 4);

        } else {
            batch.draw(new TextureRegion(currentFrame), getPosition().x - 6, getPosition().y - 4);
        }

    }

    @Override
    public void drawHighlight(SpriteBatch batch, float Time) {
        TextureRegion currentFrame = idle.getKeyFrame(Time, true);

        //setOutlineShaderColor(Color.YELLOW, 0.8f);

        //batch.setShader(OutlineShader);
        //batch.draw(currentFrame, getPosition().x-6, getPosition().y-4);
        //batch.setShader(null);

    }

    @Override
    public void drawGui(SpriteBatch batch, float Time) {
        stage.draw(batch, 1);
        batch.draw(Exclamation, (int) getPosition().x + 6, (int) getPosition().y + 63 + (float) (Math.sin(Time) * 2));
    }

    @Override
    public void interact(GameState G) {
        if (G instanceof PlayState) {
            ((PlayState) G).AddDialog("Hank", "Wow, Looks like things still work",10, new Texture(Gdx.files.internal("Sprites/Gunter.png")));
        }
    }

    @Override
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
        NameLabel.setPosition((int) getPosition().x + 3, (int) getPosition().y + 50);
        HealthBar.setValue(getHealth() / 10);
        HealthBar.setPosition((int) getPosition().x + 15 - (HealthBar.getWidth() / 2), (int) getPosition().y + 44);
    }
}
