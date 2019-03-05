package com.thecubecast.reengine.graphics;


import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.NinePatch;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;

public class ScreenShakeCameraController {

    // how much to shake by
    public float maxXOffset;
    public float maxYOffset;
    public float maxAngleDegrees;

    // how fast to move through the perlin Noise
    public float xOffsetSpeed = 5f;
    public float yOffsetSpeed = 5f;
    public float rotationSpeed = 10f;


    private OrthographicCamera worldCamera;
    private OrthographicCamera viewCamera; // need another camera to not mess with the real one
    private SimplexNoise noise;
    private float trauma;
    private float accumTime;
    private Texture debugTexture;
    private NinePatch outlineNinePatch;
    private Texture pixelTex;


    public ScreenShakeCameraController(OrthographicCamera worldCamera) {
        reSize(worldCamera);
        noise = new SimplexNoise(64, .8f, 2);
        trauma = 0;
        pixelTex = new Texture("white-pixel.png");
        Texture outLineTexture = new Texture("outline.png");
        outlineNinePatch = new NinePatch(outLineTexture, 4, 4, 4, 4);

        // Debug for drawing the perlin noise
        Pixmap pixmap = new Pixmap(128, 128, Pixmap.Format.RGBA8888);
        for (int x = 0; x < 128; x++) {
            for (int y = 0; y < 128; y++) {
                float noiseValue = (float) ((noise.getNoise(x, y) + 1f) / 2f);
                int color = Color.rgba8888(noiseValue, noiseValue, noiseValue, 1f);
                pixmap.drawPixel(x, y, color);
            }
        }
        debugTexture = new Texture(pixmap);
        pixmap.dispose();
    }


    /**
     * Called every frame.
     * This will update the shake camera
     *
     * @param dt frame delta
     */
    public void update(float dt) {
        accumTime += dt;

        // reset view camera
        viewCamera.position.set(worldCamera.position);
        viewCamera.up.set(worldCamera.up);
        viewCamera.zoom = worldCamera.zoom;

        trauma = MathUtils.clamp(trauma, 0f, 1f);
        float shake = getShakeAmount();
        float offsetX = maxXOffset * shake * (float) noise.getNoise(1, accumTime * xOffsetSpeed);
        float offsetY = maxYOffset * shake * (float) noise.getNoise(20, accumTime * yOffsetSpeed);
        float angle = maxAngleDegrees * shake * (float) noise.getNoise(30, accumTime * rotationSpeed);

        viewCamera.position.add((int) offsetX, (int) offsetY, 0);
        viewCamera.rotate(angle);
        viewCamera.update();

        trauma = MathUtils.clamp(trauma - dt, 0f, 1f);

    }


    /**
     * Adds damage to the screen shake amount, values between .1 and .5 work best
     * Max combined damage trauma is 1f
     *
     * @param damage between 0 and 1
     */
    public void addDamage(float damage) {
        trauma += damage;
    }

    /**
     * sets damage to the screen shake amount, values between .1 and .5 work best
     * Max combined damage trauma is 1f
     *
     * @param damage between 0 and 1
     */
    public void setDamage(float damage) {
        trauma = damage;
    }

    private float getShakeAmount() {
        return trauma * trauma;
    }


    /**
     * Use this instead of the normal cameras projection Matrix
     *
     * @return the shaken camera matrix
     */
    public Matrix4 getCombinedMatrix() {
        return viewCamera.combined;
    }

    public void renderDebug(SpriteBatch batch, OrthographicCamera screenCamera) {
        batch.setColor(Color.WHITE);
        batch.draw(debugTexture, screenCamera.viewportWidth - 148, 20);
        float height = screenCamera.viewportHeight - 40;
        batch.setColor(Color.RED);
        batch.draw(pixelTex, 20, 20, 20, height * trauma);
        batch.draw(pixelTex, 45, 20, 20, height * getShakeAmount());

        batch.setColor(Color.WHITE);
        outlineNinePatch.draw(batch, 20, 20, 20, height);
        outlineNinePatch.draw(batch, 45, 20, 20, height);
    }

    public void reSize(OrthographicCamera newCam) {
        this.worldCamera = newCam;
        viewCamera = new OrthographicCamera(worldCamera.viewportWidth, worldCamera.viewportHeight);

        maxXOffset = worldCamera.viewportWidth * .2f;
        maxYOffset = worldCamera.viewportHeight * .25f;
        maxAngleDegrees = worldCamera.viewportWidth * .003f;
    }
}