package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.gamestates.GameState;

import static com.thecubecast.reengine.data.GameStateManager.Render;

public class Interactable extends Trigger {

    public TextureAtlas.AtlasRegion Image;

    public String ID = "None";
    public String Name = "";
    public String Description = "";

    public boolean Highlight = false;
    public Color HighlightColor = Color.WHITE;

    public String TexLocation = "";

    public Interactable(int x, int y, int z, Vector3 size, WorldObject.type State, boolean collision) {
        super(x, y, z, size, State, collision, "", TriggerType.None);
        this.setCollidable(collision);
    }

    public Interactable(int x, int y, int z, Vector3 size, WorldObject.type State, boolean collision, String RawEvents, TriggerType TType) {
        super(x, y, z, size, State, collision, RawEvents, TType);
        this.setCollidable(collision);
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {
        super.update(delta, G);
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        if (Highlight) {
            batch.flush();
            //batch.setShader(OutlineShader);
            //setOutlineShaderColor(this.HighlightColor, 0.8f);
            if (Image != null)
                batch.draw(Image, getPosition().x, getPosition().y);
            batch.setShader(null);
        } else {
            if (Image != null)
                batch.draw(Image, getPosition().x, getPosition().y);
        }
    }

    /**
     * This will give you the area of the sprite, instead of the objects hitbox
     *
     * @return the hitbox of the Sprite
     */
    public BoundingBox getImageHitbox() {

        if (Image != null) {
            BoundingBox temp = new BoundingBox(this.getPosition(), new Vector3(Image.getRegionWidth(), Image.getRegionHeight(), 0).add(this.getPosition()));
            return temp;
        } else {
            BoundingBox temp = new BoundingBox(this.getPosition(), new Vector3(getSize().x, getSize().y, 0).add(this.getPosition()));
            return temp;
        }
    }

    public void drawGui(SpriteBatch batch, float Time) {}

    public void Activated() {

    }

    public String getTexLocation() {
        return TexLocation;
    }

    public void setTexLocation(String texLocation) {
        TexLocation = texLocation;
        if (texLocation.equals(""))
            Image = null;
        else if (texLocation.contains(".png")) {
            Texture tempimage = new Texture(Gdx.files.internal(TexLocation));
            Image = new TextureAtlas.AtlasRegion(tempimage, 0, 0, tempimage.getWidth(), tempimage.getHeight());
        }
        else {
            Image = Render.getTexture(TexLocation);
        }

    }

    public Interactable CreateNew() {

        Interactable tempObj = new Interactable((int) this.getPosition().x, (int) this.getPosition().y, (int) this.getPosition().z, this.getSize(), this.getState(), this.isCollidable(), this.RawCommands, this.getActivationType());
        tempObj.setTexLocation(this.getTexLocation());
        tempObj.Name = this.Name;
        tempObj.Description = this.Description;

        tempObj.setHitboxOffset(this.getHitboxOffset());

        return tempObj;
    }
}
