package com.thecubecast.reengine.worldobjects.Triggers;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.data.CropType;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.Item;
import com.thecubecast.reengine.data.ParticleHandler;
import com.thecubecast.reengine.data.dcputils.StuffUtilsKt;
import com.thecubecast.reengine.gamestates.DialogStateExtention;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.gamestates.PlayState;
import com.thecubecast.reengine.graphics.ScreenShakeCameraController;
import com.thecubecast.reengine.graphics.scene2d.UI_state;
import com.thecubecast.reengine.worldobjects.NPC;
import com.thecubecast.reengine.worldobjects.SeedPlayer;
import com.thecubecast.reengine.worldobjects.WorldItem;
import com.thecubecast.reengine.worldobjects.WorldObject;

import java.util.List;
import java.util.Random;

import static com.thecubecast.reengine.data.GameStateManager.*;

public class FarmTile extends Interactable {

    int CropID = -1;
    CropType CropData;

    int tics = 0;
    int Diff = new Random().nextInt(100);
    
    int CropLife = 5;
    boolean Collidingwith;
    int RotateStrength = 0;

    int PromptTics;
    TextureRegion XpromptUp;
    TextureRegion XpromptDown;
    TextureRegion ApromptUp;
    TextureRegion ApromptDown;
    TextureRegion Shadow;
    boolean UpDown = true;

    public FarmTile(int x, int y, int z, int CropID) {
        super(x, y, z, new Vector3(16,16,8), type.Static, false, "", TriggerType.None);
        Name = "Crop";

        this.CropID = CropID;

        SetupCrop();

        if (x % 16 > 8) {
            setPositionX(x + x % 16);
        } else {
            setPositionX(x - x % 16);
        }

        if (y % 16 > 8) {
            setPositionY(y + y % 16);
        } else {
            setPositionY(y - y % 16);
        }

        XpromptUp = GameStateManager.Render.getTexture("Xprompt_Up");
        XpromptDown = GameStateManager.Render.getTexture("Xprompt_Down");
        ApromptUp = GameStateManager.Render.getTexture("Aprompt_Up");
        ApromptDown = GameStateManager.Render.getTexture("Aprompt_Down");
        Shadow = GameStateManager.Render.getTexture("Shadow");

    }

    @Override
    public void init(int Width, int Height) {

    }

    @Override
    public void drawGui(SpriteBatch batch, float Time) {
        super.drawGui(batch, Time);
        if (Highlight) {
            if (UpDown) {
                if (HighlightColor.equals(Color.BLUE))
                    batch.draw(XpromptUp, getPosition().x+2, getPosition().y+18);
                else if (HighlightColor.equals(Color.GREEN))
                    batch.draw(ApromptUp, getPosition().x+2, getPosition().y+18);
            }
            else {
                if (HighlightColor.equals(Color.BLUE))
                    batch.draw(XpromptDown, getPosition().x+2, getPosition().y+18);
                else if (HighlightColor.equals(Color.GREEN))
                    batch.draw(ApromptDown, getPosition().x+2, getPosition().y+18);
            }
        }

    }

    @Override
    public void update(float delta, GameState G) {

        super.update(delta, G);

        PromptTics++;
        if (PromptTics % 30 == 0) {
            UpDown = !UpDown;
        }

        if (G instanceof PlayState) {

            if (Description.equals(""))
                return;
            else if (!Render.hasTextures(Description)) {
                return;
            }

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

        if (Highlight) {
            if (HighlightColor.equals(Color.BLUE) || HighlightColor.equals(Color.GREEN) || HighlightColor.equals(Color.YELLOW) || HighlightColor.equals(Color.RED))
                batch.draw(Shadow, getPosition().x, getPosition().y + 4 + getPosition().z/2);
        }

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
        if (Description.equals(""))
            UI.setState(UI_state.SeedSelection);
        else if (getCropLife() == 1) {
            Entities.add(new WorldItem((int)getPosition().x,(int)getPosition().y,(int)getPosition().z, GenerateDrops()));
            Description = "";
            CropData = null;
            CropLife = 5;
        }
    }

    @Override
    public Interactable CreateNew() {
        FarmTile tempObj = new FarmTile((int) this.getPosition().x, (int) this.getPosition().y, (int) this.getPosition().z, this.CropData.SeedItemID);

        return tempObj;
    }

    public void SetupCrop() {
        if (CropID > -1) {
            CropData = CropPresets.get(CropID);
            Description = CropData.TexLocation;
        }
    }

    public void SetupCrop(CropType CropData) {
        System.out.println(CropData.SeedItemID);
        this.CropData = CropData;
        CropID = CropData.ID;
        if (CropID > -1) {
            Description = CropData.TexLocation;
        }
    }

    public Item GenerateDrops() {
        Random rand = new Random();

        if (CropData.MinDrops == CropData.MaxDrops) {
            return ItemPresets.get(CropData.ItemDropID).setQuantity(CropData.MinDrops);
        } else
            return ItemPresets.get(CropData.ItemDropID).setQuantity(rand.nextInt(CropData.MaxDrops-CropData.MinDrops)+CropData.MinDrops);
    }
}
