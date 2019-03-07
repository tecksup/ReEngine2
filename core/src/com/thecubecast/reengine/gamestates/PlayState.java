// GameState that tests new mechanics.

package com.thecubecast.reengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.data.Cube;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.ParticleHandler;
import com.thecubecast.reengine.data.tkmap.TkMap;
import com.thecubecast.reengine.graphics.scene2d.UIFSM;
import com.thecubecast.reengine.graphics.scene2d.UI_state;
import com.thecubecast.reengine.graphics.ScreenShakeCameraController;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledGraph;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledNode;
import com.thecubecast.reengine.worldobjects.ai.pawn.Pawn_Enemy;
import com.thecubecast.reengine.worldobjects.*;
import com.thecubecast.reengine.worldobjects.entityprefabs.Pawn;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.reengine.data.GameStateManager.AudioM;

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
    public HackSlashPlayer player;

    TkMap WorldMap;

    //ai
    FlatTiledGraph MapGraph;

    Music MusicID;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        gsm.setUIScale(gsm.Scale);
    }

    public void init() {

        WorldMap = new TkMap("Saves/World.cube");

        ArrayList<WorldObject> tempobjsshit = WorldMap.getObjects();
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

        player = new HackSlashPlayer(10 * 16, 3 * 16, gsm);

        Entities.add(player);

        //Setup Dialog Instance
        MenuInit(gsm.UIWidth, gsm.UIHeight);

        gsm.DiscordManager.setPresenceDetails("topdown Demo - Level 1");
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

        MapGraph = new FlatTiledGraph(WorldMap);

        Entities.add(new Pawn("[PURPLE]pawn", 400,250,0, new Vector3(8,8,16), 1, 50, NPC.intractability.Silent, false, MapGraph, gsm));
        Entities.add(new Pawn("[PURPLE]pawn", 470,240,0, new Vector3(8,8,16), 1, 50, NPC.intractability.Silent, false, MapGraph, gsm));
        Entities.add(new Pawn("[PURPLE]pawn", 710,330,0, new Vector3(8,8,16), 1, 50, NPC.intractability.Silent, false, MapGraph, gsm));
        Entities.add(new Pawn("[PURPLE]pawn", 800,425,0, new Vector3(8,8,16), 1, 50, NPC.intractability.Silent, false, MapGraph, gsm));

        MusicID = AudioM.playMusic("TimeBroke.wav", true, true);

    }

    public void update() {

        Particles.Update();

        for (int i = 0; i < Entities.size(); i++) {
            if (Entities.get(i) instanceof Pawn_Enemy) {
                ((Pawn_Enemy)Entities.get(i)).update(Gdx.graphics.getDeltaTime(), this, player);
                if (!((NPC) Entities.get(i)).isAlive()) {
                    Particles.AddParticleEffect("Leaf", player.getIntereactBox().getCenterX(), player.getIntereactBox().getCenterY());
                    Entities.remove(i);
                }
            } else if (Entities.get(i) instanceof NPC) {
                if (!((NPC) Entities.get(i)).isAlive()) {
                    Particles.AddParticleEffect("Leaf", player.getIntereactBox().getCenterX(), player.getIntereactBox().getCenterY());
                    Entities.remove(i);
                }
            } else if (Entities.get(i) instanceof Interactable) {
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

        List<WorldObject> Remove = new ArrayList<>();

        for (int i = 0; i < Entities.size(); i++) {

            if (Entities.get(i) instanceof Bullet) {

                ((Bullet)Entities.get(i)).timeAlive += Gdx.graphics.getDeltaTime();

                if (((Bullet)Entities.get(i)).timeAlive >= ((Bullet)Entities.get(i)).Lifespan){
                    Remove.add(Entities.get(i));
                    break;
                }

                WorldObject tempPar = ((Bullet)Entities.get(i)).Parrent;
                if (Entities.get(i).checkCollision(new Vector3(Entities.get(i).getPosition().x, Entities.get(i).getPosition().y, 0), Collisions, true)) {
                    Remove.add(Entities.get(i));
                } else {
                    for (int j = 0; j < Entities.size(); j++) {
                        if (Entities.get(j).equals(tempPar)) {

                        } else if (Entities.get(j) instanceof Bullet) {

                        } else if (Entities.get(j).getHitbox().contains(Entities.get(i).getPosition())) {

                            if(Entities.get(j) instanceof NPC) {
                                Remove.add(Entities.get(i));
                                ((NPC) Entities.get(j)).setHealth(((NPC) Entities.get(j)).getHealth()-10);
                            } else if (Entities.get(j) instanceof HackSlashPlayer) {
                                if (!((HackSlashPlayer) Entities.get(j)).Rolling) {
                                    Remove.add(Entities.get(i));
                                    ((HackSlashPlayer) Entities.get(j)).Health--;
                                }
                            }
                        }
                    }
                }

            }
        }

        for (int i = 0; i < Remove.size(); i++) {
            Entities.remove(Remove.get(i));
        }

        cameraUpdate(player, camera, Entities,0,0, WorldMap.getWidth()*WorldMap.getTileSize(), WorldMap.getHeight()*WorldMap.getTileSize());

        if (player.Health > 0) {
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

        gsm.Render.GUIDrawText(g, 80,95,"Use WASD to move", Color.WHITE);
        gsm.Render.GUIDrawText(g, 170,140,"SPACE to roll", Color.WHITE);
        gsm.Render.GUIDrawText(g, 170,130,"And CLICK to attack!", Color.WHITE);
        //gsm.Render.GUIDrawText(g, 810,648,"Press F", Color.WHITE);

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
        //Particles.Draw(g);

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
            gsm.Render.GUIDrawText(g, (int) pos.x / 16 * 16 + 1, (int) pos.y / 16 * 16 + 1, ((int) pos.x) + " : " +( (int) pos.y) + "", Color.WHITE);
        }

        g.end();

        //DEBUG CODE
        gsm.Render.debugRenderer.setProjectionMatrix(camera.combined);
        gsm.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (GameStateManager.Debug) {

            //Gonna make a debug renderer for the MapGraph, or i guess now? changing that fixed the crashing

            for (int y = 0; y < WorldMap.getHeight(); y++) {
                for (int x = 0; x < WorldMap.getWidth(); x++) {
                    switch (MapGraph.getNode(x, y).type) {
                        case FlatTiledNode.GROUND:
                            gsm.Render.debugRenderer.setColor(Color.GREEN);
                            gsm.Render.debugRenderer.rect(x * 16, y * 16, 16, 16);
                            break;
                        case FlatTiledNode.COLLIDABLE:
                            gsm.Render.debugRenderer.setColor(Color.RED);
                            gsm.Render.debugRenderer.rect(x * 16, y * 16, 16, 16);
                            break;
                        default:
                            //gsm.Render.debugRenderer.setColor(Color.WHITE);
                            //gsm.Render.debugRenderer.rect(x * 16, y * 16, 16, 16);
                            break;
                    }
                }
            }

            gsm.Render.debugRenderer.setColor(Color.FIREBRICK);
            for (int i = 0; i < Entities.size(); i++) {
                if(Entities.get(i) instanceof Pawn_Enemy) {
                    Pawn_Enemy temp = (Pawn_Enemy) Entities.get(i);
                    int nodeCount = temp.getPath().getCount();
                    for (int j = 0; j < nodeCount; j++) {
                        FlatTiledNode node = temp.getPath().nodes.get(j);
                        gsm.Render.debugRenderer.rect(node.x * 16 + 4, node.y * 16 + 4, 4, 4);
                    }
                }
            }

            gsm.Render.debugRenderer.setColor(Color.FOREST);
            for (int i = 0; i < Entities.size(); i++) {
                if(Entities.get(i) instanceof Student) {
                    Student temp = (Student) Entities.get(i);
                    gsm.Render.debugRenderer.rect(temp.getDestination().x+2, temp.getDestination().y+2, 12, 12);
                }
            }



            for (int i = 0; i < Collisions.size(); i++) {

                //The bottom
                gsm.Render.debugRenderer.setColor(Color.YELLOW);
                gsm.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y + Collisions.get(i).getPrism().min.z / 2, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());

                //The top of the Cube
                gsm.Render.debugRenderer.setColor(Color.RED);
                gsm.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y + Collisions.get(i).getPrism().getDepth() / 2 + Collisions.get(i).getPrism().min.z / 2, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());

                gsm.Render.debugRenderer.setColor(Color.ORANGE);
            }

            for (int i = 0; i < Entities.size(); i++) {
                //gsm.Render.debugRenderer.box(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y, Entities.get(i).getHitbox().min.z, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight(), Entities.get(i).getHitbox().getDepth());

                //The bottom
                gsm.Render.debugRenderer.setColor(Color.GREEN);
                gsm.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().min.z / 2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

                //The top of the Cube
                gsm.Render.debugRenderer.setColor(Color.BLUE);
                gsm.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().getDepth() / 2 + Entities.get(i).getHitbox().min.z / 2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

            }

            //The bottom of the PLAYER
            gsm.Render.debugRenderer.setColor(Color.YELLOW);
            gsm.Render.debugRenderer.rect(player.getHitbox().min.x, player.getHitbox().min.y + player.getHitbox().min.z / 2, player.getHitbox().getWidth(), player.getHitbox().getHeight());
            //The top of the Cube
            gsm.Render.debugRenderer.setColor(Color.RED);
            gsm.Render.debugRenderer.rect(player.getHitbox().min.x, player.getHitbox().min.y + player.getHitbox().getDepth() / 2 + player.getHitbox().min.z / 2, player.getHitbox().getWidth(), player.getHitbox().getHeight());

            gsm.Render.debugRenderer.setColor(Color.PURPLE);
            gsm.Render.debugRenderer.box(player.getIntereactBox().min.x, player.getIntereactBox().min.y, player.getIntereactBox().min.z, player.getIntereactBox().getWidth(), player.getIntereactBox().getHeight(), player.getIntereactBox().getDepth());

        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) { //KeyHit
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            gsm.Render.debugRenderer.setColor(Color.WHITE);
            gsm.Render.debugRenderer.rect(((int) pos.x / 16) * 16 + 1, ((int) pos.y / 16) * 16 + 1, 15, 15);
        }

        gsm.Render.debugRenderer.end();

    }

    public void drawUI(SpriteBatch g, int height, int width, float Time) {
        //Draws things on the screen, and not the world positions
        g.setProjectionMatrix(GuiCam.combined);
        g.begin();

        if(MenuOpen) {
            if (!gsm.UI.getState().equals(UI_state.INGAMEUI))
                gsm.UI.setState(UI_state.INGAMEUI);
        } else {
            if (!gsm.UI.getState().equals(UI_state.InGameHome))
                gsm.UI.setState(UI_state.InGameHome);
        }

        MenuDraw(g, Gdx.graphics.getDeltaTime());
        g.end();
        gsm.UI.Draw();
    }

    private void handleInput() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            MenuOpen = !MenuOpen;
        }

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

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            player.setVelocityY(player.getVelocity().y + 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            player.setVelocityY(player.getVelocity().y - 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            player.setVelocityX(player.getVelocity().x + 1);
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            player.setVelocityX(player.getVelocity().x - 1);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (player.RollingTime < 0.1) {
                player.Rolling = true;
                player.RollingTime += 0.5f;
            }
        }

        if (Gdx.input.getX() < Gdx.graphics.getWidth()/2) {
            player.setFacing(true);
        } else {
            player.setFacing(false);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.C) || Gdx.input.justTouched()) { // ATTACK

            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            //Get the direction of the attack based on whether mouse is on left or right of the screen.

            if (player.AttackTime < .1f) {
                for (int i = 0; i < Entities.size(); i++) {
                    if (player.getAttackBox().intersects(Entities.get(i).getHitbox())) {
                        if (Entities.get(i) instanceof NPC) {
                            NPC Entitemp = (NPC) Entities.get(i);

                            float HitVelocity = 40;

                            Vector3 hitDirection = new Vector3(1 * HitVelocity, 0 * HitVelocity, 0);
                            Entitemp.damage(10, hitDirection);
                            shaker.addDamage(0.35f);
                        } else if (Entities.get(i) instanceof Trigger) {
                            if (((Trigger) Entities.get(i)).getActivationType().equals(Trigger.TriggerType.OnAttack)) {
                                ((Trigger) Entities.get(i)).RunCommands(player, shaker, this, null, Particles, Entities);
                                ((Trigger) Entities.get(i)).JustRan = true;
                            }
                        }
                    }
                }

                player.AttackTime += 0.35f;
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
            AddDialog("pawn", "It's working!");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_7)) {
            Bullet temp = new Bullet((int)player.getPosition().x, (int)player.getPosition().y, (int)player.getPosition().z, new Vector3(5,-5, 0), player);
            Entities.add(temp);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.T)) {
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            player.setPosition(((int) pos.x / 16) * 16 + 1, ((int) pos.y / 16) * 16 + 1, 0);
        }

    }

    public void reSize(SpriteBatch g, int H, int W) {

        /*Vector3 campostemp = camera.position;
        camera.setToOrtho(false, gsm.WorldWidth, gsm.WorldHeight);
        camera.position.set(campostemp);
        GuiCam.setToOrtho(false, gsm.UIWidth, gsm.UIHeight);
        shaker.reSize(camera);

        UI.reSize();

        //shaker.reSize(camera); */
    }

    public void cameraUpdate(WorldObject mainFocus, OrthographicCamera cam, List<WorldObject> Entities, int MinX, int MinY, int MaxX, int MaxY) {

        Vector2 FocalPoint = new Vector2(mainFocus.getPosition().x, mainFocus.getPosition().y);
        float totalFocusPoints = 1;

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

        cam.position.set((int) (FocalPoint.x / totalFocusPoints), (int) (FocalPoint.y / totalFocusPoints), 0);

        cam.update();
    }

    @Override
    public void dispose() {
        Collisions.clear();
        Entities.clear();
        AudioM.stopMusic(MusicID);
    }

    @Override
    public void Shutdown() {
        AudioM.stopMusic(MusicID);
    }

}