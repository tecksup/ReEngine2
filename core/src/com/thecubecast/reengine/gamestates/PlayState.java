// GameState that tests new mechanics.

package com.thecubecast.reengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.thecubecast.reengine.data.*;
import com.thecubecast.reengine.data.dcputils.DcpTilKt;
import com.thecubecast.reengine.data.tkmap.TkMap;
import com.thecubecast.reengine.graphics.scene2d.UI_state;
import com.thecubecast.reengine.graphics.ScreenShakeCameraController;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledGraph;
import com.thecubecast.reengine.worldobjects.ai.pathfinding.FlatTiledNode;
import com.thecubecast.reengine.worldobjects.*;
import com.thecubecast.reengine.worldobjects.entityprefabs.Dummy;
import com.thecubecast.reengine.worldobjects.entityprefabs.Firepit;

import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.reengine.data.GameStateManager.AudioM;
import static com.thecubecast.reengine.data.GameStateManager.ItemPresets;
import static com.thecubecast.reengine.data.GameStateManager.ctm;
import static com.thecubecast.reengine.graphics.Draw.FillColorShader;
import static com.thecubecast.reengine.graphics.Draw.setFillColorShaderColor;

public class PlayState extends DialogStateExtention {

    //GUI
    boolean MenuOpen = true;
    boolean HudOpen = true;

    //Camera
    OrthographicCamera GuiCam;
    public static OrthographicCamera camera;
    ScreenShakeCameraController shaker;

    //Particles
    public static ParticleHandler Particles;

    //GameObjects
    public SeedPlayer player;

    TkMap WorldMap;

    //ai
    FlatTiledGraph MapGraph;

    Music MusicID;

    public PlayState(GameStateManager gsm) {
        super(gsm);
        gsm.setWorldScale(3);
    }

    public void init() {

        WorldMap = new TkMap("Saves/Fight.cube");
        MapGraph = new FlatTiledGraph(WorldMap);

        ArrayList<WorldObject> tempobjsshit = WorldMap.getObjects(MapGraph, gsm);
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

        player = new SeedPlayer((int) gsm.PlayerSpawn.x, (int) gsm.PlayerSpawn.y,0,this);

        Entities.add(player);

        //Entities.add(new Dummy("Dummy", 16*16, 16*16,0, new Vector3(2,2,2), 100, 100, NPC.intractability.Silent, true));

        for (int x = 0; x < WorldMap.getWidth(); x++) {
            for (int y = 0; y < WorldMap.getHeight(); y++) {
                if (WorldMap.getCollision()[x][y]) {
                    Collisions.add(new Cube(x * 16, y * 16, 0, 16, 16, 16));
                }
            }
        }

        //Setup Dialog Instance
        MenuInit(GameStateManager.UIWidth, GameStateManager.UIHeight);

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

        //MusicID = AudioM.playMusic("TimeBroke.wav", true, true);

    }

