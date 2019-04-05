package com.thecubecast.reengine.gamestates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.StretchViewport;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.graphics.scene2d.Dialog;
import com.thecubecast.reengine.graphics.scene2d.TkLabel;

import java.util.ArrayList;
import java.util.List;

public abstract class DialogStateExtention extends GameState {

    //Dialog Vars
    private Skin skin;
    private Stage Guistage;
    private Table table;

    Boolean DialogOpen = false;
    int DialogTics = 0;
    private List<Dialog> DialogCache = new ArrayList<>();
    TkLabel dialogBoxTitle;
    Image dialogBoxFace;
    TypingLabel dialogBoxText;

    public DialogStateExtention(GameStateManager gsm) {
        super(gsm);
    }

    public void setupSkin() {
        skin = new Skin(Gdx.files.internal("Skins/test1/skin.json"));
        skin.getFont("Mecha").getData().markupEnabled = true;
        skin.getFont("Pixel").getData().markupEnabled = true;
    }

    public void MenuInit(int width, int height) {

        setupSkin();

        Guistage = new Stage(new StretchViewport(width, height));
        Gdx.input.setInputProcessor(Guistage);

        table = new Table();
        table.setFillParent(true);
        Guistage.addActor(table);

        Table dialogBox = new Table(skin);
        dialogBox.setBackground("Table_dialog");
        Table dialogBoxTitleT = new Table();
        Table dialogBoxTextT = new Table();

        dialogBox.add(dialogBoxTitleT).left().expandX().padLeft(3).padTop(-2).row();
        dialogBox.add(dialogBoxTextT).expandX().left().padLeft(3);

        dialogBoxTextT.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                DialogNext();
            }

            @Override
            public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                //dialogBoxIcon.setDrawable(skin, "A_icon_alt");
            }

            @Override
            public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                //dialogBoxIcon.setDrawable(skin, "A_icon");
            }
        });

        dialogBoxTitle = new TkLabel("", skin);
        dialogBoxTitle.setAlignment(Align.left);
        dialogBoxFace = new Image(GameStateManager.Render.getTexture("face"));
        dialogBoxFace.setSize(20, 20);
        dialogBoxText = new TypingLabel("{COLOR=GREEN}Hello", skin);
        dialogBoxText.setAlignment(Align.left);
        dialogBoxText.setWrap(true);

        dialogBoxTitleT.add(dialogBoxTitle).left().fillX();
        dialogBoxTextT.add(dialogBoxFace).expandX().left().padRight(5);
        dialogBoxTextT.add(dialogBoxText).expandX().center();

        table.add(dialogBox).bottom().fillX().expand();

    }

    public void AddDialog(Dialog object) {
        DialogCache.add(object);

        DialogOpen = true;
        UpdateDialogBox();
    }

    public void AddDialog(String Speaker, String Conversation) {
        Dialog temp = new Dialog(Speaker, Conversation) {
            public void exit() {
            }
        };

        DialogCache.add(temp);
        DialogOpen = true;
        UpdateDialogBox();
    }

    public void AddDialog(String Speaker, String Conversation, int Cooldown) {
        Dialog temp = new Dialog(Speaker, Conversation) {
            public void exit() {
            }
        };

        temp.setCooldown(Cooldown);

        DialogCache.add(temp);
        DialogOpen = true;
        UpdateDialogBox();
    }

    public void AddDialog(String Speaker, String Conversation, int Cooldown, Texture texture) {
        Dialog temp = new Dialog(Speaker, texture, Conversation) {
            public void exit() {
            }
        };

        temp.setCooldown(Cooldown);

        DialogCache.add(temp);
        DialogOpen = true;
        UpdateDialogBox();
    }

    public void UpdateDialogBox() {
        DialogTics = 0;
        if (DialogCache.size() > 0) {
            dialogBoxTitle.setText(DialogCache.get(0).getSpeaker());
            dialogBoxText.restart(DialogCache.get(0).getText());
            dialogBoxFace.setDrawable(new TextureRegionDrawable(new TextureRegion(DialogCache.get(0).getSpeakerImage())));
        }
    }

    public void DialogNext() {
        if (DialogOpen) {
            if (DialogTics > DialogCache.get(0).getCooldown()) {
                if (DialogCache.size() > 0) {
                    DialogCache.remove(0).exit();
                    //REPLACE THIS LINE WITH DIALOG CLOSE SOUND
                    if (DialogCache.size() == 0) {
                        DialogOpen = false;
                    }
                    UpdateDialogBox();
                } else {
                    DialogCache.remove(0).exit();
                    DialogOpen = false;
                }
            } else {
                dialogBoxText.skipToTheEnd();
            }
        }
    }

    public void MenuDraw(SpriteBatch batch, float Delta) {
        if (DialogTics < 1000)
            DialogTics++;
        Guistage.act(0.015f);

        table.setVisible(DialogOpen);

        Guistage.getRoot().draw(batch, 1);
    }

}
