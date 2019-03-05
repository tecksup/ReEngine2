package com.thecubecast.reengine.data.ogmo;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.XmlReader;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.worldobjects.Player;
import com.thecubecast.reengine.worldobjects.WorldObject;

import java.util.List;

/**
 * Not used, Entities are just loaded straight into world
 */
public class OelEntitiesLayer extends OelLayer {

    XmlReader.Element Layer;

    //Just for calculation of coords
    private int width;
    private int height;


    public OelEntitiesLayer(String Name, XmlReader.Element Text) {
        super(-1, -1, Name);
        Layer = Text;
    }

    public void loadEntities(OelMap map, Player player, List<WorldObject> Entities) {
        //The Objects layer
        if (Layer.getName().equals("Objects") || Layer.getName().equals("Props")) {
            for (int i = 0; i < Layer.getChildCount(); i++) {
                if (Layer.getChild(i).getName().equals("Player")) {
                    player.setPosition(Integer.parseInt(Layer.getChild(i).getAttribute("x")), height - Integer.parseInt(Layer.getChild(i).getAttribute("y")), 0);
                } else if (Layer.getChild(i).getName().equals("CameraHint")) {
                    WorldObject temp = new WorldObject() {
                        @Override
                        public void init(int Width, int Height) {

                        }

                        @Override
                        public void update(float delta, GameState G) {

                        }

                        @Override
                        public void draw(SpriteBatch batch, float Time) {

                        }
                    };

                    temp.setPosition(Integer.parseInt(Layer.getChild(i).getAttribute("x")), height - Integer.parseInt(Layer.getChild(i).getAttribute("y")), 0);
                    temp.FocusStrength = (float) Integer.parseInt(Layer.getChild(i).getAttribute("FocusStrength")) / 10;

                    Entities.add(temp);
                } else if (Layer.getChild(i).getName().equals("Car")) {
                    WorldObject temp = new WorldObject(Integer.parseInt(Layer.getChild(i).getAttribute("x")), height - Integer.parseInt(Layer.getChild(i).getAttribute("y")) - 57, 0, new Vector3(32, 57, 0)) {
                        Texture Car = new Texture(Gdx.files.internal("Sprites/car.png"));

                        @Override
                        public void init(int Width, int Height) {

                        }

                        @Override
                        public void update(float delta, GameState G) {

                        }

                        @Override
                        public void draw(SpriteBatch batch, float Time) {
                            batch.draw(Car, getPosition().x, getPosition().y);
                        }


                    };

                    temp.setCollidable(true);
                    Entities.add(temp);
                } else if (Layer.getChild(i).getName().equals("Object")) {
                    String tempImgLoc = Layer.getChild(i).getAttribute("SpriteLocation");
                    WorldObject temp = new WorldObject(Integer.parseInt(Layer.getChild(i).getAttribute("x")), height - Integer.parseInt(Layer.getChild(i).getAttribute("y")), 0, new Vector3(16, 16, 0)) {
                        Texture Image = new Texture(Gdx.files.internal(tempImgLoc));

                        @Override
                        public void init(int Width, int Height) {

                        }

                        @Override
                        public void update(float delta, GameState G) {

                        }

                        @Override
                        public void draw(SpriteBatch batch, float Time) {
                            batch.draw(Image, getPosition().x, getPosition().y);
                        }


                    };

                    temp.setCollidable(Layer.getChild(i).getBoolean("Collidable"));
                    Entities.add(temp);
                } else {
                    String tempImgLoc = Layer.getChild(i).getAttribute("SpriteLocation");
                    Texture tempImage = new Texture(Gdx.files.internal(tempImgLoc));
                    WorldObject temp = new WorldObject(Integer.parseInt(Layer.getChild(i).getAttribute("x")), height - Integer.parseInt(Layer.getChild(i).getAttribute("y")) - tempImage.getHeight(), 0, new Vector3(16, 16, 0)) {
                        Texture Image = new Texture(Gdx.files.internal(tempImgLoc));

                        @Override
                        public void init(int Width, int Height) {

                        }

                        @Override
                        public void update(float delta, GameState G) {

                        }

                        @Override
                        public void draw(SpriteBatch batch, float Time) {
                            batch.draw(Image, getPosition().x, getPosition().y);
                        }


                    };

                    temp.setCollidable(Layer.getChild(i).getBoolean("Collidable"));
                    Entities.add(temp);
                }

            }
        }

        //The Areas layer
        if (Layer.getName().equals("Areas")) {
            for (int i = 0; i < Layer.getChildCount(); i++) {

            }
        }
    }

    public void getLayerAttribute(String AttributeName) {

    }

    public void setWidth(int width) {
        this.width = width;
    }

    public void setHeight(int height) {
        this.height = height;
    }
}