    public void update() {

        Particles.Update();

        for (int i = 0; i < Entities.size(); i++) {
            if (player.AttackPhase.AttackOnNextHit) {
                if (player.getAttackBox().intersects(Entities.get(i).getHitbox())) {
                    if (Entities.get(i) instanceof NPC) {
                        NPC Entitemp = (NPC) Entities.get(i);

                        System.out.println("Got Hit");

                        Particles.AddParticleEffect("sparkle", Entitemp.getPosition().x + 8, Entitemp.getPosition().y + 8);

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
        }

        player.AttackPhase.AttackOnNextHit = false;

        for (int i = 0; i < Entities.size(); i++) {
            if (Entities.get(i) instanceof WorldItem) {
                Entities.get(i).update(Gdx.graphics.getDeltaTime(), this);
                WorldItem Entitemp = (WorldItem) Entities.get(i);

                if (Entitemp.JustDroppedDelay <= 0) {

                    Vector3 tempCenter = new Vector3(player.getPosition().x + player.getSize().x / 2 + 4, player.getPosition().y + player.getSize().y / 2, player.getPosition().z + player.getSize().z / 2);
                    Vector3 CBS = new Vector3(48, 48, 32); //CollectionBoxSize

                    if (Entitemp.ifColliding(new Rectangle(tempCenter.x - CBS.x / 2, tempCenter.y - CBS.y / 2, CBS.x, CBS.y))) {
                        Entitemp.setPosition(new Vector3(tempCenter).sub(Entitemp.getPosition()).clamp(0, 2).add(Entitemp.getPosition()));
                    }

                    if (Entitemp.getHitbox().intersects(player.getHitbox())) {
                        //Add the item to inventory
                        player.AddToInventory(Entitemp.item);
                        Entities.remove(i);
                    }
                }
            } else if (Entities.get(i) instanceof PathfindingWorldObject) {
                Entities.get(i).update(Gdx.graphics.getDeltaTime(), this);
                if (!((NPC) Entities.get(i)).isAlive()) {
                    Entities.remove(i);
                }
            } else if (Entities.get(i) instanceof NPC) {
                if (!((NPC) Entities.get(i)).isAlive()) {
                    Entities.get(i).update(Gdx.graphics.getDeltaTime(), this);
                    Particles.AddParticleEffect("Leaf", player.getIntereactBox().getCenterX(), player.getIntereactBox().getCenterY());
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

                        if (Entities.get(i) instanceof Storage) {
                            gsm.UI.StorageOpen = (Storage) Entities.get(i);
                            MenuOpen = !MenuOpen;
                            HudOpen = !HudOpen;
                            gsm.UI.setState(UI_state.InventoryAndStorage);
                            gsm.UI.setVisable(true);
                        } else if (Entities.get(i) instanceof Interactable) {
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
                            } else if (Entities.get(j) instanceof ProtoType_Player) {
                                if (!((ProtoType_Player) Entities.get(j)).Rolling) {
                                    Remove.add(Entities.get(i));
                                    ((ProtoType_Player) Entities.get(j)).Health--;
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

        cameraUpdate(player, camera, Entities,8,8, WorldMap.getWidth()*WorldMap.getTileSize()-8, WorldMap.getHeight()*WorldMap.getTileSize()-8);

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

        //DEBUG CODE
        GameStateManager.Render.debugRenderer.setProjectionMatrix(camera.combined);
        GameStateManager.Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (GameStateManager.Debug && MenuOpen) {

            //Gonna make a debug renderer for the MapGraph, or i guess now? changing that fixed the crashing

            for (int y = 0; y < WorldMap.getHeight(); y++) {
                for (int x = 0; x < WorldMap.getWidth(); x++) {
                    switch (MapGraph.getNode(x, y).type) {
                        case FlatTiledNode.GROUND:
                            GameStateManager.Render.debugRenderer.setColor(Color.GREEN);
                            GameStateManager.Render.debugRenderer.rect(x * 16, y * 16, 16, 16);
                            break;
                        case FlatTiledNode.COLLIDABLE:
                            GameStateManager.Render.debugRenderer.setColor(Color.RED);
                            GameStateManager.Render.debugRenderer.rect(x * 16, y * 16, 16, 16);
                            break;
                        default:
                            //gsm.Render.debugRenderer.setColor(Color.WHITE);
                            //gsm.Render.debugRenderer.rect(x * 16, y * 16, 16, 16);
                            break;
                    }
                }
            }

            if (false) {

                GameStateManager.Render.debugRenderer.setColor(Color.FIREBRICK);
                for (int i = 0; i < Entities.size(); i++) {
                    if (Entities.get(i) instanceof PathfindingWorldObject) {
                        PathfindingWorldObject temp = (PathfindingWorldObject) Entities.get(i);
                        int nodeCount = temp.getPath().getCount();
                        for (int j = 0; j < nodeCount; j++) {
                            FlatTiledNode node = temp.getPath().nodes.get(j);
                            GameStateManager.Render.debugRenderer.rect(node.x * 16 + 4, node.y * 16 + 4, 4, 4);
                        }
                    }
                }

                GameStateManager.Render.debugRenderer.setColor(Color.FOREST);
                for (int i = 0; i < Entities.size(); i++) {
                    if (Entities.get(i) instanceof PathfindingWorldObject) {
                        PathfindingWorldObject temp = (PathfindingWorldObject) Entities.get(i);
                        if (temp.getDestination() != null) {
                            GameStateManager.Render.debugRenderer.rect(temp.getDestination().x + 2, temp.getDestination().y + 2, 12, 12);
                        }
                    }
                }

            }


            for (int i = 0; i < Collisions.size(); i++) {

                //The bottom
                GameStateManager.Render.debugRenderer.setColor(Color.YELLOW);
                GameStateManager.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y + Collisions.get(i).getPrism().min.z / 2, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());

                //The top of the Cube
                GameStateManager.Render.debugRenderer.setColor(Color.RED);
                GameStateManager.Render.debugRenderer.rect(Collisions.get(i).getPrism().min.x, Collisions.get(i).getPrism().min.y + Collisions.get(i).getPrism().getDepth() / 2 + Collisions.get(i).getPrism().min.z / 2, Collisions.get(i).getPrism().getWidth(), Collisions.get(i).getPrism().getHeight());

                GameStateManager.Render.debugRenderer.setColor(Color.ORANGE);
            }

            for (int i = 0; i < Entities.size(); i++) {
                //gsm.Render.debugRenderer.box(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y, Entities.get(i).getHitbox().min.z, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight(), Entities.get(i).getHitbox().getDepth());

                //The bottom
                GameStateManager.Render.debugRenderer.setColor(Color.GREEN);
                GameStateManager.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().min.z / 2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

                //The top of the Cube
                GameStateManager.Render.debugRenderer.setColor(Color.BLUE);
                GameStateManager.Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().getDepth() / 2 + Entities.get(i).getHitbox().min.z / 2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

            }

            //The bottom of the PLAYER
            GameStateManager.Render.debugRenderer.setColor(Color.YELLOW);
            GameStateManager.Render.debugRenderer.rect(player.getHitbox().min.x, player.getHitbox().min.y + player.getHitbox().min.z / 2, player.getHitbox().getWidth(), player.getHitbox().getHeight());
            //The top of the Cube
            GameStateManager.Render.debugRenderer.setColor(Color.RED);
            GameStateManager.Render.debugRenderer.rect(player.getHitbox().min.x, player.getHitbox().min.y + player.getHitbox().getDepth() / 2 + player.getHitbox().min.z / 2, player.getHitbox().getWidth(), player.getHitbox().getHeight());

            GameStateManager.Render.debugRenderer.setColor(Color.PURPLE);
            GameStateManager.Render.debugRenderer.box(player.getIntereactBox().min.x, player.getIntereactBox().min.y, player.getIntereactBox().min.z, player.getIntereactBox().getWidth(), player.getIntereactBox().getHeight(), player.getIntereactBox().getDepth());

            //Item Collection
            gsm.Render.debugRenderer.setColor(Color.DARK_GRAY);
            Vector3 tempCenter = new Vector3(player.getPosition().x + player.getSize().x / 2 + 4, player.getPosition().y + player.getSize().y / 2, player.getPosition().z + player.getSize().z / 2);
            Vector3 CBS = new Vector3(48, 48, 32); //CollectionBoxSize
            gsm.Render.debugRenderer.rect(tempCenter.x - CBS.x / 2, tempCenter.y - CBS.y / 2, CBS.x, CBS.y);


            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            GameStateManager.Render.debugRenderer.setColor(Color.LIGHT_GRAY);
            GameStateManager.Render.debugRenderer.circle(player.getPosition().x + (player.getSize().x/2), player.getPosition().y + (player.getSize().y/2), 20);

            if (gsm.ctm.controllers.size() != 0) {
                float AxisX = 0;
                float AxisY = 0;
                if (Math.abs(ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_X)) > 0.1) {
                    AxisX = ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_X);
                }
                if (Math.abs(ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y)) > 0.1) {
                    AxisY = ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y);
                }
                if (AxisX != 0 || AxisY != 0) {
                    player.FacingAngle = DcpTilKt.getAngleBetweenPoints(player.getPosition().x + (player.getSize().x / 2), player.getPosition().y + (player.getSize().y / 2), player.getPosition().x + (player.getSize().x / 2) + (AxisX * 10), player.getPosition().y + (player.getSize().y / 2) + (AxisY * 10));
                }
                GameStateManager.Render.debugRenderer.circle(player.getPosition().x + (player.getSize().x / 2) + (gsm.ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_X) * 10), player.getPosition().y + (player.getSize().y / 2) + (gsm.ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y) * 10), 5);
                GameStateManager.Render.debugRenderer.line(new Vector3(player.getPosition().x + (player.getSize().x / 2), player.getPosition().y + (player.getSize().y / 2), player.getPosition().z), new Vector3(player.getPosition().x + (player.getSize().x / 2) + (AxisX * 10), player.getPosition().y + (player.getSize().y / 2) + (AxisY * 10), 0));
            } else {
                player.FacingAngle = DcpTilKt.getAngleBetweenPoints(player.getPosition().x + (player.getSize().x/2), player.getPosition().y + (player.getSize().y/2), pos.x, pos.y);
                GameStateManager.Render.debugRenderer.circle(pos.x, pos.y, 5);
                GameStateManager.Render.debugRenderer.line(new Vector3(player.getPosition().x + (player.getSize().x/2), player.getPosition().y + (player.getSize().y/2), player.getPosition().z), pos);

            }

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
            HudOpen = true;
        } else {
            if (HudOpen) {
                if (!gsm.UI.getState().equals(UI_state.InGameHome)) {
                    gsm.UI.setState(UI_state.InGameHome);
                    gsm.UI.stage.setViewport(new FitViewport(GameStateManager.UIWidth, GameStateManager.UIHeight));
                    Gdx.input.setInputProcessor(gsm.UI.stage);
                }
            } else {
                gsm.UI.stage.setViewport(new FitViewport(GameStateManager.UIWidth, GameStateManager.UIHeight));
                Gdx.input.setInputProcessor(gsm.UI.stage);
            }
        }

        //Draws things on the screen, and not the world positions
        g.setProjectionMatrix(GuiCam.combined);
        g.begin();
        if (MenuOpen)
            MenuDraw(g, Gdx.graphics.getDeltaTime());
        g.end();
        gsm.UI.Draw();

        if (gsm.UI.CursorItem != null) {
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            GuiCam.unproject(pos);
            if (!gsm.UI.CursorItem.isStructure()) {
                g.begin();
                g.draw(new Texture(Gdx.files.internal(gsm.UI.CursorItem.getTexLocation())), pos.x / 2, pos.y / 2, 16, 16);
            } else {
                g.flush();
                g.setShader(FillColorShader);
                setFillColorShaderColor(Color.GREEN, 0.6f);
                g.begin();
                g.draw(new Texture(Gdx.files.internal(gsm.UI.CursorItem.getTexLocation())), pos.x / 2, pos.y / 2);
                g.setShader(null);
            }
            g.end();
        }
    }

    private void handleInput() {

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || ctm.isButtonJustDown(0, ControlerManager.buttons.BUTTON_START)) {
            MenuOpen = !MenuOpen;
            HudOpen = true;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.O)) {
            Entities.add(new WorldItem(100, 100, 5, ItemPresets.get(4)));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            gsm.UI.setState(UI_state.Inventory);
            MenuOpen = !MenuOpen;
            HudOpen = !HudOpen;
        }

