package com.thecubecast.reengine.data;

import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;

public class Cube {
    private BoundingBox Prism;
    private int Hash;

    public Cube(Vector3 Position, Vector3 Size) {
        Vector3 temp = Position;
        temp.add(Size);
        this.Prism = new BoundingBox(Position, temp);
    }

    public Cube(int x, int y, int z, int width, int length, int height) {
        Vector3 Position1 = new Vector3(x, y, z);
        Vector3 Position2 = new Vector3(width, length, height).add(Position1);
        this.Prism = new BoundingBox(Position1, Position2);
    }

    public int getHash() {
        return Hash;
    }

    public void setHash(int hash) {
        Hash = hash;
    }

    public BoundingBox getPrism() {
        return Prism;
    }

    public void setPrism(BoundingBox prism) {
        Prism = prism;
    }
}