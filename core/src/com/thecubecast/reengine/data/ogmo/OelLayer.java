package com.thecubecast.reengine.data.ogmo;

public abstract class OelLayer {

    private int Width;
    private int Height;
    private String Name;

    public OelLayer(int Width, int Height, String Name) {
        this.Width = Width / 16;
        this.Height = Height / 16;
        this.Name = Name;
    }

    public int getWidth() {
        return Width;
    }

    public int getHeight() {
        return Height;
    }

    public String getName() {
        return Name;
    }
}
