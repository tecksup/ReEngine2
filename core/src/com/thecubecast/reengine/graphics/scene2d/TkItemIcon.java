package com.thecubecast.reengine.graphics.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;

import static com.thecubecast.reengine.data.GameStateManager.ItemPresets;

public class TkItemIcon extends Stack {

    boolean isHovering = false;

    boolean isEquipment = false;

    Table LabelTable;

    Image Icons;
    TypingLabel Quant;

    public TkItemIcon(Skin skin, int itemID) {

        super();

        LabelTable = new Table();

        Icons = new Image();
        this.add(Icons);

        if (itemID >= 0) {
            Icons.setVisible(true);
            Icons.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(ItemPresets.get(itemID).getTexLocation())))));
        } else {
            Icons.setVisible(false);
        }

    }

    public TkItemIcon(Skin skin, int itemID, int Quantity) {

        this(skin, itemID);

        if (Quantity > 99)
            Quant = new TypingLabel("99+", skin);
        else
            Quant = new TypingLabel(Quantity + "", skin);

        Quant.skipToTheEnd();

        LabelTable.add(Quant);
        LabelTable.bottom().right();
        this.add(LabelTable);

    }

    public void reload(int IconID) {
        if (IconID >= 0) {
            Icons.setVisible(true);
            Icons.setDrawable(new TextureRegionDrawable(new TextureRegion(new Texture(Gdx.files.internal(ItemPresets.get(IconID).getTexLocation())))));
        } else {
            Icons.setVisible(false);
        }
    }
}
