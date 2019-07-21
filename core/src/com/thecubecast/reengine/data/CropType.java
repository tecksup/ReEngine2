package com.thecubecast.reengine.data;

public class CropType {
    public String Name;
    public int ID;
    public String TexLocation;
    public int SeedItemID;
    public int GrowingTimeMin;
    public int GrowingTimeMax;
    public int MinDrops;
    public int MaxDrops;
    public int ItemDropID;

    public CropType(String name, int ID, String texLocation, int seedItemID, int growingTimeMin, int growingTimeMax, int minDrops, int maxDrops, int itemDropID) {
        Name = name;
        this.ID = ID;
        TexLocation = texLocation;
        SeedItemID = seedItemID;
        GrowingTimeMin = growingTimeMin;
        GrowingTimeMax = growingTimeMax;
        MinDrops = minDrops;
        MaxDrops = maxDrops;
        ItemDropID = itemDropID;
    }
}
