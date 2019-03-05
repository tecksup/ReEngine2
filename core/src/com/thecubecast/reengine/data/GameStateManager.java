// The GameStateManager does exactly what its
// name says. It contains a list of gamestates.
// It decides which GameState to update() and
// draw() and handles switching between different
// gamestates.

package com.thecubecast.reengine.data;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.math.Matrix4;
import com.thecubecast.reengine.gamestates.*;
import com.thecubecast.reengine.graphics.Draw;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameStateManager {
    public static boolean Debug = false;

    public enum State {
        INTRO, MENU, PLAY, LOADING, EDITOR
    }

    public State currentState;
    private State previousState;

    public GameState gameState;

    public float CurrentTime;
    public float DeltaTime;

    //Public render function object
    public static Draw Render;
    public int ticks = 0;

    private OrthographicCamera MainCam;

    private FrameBuffer WorldFBO;
    private FrameBuffer UIFBO;

    //Public Audio handler
    public static SoundManager AudioM;

    public static ControlerManager ctm;

    public Discord DiscordManager;

    //The cursor image
    public enum CursorType {
        Normal, Old, Question
    }

    public CursorType OldCursor = CursorType.Normal;
    public CursorType Cursor = CursorType.Normal;

    //screen
    private int Width;
    private int Height;
    public int Scale = 4;
    public int UIScale = 4;

    public static int WorldWidth;
    public static int WorldHeight;

    public static int UIWidth;
    public static int UIHeight;

    public GameStateManager(int W, int H) {

        Width = W;
        Height = H;
        WorldWidth = Width / Scale;
        WorldHeight = Height / Scale;
        UIWidth = Width / (UIScale);
        UIHeight = Height / (UIScale);

        WorldFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Width / Scale, Height / Scale, false);
        UIFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Width / (UIScale), Height / (UIScale), false);

        MainCam = new OrthographicCamera();
        MainCam.setToOrtho(false, Width, Height);

        ctm = new ControlerManager();

        DiscordManager = new Discord("405784101245943810");

        Render = new Draw();
        AudioM = new SoundManager();

        Render.Init();
        AudioM.init();

        LoadState("STARTUP"); //THIS IS THE STATE WERE WE START WHEN THE GAME IS RUN
    }

    public void LoadState(String LoadIt) {
        previousState = currentState;
        unloadState();
        currentState = State.LOADING;
        //Set up the loading state
        gameState = new LoadingState(this);
        ((LoadingState) gameState).setLoad(LoadIt);
        gameState.init();
    }

    public void setState(State i) {
        previousState = currentState;
        unloadState();
        currentState = i;
        switch (currentState) {
            case INTRO:
                Common.print("Loaded state Intro");
                gameState = new IntroState(this);
                gameState.init();
                break;
            case MENU:
                Common.print("Loaded state MENU");
                gameState = new MainMenuState(this);
                gameState.init();
                break;
            case PLAY:
                Common.print("Loaded state PLAY");
                gameState = new PlayState(this);
                gameState.init();
                break;
            case LOADING:
                break;
            case EDITOR:
                Common.print("Loaded state EDITOR");
                gameState = new EditorState(this);
                gameState.init();
                break;
        }

    }

    /**
     * unloads the current state
     * calls dispose on the current gamestate first
     **/
    public void unloadState() {
        //Common.print("Unloaded state " + i);
        if (gameState != null)
            gameState.dispose();
        gameState = null;
    }

    public void update() {
        if (Cursor != OldCursor) {
            OldCursor = Cursor;
            int CursorID = 0;
            switch (Cursor) {
                case Normal:
                    CursorID = 0;
                    break;
                case Old:
                    CursorID = 1;
                    break;
                case Question:
                    CursorID = 2;
                    break;
            }
            com.badlogic.gdx.graphics.Cursor customCursor = Gdx.graphics.newCursor(new Pixmap(Gdx.files.internal("cursor" + CursorID + ".png")), 0, 0);
            Gdx.graphics.setCursor(customCursor);
        }
        if (gameState != null) {
            gameState.update();
        }

        AudioM.update();

        DiscordManager.UpdatePresence();
        ctm.update();

        MainCam.update();
    }

    public void draw(SpriteBatch bbg, int W, int H, float Time) {
        Width = W;
        Height = H;
        CurrentTime = Time;
        DeltaTime = Math.min(Gdx.graphics.getDeltaTime(), 1f / 60f);
        if (gameState != null) {
            //Notice how the height and width are swapped, woops
            Texture World = drawWorld(bbg, WorldFBO.getHeight(), WorldFBO.getWidth(), Time);
            Texture UI = drawUI(bbg, UIFBO.getHeight(), UIFBO.getWidth(), Time);

            bbg.setProjectionMatrix(MainCam.combined);
            bbg.begin();
            bbg.draw(World, 0, H, W, -H);
            bbg.draw(UI, 0, H, W, -H);
            bbg.end();

        }

        if (Debug) {
            //Common.print("Render Calls: " + bbg.totalRenderCalls);
            //bbg.totalRenderCalls = 0;
        }

    }

    /**
     * This is for drawing the world, with the standard pixel art scaling in the engine
     */
    public Texture drawWorld(SpriteBatch bbg, int W, int H, float Time) {
        WorldFBO.bind();
        WorldFBO.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        gameState.draw(bbg, W, H, Time);
        WorldFBO.end();
        FrameBuffer.unbind();

        WorldFBO.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        return WorldFBO.getColorBufferTexture();
    }

    /**
     * This is for drawing to the slightly larger FBO for UI. With a pixel density twice as large as drawWorld()
     */
    public Texture drawUI(SpriteBatch bbg, int W, int H, float Time) {
        UIFBO.bind();
        UIFBO.begin();
        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL_COLOR_BUFFER_BIT);
        gameState.drawUI(bbg, W, H, Time);
        UIFBO.end();
        FrameBuffer.unbind();

        UIFBO.getColorBufferTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);

        return UIFBO.getColorBufferTexture();


    }

    public void reSize(SpriteBatch bbg, int H, int W) {
        System.out.println("Resize Just Ran");
        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, W, H);
        bbg.setProjectionMatrix(matrix);

        MainCam.setToOrtho(false, W, H);

        Width = W;
        Height = H;
        WorldWidth = Width / Scale;
        WorldHeight = Height / Scale;
        UIWidth = Width / (UIScale);
        UIHeight = Height / (UIScale);

        if (Width/Scale <= 10) {
            System.out.println("It's too small!");
            Width = 32;
        }

        if (Height/Scale <= 10) {
            System.out.println("It's too small!");
            Height = 32;
        }

        WorldFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Width / Scale, Height / Scale, false);
        UIFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Width / (UIScale), Height / (UIScale), false);

        if (gameState != null) {
            gameState.reSize(bbg, H, W);
        }

    }

    public void setUIScale(int Scale) {
        this.UIScale = Scale;

        UIWidth = Width / (UIScale);
        UIHeight = Height / (UIScale);

        if (Width/Scale <= 10) {
            System.out.println("It's too small!");
            Width = 32;
        }

        if (Height/Scale <= 10) {
            System.out.println("It's too small!");
            Height = 32;
        }

        UIFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Width / (UIScale), Height / (UIScale), false);

        if (gameState != null) {
            gameState.reSize(null, Height, Width);
        }
    }

    public void dispose() {
        DiscordManager.dispose();
        gameState.dispose();
    }

    public void Shutdown() {
        gameState.Shutdown();
        gameState.dispose();
    }
}
