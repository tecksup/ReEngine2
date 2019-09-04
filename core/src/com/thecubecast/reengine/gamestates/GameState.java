// Blueprint for all GameState subclasses.
// Has a reference to the GameStateManager
// along with the four methods that must
// be overridden.

package com.thecubecast.reengine.gamestates;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.thecubecast.reengine.data.Cube;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.worldobjects.WorldObject;

import java.util.ArrayList;
import java.util.List;

public abstract class GameState {

    public List<Cube> Collisions = new ArrayList<>();
    public static List<WorldObject> Entities = new ArrayList<>();

    protected GameStateManager gsm;

    public GameState(GameStateManager gsm) {
        this.gsm = gsm;
    }

    public abstract void update();

    public abstract void draw(SpriteBatch g, int height, int width, float Time);

    public abstract void drawUI(SpriteBatch g, int height, int width, float Time);

    public void reSize(SpriteBatch g, int h, int w) {
    }

    public void dispose() {

    }
}
