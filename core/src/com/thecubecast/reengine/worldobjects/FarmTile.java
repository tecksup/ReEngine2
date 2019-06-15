package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.data.ParticleHandler;
import com.thecubecast.reengine.data.dcputils.StuffUtilsKt;
import com.thecubecast.reengine.gamestates.DialogStateExtention;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.gamestates.PlayState;
import com.thecubecast.reengine.graphics.ScreenShakeCameraController;

import java.util.List;
import java.util.Random;

import static com.thecubecast.reengine.data.GameStateManager.Render;

public class FarmTile extends Interactable {

    int tics = 0;
    int Diff = new Random().nextInt(100);
    
    int CropLife = 5;
    boolean Collidingwith;
    int RotateStrength = 0;

    public FarmTile(int x, int y, int z, String Type) {
        super(x, y, z, new Vector3(16,16,8), type.Static, false, "", TriggerType.None);
        Name = "Crop";
        Description = Type;
    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void update(float delta, GameState G) {

        if (G instanceof PlayState) {

            tics++;
            if (tics % (30 + Diff) == 0) {
                tics = 0;
                if (CropLife - 1 >= 1)
                    CropLife--;
            }

            WorldObject tempObj = null;

            for (int i = 0; i < ((PlayState) G).Entities.size(); i++) {
                if (((PlayState) G).Entities.get(i) instanceof NPC || ((PlayState) G).Entities.get(i) instanceof SeedPlayer) {
                    Collidingwith = ((PlayState) G).Entities.get(i).getHitbox().intersects(this.getHitbox());
                    if (Collidingwith) {
                        tempObj = ((PlayState) G).Entities.get(i);
                        break;
                    }
                }
            }


            if (Collidingwith && tempObj != null) {
                int tempPlayer = (int) (tempObj.getPosition().x + tempObj.getSize().x / 2);
                int tempThis = (int) (this.getPosition().x + this.getSize().x / 2);

                RotateStrength = (int) Math.abs(tempObj.getPosition().dst(this.getPosition()));
                RotateStrength = (int) StuffUtilsKt.mapToRange(RotateStrength, 0, 15, 15, 0);

                if (tempThis - tempPlayer < 0) {
                    RotateStrength *= -1;
                }

            }
        } else {
            CropLife = 1;
        }
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {

        if (Render.hasTextures(Description)) {

            if (Collidingwith) {
                batch.draw(Render.getTextures(Description).get(CropLife), getPosition().x, getPosition().y, Render.getTextures(Description).get(CropLife).getRegionWidth() / 2, 0, Render.getTextures(Description).get(CropLife).getRegionWidth(), Render.getTextures(Description).get(CropLife).getRegionHeight(), 1, 1, -1 * RotateStrength);
            } else {
                batch.draw(Render.getTextures(Description).get(CropLife), getPosition().x, getPosition().y, Render.getTextures(Description).get(CropLife).getRegionWidth() / 2, 0, Render.getTextures(Description).get(CropLife).getRegionWidth(), Render.getTextures(Description).get(CropLife).getRegionHeight(), 1, 1, 0);

            }
        }
    }

    public void setCropLife(int cropLife) {
        CropLife = cropLife;
    }

    public int getCropLife() {
        return CropLife;
    }

    @Override
    public void Interact(WorldObject player, ScreenShakeCameraController shaker, DialogStateExtention dialog, WorldObject MainCameraFocusPoint, ParticleHandler Particles, List<WorldObject> Entities) {
        super.Interact(player, shaker, dialog, MainCameraFocusPoint, Particles, Entities);
    }

    @Override
    public Interactable CreateNew() {
        FarmTile tempObj = new FarmTile((int) this.getPosition().x, (int) this.getPosition().y, (int) this.getPosition().z, this.Description);

        return tempObj;
    }
}
