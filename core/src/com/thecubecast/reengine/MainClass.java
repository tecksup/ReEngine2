package com.thecubecast.reengine;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Cursor;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thecubecast.reengine.data.Common;
import com.thecubecast.reengine.data.GameStateManager;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;
import static com.thecubecast.reengine.data.Common.GetMonitorSizeH;
import static com.thecubecast.reengine.data.Common.GetMonitorSizeW;

public class MainClass extends ApplicationAdapter{

    //The Drawing Variable
    private int W;
    private int H;

    private SpriteBatch batch;

    // game state manager
    public GameStateManager gsm;

    // A variable for tracking elapsed time for the animation
    private float stateTime;

    @Override
    public void create() { // INIT FUNCTION

        String[] temp = new String[]{"", ""};
        if (Gdx.app.getPreferences("properties").contains("Resolution")) {
            temp = Gdx.app.getPreferences("properties").getString("Resolution").split("X");
        } else {
            temp[0] = "1280";
            temp[1] = "720";
        }

        Gdx.graphics.setWindowedMode(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));

        Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();
        window.setPosition(GetMonitorSizeW() / 2 - Gdx.graphics.getWidth() / 2, GetMonitorSizeH() / 2 - Gdx.graphics.getHeight() / 2);

        if (Gdx.app.getPreferences("properties").contains("FullScreen")) {
            if (Gdx.app.getPreferences("properties").getBoolean("FullScreen")) {
                Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
            }
        }

        Cursor customCursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor0.png")), 0, 0);
        Gdx.graphics.setCursor(customCursor);

        stateTime = 0f;

        //Just setting up the variables
        W = Gdx.graphics.getWidth();
        H = Gdx.graphics.getHeight();

        gsm = new GameStateManager(W, H);

        //This is essentially the graphics object we draw too
        batch = new SpriteBatch();
    }

    @Override
    public void render() { // UPDATE Runs every frame. 60FPS

        //Gdx.gl.glClearColor( 1, 1, 1, 1 );
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);

        //Code goes here

        UpdateInput();
        Update(); //UPDATE


        stateTime += Gdx.graphics.getDeltaTime(); // Accumulate elapsed animation time

        Draw(batch); //DRAW
        batch.begin();
        gsm.Render.GUIDrawText(batch, 0, H, Gdx.graphics.getFramesPerSecond() + "", Color.YELLOW);
        batch.end();
    }

    public void UpdateInput() {

        if (Gdx.input.isKeyJustPressed(Keys.GRAVE)) { //KeyHit
            Common.ProperShutdown(gsm);
        }

        if (Gdx.input.isKeyPressed(Keys.CONTROL_LEFT) && Gdx.input.isKeyPressed(Keys.SHIFT_LEFT) && Gdx.input.isKeyJustPressed(Keys.D)) { //KeyHit
            GameStateManager.Debug = !GameStateManager.Debug;
        }
    }

    public void Update() {

        //Figure out how to do this before you start exporting things to external files
        gsm.update();
    }

    public void Draw(SpriteBatch bbg) {
        //Figure out how to do this before you start exporting things to external files
        gsm.draw(bbg, W, H, stateTime);
    }

    @Override
    public void resize(int width, int height) {
        W = width;
        H = height;
        if (gsm.Debug) {
            Common.print("Ran Resize!");
            Common.print("" + width + " and H: " + height);
        }
        gsm.reSize(batch, H, W);
    }


    @Override
    public void dispose() { //SHUTDOWN FUNCTION
        batch.dispose();
        gsm.dispose();
    }

}