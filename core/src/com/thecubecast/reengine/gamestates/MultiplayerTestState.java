// GameState that tests new mechanics.

package com.thecubecast.reengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.thecubecast.reengine.data.Common;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.tkmap.TkMap;
import com.thecubecast.reengine.graphics.scene2d.UIFSM;
import com.thecubecast.reengine.graphics.scene2d.UI_state;
import kryonetwork.KryoClient;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MultiplayerTestState extends GameState {

    private KryoClient network;

    private Vector3 position;
    private long last_time;
    private int deltaTime;

    private List<Rectangle> Collisions = new ArrayList<>();

    //Camera
    OrthographicCamera GuiCam;
    public OrthographicCamera camera;

    TkMap MapTemp;

    public MultiplayerTestState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {

        gsm.DiscordManager.setPresenceDetails("Multiplayer Demo - Level 1");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;

        //SETUP NETWORK CONNECTION
        try {
            network = new KryoClient(gsm.Username, "localhost", 54555, 54777);
            while (!network.established) {
                Common.sleep(5);
            }
        } catch (IOException e) {
            e.printStackTrace();
            gsm.ErrorMessages = "[RED]Server Not Found";
            gsm.setState(GameStateManager.State.MENU);
            return;
        }

        Collisions.add(new Rectangle(2, 2, 2, 2));
        Collisions.add(new Rectangle(4, 7, 1, 4));

        camera = new OrthographicCamera();
        GuiCam = new OrthographicCamera();
        camera.setToOrtho(false, GameStateManager.WorldWidth, GameStateManager.WorldHeight);
        GuiCam.setToOrtho(false, GameStateManager.UIWidth, GameStateManager.UIHeight);

        if (network != null) {
            camera.position.set((network.GetClient().x * 64), (network.GetClient().y * 64), camera.position.z);
            position = camera.position;

            //Setup Network
            network.Update();
        }

        gsm.UI.stage.getViewport().setCamera(GuiCam);
        gsm.UI.inGame = true;
        gsm.UI.setState(UI_state.InGameHome);
        gsm.UI.setVisable(false);

    }

    public void update() {
        handleInput();

        long time = System.nanoTime();
        deltaTime = (int) ((time - last_time) / 1000000);
        last_time = time;

        camera.update();

        network.Update();
    }

    public void draw(SpriteBatch g, int width, int height, float Time) {
        Gdx.gl.glClearColor(135 / 255f, 206 / 255f, 235 / 255f, 1);
        RenderCam();

        g.begin();
        g.setProjectionMatrix(camera.combined);

        if (network.GetUsers().size() != 0) {
            for (int l = 0; l < network.GetUsers().size(); l++) {
                GameStateManager.Render.GUIDrawText(g, Common.roundDown((network.GetUsers().get(l).x * 64)), Common.roundDown((network.GetUsers().get(l).y * 64)), network.GetUsers().get(l).username, Color.WHITE);
            }

        }

        g.end();

        int size = 16; // gsm.Render.GUI[00].getWidth()
        Rectangle player = new Rectangle(network.GetClient().x, network.GetClient().y, size, size);
        player.setCenter(network.GetClient().x + size / 2, network.GetClient().y + size / 2);

    }

    public void drawUI(SpriteBatch g, int height, int width, float Time) {
        //Draws things on the screen, and not the world positions
        g.setProjectionMatrix(GuiCam.combined);
        g.begin();
        //GUI must draw last
        GameStateManager.Render.GUIDrawText(g, 50, 50, "" + network.GetClient(), Color.WHITE);
        g.end();

        gsm.UI.Draw();

    }

    public void RenderCam() {
        camera.update();
    }

    public void FollowCam(OrthographicCamera cam, int playerx, int playery, float lerp) {
        int mapBoundX = 10000;
        int mapBoundY = 10000;

        position.x += (playerx * 64 - position.x) * lerp * deltaTime;
        position.y += (playery * 64 - position.y) * lerp * deltaTime;

        //    float PosibleX = position.x + (playerx - position.x) * lerp * deltaTime;
        //    if (PosibleX - (Gdx.graphics.getWidth()/2) >= 0 && PosibleX - (Gdx.graphics.getWidth()/2) <= mapBoundX) {
        //        position.x += (playerx - position.x) * lerp * deltaTime;
        //    }

        //    float PosibleY = position.y + (playery - position.y) * lerp * deltaTime;
        //    if (PosibleY - (Gdx.graphics.getHeight()/2) >= 0 && PosibleY - (Gdx.graphics.getHeight()/2) <= mapBoundY) {
        //        position.y += (playery - position.y) * lerp * deltaTime;
        //    } else if (PosibleY - (Gdx.graphics.getHeight()/2) >= mapBoundY) {
        //        position.y += (playery+160 - position.y) * lerp * deltaTime;
        //    }

        //position.x += ((player.getLocation()[0]*64)+40 - position.x) * lerp * deltaTime;
        //position.y += ((player.getLocation()[1]*64)+40 - position.y) * lerp * deltaTime;

        cam.position.set(position.x, position.y, cam.position.z);
        cam.update();
    }

    private void handleInput() {

        Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(pos);

        Vector2 Location = new Vector2(network.GetClient().x, network.GetClient().y);

        Vector2 center = new Vector2(Gdx.graphics.getWidth() / 2, Gdx.graphics.getHeight() / 2);
        Vector2 MousePos = new Vector2(Gdx.input.getX(), Gdx.input.getY());

        float angle = Common.GetAngle(center, MousePos);

        //Common.print("Angle: " + Location.angle(MousePos));
        //Common.print("Start: " + Location + " Mouse: " + MousePos);

        Vector2 move = new Vector2(0, 0);

        if (Gdx.input.isKeyPressed(Keys.W)) { //KeyHit
            move.y += 1;
            //move(new Vector2(0, 1), Location);
        }
        if (Gdx.input.isKeyPressed(Keys.S)) { //KeyHit
            move.y -= 1;
            //move(new Vector2(0, -1), Location);
        }
        if (Gdx.input.isKeyPressed(Keys.A)) { //KeyHit
            move.x -= 1;
            //move(new Vector2(-1, 0), Location);
        }
        if (Gdx.input.isKeyPressed(Keys.D)) { //KeyHit
            move.x += 1;
            //move(new Vector2(1, 0), Location);
        }

        move(new Vector2(move.x, move.y), Location);

        network.updateClientPos(Location, angle);
        //camera.position.set((network.GetClient().x*40)+40, (network.GetClient().y*40)+40, camera.position.z);
        FollowCam(camera, Common.roundDown(Location.x), Common.roundDown(Location.y), 0.01f);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (gsm.UI.Visible) {
                gsm.UI.setVisable(!gsm.UI.Visible);
                Gdx.input.setInputProcessor(gsm.UI.stage);
            } else if (!gsm.UI.Visible) {
                gsm.UI.setState(UI_state.InGameHome);
                Gdx.input.setInputProcessor(gsm.UI.stage);
            }
            //gsm.ctm.newController("template");
        }

    }

    public void move(Vector2 pos, Vector2 Location) {
        if (pos.x < 0) { //Moving left
            if (checkCollision(Location.x - (pos.x * -1), Location.y)) {
                //Cant move
            } else {
                Location.x -= (pos.x * -1);
            }
        } else if (pos.x > 0) { // Moving right
            if (checkCollision(Location.x + pos.x, Location.y)) {
                //Cant move
            } else {
                Location.x += (pos.x);
            }
        }

        if (pos.y < 0) { // Moving down
            if (checkCollision(Location.x, Location.y - (pos.y * -1))) {
                //Cant move
            } else {
                Location.y -= (pos.y * -1);
            }
        } else if (pos.y > 0) {
            if (checkCollision(Location.x, Location.y + pos.y)) {
                //Cant move
            } else {
                Location.y += pos.y;
            }
        }
    }

    public boolean checkCollision(float posx, float posy) {
        for (int i = 0; i < Collisions.size(); i++) {
            if (posx >= Collisions.get(i).getX() && posx < (Collisions.get(i).getX() + Collisions.get(i).getWidth()) && posy >= Collisions.get(i).getY() && posy < (Collisions.get(i).getY() + Collisions.get(i).getHeight())) {
                return true; // Dont move
            }
        }
        return false;
    }

    public void reSize(SpriteBatch g, int H, int W) {
        camera = new OrthographicCamera();
        GuiCam = new OrthographicCamera();
        camera.setToOrtho(false, GameStateManager.WorldWidth, GameStateManager.WorldHeight);
        GuiCam.setToOrtho(false, GameStateManager.UIWidth, GameStateManager.UIHeight);
        //shaker = new ScreenShakeCameraController(camera);


    }

    @Override
    public void dispose() {
        super.dispose();
        if (network != null)
            network.Disconnect();
    }

    @Override
    public void Shutdown() {

    }
}