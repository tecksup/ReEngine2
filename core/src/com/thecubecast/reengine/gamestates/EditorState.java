// GameState that tests new mechanics.

package com.thecubecast.reengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.thecubecast.reengine.data.Common;
import com.thecubecast.reengine.data.Cube;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.ParticleHandler;
import com.thecubecast.reengine.data.tkmap.TkMap;
import com.thecubecast.reengine.graphics.scene2d.TkImageButton;
import com.thecubecast.reengine.graphics.scene2d.TkTextButton;
import com.thecubecast.reengine.graphics.scene2d.UI_state;
import com.thecubecast.reengine.graphics.ScreenShakeCameraController;
import com.thecubecast.reengine.worldobjects.*;
import com.thecubecast.reengine.worldobjects.Triggers.FarmTile;
import com.thecubecast.reengine.worldobjects.Triggers.Interactable;
import com.thecubecast.reengine.worldobjects.Triggers.TextWorldObject;
import com.thecubecast.reengine.worldobjects.Triggers.Trigger;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import static com.thecubecast.reengine.data.Common.updategsmValues;
import static com.thecubecast.reengine.data.GameStateManager.Render;

public class EditorState extends GameState {

    boolean OverHud = false;
    boolean DraggingObject = false;
    int[] draggingOffset = {0,0};

    public enum BrushSizes {small, medium, large}

    private BrushSizes BrushSize = BrushSizes.small;

    int TileIDSelected = 0;
    boolean Erasing = false;
    boolean Fill = false;

    private enum selection {
        Ground, Forground, Collision, Object, None
    }

    private selection selected = selection.None;

    private Interactable Prefab = null;

    private boolean SelectionDragging = false;
    private Vector2[] SelectedArea;
    private List<WorldObject> SelectedObjects = new ArrayList<>();
    private List<WorldObject> Copied_Objects = new ArrayList<>();

    WorldObject CameraFocusPointEdit = new WorldObject() {
        @Override
        public void init(int Width, int Height) {

        }

        @Override
        public void update(float delta, GameState G) {

        }

        @Override
        public void draw(SpriteBatch batch, float Time) {

        }
    };

    //Camera
    OrthographicCamera GuiCam;
    public OrthographicCamera camera;
    ScreenShakeCameraController shaker;

    Vector3 StartDrag;
    boolean Dragging = false;
    WorldObject MainCameraFocusPoint;

    //Particles
    public static ParticleHandler Particles;

    //GameObjects
    public static List<Cube> Collisions = new ArrayList<>();
    private static List<WorldObject> Entities = new ArrayList<>();

    //Map Variables
    String SaveNameText = gsm.SaveSelected;
    TkMap tempshitgiggle;

    Skin skin;
    Stage UIStage;
    Table InfoTable;
    Table EditorTable;
    WorldObject HiddenButtonTriggeresLoading;

    public EditorState(GameStateManager gsm) {
        super(gsm);
        gsm.setUIScale(2);
        gsm.setWorldScale(3);

        //double Started = System.nanoTime();

        tempshitgiggle = new TkMap("Saves/" + SaveNameText + ".cube");
        //System.out.println("Loading Map Took " + ((System.nanoTime() - Started)/1000000000.0) + " seconds to complete");
        ArrayList<WorldObject> tempobjsshit = tempshitgiggle.getObjects(null, gsm);
        for (int i = 0; i < tempobjsshit.size(); i++) {
            Entities.add(tempobjsshit.get(i));

        }
        //System.out.println("Loading Objects Took " + ((System.nanoTime() - Started)/1000000000.0) + " seconds to complete");

        //System.out.println("Loading Everything Took " + ((System.nanoTime() - Started)/1000000000.0) + " seconds to complete");

        MainCameraFocusPoint = CameraFocusPointEdit;

        gsm.DiscordManager.setPresenceDetails("Level Editor");
        gsm.DiscordManager.setPresenceState("Working so very well...");
        gsm.DiscordManager.getPresence().largeImageText = "";
        gsm.DiscordManager.getPresence().startTimestamp = System.currentTimeMillis() / 1000;

        //Camera setup
        camera = new OrthographicCamera();
        GuiCam = new OrthographicCamera();
        camera.setToOrtho(false, GameStateManager.WorldWidth, GameStateManager.WorldHeight);
        GuiCam.setToOrtho(false, GameStateManager.UIWidth, GameStateManager.UIHeight);
        shaker = new ScreenShakeCameraController(camera);

        gsm.UI.stage.getViewport().setCamera(GuiCam);
        gsm.UI.inGame = true;
        gsm.UI.setState(UI_state.InGameHome);
        gsm.UI.setVisable(false);

        //Particles
        Particles = new ParticleHandler();

        UISetup();

    }

