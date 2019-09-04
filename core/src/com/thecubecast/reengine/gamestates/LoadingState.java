// GameState that shows logo.

package com.thecubecast.reengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ProgressBar;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.thecubecast.reengine.data.GameStateManager;

public class LoadingState extends GameState {

    OrthographicCamera camera;

    int tics = 0;

    private String Load;

    private Skin skin;
    private Stage stage;
    private Table table;
    ProgressBar progress;

    public void setLoad(String LoadIt) {
        Load = LoadIt;
    }

    public LoadingState(GameStateManager gsm) {
        super(gsm);

        Gdx.graphics.setVSync(false);

        camera = new OrthographicCamera();

        //Common.print("Loading " + Load);
        MenuInit();
    }

    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
    }

    public void MenuInit() {

        setupSkin();
        stage = new Stage();
        Gdx.input.setInputProcessor(stage);

        table = new Table();
        table.setFillParent(true);
        table.bottom().left().padLeft(26f).padBottom(10f);
        stage.addActor(table);


        progress = new ProgressBar(0f, 1f, 0.01f, false, skin);
        table.add(progress);
        table.row();
    }


    public void update() {
        tics++;
        GameStateManager.Render.manager.update();
        progress.setValue(GameStateManager.Render.manager.getProgress());
        if (Load.equals("STARTUP")) {
            if (GameStateManager.Render.manager.getProgress() == 1) {
                GameStateManager.Render.retrieveTextureAtlas();

                Gdx.graphics.setVSync(true);
                gsm.setState(GameStateManager.State.MENU);
            }
        }
        handleInput();
    }

    public void draw(SpriteBatch g, int height, int width, float Time) {
        camera.setToOrtho(false, width, height);
        g.setProjectionMatrix(camera.combined);
        g.begin();

        stage.act(Gdx.graphics.getDeltaTime());

        //gsm.Render.DrawAnimatedTile(g, gsm.Render.LoadingAnimation, 50, 50, 2.0f, 2.0f, Time);

        GameStateManager.Render.DrawAnimatedTile(g, GameStateManager.Render.LoadingAnimation, 2, 2, Time);
        stage.getRoot().draw(g, 1);
        g.end();
    }

    public void drawUI(SpriteBatch g, int height, int width, float Time) {
        //Draws things on the screen, and not the world positions
        g.begin();
        //GUI must draw last

        g.end();
    }

    public void handleInput() {

        if (Gdx.input.isKeyJustPressed(Keys.ENTER)) { //KeyHit
            //gsm.setState(GameStateManager.INTRO);
        }

    }

    public void reSize(SpriteBatch g, int wi, int he) {
        MenuInit();
    }

}