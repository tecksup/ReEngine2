package com.thecubecast.reengine.graphics.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.DefaultStateMachine;
import com.badlogic.gdx.ai.fsm.StateMachine;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.ai.msg.Telegraph;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.Item;
import com.thecubecast.reengine.worldobjects.SeedPlayer;
import com.thecubecast.reengine.worldobjects.Triggers.Storage;

public class UIFSM implements Telegraph {

    public SeedPlayer player;
    public Storage StorageOpen;
    public static Item CursorItem;
    public boolean ClickedOutsideInventory = true;
    public Item LastUsedSeedType;

    //-1 if nothing selected, positive numbers including 0 are for actual crafting ids
    public static int InventorySlotSelected = -1;

    public boolean inGame = false;
    public boolean Visible = true;

    protected StateMachine<UIFSM, UI_state> stateMachine;

    protected Skin skin;
    public Stage stage;

    protected GameStateManager gsm;

    public UIFSM(GameStateManager gsm) {


        this.gsm = gsm;

        stage = new Stage(new FitViewport(GameStateManager.UIWidth, GameStateManager.UIHeight));

        Gdx.input.setInputProcessor(stage);

        setupSkin();

        stateMachine = new DefaultStateMachine<UIFSM, UI_state>(this, UI_state.Home);
        stateMachine.getCurrentState().enter(this);
    }

    public void setState(UI_state State) {
        stateMachine.changeState(State);
        setVisable(true);
    }

    public UI_state getState() {
        return stateMachine.getCurrentState();
    }

    public boolean isVisible() {
        return Visible;
    }

    public void setVisable(boolean visable) {
        Visible = visable;
    }

    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
        skin.getFont("Pixel").getData().markupEnabled = true;
    }

    public void Draw() {

        stage.getViewport().update(GameStateManager.UIWidth, GameStateManager.UIHeight, true);
        stage.draw();
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
    }

    public void update() {
        stateMachine.update();
    }

    public void reSize() {
        stage = new Stage(new FitViewport(GameStateManager.UIWidth, GameStateManager.UIHeight));

        Gdx.input.setInputProcessor(stage);

        //stage.getViewport().setCamera(cam);

        setupSkin();

        stateMachine.getCurrentState().enter(this);
    }

    @Override
    public boolean handleMessage(Telegram msg) {
        return false;
    }
}