    public void update() {

        if (selected != selection.Object && SelectedObjects.size() > 0) {
            for (int i = 0; i < SelectedObjects.size(); i++) {
                SelectedObjects.get(i).setDebugView(false);
            }
            SelectedObjects.clear();
            HiddenButtonTriggeresLoading.init(0, 0);
        }

        Particles.Update();

        if (SelectedObjects.size() == 0) {

            for (int i = 0; i < Entities.size(); i++) {
                Entities.get(i).update(Gdx.graphics.getDeltaTime(), this);

                Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                camera.unproject(pos);
                if (selected.equals(selection.Object)) {
                    if (Entities.get(i).getHitbox().contains(new Vector3(pos.x, pos.y, 2))) {
                        //Entities.get(i).setDebugView(true);
                        if (SelectedArea == null && Gdx.input.isTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT) && Prefab == null && !DraggingObject) {
                            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                                SelectedObjects.add(Entities.get(i));
                                Entities.get(i).setDebugView(true);
                            } else {

                                if (SelectedObjects.size() == 1) {
                                    if (SelectedObjects.get(0).equals(Entities.get(i))) {
                                        break;
                                    }
                                }

                                for (int j = 0; j < SelectedObjects.size(); j++) {
                                    SelectedObjects.get(j).setDebugView(false);
                                }
                                SelectedObjects.clear();

                                SelectedObjects.add(Entities.get(i));
                                Entities.get(i).setDebugView(true);
                                HiddenButtonTriggeresLoading.init(0, 0);
                                break;
                            }
                        }
                    }
                }

            }
        }

        cameraUpdate(MainCameraFocusPoint, camera, Entities, 0, 0, tempshitgiggle.getWidth() * tempshitgiggle.getTileSize(), tempshitgiggle.getHeight() * tempshitgiggle.getTileSize());

        handleInput();

        UIStage.act(Gdx.graphics.getDeltaTime());

    }

    public void draw(SpriteBatch g, int height, int width, float Time) {

        shaker.update(gsm.DeltaTime);
        g.setProjectionMatrix(shaker.getCombinedMatrix());

        Rectangle drawView = new Rectangle(camera.position.x - camera.viewportWidth / 2 - camera.viewportWidth / 4, camera.position.y - camera.viewportHeight / 2 - camera.viewportHeight / 4, camera.viewportWidth + camera.viewportWidth / 4, camera.viewportHeight + camera.viewportHeight / 4);

        g.setShader(null);
        g.begin();

        tempshitgiggle.Tileset.Update(Gdx.graphics.getDeltaTime());

        if (!selected.equals(selection.Object)) {
            Prefab = null;
        }

        if (selected.equals(selection.Forground)) {
            tempshitgiggle.DrawGround(camera, g, 0.8f);
            tempshitgiggle.DrawForground(camera, g, 1f);
        }
        else if (selected.equals(selection.Ground)) {
            tempshitgiggle.DrawGround(camera, g, 1f);
            tempshitgiggle.DrawForground(camera, g, 0.8f);
        }
        else {
            tempshitgiggle.Draw(camera, g);
        }

        if (selected.equals(selection.Collision)) {
            //MapRenderer.renderLayer(g, Map, "Collision");
            tempshitgiggle.DrawCollision(camera, g);
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
        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Prefab == null && SelectedObjects.size() == 0) { //KeyHit
            gsm.Cursor = GameStateManager.CursorType.Question;

            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            Render.GUIDrawText(g, Common.roundDown(pos.x) - 5, Common.roundDown(pos.y) - 5, "X: " + ((int) pos.x / 16) + " Y: " + ((int) pos.y / 16), Color.WHITE);
        } else {
            gsm.Cursor = GameStateManager.CursorType.Normal;
        }

        //Particles
        Particles.Draw(g);

        //Renders the GUI for entities
        for (int i = 0; i < Entities.size(); i++) {
            if (Entities.get(i) instanceof NPC) {
                NPC Entitemp = (NPC) Entities.get(i);
                if (drawView.overlaps(new Rectangle(Entitemp.getPosition().x, Entitemp.getPosition().y, Entitemp.getSize().x, Entitemp.getSize().y))) {
                    ((NPC) Entities.get(i)).drawGui(g, Time);
                }
            }
        }

        Vector3 pos312 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(pos312);
        if (selected.equals(selection.Ground) && !Erasing && !OverHud) {
            TextureRegion frame = tempshitgiggle.Tileset.getTile(TileIDSelected).getFrame();
            g.draw(frame, ((int) pos312.x / 16) * 16, ((int) pos312.y / 16) * 16);
        } else if (selected.equals(selection.Forground) && !Erasing && !OverHud) {
            TextureRegion frame = tempshitgiggle.Tileset.getTile(TileIDSelected).getFrame();
            g.draw(frame, ((int) pos312.x / 16) * 16, ((int) pos312.y / 16) * 16);
        }

        g.end();

        //DEBUG CODE
        Render.debugRenderer.setProjectionMatrix(camera.combined);
        Render.debugRenderer.begin(ShapeRenderer.ShapeType.Line);

        if (GameStateManager.Debug) {

            Render.debugRenderer.setColor(Color.WHITE);
            Render.debugRenderer.rect(CameraFocusPointEdit.getPosition().x, CameraFocusPointEdit.getPosition().y, 2, 2);

        }

        for (int i = 0; i < Entities.size(); i++) {
            //gsm.Render.debugRenderer.box(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y, Entities.get(i).getHitbox().min.z, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight(), Entities.get(i).getHitbox().getDepth());

            if (Entities.get(i).isDebugView()) {
                //The bottom
                Render.debugRenderer.setColor(Color.GREEN);
                Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().min.z / 2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

                //The top of the Cube
                Render.debugRenderer.setColor(Color.BLUE);
                Render.debugRenderer.rect(Entities.get(i).getHitbox().min.x, Entities.get(i).getHitbox().min.y + Entities.get(i).getHitbox().getDepth() / 2 + Entities.get(i).getHitbox().min.z / 2, Entities.get(i).getHitbox().getWidth(), Entities.get(i).getHitbox().getHeight());

            }

        }

        if (SelectedObjects.size() > 0) {

            float LowestX, LowestY, HighestX, HighestY;

            LowestX = SelectedObjects.get(0).getPosition().x;
            LowestY = SelectedObjects.get(0).getPosition().y;
            HighestX = SelectedObjects.get(0).getHitbox().max.x + SelectedObjects.get(0).getHitboxOffset().x;
            HighestY = SelectedObjects.get(0).getHitbox().max.y + SelectedObjects.get(0).getHitboxOffset().y;

            for (int i = 1; i < SelectedObjects.size(); i++) {

                if (LowestX > SelectedObjects.get(i).getPosition().x) {
                    LowestX = SelectedObjects.get(i).getPosition().x;
                } else if (HighestX < (SelectedObjects.get(i).getPosition().x + SelectedObjects.get(i).getSize().x)) {
                    HighestX = SelectedObjects.get(i).getHitbox().max.x + SelectedObjects.get(i).getHitboxOffset().x;
                }

                if (LowestY > SelectedObjects.get(i).getPosition().y) {
                    LowestY = SelectedObjects.get(i).getPosition().y;
                } else if (HighestY < (SelectedObjects.get(i).getPosition().y + SelectedObjects.get(i).getSize().y)) {
                    HighestY = SelectedObjects.get(i).getHitbox().max.y + SelectedObjects.get(i).getHitboxOffset().y;
                }
            }

            BoundingBox PrismPla = new BoundingBox(new Vector3(LowestX, LowestY, 2), new Vector3(HighestX, HighestY, 4));

            Render.debugRenderer.setColor(Color.GREEN);
            Render.debugRenderer.rect(PrismPla.min.x, PrismPla.min.y + PrismPla.min.z / 2, PrismPla.getWidth(), PrismPla.getHeight());
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Prefab == null && SelectedObjects.size() == 0) {
            Render.debugRenderer.setColor(Color.WHITE);
            Render.debugRenderer.rect(((int) pos312.x / 16) * 16 + 1, ((int) pos312.y / 16) * 16 + 1, 15, 15);
        }

        if (Erasing) {
            Render.debugRenderer.setColor(Color.WHITE);
            Render.debugRenderer.rect(((int) pos312.x / 16) * 16 + 1, ((int) pos312.y / 16) * 16 + 1, 15, 15);
        }

        if (Prefab != null && Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            Render.debugRenderer.setColor(Color.WHITE);
            Render.debugRenderer.rect(((int) pos312.x / 16) * 16 + 1, ((int) pos312.y / 16) * 16 + 1, 15, 15);
        }

        if (SelectedArea != null) {
            Vector3 PosStart = new Vector3(SelectedArea[0].x, SelectedArea[0].y, 0);
            camera.unproject(PosStart);
            Render.debugRenderer.setColor(Color.ORANGE);
            Render.debugRenderer.rect(PosStart.x, PosStart.y, -Common.roundUp(SelectedArea[0].x - SelectedArea[1].x) / gsm.Scale, Common.roundUp(SelectedArea[0].y - SelectedArea[1].y) / gsm.Scale);
        }

        Render.debugRenderer.end();

    }

    public void drawUI(SpriteBatch g, int height, int width, float Time) {
        //Draws things on the screen, and not the world positions
        g.setProjectionMatrix(GuiCam.combined);
        g.begin();
        if (Prefab != null) {
            Vector3 pos312 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            GuiCam.unproject(pos312);

            Prefab.setPosition(pos312.x - (Prefab.getSize().x / 2), pos312.y - (Prefab.getSize().y / 2), 0);

            Prefab.draw(g, Time);
        }
        g.end();

        UIStage.getViewport().update(GameStateManager.UIWidth, GameStateManager.UIHeight, true);
        UIStage.draw();
        UIStage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        gsm.UI.Draw();
    }

    private void handleInput() {

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.O)) {
/*
            TexturePacker.process("../../images/atlas", "textureAtlas", "atlas");

            //Render.manager.load(Render.textureAtlasFilename, TextureAtlas.class);

            Render.textureAtlas = new TextureAtlas(Render.textureAtlasFilename);

            GameStateManager.Render.retrieveTextureAtlas();

            tempshitgiggle.Tileset.RefreshAtlas();
*/
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.E) && UIStage.getKeyboardFocus() == null) {
            Erasing = !Erasing;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.F) && UIStage.getKeyboardFocus() == null) {
            Fill = !Fill;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.S)) {
            SaveMap(SaveNameText);
        }

        if (selected.equals(selection.Object) && Prefab == null && Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.A) && UIStage.getKeyboardFocus() == null) {
            SelectedObjects.clear();
            SelectedObjects.addAll(Entities);

            for (int i = 0; i < SelectedObjects.size(); i++) {
                SelectedObjects.get(i).setDebugView(true);
            }
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.Z)) {
            //System.out.println("Copied");
            tempshitgiggle.undo();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.R)) {
            //System.out.println("Copied");
            tempshitgiggle.redo();
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.C)) {
            //System.out.println("Copied");
            Copied_Objects.clear();
            Copied_Objects.addAll(SelectedObjects);
        }

        if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT) && Gdx.input.isKeyJustPressed(Input.Keys.V)) {
            //System.out.println("Pasted");

            //Get the middle of the screen position in the world coords
            Vector3 camMiddle = new Vector3(camera.position.x, camera.position.y, 0);

            //calculate the average location of all the copied entities
            Vector3 AveragePos = new Vector3();

            for (int i = 0; i < Copied_Objects.size(); i++) {
                AveragePos.add(Copied_Objects.get(i).getPosition());
            }
            AveragePos.x = AveragePos.x/Copied_Objects.size();
            AveragePos.y = AveragePos.y/Copied_Objects.size();
            AveragePos.z = AveragePos.z/Copied_Objects.size();

            //Calculate offset
            camMiddle.sub(AveragePos);

            //Add the change from average location of copied, to location of middle screen
            for (int i = 0; i < Copied_Objects.size(); i++) {
                Interactable temp = ((Interactable)Copied_Objects.get(i)).CreateNew();

                temp.setPosition(temp.getPosition().x + camMiddle.x, temp.getPosition().y + camMiddle.y, AveragePos.z);
                Entities.add(temp);
                SelectedObjects.add(temp);
                temp.setDebugView(true);
            }
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.PLUS) || Gdx.input.isKeyJustPressed(Input.Keys.EQUALS) && UIStage.getKeyboardFocus() == null) {
            if (!OverHud)
                gsm.setWorldScale(gsm.Scale + 1);
            if (GameStateManager.Debug)
                System.out.println(gsm.Scale);
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.MINUS) && UIStage.getKeyboardFocus() == null) {
            if (!OverHud) {
                if (gsm.Scale - 1 > 0)
                    gsm.setWorldScale(gsm.Scale - 1);
            }
            if (GameStateManager.Debug)
                System.out.println(gsm.Scale);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.FORWARD_DEL) || Gdx.input.isKeyJustPressed(Input.Keys.DEL) && UIStage.getKeyboardFocus() == null) {
            if (!OverHud) {
                for (int i = 0; i < SelectedObjects.size(); i++) {
                    Entities.remove(SelectedObjects.get(i));
                }
                SelectedObjects.clear();
            }
        }

        if (Gdx.input.isTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT)) { //KeyHit
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);

            if (!OverHud) {
                if (selected.equals(selection.Ground)) {
                    if (Fill) {
                        if (Erasing) {
                            tempshitgiggle.fillGroundArea(((int) pos.x / 16), ((int) pos.y / 16), -1);
                        } else {
                            tempshitgiggle.fillGroundArea(((int) pos.x / 16), ((int) pos.y / 16), TileIDSelected);
                        }
                    } else {
                        if (!Erasing) {
                            tempshitgiggle.setGroundCell(((int) pos.x / 16), ((int) pos.y / 16), TileIDSelected, BrushSize);
                        } else {
                            tempshitgiggle.setGroundCell(((int) pos.x / 16), ((int) pos.y / 16), -1, BrushSize);
                        }
                    }
                } else if (selected.equals(selection.Forground)) {
                    if (Fill) {
                        if (Erasing) {
                            tempshitgiggle.fillForegroundArea(((int) pos.x / 16), ((int) pos.y / 16), -1);
                        } else {
                            tempshitgiggle.fillForegroundArea(((int) pos.x / 16), ((int) pos.y / 16), TileIDSelected);
                        }
                    } else {
                        if (!Erasing) {
                            tempshitgiggle.setForegroundCell(((int) pos.x / 16), ((int) pos.y / 16), TileIDSelected, BrushSize);
                        } else {
                            tempshitgiggle.setForegroundCell(((int) pos.x / 16), ((int) pos.y / 16), -1, BrushSize);
                        }
                    }
                } else if (selected.equals(selection.Collision)) {
                    if (Fill) {

                    } else {
                        if (!Erasing) {
                            tempshitgiggle.setCollision(((int) pos.x / 16), ((int) pos.y / 16), BrushSize);
                        } else {
                            tempshitgiggle.ClearCollision(((int) pos.x / 16), ((int) pos.y / 16), BrushSize);
                        }
                    }
                } else if (selected.equals(selection.Object)) {

                    if (Prefab != null && Gdx.input.justTouched()) {
                        Vector3 pos312 = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                        camera.unproject(pos312);

                        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                            Prefab.setPosition(((int) pos312.x / 16) * 16, ((int) pos312.y / 16) * 16, 0);
                        } else {
                            Prefab.setPosition((int)pos312.x - (Prefab.getSize().x / 2), (int)pos312.y - (Prefab.getSize().y / 2), 0);
                        }
                        Entities.add(Prefab.CreateNew());
                    } else if (Prefab == null) {
                        if (SelectedObjects.size() > 0) {

                            float LowestX, LowestY, HighestX, HighestY;

                            LowestX = SelectedObjects.get(0).getPosition().x;
                            LowestY = SelectedObjects.get(0).getPosition().y;
                            HighestX = SelectedObjects.get(0).getHitbox().max.x + SelectedObjects.get(0).getHitboxOffset().x;
                            HighestY = SelectedObjects.get(0).getHitbox().max.y + SelectedObjects.get(0).getHitboxOffset().y;

                            for (int i = 1; i < SelectedObjects.size(); i++) {

                                if (LowestX > SelectedObjects.get(i).getPosition().x) {
                                    LowestX = SelectedObjects.get(i).getPosition().x;
                                } else if (HighestX < (SelectedObjects.get(i).getPosition().x + SelectedObjects.get(i).getSize().x)) {
                                    HighestX = SelectedObjects.get(i).getHitbox().max.x + SelectedObjects.get(i).getHitboxOffset().x;
                                }

                                if (LowestY > SelectedObjects.get(i).getPosition().y) {
                                    LowestY = SelectedObjects.get(i).getPosition().y;
                                } else if (HighestY < (SelectedObjects.get(i).getPosition().y + SelectedObjects.get(i).getSize().y)) {
                                    HighestY = SelectedObjects.get(i).getHitbox().max.y + SelectedObjects.get(i).getHitboxOffset().y;
                                }
                            }

                            BoundingBox PrismPla = new BoundingBox(new Vector3(LowestX, LowestY, 2), new Vector3(HighestX, HighestY, 4));

                            if (PrismPla.contains(new Vector3(pos.x, pos.y, 2)) && SelectedArea == null) {
                                //This is where your gonna move them around
                                if (!DraggingObject) {
                                    if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                                        draggingOffset[0] = (int) pos.x;
                                        draggingOffset[1] = (int) pos.y;
                                    } else {
                                        draggingOffset[0] = (int) pos.x;
                                        draggingOffset[1] = (int) pos.y;
                                    }
                                }
                                DraggingObject = true;

                            }
                        }

                        if (DraggingObject) {
                            for (int i = 0; i < SelectedObjects.size(); i++) {

                                Vector3 tempPos = SelectedObjects.get(i).getPosition();

                                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
                                    tempPos.x += (draggingOffset[0] - (int) pos.x) * -1;
                                    tempPos.y += (draggingOffset[1] - (int) pos.y) * -1;

                                    SelectedObjects.get(i).setPosition(((int)tempPos.x/16)*16, ((int)tempPos.y/16)*16, SelectedObjects.get(i).getPosition().z);
                                } else {
                                    tempPos.x += (draggingOffset[0] - (int) pos.x) * -1;
                                    tempPos.y += (draggingOffset[1] - (int) pos.y) * -1;

                                    SelectedObjects.get(i).setPosition(tempPos.x, tempPos.y, SelectedObjects.get(i).getPosition().z);
                                }
                            /*
                            if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                                float temptempx = pos.x - draggingOffset[0];
                                float temptempy = pos.y - draggingOffset[1];
                                SelectedObjects.get(i).setPosition(((int)(SelectedObjects.get(i).getPosition().x + temptempx/16))*16, ((int)(SelectedObjects.get(i).getPosition().y + temptempy/16))*16, SelectedObjects.get(i).getPosition().z);
                            } else {
                                SelectedObjects.get(i).setPosition(pos.x, pos.y, SelectedObjects.get(i).getPosition().z);
                            }
                            */

                                HiddenButtonTriggeresLoading.init(0,0);
                            }
                            draggingOffset[0] = (int) pos.x;
                            draggingOffset[1] = (int) pos.y;
                        }

                        if (SelectionDragging && !DraggingObject) {

                            if (SelectedArea == null) {
                                SelectedArea = new Vector2[]{new Vector2(Gdx.input.getX(), Gdx.input.getY()), new Vector2(0, 0)};
                            }

                            if (SelectedArea != null) {
                                SelectedArea[1].set(Gdx.input.getX(), Gdx.input.getY());
                            }
                        } else {
                            SelectedArea = null;
                        }
                    }
                } else if (selected.equals(selection.None)) {

                }
            }

        } else {
            DraggingObject = false;
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) { //KeyHit
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);

            if (selected.equals(selection.Ground)) {
                TileIDSelected = tempshitgiggle.getGround()[(int)pos.x/16][(int)pos.y/16];
            } else if (selected.equals(selection.Forground)) {
                TileIDSelected = tempshitgiggle.getForeground()[(int)pos.x/16][(int)pos.y/16];
            }
        }

        if (Gdx.input.isButtonPressed(Input.Buttons.MIDDLE)) {
            Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
            camera.unproject(pos);
            if (Dragging == false) {
                StartDrag = pos;
                Dragging = true;
            }

            CameraFocusPointEdit.setPosition(camera.position);

            CameraFocusPointEdit.setPosition(CameraFocusPointEdit.getPosition().x + Common.roundUp(StartDrag.x - pos.x), CameraFocusPointEdit.getPosition().y + Common.roundUp(StartDrag.y - pos.y), CameraFocusPointEdit.getPosition().z);
        } else {
            Dragging = false;
        }

        Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
        camera.unproject(pos);
        updategsmValues(gsm, pos);

        if (!gsm.UI.Visible) {
            Gdx.input.setInputProcessor(UIStage);
            UIStage.getViewport().setCamera(GuiCam);
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if (UIStage.getKeyboardFocus() != null) {
                UIStage.setKeyboardFocus(null);
            } else if (Prefab != null) {
                if (!OverHud) {
                    if (selected.equals(selection.Object)) {
                        Prefab = null;
                    }
                }
            }else {
                if (gsm.UI.Visible) {
                    gsm.UI.setVisable(!gsm.UI.Visible);
                    UIStage.setViewport(new FitViewport(GameStateManager.UIWidth, GameStateManager.UIHeight));
                    Gdx.input.setInputProcessor(UIStage);
                    UIStage.getViewport().setCamera(GuiCam);
                } else if (!gsm.UI.Visible) {
                    gsm.UI.setState(UI_state.InGameHome);
                    gsm.UI.setVisable(gsm.UI.Visible);
                    Gdx.input.setInputProcessor(gsm.UI.stage);
                }
            }
        }

    }

    public void reSize(SpriteBatch g, int H, int W) {

        //System.out.println("Resized");

        Vector3 temppos = camera.position;

        camera = new OrthographicCamera();
        GuiCam = new OrthographicCamera();
        camera.setToOrtho(false, GameStateManager.WorldWidth, GameStateManager.WorldHeight);
        camera.position.set(temppos);
        GuiCam.setToOrtho(false, GameStateManager.UIWidth, GameStateManager.UIHeight);
        shaker = new ScreenShakeCameraController(camera);

        UISetup();
    }

    public void UISetup() {
        UIStage = new Stage(new FitViewport(GameStateManager.UIWidth, GameStateManager.UIHeight)) {
            @Override
            public boolean touchUp(int screenX, int screenY, int pointer, int button) {
                if (SelectionDragging) {
                    if (OverHud || SelectedArea == null) {
                        return super.touchUp(screenX, screenY, pointer, button);
                    }
                    Vector3 PosStart = new Vector3(SelectedArea[0].x, SelectedArea[0].y, 0);
                    Vector3 PosEND = new Vector3(SelectedArea[1].x, SelectedArea[1].y, 0);
                    camera.unproject(PosStart);
                    camera.unproject(PosEND);

                    BoundingBox tempSelection = new BoundingBox(PosStart, PosEND);

                    if (Gdx.input.isKeyPressed(Input.Keys.CONTROL_LEFT)) {
                        for (int i = 0; i < Entities.size(); i++) {
                            if (Entities.get(i).getHitbox().intersects(tempSelection)) {
                                SelectedObjects.add(Entities.get(i));
                                Entities.get(i).setDebugView(true);
                            }
                        }
                    } else if (!DraggingObject){
                        for (int j = 0; j < SelectedObjects.size(); j++) {
                            SelectedObjects.get(j).setDebugView(false);
                        }
                        SelectedObjects.clear();

                        for (int i = 0; i < Entities.size(); i++) {
                            if (Entities.get(i).getHitbox().intersects(tempSelection)) {
                                SelectedObjects.add(Entities.get(i));
                                Entities.get(i).setDebugView(true);
                                HiddenButtonTriggeresLoading.init(0,0);
                            }
                        }
                    }
                }

                SelectionDragging = false;
                SelectedArea = null;

                return super.touchUp(screenX, screenY, pointer, button);
            }

            @Override
            public boolean touchDragged(int screenX, int screenY, int pointer) {
                if (Gdx.input.isButtonPressed(Input.Buttons.LEFT) && selected.equals(selection.Object)) {
                    SelectionDragging = true;
                }

                return super.touchDragged(screenX, screenY, pointer);
            }

            @Override
            public boolean scrolled(int amount) {
                return super.scrolled(amount);
            }
        };
        Gdx.input.setInputProcessor(UIStage);
        UIStage.getViewport().setCamera(GuiCam);
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));

        InfoTable = new Table(skin);
        InfoTable.setFillParent(true);
        InfoTable.top().left();

        TextField Savename = new TextField(SaveNameText, skin);

        InfoTable.add(Savename).pad(15).top().left();
        TkTextButton SaveButton = new TkTextButton("Save", skin);
        SaveButton.togglable = false;
        SaveButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                SaveMap(Savename.getText());
            }
        });
        InfoTable.add(SaveButton);

        Table TopRightTable = new Table(skin);
        TopRightTable.setFillParent(true);
        TopRightTable.top().right();
        Button TopRightBoxStuff = new Button(skin, "Blank");
        TopRightBoxStuff.setBackground("Window_green");

        EditorTable = new Table(skin);
        EditorTable.setFillParent(true);
        EditorTable.bottom().right();
        Button BoxStuff = new Button(skin, "Blank") {
            @Override
            public void act(float delta) {
                super.act(delta);
                OverHud = isOver() || TopRightBoxStuff.isOver();
            }
        };
        BoxStuff.setBackground("Window_green");

        TkTextButton Background = new TkTextButton("Background", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (selected.equals(selection.Ground)) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        Background.togglable = false;
        Background.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selected.equals(selection.Ground)) {
                    selected = selection.None;
                } else {
                    selected = selection.Ground;
                    Erasing = false;
                    Fill = false;
                }
            }
        });
        TkTextButton Foreground = new TkTextButton("Foreground", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (selected.equals(selection.Forground)) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        Foreground.togglable = true;
        Foreground.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selected.equals(selection.Forground)) {
                    selected = selection.None;
                } else {
                    selected = selection.Forground;
                    Erasing = false;
                    Fill = false;
                }
            }
        });
        TkTextButton Collision = new TkTextButton("Collision", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (selected.equals(selection.Collision)) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        Collision.togglable = true;
        Collision.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selected.equals(selection.Collision)) {
                    selected = selection.None;
                } else {
                    selected = selection.Collision;
                    Erasing = false;
                    Fill = false;
                }
            }
        });
        TkTextButton Objects = new TkTextButton("Objects", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (selected.equals(selection.Object)) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        Objects.togglable = true;
        Objects.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (selected.equals(selection.Object)) {
                    selected = selection.None;
                } else {
                    selected = selection.Object;
                    Erasing = false;
                    Fill = false;
                }
            }
        });
        TkImageButton FillTool = new TkImageButton(skin, "Fill") {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (Fill) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        FillTool.togglable = true;
        FillTool.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Fill = !Fill;
            }
        });
        TkImageButton Eraser = new TkImageButton(skin, "Eraser") {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (Erasing) {
                    this.setChecked(true);
                } else {
                    this.setChecked(false);
                }
            }
        };
        Eraser.togglable = true;
        Eraser.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                Erasing = !Erasing;
            }
        });
        TkImageButton Hide = new TkImageButton(skin, "Dropdown");
        Hide.togglable = true;
        float YPos = BoxStuff.getY();
        Hide.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                if (Hide.isChecked()) {
                    BoxStuff.setPosition(BoxStuff.getX(), YPos - 105);
                } else {
                    BoxStuff.setPosition(BoxStuff.getX(), YPos);
                }
            }
        });
        Table ButtonHolder = new Table();
        ButtonHolder.add(Background).row();
        ButtonHolder.add(Foreground).row();
        ButtonHolder.add(Collision).row();
        ButtonHolder.add(Objects).row();
        ButtonHolder.add(FillTool).row();
        ButtonHolder.add(Eraser).row();
        EditorTable.add(BoxStuff);

        TopRightBoxStuff.add(ButtonHolder);

        TopRightTable.add(TopRightBoxStuff);

        Table TilesList = new Table(skin);
        TilesList.setName("TilesList");
        Table TilesFGList = new Table(skin);
        TilesFGList.setName("TilesFGList");
        Table CollisionEditor = new Table(skin);
        CollisionEditor.setName("CollisionEditor");

        Table ObjectEditorPrefab = new Table(skin);
        ObjectEditorPrefab.setName("ObjectEditorPrefab");
        Table ObjectEditorConfig = new Table(skin);
        ObjectEditorConfig.setName("ObjectEditorConfig");
        Table ObjectEditorPane = new Table(skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                //Check the type of tileset, and change from background or foreground tiles
                if (SelectedObjects.size() >= 1 && this.getCell(ObjectEditorConfig) == null) {
                    this.clear();
                    this.add(ObjectEditorConfig);
                } else if (SelectedObjects.size() == 0 && this.getCell(ObjectEditorPrefab) == null){
                    this.clear();
                    this.add(ObjectEditorPrefab);
                }
            }
        };
        ObjectEditorPane.setName("ObjectEditor");

        ScrollPane RecipeScroll = new ScrollPane(TilesList, skin) {
            private boolean FG = false;

            @Override
            public void act(float delta) {
                super.act(delta);
                //Check the type of tileset, and change from background or foreground tiles
                if (selected.equals(selection.Ground) && !this.getActor().getName().equals("TilesList")) {
                    EditorTable.setVisible(true);
                    this.setActor(TilesList);
                } else if (selected.equals(selection.Forground) && !this.getActor().getName().equals("TilesFGList")) {
                    EditorTable.setVisible(true);
                    this.setActor(TilesFGList);
                } else if (selected.equals(selection.Collision) && !this.getActor().getName().equals("CollisionEditor")) {
                    EditorTable.setVisible(false);
                    this.setActor(CollisionEditor);
                } else if (selected.equals(selection.Object) && !this.getActor().getName().equals("ObjectEditor")) {
                    EditorTable.setVisible(true);
                    this.setActor(ObjectEditorPane);
                } else if (selected.equals(selection.None)) {
                    EditorTable.setVisible(false);
                    this.getActor().setName("None");
                }
            }
        };
        RecipeScroll.setupOverscroll(5, 50f, 100f);
        RecipeScroll.addListener(new ClickListener() {
            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                super.enter(event, x, y, pointer, fromActor);
                UIStage.setScrollFocus(RecipeScroll);
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                super.exit(event, x, y, pointer, toActor);
                UIStage.setScrollFocus(null);
            }
        });

        //Ground stuff
        for (int i = 1; i < tempshitgiggle.Tileset.getTilesSize() + 1; i++) {
            int tempi = i - 1;
            TextureRegion frame = tempshitgiggle.Tileset.getTile(i - 1).getFrame();
            ImageButton tempimage = new ImageButton(new TextureRegionDrawable(frame)) {
                int MYID = tempi;

                @Override
                public void act(float delta) {
                    super.act(delta);
                    if (TileIDSelected == MYID) {
                        this.setDebug(true);
                        this.getImage().setColor(Color.ORANGE);
                    } else {
                        this.setDebug(true);
                        this.getImage().setColor(Color.WHITE);
                    }
                }

            };
            tempimage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    TileIDSelected = tempi;
                }
            });
            TilesList.add(tempimage);
            if (i % tempshitgiggle.TilesetWidth == 0) {
                TilesList.row();
            }
        }

        //Foreground stuff
        for (int i = 1; i < tempshitgiggle.Tileset.getTilesSize() + 1; i++) {

            int tempi = i - 1;
            TextureRegion frame = tempshitgiggle.Tileset.getTile(i - 1).getFrame();
            ImageButton tempimage = new ImageButton(new TextureRegionDrawable(frame)) {
                int MYID = tempi;

                @Override
                public void act(float delta) {
                    super.act(delta);
                    if (TileIDSelected == MYID) {
                        this.setDebug(true);
                        this.getImage().setColor(Color.ORANGE);
                    } else {
                        this.setDebug(true);
                        this.getImage().setColor(Color.WHITE);
                    }
                }
            };
            tempimage.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    TileIDSelected = tempi;
                }
            });
            TilesFGList.add(tempimage);
            if (i % tempshitgiggle.TilesetWidth == 0) {
                TilesFGList.row();
            }
        }

        //Object Prefab Stuff
        if (Gdx.files.internal("Saves/" + gsm.SaveSelected + ".ecube").exists()) {
            JsonParser jsonReaderthing = new JsonParser();
            JsonObject MapObject = jsonReaderthing.parse(Gdx.files.internal("Saves/" + gsm.SaveSelected + ".ecube").readString()).getAsJsonObject();
            JsonArray MapPrefabs = MapObject.getAsJsonArray("Objects");
            //System.out.println("Size: " + MapPrefabs.size());

            for (int i = 1; i < MapPrefabs.size()+1; i++) {

                int tempi = i - 1;
                JsonObject tempObject = MapPrefabs.get(tempi).getAsJsonObject();
                TextureAtlas.AtlasRegion Image;
                if (tempObject.get("TexLocation").getAsString().equals(""))
                    Image = Render.getTexture("trans");
                else if (tempObject.get("TexLocation").getAsString().contains(".png")) {
                    Texture tempimage = new Texture(Gdx.files.internal(tempObject.get("TexLocation").getAsString()));
                    Image = new TextureAtlas.AtlasRegion(tempimage, 0, 0, tempimage.getWidth(), tempimage.getHeight());
                }
                else {
                    Image = Render.getTexture(tempObject.get("TexLocation").getAsString());
                }

                ImageButton tempimage = new ImageButton(new TextureRegionDrawable(new TextureRegion(Image)));
                tempimage.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {

                        String Name = tempObject.get("Name").getAsString();
                        String Description = tempObject.get("Description").getAsString();
                        int W = tempObject.get("Width").getAsInt();
                        int H = tempObject.get("Height").getAsInt();
                        int D = tempObject.get("Depth").getAsInt();
                        int OffsetX = tempObject.get("WidthOffset").getAsInt();
                        int OffsetY = tempObject.get("HeightOffset").getAsInt();
                        int OffsetZ = tempObject.get("DepthOffset").getAsInt();
                        String tempImgLoc = tempObject.get("TexLocation").getAsString();
                        String RawEvents = tempObject.get("Event").getAsString();
                        Trigger.TriggerType TriggerType = Trigger.TriggerType.None;
                        WorldObject.type Type;
                        boolean Collidable = false;
                        if (tempObject.get("Physics").getAsString().equals("Static")) {
                            Type = WorldObject.type.Static;
                            if (tempObject.get("Collidable").getAsBoolean())
                                Collidable = true;
                        } else if (tempObject.get("Physics").getAsString().equals("Dynamic")) {
                            Type = WorldObject.type.Dynamic;
                        } else {
                            Type = WorldObject.type.Static;
                        }

                        if (tempObject.get("TriggerType").getAsString().equals("OnEntry")) {
                            TriggerType = Trigger.TriggerType.OnEntry;
                        } else if (tempObject.get("TriggerType").getAsString().equals("OnTrigger")) {
                            TriggerType = Trigger.TriggerType.OnTrigger;
                        } else if (tempObject.get("TriggerType").getAsString().equals("OnExit")) {
                            TriggerType = Trigger.TriggerType.OnExit;
                        } else if (tempObject.get("TriggerType").getAsString().equals("OnInteract")) {
                            TriggerType = Trigger.TriggerType.OnInteract;
                        } else if (tempObject.get("TriggerType").getAsString().equals("OnClick")) {
                            TriggerType = Trigger.TriggerType.OnClick;
                        } else if (tempObject.get("TriggerType").getAsString().equals("OnAttack")) {
                            TriggerType = Trigger.TriggerType.OnAttack;
                        }

                        Interactable tempObj = new Interactable(0,0,0, new Vector3(W, H, D), Type, Collidable, RawEvents, TriggerType);
                        tempObj.setTexLocation(tempImgLoc);
                        tempObj.Name = Name;
                        tempObj.Description = Description;

                        tempObj.setHitboxOffset(new Vector3(OffsetX, OffsetY, OffsetZ));

                        if (tempObj.Name.equals("Text")) {
                            tempObj = new TextWorldObject(0,0,0, "Text", Render.font);
                        } else if (tempObj.Name.equals("Crop")) {
                            tempObj = new FarmTile(0,0,0, -1);
                            ((FarmTile)tempObj).setCropLife(1);
                        }

                        Prefab = tempObj;
                    }
                });
                Table Container = new Table(skin) {
                    @Override
                    public void act(float delta) {
                        super.act(delta);
                        if (tempimage.isOver()) {
                            this.setBackground("Outline");
                        } else {
                            this.setBackground("Blank");
                        }
                    }
                };
                Container.add(tempimage).pad(-2);
                ObjectEditorPrefab.add(Container);
                if (i % 6 == 0) {
                    ObjectEditorPrefab.row();
                }
            }
        }

        //Object Editor Stuff
        Label NameL = new Label("Name", skin);
        TextField Name = new TextField("", skin);
        Name.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    Interactable temp = (Interactable) SelectedObjects.get(0);
                    temp.Name = Name.getText();
                }
            }
        });
        ObjectEditorConfig.add(NameL);
        ObjectEditorConfig.add(Name).row();
        //
        Label DescriptionL = new Label("Description", skin);
        TextField Description = new TextField("", skin);
        Description.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    Interactable temp = (Interactable) SelectedObjects.get(0);
                    temp.Description = Description.getText();
                }
            }
        });
        ObjectEditorConfig.add(DescriptionL);
        ObjectEditorConfig.add(Description).row();
        //
        Label XL = new Label("X", skin);
        TextField X = new TextField("", skin);
        X.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    if (X.getText().matches("-?\\d+(\\.\\d+)?")) {
                        SelectedObjects.get(0).setPositionX(Integer.parseInt(X.getText()));
                    }
                }
            }
        });
        ObjectEditorConfig.add(XL);
        ObjectEditorConfig.add(X).row();
        //
        Label YL = new Label("Y", skin);
        TextField Y = new TextField("", skin);
        Y.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    if (Y.getText().matches("-?\\d+(\\.\\d+)?")) {
                        SelectedObjects.get(0).setPositionY(Integer.parseInt(Y.getText()));
                    }
                }
            }
        });
        ObjectEditorConfig.add(YL);
        ObjectEditorConfig.add(Y).row();
        //
        Label ZL = new Label("Z", skin);
        TextField Z = new TextField("", skin);
        Z.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    if (Z.getText().matches("-?\\d+(\\.\\d+)?")) {
                        SelectedObjects.get(0).setPositionZ(Integer.parseInt(Z.getText()));
                    }
                }
            }
        });
        ObjectEditorConfig.add(ZL);
        ObjectEditorConfig.add(Z).row();
        //
        Label WidthL = new Label("Width", skin);
        TextField Width = new TextField("", skin);
        Width.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    if (Width.getText().matches("-?\\d+(\\.\\d+)?")) {
                        SelectedObjects.get(0).setSize(new Vector3(Integer.parseInt(Width.getText()), (int) SelectedObjects.get(0).getSize().y, (int) SelectedObjects.get(0).getSize().z));
                    }
                }
            }
        });
        ObjectEditorConfig.add(WidthL);
        ObjectEditorConfig.add(Width).row();
        //
        Label WidthOffsetL = new Label("Width Offset", skin);
        TextField WidthOffset = new TextField("", skin);
        WidthOffset.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    if (WidthOffset.getText().matches("-?\\d+(\\.\\d+)?")) {
                        SelectedObjects.get(0).setHitboxOffset(new Vector3(Integer.parseInt(WidthOffset.getText()), (int) SelectedObjects.get(0).getHitboxOffset().y, (int) SelectedObjects.get(0).getHitboxOffset().z));
                    }
                }
            }
        });
        ObjectEditorConfig.add(WidthOffsetL);
        ObjectEditorConfig.add(WidthOffset).row();
        //
        Label HeightL = new Label("Height", skin);
        TextField Height = new TextField("", skin);
        Height.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    if (Height.getText().matches("-?\\d+(\\.\\d+)?")) {
                        SelectedObjects.get(0).setSize(new Vector3((int) SelectedObjects.get(0).getSize().x, Integer.parseInt(Height.getText()), (int) SelectedObjects.get(0).getSize().z));
                    }
                }
            }
        });
        ObjectEditorConfig.add(HeightL);
        ObjectEditorConfig.add(Height).row();
        //
        Label HeightOffsetL = new Label("Height Offset", skin);
        TextField HeightOffset = new TextField("", skin);
        HeightOffset.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    if (HeightOffset.getText().matches("-?\\d+(\\.\\d+)?")) {
                        SelectedObjects.get(0).setHitboxOffset(new Vector3((int) SelectedObjects.get(0).getHitboxOffset().x, Integer.parseInt(HeightOffset.getText()), (int) SelectedObjects.get(0).getHitboxOffset().z));
                    }
                }
            }
        });
        ObjectEditorConfig.add(HeightOffsetL);
        ObjectEditorConfig.add(HeightOffset).row();
        //
        Label DepthL = new Label("Depth", skin);
        TextField Depth = new TextField("", skin);
        Depth.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    if (Depth.getText().matches("-?\\d+(\\.\\d+)?")) {
                        SelectedObjects.get(0).setSize(new Vector3((int) SelectedObjects.get(0).getSize().x, (int) SelectedObjects.get(0).getSize().y, Integer.parseInt(Depth.getText())));
                    }
                }
            }
        });
        ObjectEditorConfig.add(DepthL);
        ObjectEditorConfig.add(Depth).row();
        //
        Label DepthOffsetL = new Label("Depth Offset", skin);
        TextField DepthOffset = new TextField("", skin);
        DepthOffset.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    if (DepthOffset.getText().matches("-?\\d+(\\.\\d+)?")) {
                        SelectedObjects.get(0).setHitboxOffset(new Vector3((int) SelectedObjects.get(0).getHitboxOffset().x, (int) SelectedObjects.get(0).getHitboxOffset().y, Integer.parseInt(DepthOffset.getText())));
                    }
                }
            }
        });
        ObjectEditorConfig.add(DepthOffsetL);
        ObjectEditorConfig.add(DepthOffset).row();
        //
        Label TextureL = new Label("Texture Path", skin);
        TextField Texture = new TextField("", skin);
        Texture.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    Interactable temp = (Interactable) SelectedObjects.get(0);
                    temp.TexLocation = Texture.getText();
                }
            }
        });
        ObjectEditorConfig.add(TextureL);
        ObjectEditorConfig.add(Texture).row();
        //
        Label PhysicsL = new Label("Physics Type", skin);
        SelectBox Physics = new SelectBox(skin);
        Physics.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    if (Physics.getSelected().equals("Static")) {
                        SelectedObjects.get(0).setState(WorldObject.type.Static);
                    } else if (Physics.getSelected().equals("Dynamic")) {
                        SelectedObjects.get(0).setState(WorldObject.type.Dynamic);
                    }
                }
            }
        });
        Physics.setItems("Static", "Dynamic");
        CheckBox Collidable = new CheckBox("Collidable", skin);
        Collidable.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() == 1) {
                    SelectedObjects.get(0).setCollidable(Collidable.isChecked());
                }
            }
        });
        Table StupidFittingThing = new Table();
        StupidFittingThing.add(Physics);
        StupidFittingThing.add(Collidable);
        ObjectEditorConfig.add(PhysicsL);
        ObjectEditorConfig.add(StupidFittingThing).fillX().row();
        //
        SelectBox TriggerTypeChoice = new SelectBox(skin);
        TriggerTypeChoice.setItems("OnEntry", "OnTrigger", "OnExit", "OnInteract", "OnClick", "OnAttack");
        TriggerTypeChoice.setSelected("OnInteract");
        TriggerTypeChoice.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {

                Trigger.TriggerType TriggerType = Trigger.TriggerType.None;

                if (TriggerTypeChoice.getSelected().equals("OnEntry")) {
                    TriggerType = Trigger.TriggerType.OnEntry;
                } else if (TriggerTypeChoice.getSelected().equals("OnTrigger")) {
                    TriggerType = Trigger.TriggerType.OnTrigger;
                } else if (TriggerTypeChoice.getSelected().equals("OnExit")) {
                    TriggerType = Trigger.TriggerType.OnExit;
                } else if (TriggerTypeChoice.getSelected().equals("OnInteract")) {
                    TriggerType = Trigger.TriggerType.OnInteract;
                } else if (TriggerTypeChoice.getSelected().equals("OnAttack")) {
                    TriggerType = Trigger.TriggerType.OnAttack;
                }  else if (TriggerTypeChoice.getSelected().equals("OnClick")) {
                    TriggerType = Trigger.TriggerType.OnClick;
                }

                if (SelectedObjects.size() > 0) {
                    if (SelectedObjects.get(0) instanceof Interactable) {
                        ((Interactable)SelectedObjects.get(0)).setActivationType(TriggerType);
                    }
                }
            }
        });
        Label EventL = new Label("Script", skin);
        TextArea EventCode = new TextArea("", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (SelectedObjects.size() > 0 && SelectedObjects.get(0) instanceof Interactable) {
                    if (((Interactable)SelectedObjects.get(0)).getActivationType().equals(Trigger.TriggerType.None)) {
                        setDisabled(true);
                    } else {
                        setDisabled(false);
                    }
                }
            }
        };
        EventCode.addListener(new ChangeListener() {
            @Override
            public void changed(ChangeEvent event, Actor actor) {
                if (SelectedObjects.size() > 0) {
                    if (SelectedObjects.get(0) instanceof Interactable) {
                        ((Interactable)SelectedObjects.get(0)).setRawCommands(EventCode.getText());
                    }
                }
            }
        });
        ObjectEditorConfig.add(TriggerTypeChoice).fillX().row();
        ObjectEditorConfig.add(EventL);
        ObjectEditorConfig.add(EventCode).height(64).row();
        TkTextButton DuplicateOrCreate = new TkTextButton("", skin) {
            @Override
            public void act(float delta) {
                super.act(delta);
                if (SelectedObjects.size() == 1) {
                    setText("Duplicate");
                } else {
                    setText("Create New");
                }
            }
        };
        DuplicateOrCreate.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                WorldObject.type Type;
                if (Physics.getSelected().equals("Static")) {
                    Type = WorldObject.type.Static;
                } else if (Physics.getSelected().equals("Dynamic")) {
                    Type = WorldObject.type.Dynamic;
                } else {
                    Type = WorldObject.type.Static;
                }

                Interactable tempObj = new Interactable(Integer.parseInt(X.getText()), Integer.parseInt(Y.getText()), Integer.parseInt(Z.getText()), new Vector3(Integer.parseInt(Width.getText()), Integer.parseInt(Height.getText()), Integer.parseInt(Depth.getText())), Type, Collision.isChecked());
                tempObj.setTexLocation(Texture.getText());
                tempObj.Name = Name.getText();
                tempObj.Description = Description.getText();

                tempObj.setHitboxOffset(new Vector3(Integer.parseInt(WidthOffset.getText()), Integer.parseInt(HeightOffset.getText()), Integer.parseInt(DepthOffset.getText())));

                Entities.add(tempObj);
                tempObj.setDebugView(true);
                SelectedObjects.add(tempObj);
            }
        });
        TkTextButton Delete = new TkTextButton("Delete", skin);
        Delete.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                for (int i = 0; i < SelectedObjects.size(); i++) {
                    Entities.remove(SelectedObjects.get(i));
                }
                SelectedObjects.clear();
            }
        });
        ObjectEditorConfig.add(DuplicateOrCreate);
        ObjectEditorConfig.add(Delete).row();

        ObjectEditorPane.add(ObjectEditorPrefab);

        HiddenButtonTriggeresLoading = new WorldObject() {

            @Override
            public void init(int tempw, int temph) {
                if (SelectedObjects.size() == 1) {
                    Interactable temp = (Interactable) SelectedObjects.get(0);
                    Name.setText(temp.Name);
                    Description.setText(temp.Description);
                    X.setText("" + (int) temp.getPosition().x);
                    Y.setText("" + (int) temp.getPosition().y);
                    Z.setText("" + (int) temp.getPosition().z);
                    Width.setText("" + (int) temp.getSize().x);
                    WidthOffset.setText("" + (int) temp.getHitboxOffset().x);
                    Height.setText("" + (int) temp.getSize().y);
                    HeightOffset.setText("" + (int) temp.getHitboxOffset().y);
                    Depth.setText("" + (int) temp.getSize().z);
                    DepthOffset.setText("" + (int) temp.getHitboxOffset().z);
                    Texture.setText(temp.getTexLocation());
                    Physics.setSelected(temp.getState());
                    Collidable.setChecked(temp.isCollidable());

                    TriggerTypeChoice.setSelected(temp.getActivationType().name().toString());
                    EventCode.setText(temp.getRawCommands());
                } else if (SelectedObjects.size() == 0) {
                    Name.setText("");
                    Description.setText("");
                    X.setText("");
                    Y.setText("");
                    Z.setText("");
                    Width.setText("");
                    WidthOffset.setText("");
                    Height.setText("");
                    HeightOffset.setText("");
                    Depth.setText("");
                    DepthOffset.setText("");
                    Texture.setText("");
                    Physics.setSelected("Static");
                    Collidable.setChecked(false);
                    TriggerTypeChoice.setSelected("None");
                    EventCode.setText("None");
                } else if (SelectedObjects.size() > 1) {
                    Interactable temp = (Interactable) SelectedObjects.get(0);
                    Name.setText("Several Objects Selected");
                    Description.setText("At the moment, editing of only one object at a time is supported!");
                    X.setText("");
                    Y.setText("");
                    Z.setText("");
                    Width.setText("");
                    WidthOffset.setText("");
                    Height.setText("");
                    HeightOffset.setText("");
                    Depth.setText("");
                    DepthOffset.setText("");
                    Texture.setText("");
                    Physics.setSelected("Static");
                    Collidable.setChecked(temp.isCollidable());
                    TriggerTypeChoice.setSelected("None");
                    EventCode.setText("None");
                }

            }

            @Override
            public void update(float delta, GameState G) {

            }

            @Override
            public void draw(SpriteBatch batch, float Time) {

            }
        };
        BoxStuff.add(RecipeScroll).height(100).padTop(5);

        Table Title = new Table(skin);
        //Title.add(new Label("Objects", skin));
        Title.add(Hide);
        Title.setBackground("Window_red");

        BoxStuff.row();
        BoxStuff.pack();
        EditorTable.pack();
        BoxStuff.add(Title).right().padTop(-EditorTable.getHeight()*2).row();

        UIStage.addActor(InfoTable);
        UIStage.addActor(TopRightTable);
        UIStage.addActor(EditorTable);
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
        } else {
            FocalPoint.x = FocalPoint.x / totalFocusPoints;
        }

        if (FocalPoint.y - cam.viewportHeight / 2 <= MinY) {
            FocalPoint.y = MinY + cam.viewportHeight / 2;
        } else if (FocalPoint.y + cam.viewportHeight / 2 >= MaxY) {
            FocalPoint.y = MaxY - cam.viewportHeight / 2;
        } else {
            FocalPoint.y = FocalPoint.y / totalFocusPoints;
        }

        cam.position.set((int) (FocalPoint.x), (int) (FocalPoint.y), 0);

        cam.update();
    }

    public void SaveMap(String Savename) {

        double Started = System.nanoTime();

        Path path = Paths.get("Saves", Savename + ".cube");
        ArrayList<String> lines = new ArrayList<String>();
        lines.add(tempshitgiggle.SerializeMap(Entities));

        System.out.println("Serialization Took " + ((System.nanoTime() - Started)/1000000000.0) + " seconds to complete");

        try {
            Files.deleteIfExists(path);
            Files.write(path, lines, Charset.forName("UTF-8"), StandardOpenOption.CREATE);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Saved Map!");
        System.out.println("Took " + ((System.nanoTime() - Started)/1000000000.0) + " seconds to complete");
    }

    @Override
    public void dispose() {
        Collisions.clear();
        Entities.clear();
    }


}