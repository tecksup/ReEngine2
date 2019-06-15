package com.thecubecast.reengine.graphics.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Stack;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.thecubecast.reengine.data.Item;
import com.thecubecast.reengine.gamestates.PlayState;
import com.thecubecast.reengine.worldobjects.SeedPlayer;
import com.thecubecast.reengine.worldobjects.Storage;

import static com.thecubecast.reengine.graphics.scene2d.UIFSM.CursorItem;

public class TkItem extends Stack {

    boolean isHovering = false;

    SeedPlayer player;

    boolean isEquipment = false;

    boolean isStorage = false;
    Storage StorageBox;

    private int id;
    Item BackupItem;

    Skin backupSkin;

    Table LabelTable;

    Image Icons;
    TypingLabel Quant;

    public TkItem(Skin skin, int ItemArrayPos, SeedPlayer player) {

        super();

        this.player = player;

        backupSkin = skin;

        this.id = ItemArrayPos;
        BackupItem = player.Inventory[id];

        LabelTable = new Table();

        if (player.Inventory[id] == null) {
            Icons = new Image();
            Quant = new TypingLabel("", skin);
            Quant.skipToTheEnd();
        } else {
            Texture Icon = new Texture(Gdx.files.internal(player.Inventory[id].getTexLocation()));
            Icons = new Image(Icon);
            if (player.Inventory[id].getQuantity() > 99)
                Quant = new TypingLabel("99+", skin);
            else
                Quant = new TypingLabel(player.Inventory[id].getQuantity() + "", skin);
            Quant.skipToTheEnd();
        }

        this.add(Icons);
        LabelTable.add(Quant);
        LabelTable.bottom().right();
        this.add(LabelTable);

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Play a click sound
                //AudioM.play("Click");

                if (CursorItem == null) { //Pickup
                    CursorItem = player.Inventory[id];
                    player.Inventory[id] = null;
                    Reload();

                } else {
                    if (getItem() != null) {
                        if (CursorItem.getName().equals(player.Inventory[id].getName())) { //Stack
                            player.Inventory[id].setQuantity(player.Inventory[id].getQuantity() + CursorItem.getQuantity());
                            CursorItem = null;
                            Reload();
                        } else { //SWAP
                            Item tempItem = CursorItem;
                            CursorItem = getItem();
                            player.Inventory[id] = tempItem;
                            Reload();
                        }

                    } else {
                        //Place
                        player.Inventory[id] = CursorItem;
                        CursorItem = null;
                        Reload();

                    }
                }
            }
        });

        this.addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Play a click sound
                //AudioM.play("Click");

                if (CursorItem == null) { //Pickup half
                    if (player.Inventory[id].getQuantity() % 2 > 0) {
                        CursorItem = new Item(player.Inventory[id]);
                        CursorItem.setQuantity(CursorItem.getQuantity() / 2 + 1);
                        player.Inventory[id].setQuantity(player.Inventory[id].getQuantity() / 2);
                    } else {
                        CursorItem = new Item(player.Inventory[id]);
                        CursorItem.setQuantity(CursorItem.getQuantity() / 2);
                        player.Inventory[id].setQuantity(player.Inventory[id].getQuantity() / 2);
                    }
                    Reload();

                } else {
                    if (getItem() != null) {
                    } else {
                        //Place half
                        if (CursorItem.getQuantity() % 2 > 0) {
                            Item temp = new Item(CursorItem);
                            temp.setQuantity(CursorItem.getQuantity() / 2 + 1);
                            CursorItem.setQuantity(CursorItem.getQuantity() / 2);
                            player.Inventory[id] = temp;
                        } else {
                            Item temp = new Item(CursorItem);
                            temp.setQuantity(CursorItem.getQuantity() / 2);
                            CursorItem.setQuantity(CursorItem.getQuantity() / 2);
                            player.Inventory[id] = temp;
                        }

                        Reload();

                    }
                }

            }
        });

    }

    public TkItem(Skin skin, int ItemArrayPos, SeedPlayer player, boolean DUD) {
        super();

        this.player = player;

        backupSkin = skin;

        this.isEquipment = true;

        this.id = ItemArrayPos;
        BackupItem = player.Equipment[id];

        LabelTable = new Table();

        if (player.Equipment[id] == null) {
            Icons = new Image();
            Quant = new TypingLabel("", skin);
            Quant.skipToTheEnd();
        } else {
            Texture Icon = new Texture(Gdx.files.internal(player.Equipment[id].getTexLocation()));
            Icons = new Image(Icon);
            if (player.Inventory[id].getQuantity() > 99)
                Quant = new TypingLabel("99+", skin);
            else
                Quant = new TypingLabel(player.Inventory[id].getQuantity() + "", skin);
            Quant.skipToTheEnd();
        }

        this.add(Icons);
        LabelTable.add(Quant);
        LabelTable.bottom().right();
        this.add(LabelTable);

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Play a click sound
                //AudioM.play("Click");

                if (CursorItem == null) { //Pickup
                    CursorItem = player.Equipment[id];
                    player.Equipment[id] = null;
                    Reload();

                } else {
                    if (getItem() != null) {
                        if (CursorItem.getName().equals(player.Equipment[id].getName())) { //Stack
                            player.Equipment[id].setQuantity(player.Equipment[id].getQuantity() + CursorItem.getQuantity());
                            CursorItem = null;
                            Reload();
                        } else { //SWAP
                            Item tempItem = CursorItem;
                            CursorItem = getItem();
                            player.Equipment[id] = tempItem;
                            Reload();
                        }

                    } else {
                        //Place
                        player.Equipment[id] = CursorItem;
                        CursorItem = null;
                        Reload();

                    }
                }
            }
        });

        this.addListener(new ClickListener(Input.Buttons.RIGHT) {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Play a click sound
                //AudioM.play("Click");

                if (CursorItem == null) { //Pickup half
                    if (player.Equipment[id].getQuantity() % 2 > 0) {
                        CursorItem = new Item(player.Equipment[id]);
                        CursorItem.setQuantity(CursorItem.getQuantity() / 2 + 1);
                        player.Equipment[id].setQuantity(player.Equipment[id].getQuantity() / 2);
                    } else {
                        CursorItem = new Item(player.Equipment[id]);
                        CursorItem.setQuantity(CursorItem.getQuantity() / 2);
                        player.Equipment[id].setQuantity(player.Equipment[id].getQuantity() / 2);
                    }
                    Reload();

                } else {
                    if (getItem() != null) {
                    } else {
                        //Place half
                        if (player.Equipment[id].getQuantity() % 2 > 0) {
                            Item temp = new Item(CursorItem);
                            temp.setQuantity(CursorItem.getQuantity() / 2 + 1);
                            CursorItem.setQuantity(CursorItem.getQuantity() / 2);
                            player.Equipment[id] = temp;
                        } else {
                            Item temp = new Item(CursorItem);
                            temp.setQuantity(CursorItem.getQuantity() / 2);
                            CursorItem.setQuantity(CursorItem.getQuantity() / 2);
                            player.Equipment[id] = temp;
                        }

                        Reload();

                    }
                }

            }
        });

    }

    /**
     * @param skin
     * @param ItemArrayPos for the Storage object item location in array it's accessing
     * @param DUD          this tells us what storage object we are accessing
     */
    public TkItem(Skin skin, int ItemArrayPos, SeedPlayer player, Storage DUD) {

        super();

        this.player = player;

        backupSkin = skin;

        isStorage = true;
        StorageBox = DUD;

        this.id = ItemArrayPos;
        BackupItem = StorageBox.getInventory()[id];

        LabelTable = new Table();

        if (StorageBox.getInventory()[id] == null) {
            Icons = new Image();
            Quant = new TypingLabel("", skin);
            Quant.skipToTheEnd();
        } else {
            Texture Icon = new Texture(Gdx.files.internal(StorageBox.getInventory()[id].getTexLocation()));
            Icons = new Image(Icon);
            if (StorageBox.getInventory()[id].getQuantity() > 99)
                Quant = new TypingLabel("99+", skin);
            else
                Quant = new TypingLabel(StorageBox.getInventory()[id].getQuantity() + "", skin);
            Quant.skipToTheEnd();
        }

        this.add(Icons);
        LabelTable.add(Quant);
        LabelTable.bottom().right();
        this.add(LabelTable);

        this.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                //Play a click sound
                //AudioM.play("Click");

                if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT) || Gdx.input.isKeyPressed(Input.Keys.SHIFT_RIGHT)) {
                    if (getItem() != null) {
                        player.AddToInventory(StorageBox.getInventory()[id]);
                        StorageBox.getInventory()[id] = null;
                    }
                } else {
                    if (CursorItem == null) { //Pickup
                        CursorItem = StorageBox.getInventory()[id];
                        StorageBox.getInventory()[id] = null;
                        Reload();

                    } else {
                        if (getItem() != null) {
                            if (CursorItem.getName().equals(StorageBox.getInventory()[id].getName())) { //Stack
                                StorageBox.getInventory()[id].setQuantity(StorageBox.getInventory()[id].getQuantity() + CursorItem.getQuantity());
                                CursorItem = null;
                                Reload();
                            } else { //SWAP
                                Item tempItem = CursorItem;
                                CursorItem = getItem();
                                StorageBox.getInventory()[id] = tempItem;
                                Reload();
                            }

                        } else {
                            //Place
                            StorageBox.getInventory()[id] = CursorItem;
                            CursorItem = null;
                            Reload();

                        }
                    }
                }

            }
        });
    }


    @Override
    public void act(float delta) {
        super.act(delta);
        if (isEquipment) {
            if (!Item.compare(BackupItem, player.Equipment[id])) {
                BackupItem = player.Equipment[id];
                Reload();
            } else if (BackupItem != null) {
                if (BackupItem.getQuantity() != player.Equipment[id].getQuantity()) {
                    BackupItem = player.Equipment[id];
                    Reload();
                }
            }
        } else if (isStorage) {
            if (!Item.compare(BackupItem, player.Inventory[id])) {
                BackupItem = StorageBox.getInventory()[id];
                Reload();
            } else if (BackupItem != null) {
                if (BackupItem.getQuantity() != StorageBox.getInventory()[id].getQuantity()) {
                    BackupItem = StorageBox.getInventory()[id];
                    Reload();
                }
            }
        } else {
            if (!Item.compare(BackupItem, player.Inventory[id])) {
                BackupItem = player.Inventory[id];
                Reload();
            } else if (BackupItem != null) {
                if (BackupItem.getQuantity() != player.Inventory[id].getQuantity()) {
                    BackupItem = player.Inventory[id];
                    Reload();
                }
            }
        }

    }

    public void Reload() {
        if (isEquipment) {
            if (player.Equipment[id] == null) {
                Icons.setDrawable(null);
                Quant.setText("");
            } else {
                Texture Icon = new Texture(Gdx.files.internal(player.Equipment[id].getTexLocation()));
                Icons.setDrawable(new TextureRegionDrawable(new TextureRegion(Icon)));

                if (player.Equipment[id].getQuantity() > 99)
                    Quant.setText("99+");
                else
                    Quant.setText(player.Equipment[id].getQuantity() + "");
                Quant.skipToTheEnd();
            }
        } else if (isStorage) {
            if (StorageBox.getInventory()[id] == null) {
                Icons.setDrawable(null);
                Quant.setText("");
            } else {
                Texture Icon = new Texture(Gdx.files.internal(StorageBox.getInventory()[id].getTexLocation()));
                Icons.setDrawable(new TextureRegionDrawable(new TextureRegion(Icon)));

                if (StorageBox.getInventory()[id].getQuantity() > 99)
                    Quant.setText("99+");
                else
                    Quant.setText(StorageBox.getInventory()[id].getQuantity() + "");
                Quant.skipToTheEnd();
            }
        } else {
            if (player.Inventory[id] == null) {
                Icons.setDrawable(null);
                Quant.setText("");
            } else {
                Texture Icon = new Texture(Gdx.files.internal(player.Inventory[id].getTexLocation()));
                Icons.setDrawable(new TextureRegionDrawable(new TextureRegion(Icon)));

                if (player.Inventory[id].getQuantity() > 99)
                    Quant.setText("99+");
                else
                    Quant.setText(player.Inventory[id].getQuantity() + "");
                Quant.skipToTheEnd();
            }
        }
    }

    public Item getItem() {
        if (isEquipment) {
            return player.Equipment[id];
        } else if (isStorage) {
            return StorageBox.getInventory()[id];
        } else {
            return player.Inventory[id];
        }
    }
}
