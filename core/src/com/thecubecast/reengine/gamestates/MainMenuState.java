// GameState that shows Main Menu.

package com.thecubecast.reengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.ParticleHandler;
import com.thecubecast.reengine.graphics.scene2d.UIFSM;


public class MainMenuState extends GameState {

    OrthographicCamera cameraGui;

    int BGMusicID;

    //Particles
    public static ParticleHandler Particles;

    public MainMenuState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        //Particles
        Particles = new ParticleHandler();

        gsm.DiscordManager.setPresenceState("In Menus");

        cameraGui = new OrthographicCamera();
        gsm.UI.stage.getViewport().setCamera(cameraGui);

        //BGMusicID = AudioM.playMusic("menu.wav", true, true);
    }

    public void update() {
        Particles.Update();
        handleInput();

        cameraGui.update();
    }

    public void draw(SpriteBatch bbg, int height, int width, float Time) {

        //DEBUG CODE
        gsm.Render.debugRenderer.setProjectionMatrix(cameraGui.combined);
        gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Filled);

        gsm.Render.debugRenderer.end();

        cameraGui.setToOrtho(false, width, height);
        bbg.setProjectionMatrix(cameraGui.combined);
        bbg.begin();

        //Particles
        Particles.Draw(bbg);

        bbg.end();


    }

    public void drawUI(SpriteBatch g, int height, int width, float Time) {
        //Draws things on the screen, and not the world positions
        cameraGui.setToOrtho(false, width, height);
        g.setProjectionMatrix(cameraGui.combined);
        g.begin();
        //GUI must draw last
        g.end();
        gsm.UI.Draw();
    }


    public void handleInput() {
        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Keys.E)) {
            gsm.setState(GameStateManager.State.EDITOR);
        }
    }

    public void reSize(SpriteBatch g, int H, int W) {
        //stage.getViewport().update(Gdx.graphics.getWidth(),Gdx.graphics.getHeight(), true);
        cameraGui.setToOrtho(false);
        gsm.UI.reSize();
    }

    @Override
    public void Shutdown() {
        //AudioM.stopMusic(BGMusicID);
    }

    @Override
    public void dispose() {
        //AudioM.stopMusic(BGMusicID);
    }

}