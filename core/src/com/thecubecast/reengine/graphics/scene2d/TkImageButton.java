package com.thecubecast.reengine.graphics.scene2d;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

import static com.thecubecast.reengine.data.GameStateManager.AudioM;

public class TkImageButton extends ImageButton {

    boolean Selected;
    public boolean togglable = false;

    public TkImageButton(Skin skin, String Stylename) {
        super(skin, Stylename);
        custom();
    }

    private void custom() {
        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Play a click sound
                AudioM.play("Click");
            }
        });
    }

    @Override
    protected void drawBackground(Batch batch, float parentAlpha, float x, float y) {
        if (Selected) {
            setBackground(getStyle().over);
        }

        super.drawBackground(batch, parentAlpha, x, y);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        if (!togglable) {
            super.setChecked(false);
        }
    }
}
