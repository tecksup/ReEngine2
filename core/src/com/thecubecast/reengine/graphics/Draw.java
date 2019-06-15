package com.thecubecast.reengine.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ObjectSet;

import java.util.HashMap;

public class Draw {

    public AssetManager manager = new AssetManager();
    public TextureAtlas textureAtlas;

    public String textureAtlasFilename = "textureAtlas/atlas.atlas";

    public static ShaderProgram OutlineShader;
    public static ShaderProgram FillColorShader;

    //Animation Variables
    public Animation<TextureRegion> LoadingAnimation; // Must declare frame type (TextureRegion)
    Texture LoadingSheet;

    public BitmapFont font;

    public void LoadShaders() {
        String OutlineShadervertexShader = Gdx.files.internal("Shaders/Outline/vertex.glsl").readString();
        String OutlineShaderfragmentShader = Gdx.files.internal("Shaders/Outline/fragment.glsl").readString();
        OutlineShader = new ShaderProgram(OutlineShadervertexShader, OutlineShaderfragmentShader);

        String FillColorShadervertexShader = Gdx.files.internal("Shaders/Fill_Color/vertex.glsl").readString();
        String FillColorShaderfragmentShader = Gdx.files.internal("Shaders/Fill_Color/fragment.glsl").readString();
        FillColorShader = new ShaderProgram(FillColorShadervertexShader, FillColorShaderfragmentShader);

    }

    public void Init() {

        LoadShaders();

        TextureRegion temptemp =  new TextureRegion(new Texture(Gdx.files.internal("Fonts/Pixel.png")));
        temptemp.getTexture().setFilter(Texture.TextureFilter.Nearest, Texture.TextureFilter.Nearest);
        font = new BitmapFont(Gdx.files.internal("Fonts/Pixel.fnt"),temptemp);

        font.getData().markupEnabled = true;

        // load the texture atlas
        manager.load(textureAtlasFilename, TextureAtlas.class);

        // Initialize the Animation with the frame interval and array of frames
        LoadingAnimation = new Animation<TextureRegion>(0.1f, loadAnim(LoadingSheet, "cube_loading_sprite.png", 4, 1));
    }

    /** Retrieve the loaded texture atlas */
    public void retrieveTextureAtlas() {
        textureAtlas = manager.get(textureAtlasFilename, TextureAtlas.class);
    }

    public static TextureRegion[] loadAnim(Texture TexSheet, String FileLocation, int Cols, int Rows) {
        // Load the sprite sheet as a Texture
        TexSheet = new Texture(Gdx.files.internal(FileLocation));

        // Use the split utility method to create a 2D array of TextureRegions. This is
        // possible because this sprite sheet contains frames of equal size and they are
        // all aligned.
        TextureRegion[][] tmp = TextureRegion.split(TexSheet,
                TexSheet.getWidth() / Cols,
                TexSheet.getHeight() / Rows);

        // Place the regions into a 1D array in the correct order, starting from the top
        // left, going across first. The Animation constructor requires a 1D array.
        TextureRegion[] walkFrames = new TextureRegion[Cols * Rows];
        int index = 0;
        for (int i = 0; i < Rows; i++) {
            for (int j = 0; j < Cols; j++) {
                walkFrames[index++] = tmp[i][j];
            }
        }

        return walkFrames;
    }

    public void DrawAnimatedTile(SpriteBatch buffer, Animation<TextureRegion> animation_, int x, int y, float stateTime) {
        // Get current frame of animation for the current stateTime
        TextureRegion currentFrame = animation_.getKeyFrame(stateTime, true);

        buffer.draw(currentFrame, x, y);
    }

    public void GUIDrawText(SpriteBatch buffer, int PosX, int PosY, String Text, Color color) {
        font.setColor(color);
        font.draw(buffer, Text, PosX, PosY);
        font.setColor(Color.WHITE);
    }

    public ShapeRenderer debugRenderer = new ShapeRenderer();

    public void DrawDebugLine(Vector2 start, Vector2 end, int lineWidth, Color color, Matrix4 projectionMatrix) {
        Gdx.gl.glLineWidth(lineWidth);
        debugRenderer.setProjectionMatrix(projectionMatrix);
        debugRenderer.begin(ShapeRenderer.ShapeType.Line);
        debugRenderer.setColor(color);
        debugRenderer.line(start, end);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    public void DrawDebugPoint(Vector2 start, int lineWidth, Color color, Matrix4 projectionMatrix) {
        Gdx.gl.glLineWidth(lineWidth);
        debugRenderer.begin(ShapeRenderer.ShapeType.Filled);
        debugRenderer.circle(start.x, start.y, lineWidth);
        debugRenderer.setColor(color);
        debugRenderer.end();
        Gdx.gl.glLineWidth(1);
    }

    private HashMap<String, TextureAtlas.AtlasRegion> cachedTextures = new HashMap<>();
    private HashMap<String, Array<TextureAtlas.AtlasRegion>> cachedTextureGroups = new HashMap<>();

    public TextureAtlas.AtlasRegion getTexture(String name) {
        TextureAtlas.AtlasRegion texture = cachedTextures.get(name);

        if (texture != null)
            return texture;
        else {
            texture = textureAtlas.findRegion(name);
            if (texture == null)
                throw new RuntimeException("Region not found on atlas, name: "+ name + "\nAll regions: " + getRegionsNames());

            cachedTextures.put(name, texture);
            return texture;
        }
    }

    public Array<TextureAtlas.AtlasRegion> getTextures(String name) {
        Array<TextureAtlas.AtlasRegion> textures = cachedTextureGroups.get(name);
        if (textures != null)
            return textures;
        else {
            textures = textureAtlas.findRegions(name);
            if (textures.size == 0)
                throw new RuntimeException("Group of regions ( .findRegions() ) not found on atlas, name: " + name);

            cachedTextureGroups.put(name, textures);
            return textures;
        }
    }

    public boolean hasTextures(String name) {
        Array<TextureAtlas.AtlasRegion> textures = cachedTextureGroups.get(name);
        textures = textureAtlas.findRegions(name);
        if (textures.size == 0)
            return false;

        return true;
    }

    private String getRegionsNames() {
        ObjectSet<String> names = new ObjectSet<>();
        for (TextureAtlas.AtlasRegion region : textureAtlas.getRegions()) {
            names.add(region.name);
        }

        return names.toString("; ");
    }

    public static void setOutlineShaderColor(Color outlineColor) {
        OutlineShader.setUniform1fv("outline_Color", new float[]{outlineColor.r, outlineColor.g, outlineColor.b, outlineColor.a}, 0, 4);
    }

    public static void setOutlineShaderColor(Color outlineColor, float Alpha) {
        OutlineShader.setUniform1fv("outline_Color", new float[]{outlineColor.r, outlineColor.g, outlineColor.b, Alpha}, 0, 4);
    }

    public static void setFillColorShaderColor(Color outlineColor) {
        FillColorShader.begin();
        FillColorShader.setUniform4fv("outline_Color", new float[]{outlineColor.r, outlineColor.g, outlineColor.b, outlineColor.a}, 0, 4);
        FillColorShader.end();
    }

    public static void setFillColorShaderColor(Color outlineColor, float Alpha) {
        FillColorShader.begin();
        FillColorShader.setUniform4fv("outline_Color", new float[]{outlineColor.r, outlineColor.g, outlineColor.b, Alpha}, 0, 4);
        FillColorShader.end();
    }

}