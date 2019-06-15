package com.thecubecast.reengine.data;

import com.google.gson.JsonArray;

public class Craftable {

    // Resources to craft it, and the quantity required
    int CraftableID;

    int Quantity;

    int[][] ResourcesNeeded;

    public Craftable(int ID, JsonArray Resources, int Quantity) {

        this.CraftableID = ID;

        this.Quantity = Quantity;

        this.ResourcesNeeded = new int[Resources.size()][2];
        for (int i = 0; i < Resources.size(); i++) {
            String[] temp = Resources.get(i).getAsString().split(":");
            ResourcesNeeded[i][0] = Integer.parseInt(temp[0]);
            ResourcesNeeded[i][1] = Integer.parseInt(temp[1]);
        }

    }

    public int getCraftableID() {
        return CraftableID;
    }

    public int[][] RequiredResources() {
        return ResourcesNeeded;
    }

    public int getQuantity() {
        return Quantity;
    }
}
