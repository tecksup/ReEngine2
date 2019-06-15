package com.thecubecast.reengine.data;

import static com.thecubecast.reengine.data.GameStateManager.ItemPresets;

public class Item {
    private String Name = "";
    private int ID = 0;
    private int Quantity = 0;
    private int Max;
    private boolean Structure = false;
    private String TexLocation = "";
    private String Description = "";

    public Item(int ID, int Quantity) {
        this(ItemPresets.get(ID));
        this.Quantity = Quantity;
        this.Max = 64;
    }

    public Item(Item item) {
        this.Name = item.getName();
        this.ID = item.getID();
        this.Quantity = item.getQuantity();
        this.TexLocation = item.getTexLocation();
        this.Structure = item.Structure;
        this.Max = item.Max;
    }

    public Item(String Name, int ID, String SpriteLocation) {
        this.Name = Name;
        this.ID = ID;
        this.TexLocation = SpriteLocation;
    }

    public Item(String Name, int ID, String SpriteLocation, String Description) {
        this.Name = Name;
        this.ID = ID;
        this.TexLocation = SpriteLocation;
        this.Description = Description;
    }

    public Item(String Name, int ID, String SpriteLocation, String Description, Boolean Structure) {
        this.Name = Name;
        this.ID = ID;
        this.TexLocation = SpriteLocation;
        this.Description = Description;
        this.Structure = Structure;
    }

    public String getName() {
        return Name;
    }

    public int getID() {
        return ID;
    }

    public int getQuantity() {
        return Quantity;
    }

    public Item setQuantity(int quantity) {
        Quantity = quantity;
        return this;
    }

    public String getTexLocation() {
        return TexLocation;
    }

    public static boolean compare(Item item1, Item item2) {
        return (item1 == null ? item2 == null : item1.equals(item2));
    }

    public String getDescription() {
        return Description;
    }

    public Item setDescription(String description) {
        Description = description;
        return this;
    }

    public int getMax() {
        return Max;
    }

    public void setMax(int Max) {
        this.Max = Max;
    }

    public boolean isStructure() {
        return Structure;
    }
}
