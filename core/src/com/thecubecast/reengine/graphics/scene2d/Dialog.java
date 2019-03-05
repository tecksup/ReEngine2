package com.thecubecast.reengine.graphics.scene2d;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import static com.thecubecast.reengine.data.GameStateManager.Render;

public abstract class Dialog {

    private String Speaker;
    private String Text;
    private TextureRegion SpeakerImage;
    private int cooldown = 60;

    public Dialog(String Speaker, String Text) {
        this.Speaker = Speaker;
        this.Text = Text;
        this.SpeakerImage = Render.getTexture("face");
    }

    public Dialog(String Speaker, Texture face, String Text) {
        this.Speaker = Speaker;
        this.Text = Text;
        this.SpeakerImage = Render.getTexture("face");
    }

    public abstract void exit();

    public String getSpeaker() {
        return Speaker;
    }

    public void setSpeaker(String speaker) {
        Speaker = speaker;
    }

    public String getText() {
        return Text;
    }

    public void setText(String text) {
        Text = text;
    }

    public int getCooldown() {
        return cooldown;
    }

    public void setCooldown(int cooldown) {
        this.cooldown = cooldown;
    }

    public TextureRegion getSpeakerImage() {
        return SpeakerImage;
    }

    public void setSpeakerImage(TextureRegion speakerImage) {
        SpeakerImage = speakerImage;
    }
}
