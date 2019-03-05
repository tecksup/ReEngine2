package com.thecubecast.reengine.graphics.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class TkLabel extends Label {

    public String Suffix = "";

    /**
     * time in seconds it takes for a char to appear
     */
    float TextScrollRate = 0.035f;
    CharSequence Original = "";
    private int TextIndex = 0;

    private int tics;

    /**
     * Set to true to enable scrolling
     */
    private boolean Scrolling = false;

    public TkLabel(CharSequence text, Skin skin) {
        super(text, skin);
        Original = text;
    }

    public TkLabel(CharSequence text, Skin skin, String styleName) {
        super(text, skin, styleName);
        Original = text;
    }

    public TkLabel(CharSequence text, Skin skin, String fontName, Color color) {
        super(text, skin, fontName, color);
        Original = text;
    }

    public void setTextScrollRate(float textScrollRate) {
        TextScrollRate = textScrollRate;
    }

    public void setScrolling(boolean scrolling) {
        Scrolling = scrolling;
    }

    public void setText(CharSequence newText) {
        super.setText(newText);
        Original = newText;
        TextIndex = 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
    }

    public void endScroll() {
        TextIndex = Original.length();
    }

    public void restartScroll() {
        TextIndex = 0;
    }

    @Override
    public void act(float delta) {
        tics++;
        if (tics >= Gdx.graphics.getFramesPerSecond() * TextScrollRate) {
            if (TextIndex < Original.length())
                TextIndex = TextIndex + 1;
            tics = 0;
        }

        if (Scrolling) {
            String Output = "";

            for (int i = 0; i < Original.length(); i++) {
                if (i < TextIndex) {
                    Output += Original.charAt(i);
                } else
                    break;
            }

            //Output += "" + Original.subSequence(TextIndex, Original.length());

            super.setText(Output + Suffix);
        } else {
            super.setText(Original + Suffix);
        }


        super.act(delta);
    }
}