        if (MenuOpen) {
            if (ctm.isButtonJustDown(0, ControlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.C) || Gdx.input.justTouched()) { // ATTACK
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
                                Ent.Interact(player, shaker, this, null, Particles, Entities);
                            }
                        }
                    }

                    Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                    camera.unproject(pos);
                    //Get the direction of the attack based on whether mouse is on left or right of the screen.

                    player.Attack();

                }
            }

            if (ctm.controllers.size() > 0) {
                float AxisX = 0;
                float AxisY = 0;
                if (Math.abs(ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_X)) > 0.15) {
                    AxisX = ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_X);
                }
                if (Math.abs(ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y)) > 0.15) {
                    AxisY = ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y);
                }
                if (AxisX != 0 || AxisY != 0) {
                    player.setVelocity(new Vector3(player.getVelocity()).add(new Vector3(AxisX*1.4f, AxisY*1.4f, 0)));
                }


            } else {
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
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) || ctm.isButtonJustDown(0, ControlerManager.buttons.BUTTON_B)) {
                if (player.RollingTime < 0.1) {
                    player.Rolling = true;
                    player.RollingTime += 0.5f;
                }
            }

            if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_6)) {
                AddDialog("pawn", "{SICK}It's working!");
            }

            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.T)) {
                Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(pos);
                player.setPosition(((int) pos.x / 16) * 16 + 1, ((int) pos.y / 16) * 16 + 1, 0);
            }
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

        cam.position.set((int) (FocalPoint.x), (int) (FocalPoint.y), 0);

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