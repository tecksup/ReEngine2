package com.thecubecast.reengine.worldobjects;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.thecubecast.reengine.data.Item;

import static com.thecubecast.reengine.graphics.Draw.OutlineShader;
import static com.thecubecast.reengine.graphics.Draw.setOutlineShaderColor;

public class Storage extends Interactable {

    public int InventorySize = 30;

    private Item[] Inventory = new Item[InventorySize];

    public Storage(int x, int y, int z, Vector3 size, type State, boolean collision) {
        super(x, y, z, size, State, collision);
        ID = "Chest";
    }

    public Storage(int x, int y, int z, Vector3 size, type State, boolean collision, String rawEvents, TriggerType triggerType) {
        super(x, y, z, size, State, collision, rawEvents, triggerType);
        ID = "Chest";
    }

    @Override
    public void draw(SpriteBatch batch, float Time) {
        if (Highlight) {
            batch.flush();
            batch.setShader(OutlineShader);
            setOutlineShaderColor(this.HighlightColor, 0.8f);
            batch.draw(Image, getPosition().x, getPosition().y);
            batch.setShader(null);
        } else {
            batch.draw(Image, getPosition().x, getPosition().y);
        }
    }

    @Override
    public void Activated() {

    }

    public Item[] getInventory() {
        return Inventory;
    }

    public void setInventory(Item[] inventory) {
        Inventory = inventory;
    }

    public void ClearInventory() {
        for (int i = 0; i < InventorySize; i++) {
            Inventory[i] = null;
        }
    }

    public Item getInventoryAt(int spot) {
        return Inventory[spot];
    }

    public int getItemQuant(int ItemId) {
        int StoredResource = 0;

        for (int j = 0; j < Inventory.length; j++) {
            if (Inventory[j] != null) {
                if (Inventory[j].getID() == ItemId) {
                    //Found matching item
                    StoredResource += Inventory[j].getQuantity();
                }
            }
        }

        return StoredResource;
    }

    public boolean AddToInventory(Item item) {

        boolean found = false;

        //Finds first Matching spot
        for (int j = 0; j < Inventory.length; j++) {
            if (Inventory[j] != null) {
                if (Inventory[j].getID() == item.getID()) {
                    Inventory[j].setQuantity(Inventory[j].getQuantity() + item.getQuantity());
                    found = true;
                    break;
                }
            }
        }

        if (!found) {
            for (int j = 0; j < Inventory.length; j++) {
                if (Inventory[j] == null) {
                    Item tempItem = new Item(item);
                    Inventory[j] = tempItem;
                    found = true;
                    break;
                }
            }
        }

        return found;
    }

    public boolean DeductFromInventory(int ItemID, int Quant) {
        boolean Success = false;

        if (getItemQuant(ItemID) >= Quant) {

            int ResourceRemaining = Quant;

            for (int j = 0; j < Inventory.length; j++) {
                if (Inventory[j] != null) {
                    if (Inventory[j].getID() == ItemID) { //Found matching item
                        if (Inventory[j].getQuantity() < ResourceRemaining) { //if that item Quant is less then needed
                            ResourceRemaining -= Inventory[j].getQuantity();
                            Inventory[j] = null;
                        } else if (Inventory[j].getQuantity() == ResourceRemaining) {
                            ResourceRemaining = 0;
                            Inventory[j] = null;
                            break;
                        } else {
                            Inventory[j].setQuantity(Inventory[j].getQuantity() - ResourceRemaining);
                            break;
                        }
                    }
                }
            }

            if (ResourceRemaining == 0) {
                Success = true;
            }

        } else {
            return false;
        }

        return Success;
    }
}
