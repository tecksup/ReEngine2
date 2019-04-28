// GameState that tests new mechanics.

package com.thecubecast.reengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Interpolation;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.thecubecast.reengine.data.Cube;
import com.thecubecast.reengine.data.FallingLayer;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.ParticleHandler;
import com.thecubecast.reengine.data.tkmap.TkMap;
import com.thecubecast.reengine.graphics.scene2d.UI_state;
import com.thecubecast.reengine.graphics.ScreenShakeCameraController;
import com.thecubecast.reengine.worldobjects.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.thecubecast.reengine.data.GameStateManager.AudioM;
import static com.thecubecast.reengine.data.GameStateManager.EasyMode;
import static com.thecubecast.reengine.data.GameStateManager.Render;

public class PlayState extends DialogStateExtention {

    //GUI
    boolean MenuOpen = true;

    //Camera
    OrthographicCamera GuiCam;
    public static OrthographicCamera camera;
    ScreenShakeCameraController shaker;

    //Particles
    public static ParticleHandler Particles;

    //GameObjects
    public FallingPlayer player;

    TkMap WorldMap;

    public int Depth = 972;
    int StartingX = 512;
    public int StartingY = 13*16;
    FallingLayer[] Map;

    Music BGMusicID;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        gsm.setWorldScale(2);
    }

    public void init() {

        Map = new FallingLayer[Depth];
        Random temprand = new Random();
        for (int i = 0; i < Map.length; i++) {
            //I can randomly generate the width here when it comes time for that
            if (i % (temprand.nextInt(2)+3) == 0)
                if (i > 1 && Map[i-1].Prefab == 1000) {
                    Map[i] = new FallingLayer(14, temprand.nextInt(5));
                } else {
                    Map[i] = new FallingLayer(14, 1000);
                }
            else
                Map[i] = new FallingLayer(14, 1000);
        }

        Collisions.add(new Cube(StartingX, StartingY + 0, 2, 16, Map.length*16, 0));
        Collisions.add(new Cube(0, StartingY + Depth*16, 2, StartingX+16, 16, 0));
        Collisions.add(new Cube(StartingX+Map[0].getSize()*16-16, StartingY + Depth*16, 2, StartingX, 16, 0));
        Collisions.add(new Cube(StartingX+Map[0].getSize()*16-16, StartingY + 0, 2, 16, Map.length*16, 0));

        for (int i = 0; i < Map.length; i++) {
            for (int j = 0; j < Map[i].getSize(); j++) {
                if (Map[i].getTile(j) == 1 || Map[i].getTile(j) == 0) {
                    Collisions.add(new Cube(StartingX + (j*16), StartingY + i*16, 2, 16, 16, 16));
                }
            }
        }

        for (int i = 1; i < Map.length+1; i++) {
            for (int j = 0; j < Map[i-1].getSize(); j++) {
                if (Map[i-1].getTile(j) == 0) {
                    if (temprand.nextInt(5) == 1) {
                        Entities.add(new Spike(StartingX + (j*16), StartingY + i*16));
                    }
                }
            }
        }

        for (int i = 0; i < Map.length; i++) {
            for (int j = 0; j < Map[i].getSize(); j++) {
                if (Map[i].getTile(j) == 0) {
                    if (temprand.nextInt(15) == 1) {
                        Entities.add(new HealthPickup(StartingX + (j*16), StartingY + (i-1)*16));
                    }
                }
            }
        }

        WorldMap = new TkMap("Saves/Untitled.cube");

        player = new FallingPlayer();
        player.setPositionY(StartingY + Depth*16+17);

        Entities.add(player);

        ArrayList<WorldObject> tempobjsshit = WorldMap.getObjects(gsm);
        for (int i = 0; i < tempobjsshit.size(); i++) {
            Entities.add(tempobjsshit.get(i));
            if (tempobjsshit.get(i).isCollidable()) {
                Vector3 tempVec = tempobjsshit.get(i).getPosition();
                Vector3 tempVecOffset = tempobjsshit.get(i).getHitboxOffset();
                Vector3 tempVecSize = tempobjsshit.get(i).getSize();
                Cube tempCube = new Cube((int) tempVec.x + (int) tempVecOffset.x, (int) tempVec.y + (int) tempVecOffset.y, (int) tempVec.z + (int) tempVecOffset.z, (int) tempVecSize.x, (int) tempVecSize.y, (int) tempVecSize.z);
                Entities.get(i).CollisionHashID = Collisions.size();
                Collisions.add(tempCube);
                //System.out.println(WorldMap.getObjects().get(i).getPosition());
            }
        }

        for (int x = 0; x < WorldMap.getWidth(); x++) {
            for (int y = 0; y < WorldMap.getHeight(); y++) {
                if (WorldMap.getCollision()[x][y]) {
                    Collisions.add(new Cube(x * 16, y * 16, 0, 16, 16, 16));
                }
            }
        }

        //Setup Dialog Instance
        MenuInit(GameStateManager.UIWidth, GameStateManager.UIHeight);

        gsm.DiscordManager.setPresenceDetails("UpFall");
        gsm.DiscordManager.setPresenceState("In Game");
        gsm.DiscordManager.getPresence().largeImageText = "Level 1";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;

        //Camera setup
        camera = new OrthographicCamera();
        GuiCam = new OrthographicCamera();
        camera.setToOrtho(false, GameStateManager.WorldWidth, GameStateManager.WorldHeight);
        GuiCam.setToOrtho(false, GameStateManager.UIWidth, GameStateManager.UIHeight);
        shaker = new ScreenShakeCameraController(camera);

        gsm.UI.stage.getViewport().setCamera(GuiCam);
        gsm.UI.inGame = true;
        gsm.UI.setState(UI_state.INGAMEUI);

        //Particles
        Particles = new ParticleHandler();

        BGMusicID = AudioM.playMusic("ingame2.wav", true, true);

    }

    public void update() {

        if (player.getPosition().y <= Depth*16 || MenuOpen) {
            player.setState(WorldObject.type.Dynamic);
            player.Falling = true;
        } else {
            player.setState(WorldObject.type.Static);
            player.Falling = false;
        }

        Particles.Update();

        for (int i = 0; i < Entities.size(); i++) {
            if (Entities.get(i) instanceof Spike) {
                Entities.get(i).update(Gdx.graphics.getDeltaTime(), this);
                if (player.getHitbox().intersects(Entities.get(i).getHitbox())) {
                    player.Health--;
                    AudioM.play("Damage");
                    shaker.addDamage(0.4f);
                    Entities.remove(i);
                    break;
                }
            } else if (Entities.get(i) instanceof HealthPickup) {
                Entities.get(i).update(Gdx.graphics.getDeltaTime(), this);
                if (player.getHitbox().intersects(Entities.get(i).getHitbox())) {
                    player.Health++;
                    player.HasJump = true;
                    player.JumpCooling = -0.2f;
                    AudioM.play("Heal");
                    Entities.remove(i);
                    break;
                }
            } else if (Entities.get(i) instanceof NPC) {
                if (!((NPC) Entities.get(i)).isAlive()) {
                    Entities.get(i).update(Gdx.graphics.getDeltaTime(), this);
                    Entities.remove(i);
                }
            } else if (Entities.get(i) instanceof Interactable) {
                Entities.get(i).update(Gdx.graphics.getDeltaTime(), this);
                Interactable Entitemp = (Interactable) Entities.get(i);
                Entitemp.Trigger(player, shaker, this, null, Particles, Entities);
                Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(pos);
                if (Entitemp.getImageHitbox().contains(new Vector3(pos.x, pos.y, player.getPosition().z))) {
                    ((Interactable) Entities.get(i)).Highlight = true;
                    ((Interactable) Entities.get(i)).HighlightColor = Color.YELLOW;
                    if (Gdx.input.isTouched() && MenuOpen) {
                        //Trigger the action, mine it, open it, trigger the event code
                        ((Interactable) Entities.get(i)).HighlightColor = Color.RED;
                        if (((Interactable) Entities.get(i)).getActivationType().equals(Trigger.TriggerType.OnClick) && !((Interactable) Entities.get(i)).JustRan) {
                            ((Interactable) Entities.get(i)).RunCommands(player, shaker, this, null, Particles, Entities);
                            ((Interactable) Entities.get(i)).JustRan = true;
                        }
                        if (Entities.get(i) instanceof Interactable) {
                            Interactable temp = (Interactable) Entities.get(i);
                            temp.Activated();
                        }
                    }
                } else {
                    ((Interactable) Entities.get(i)).Highlight = false;
                }
            } else {
                Entities.get(i).update(Gdx.graphics.getDeltaTime(), this);
            }
        }

        cameraUpdate(player, camera, Entities,8,8, WorldMap.getWidth()*WorldMap.getTileSize()-8, WorldMap.getHeight()*WorldMap.getTileSize()-8);

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            MenuOpen = !MenuOpen;
        }

        if (player.Health > 0 || EasyMode) {
            handleInput();
        }
    }

    public void draw(SpriteBatch g, int height, int width, float Time) {

        shaker.update(gsm.DeltaTime);
        g.setProjectionMatrix(shaker.getCombinedMatrix());

        Rectangle drawView = new Rectangle(camera.position.x - camera.viewportWidth / 2 - camera.viewportWidth / 4, camera.position.y - camera.viewportHeight / 2 - camera.viewportHeight / 4, camera.viewportWidth + camera.viewportWidth / 4, camera.viewportHeight + camera.viewportHeight / 4);

        g.setShader(null);
        g.begin();


        WorldMap.Draw(camera, g);

        g.draw(Render.getTexture("LeftTown"), 0,StartingY + Depth*16+16);
        g.draw(Render.getTexture("Tower"), StartingX,StartingY + Depth*16+16);
        g.draw(Render.getTexture("RightTown"), StartingX+15*16 - 2,StartingY + Depth*16+16);

        for (int i = 0; i < Map.length; i++) {
            Map[i].Draw(g, camera, gsm, StartingX,StartingY + i*16);
        }

        //Block of code renders all the entities
        WorldObjectComp entitySort = new WorldObjectComp();
        WorldObjectCompDepth entitySortz = new WorldObjectCompDepth();
        Entities.sort(entitySort);
        Entities.sort(entitySortz);
        for (int i = 0; i < Entities.size(); i++) {
            if (drawView.overlaps(new Rectangle(Entities.get(i).getPosition().x, Entities.get(i).getPosition().y, Entities.get(i).getSize().x, Entities.get(i).getSize().y))) {
                Entities.get(i).draw(g, Time);
            }
        }

        //Renders my favorite little debug stuff
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //KeyHit
            gsm.Cursor = GameStateManager.CursorType.Question;
        } else {
            gsm.Cursor = GameStateManager.CursorType.Normal;
        }

        //Particles
        Particles.Draw(g);

        //Renders the GUI for entities
        for (int i = 0; i < Entities.size(); i++) {
            if (Entities.get(i) instanceof NPC) {
                if (drawView.overlaps(new Rectangle(Entities.get(i).getPosition().x, Entities.get(i).getPosition().y, Entities.get(i).getSize().x, Entities.get(i).getSize().y))) {
                    ((NPC) Entities.get(i)).drawGui(g, Time);
                }
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //KeyHit
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            GameStateManager.Render.GUIDrawText(g, (int) pos.x / 16 * 16 + 1, (int) pos.y / 16 * 16 + 1, ((int) pos.x) + " : " +( (int) pos.y) + "", Color.WHITE);
        }

        g.end();

        //DEBUG CODE IS NOT CULLED | I LAG A LOT CAUSE MY MAP IS 1000X200 tiles
        GameStateManager.Render.debugRenderer.setProjectionMatrix(camera.combined);
        GameStateManager.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (GameStateManager.Debug) {

            for (int i = 0; i < Collisions.size(); i++) {

                //The bottom
                GameStateManager.Render.debugRenderer.setColor(Color.YELLOW);
                GameStateManager.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y + Collisions.get(i).getPrism().min.z / 2, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());

                GameStateManager.Render.debugRenderer.setColor(Color.ORANGE);
            }

            for (int i = 0; i < Entities.size(); i++) {
                //gsm.Render.debugRenderer.box(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y, Entities.get(i).getHitbox().min.z, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight(), Entities.get(i).getHitbox().getDepth());

                //The bottom
                GameStateManager.Render.debugRenderer.setColor(Color.GREEN);
                GameStateManager.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().min.z / 2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

            }

            //The bottom of the PLAYER
            GameStateManager.Render.debugRenderer.setColor(Color.YELLOW);
            GameStateManager.Render.debugRenderer.rect(player.getHitbox().min.x, player.getHitbox().min.y + player.getHitbox().min.z / 2, player.getHitbox().getWidth(), player.getHitbox().getHeight());

            GameStateManager.Render.debugRenderer.setColor(Color.PURPLE);
            GameStateManager.Render.debugRenderer.box(player.getIntereactBox().min.x, player.getIntereactBox().min.y, player.getIntereactBox().min.z, player.getIntereactBox().getWidth(), player.getIntereactBox().getHeight(), player.getIntereactBox().getDepth());

        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //KeyHit
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            GameStateManager.Render.debugRenderer.setColor(Color.WHITE);
            GameStateManager.Render.debugRenderer.rect(((int) pos.x / 16) * 16 + 1, ((int) pos.y / 16) * 16 + 1, 15, 15);
        }

        GameStateManager.Render.debugRenderer.end();

    }

    public void drawUI(SpriteBatch g, int height, int width, float Time) {

        if(MenuOpen) {
            if (!gsm.UI.getState().equals(UI_state.INGAMEUI)) {
                gsm.UI.setState(UI_state.INGAMEUI);
                gsm.UI.stage.setViewport(new FitViewport(GameStateManager.UIWidth, GameStateManager.UIHeight));
                Gdx.input.setInputProcessor(gsm.UI.stage);
            }
        } else {
            if (!gsm.UI.getState().equals(UI_state.InGameHome)) {
                gsm.UI.setState(UI_state.InGameHome);
                gsm.UI.stage.setViewport(new FitViewport(GameStateManager.UIWidth, GameStateManager.UIHeight));
                Gdx.input.setInputProcessor(gsm.UI.stage);
            }
        }

        //Draws things on the screen, and not the world positions
        g.setProjectionMatrix(GuiCam.combined);
        g.begin();

        MenuDraw(g, Gdx.graphics.getDeltaTime());
        g.end();
        gsm.UI.Draw();
    }

    private void handleInput() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.F)) {
            if (DialogOpen) {
                DialogNext();
            } else {
                for (int i = 0; i < Entities.size(); i++) {
                    if (Entities.get(i).getHitbox().intersects(player.getIntereactBox())) {
                        if (Entities.get(i) instanceof NPC) {
                            NPC Entitemp = (NPC) Entities.get(i);
                            Entitemp.interact();
                        }

                        if (Entities.get(i) instanceof Trigger) {
                            Trigger Ent = (Trigger) Entities.get(i);
                            Ent.Interact(player,shaker,this,null,Particles,Entities);
                        }
                     }
                }
            }
        }

        if (player.HasJump && player.JumpCooling > player.JumpCooldown) {
            if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.SPACE)) {
                player.setVelocityY(0);
                player.setVelocityY(15);
                player.JustJumped = 0;
                player.JumpCooling = -0.2f;
                player.HasJump = false;
                player.LastOnGroundY = player.getPosition().y;
                AudioM.play("Jump");
                Particles.AddParticleEffect("Jump", player.getPosition().x + player.getSize().x/2, player.getPosition().y);
            }
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            //player.setVelocityY(player.getVelocity().y - 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setVelocityX(player.getVelocity().x + 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setVelocityX(player.getVelocity().x - 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            AddDialog("pawn", "It's working!");
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            player.setPosition(((int) pos.x / 16) * 16 + 1, ((int) pos.y / 16) * 16 + 1, 0);
        }

    }

    public void reSize(SpriteBatch g, int H, int W) {
        Vector3 temppos = camera.position;

        camera = new OrthographicCamera();
        GuiCam = new OrthographicCamera();
        camera.setToOrtho(false, GameStateManager.WorldWidth, GameStateManager.WorldHeight);
        camera.position.set(temppos);
        GuiCam.setToOrtho(false, GameStateManager.UIWidth, GameStateManager.UIHeight);
        shaker = new ScreenShakeCameraController(camera);

    }

    public void cameraUpdate(WorldObject mainFocus, OrthographicCamera cam, List<WorldObject> Entities, int MinX, int MinY, int MaxX, int MaxY) {

        Vector2 FocalPoint = new Vector2(mainFocus.getPosition().x, mainFocus.getPosition().y);
        float totalFocusPoints = 1;

        if (mainFocus.getPosition().y < Depth*16-128) {
            FocalPoint.y -= cam.viewportHeight/4;
        }

        for (int i = 0; i < Entities.size(); i++) {
            if (Entities.get(i).FocusStrength != 0) {
                if (mainFocus.getPosition().dst(Entities.get(i).getPosition()) <= 200) {
                    float tempX = Entities.get(i).getPosition().x;
                    float tempY = Entities.get(i).getPosition().y;

                    double dist = mainFocus.getPosition().dst(Entities.get(i).getPosition());

                    double influence = -((dist - 200) / 200) * 1;

                    FocalPoint.x += (tempX * (Entities.get(i).FocusStrength * influence));
                    FocalPoint.y += (tempY * (Entities.get(i).FocusStrength * influence));
                    totalFocusPoints += Entities.get(i).FocusStrength * influence;
                }
            }
        }

        FocalPoint.x = FocalPoint.x / totalFocusPoints;
        FocalPoint.y = FocalPoint.y / totalFocusPoints;

        if (FocalPoint.x - cam.viewportWidth / 2 <= MinX) {
            FocalPoint.x = MinX + cam.viewportWidth / 2;
        } else if (FocalPoint.x + cam.viewportWidth / 2 >= MaxX) {
            FocalPoint.x = MaxX - cam.viewportWidth / 2;
        }

        if (FocalPoint.y - cam.viewportHeight / 2 <= MinY) {
            FocalPoint.y = MinY + cam.viewportHeight / 2;
        } else if (FocalPoint.y + cam.viewportHeight / 2 >= MaxY) {
            FocalPoint.y = MaxY - cam.viewportHeight / 2;
        }

        //(cam.position.x - FocalPoint.x) * 0.005f * Gdx.graphics.getDeltaTime();
        cam.position.set((int) (FocalPoint.x), (int) (FocalPoint.y), 0);

        cam.update();
    }

    @Override
    public void dispose() {
        Collisions.clear();
        Entities.clear();
        BGMusicID.stop();
    }

    @Override
    public void Shutdown() {
        BGMusicID.stop();
    }

}