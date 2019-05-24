package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.gamestates.GameState;

public class TextWorldObject extends Interactable {

    Color color;
    BitmapFont Font;

    public TextWorldObject(int x, int y, int z, String Text, String FontLoc) {
        super(x,y,z, new Vector3(1,1,1), type.Static, false);
        this.Description = Text;
        this.Name = "Text";
        Font = new BitmapFont(Gdx.files.internal( FontLoc + ".fnt"), new TextureRegion(new Texture(Gdx.files.internal(FontLoc + ".png"))));
        color = Color.WHITE;
    }

    public TextWorldObject(int x, int y, int z, String Text, BitmapFont Font) {
        super(x,y,z, new Vector3(1,1,1), type.Static, false);
        this.Description = Text;
        this.Name = "Text";
        this.Font = Font;
        color = Color.WHITE;
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {
        //TODO: fix this and make the width correct
        this.setSize(new Vector3(Font.getData().getGlyph(Description.charAt(0)).width * Description.length(), -Font.getLineHeight(), 1));
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        Font.setColor(color);
        Font.draw(batch, Description, (int) this.getPosition().x, (int) this.getPosition().y);
        Font.setColor(Color.WHITE);
    }

    @Override
    public Interactable CreateNew() {
        TextWorldObject tempObj = new TextWorldObject((int) this.getPosition().x, (int) this.getPosition().y, (int) this.getPosition().z, this.Description, Font);
        tempObj.setTexLocation(this.getTexLocation());

        return tempObj;
    }
}
