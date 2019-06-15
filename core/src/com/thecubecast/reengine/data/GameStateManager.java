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
import com.badlogic.gdx.math.Vector3;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.thecubecast.reengine.gamestates.*;
import com.thecubecast.reengine.graphics.Draw;
import com.thecubecast.reengine.graphics.scene2d.UIFSM;
import com.thecubecast.reengine.graphics.scene2d.UI_state;

import java.util.HashMap;

import static com.badlogic.gdx.graphics.GL20.GL_COLOR_BUFFER_BIT;

public class GameStateManager {
    public static boolean Debug = false;

    public Vector3 PlayerSpawn = new Vector3(0,0,0);

    public String SaveSelected = "";

    public String Username = "username";
    public String IP = "localhost";
    public String ErrorMessages = "";

    public enum State {
        INTRO, MENU, PLAY, LOADING, EDITOR, MULTI
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

    public UIFSM UI;

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

    public static HashMap<Integer, Item> ItemPresets;

    //screen
    private int Width;
    private int Height;
    public int Scale = 4;
    public int UIScale = 2;

    public static int WorldWidth;
    public static int WorldHeight;

    public static int UIWidth;
    public static int UIHeight;

    public GameStateManager(int W, int H) {

        //Load Itemes.dat file and populate the hashmap
        ItemPresets = new HashMap<>();
        JsonParser temp = new JsonParser();
        JsonArray tempJson = temp.parse(Gdx.files.internal("Items.dat").readString()).getAsJsonArray();
        for (int i = 0; i < tempJson.size(); i++) {
            String Name = tempJson.get(i).getAsJsonObject().get("Name").getAsString();
            String TexLocation = tempJson.get(i).getAsJsonObject().get("TexLocation").getAsString();
            String Desc = tempJson.get(i).getAsJsonObject().get("Description").getAsString();
            boolean Struct = tempJson.get(i).getAsJsonObject().get("Structure").getAsBoolean();
            int Max = tempJson.get(i).getAsJsonObject().get("Max").getAsInt();
            int ID = tempJson.get(i).getAsJsonObject().get("ID").getAsInt();
            if (tempJson.get(i).getAsJsonObject().get("Equipment").getAsBoolean()) {
                Equipment tempItem = new Equipment(Name, ID, TexLocation, Desc);
                ItemPresets.put(ID, tempItem);
            } else {
                Item tempItem = new Item(Name, ID, TexLocation, Desc, Struct);
                tempItem.setMax(Max);
                ItemPresets.put(ID, tempItem);
            }
        }

        Width = W;
        Height = H;
        WorldWidth = Width / Scale;
        WorldHeight = Height / Scale;
        UIWidth = Width / (UIScale);
        UIHeight = Height / (UIScale);

        WorldFBO = new FrameBuffer(Pixmap.Format.RGBA8888, WorldWidth, WorldHeight, false);
        UIFBO = new FrameBuffer(Pixmap.Format.RGBA8888, UIWidth, UIHeight, false);

        MainCam = new OrthographicCamera();
        MainCam.setToOrtho(false, Width, Height);

        ctm = new ControlerManager();

        DiscordManager = new Discord("405784101245943810");

        Render = new Draw();
        AudioM = new SoundManager();

        Render.Init();
        AudioM.init();

        UI = new UIFSM(this);

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

        Gdx.input.setInputProcessor(UI.stage);
        UI.setState(UI_state.Home);

        previousState = currentState;
        unloadState();
        currentState = i;
        switch (currentState) {
            case INTRO:
                gameState = new IntroState(this);
                gameState.init();
                break;
            case MENU:
                gameState = new MainMenuState(this);
                gameState.init();
                break;
            case PLAY:
                gameState = new PlayState(this);
                gameState.init();
                break;
            case LOADING:
                break;
            case EDITOR:
                gameState = new EditorState(this);
                gameState.init();
                break;
            case MULTI:
                gameState = new MultiplayerTestState(this);
                gameState.init();
                break;
        }

        UI.inGame = false;

        if (Debug) {
            System.out.println("Loaded State " + gameState.getClass().getName());
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

        if (gameState instanceof PlayState) {
            UI.player = ((PlayState) gameState).player;
        }

        if (gameState != null) {
            gameState.update();
        }

        UI.update();

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
        Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, W, H);
        bbg.setProjectionMatrix(matrix);

        MainCam.setToOrtho(false, W, H);

        Width = W;
        Height = H;

        if (Width/Scale <= 10) {
            Width = 32;
        }

        if (Height/Scale <= 10) {
            Height = 32;
        }

        WorldWidth = Width / Scale;
        WorldHeight = Height / Scale;
        UIWidth = Width / (UIScale);
        UIHeight = Height / (UIScale);

        WorldFBO = new FrameBuffer(Pixmap.Format.RGBA8888, WorldWidth, WorldHeight, false);
        UIFBO = new FrameBuffer(Pixmap.Format.RGBA8888, UIWidth, UIHeight, false);

        if (gameState != null) {
            gameState.reSize(bbg, H, W);
        }

        UI.reSize();

    }

    public void setUIScale(int Scale) {
        this.UIScale = Scale;

        UIWidth = Width / (UIScale);
        UIHeight = Height / (UIScale);

        if (Width/Scale <= 10) {
            Width = 32;
        }

        if (Height/Scale <= 10) {
            Height = 32;
        }

        UIFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Width / (UIScale), Height / (UIScale), false);

        if (gameState != null) {
            gameState.reSize(null, Height, Width);
        }
    }

    public void setWorldScale(int Scale) {
        int DeltaScale = this.Scale - Scale;
        this.Scale = Scale;

        WorldWidth = Width / (Scale);
        WorldHeight = Height / (Scale);

        if (Width/Scale <= 10) {
            Width = 32;
        }

        if (Height/Scale <= 10) {
            Height = 32;
        }

        WorldFBO = new FrameBuffer(Pixmap.Format.RGBA8888, Width / (Scale), Height / (Scale), false);

        if (gameState != null) {
            gameState.reSize(null, Height, Width);
        }


        /*if (WorldFBO.getHeight() % 2 != 0) {
            System.out.println("BET ITS BROKEN");
            if (!Debug) {
                if (DeltaScale > 0) {
                    if (Scale - 1 > 0)
                        setWorldScale(Scale - 1);
                } else {
                    setWorldScale(Scale + 1);
                }
            }
        }*/

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
