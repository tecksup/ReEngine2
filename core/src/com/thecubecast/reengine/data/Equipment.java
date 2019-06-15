package com.thecubecast.reengine.data;

public class Equipment extends Item {
    public Equipment(int ID, int Quantity) {
        super(ID, Quantity);
    }

    public Equipment(Item item) {
        super(item);
    }

    public Equipment(String Name, int ID, String SpriteLocation) {
        super(Name, ID, SpriteLocation);
    }

    public Equipment(String Name, int ID, String SpriteLocation, String Desc) {
        super(Name, ID, SpriteLocation, Desc);
    }
}
