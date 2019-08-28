package com.thecubecast.reengine.graphics.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.thecubecast.reengine.data.Common;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.control.ControlerManager;
import com.thecubecast.reengine.data.tkmap.TkMap;
import com.thecubecast.reengine.gamestates.PlayState;
import com.thecubecast.reengine.worldobjects.Triggers.Storage;
import com.thecubecast.reengine.worldobjects.WorldItem;
import com.thecubecast.reengine.worldobjects.WorldObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor;
import static com.thecubecast.reengine.data.Common.GetMonitorSizeH;
import static com.thecubecast.reengine.data.Common.GetMonitorSizeW;
import static com.thecubecast.reengine.data.GameStateManager.AudioM;
import static com.thecubecast.reengine.data.GameStateManager.ItemPresets;
import static com.thecubecast.reengine.data.GameStateManager.ctm;
import static com.thecubecast.reengine.graphics.scene2d.UIFSM.InventorySlotSelected;
import static com.thecubecast.reengine.graphics.scene2d.UIFSM.CursorItem;

public enum UI_state implements State<UIFSM> {

    Home() {

        private Table table;

        @Override
        public void enter(UIFSM entity) {

            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            final TkTextButton PlayState = new TkTextButton("Start", entity.skin);
            table.add(PlayState).pad(2);
            table.row();

            final TkTextButton Editor = new TkTextButton("Editor", entity.skin);
            table.add(Editor).pad(2);
            table.row();

            final TkTextButton Multiplayer = new TkTextButton("Multiplayer", entity.skin);
            table.add(Multiplayer).pad(2);
            table.row();

            final TkTextButton Options = new TkTextButton("Options", entity.skin);
            table.add(Options).pad(2);
            table.row();

            final TkTextButton button3 = new TkTextButton("Quit", entity.skin);
            table.add(button3).pad(2);
            table.row();


            PlayState.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.gsm.setState(GameStateManager.State.PLAY);
                }
            });

            Editor.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //entity.gsm.setState(GameStateManager.State.EDITOR);
                    entity.setState(EditorChooser);
                }
            });

            Multiplayer.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.setState(MULTIPLAYERUI);
                }
            });

            Options.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.stateMachine.changeState(UI_state.Options);
                }
            });

            button3.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //gsm.Audio.stopMusic("8-bit-Digger");
                    //GetLogin("", "");

                    //Lwjgl3Window window = ((Lwjgl3Graphics)Gdx.graphics).getWindow();
                    //window.iconifyWindow(); // iconify the window

                    Common.ProperShutdown();
                }
            });
        }

        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    InGameHome() {


        private Table table;

        @Override
        public void enter(UIFSM entity) {

            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            final TkTextButton Continue = new TkTextButton("Return to Game", entity.skin);
            table.add(Continue).pad(2);
            table.row();

            final TkTextButton Options = new TkTextButton("Options", entity.skin);
            table.add(Options).pad(2);
            table.row();

            final TkTextButton MainMenu = new TkTextButton("Main Menu", entity.skin);
            table.add(MainMenu).pad(2);
            table.row();

            Continue.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.setVisable(false);
                }
            });

            Options.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.stateMachine.changeState(UI_state.Options);
                }
            });

            MainMenu.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //Return to main menu
                    entity.gsm.setState(GameStateManager.State.MENU);
                }
            });
        }

        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    Options() {

        private Table table;

        @Override
        public void enter(UIFSM entity) {
            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            final TkTextButton Audio = new TkTextButton("Audio", entity.skin);
            table.add(Audio).pad(2);
            table.row();

            final TkTextButton Graphics = new TkTextButton("Graphics", entity.skin);
            table.add(Graphics).pad(2);
            table.row();

            final TkTextButton Controls = new TkTextButton("Controls", entity.skin);
            table.add(Controls).pad(2);
            table.row();

            final TkTextButton back = new TkTextButton("Back", entity.skin);
            table.add(back).pad(2);
            table.row();

            Audio.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.stateMachine.changeState(UI_state.Audio);
                }
            });

            Graphics.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (entity.inGame) {
                        Graphics.setText("Must exit to main menu to edit graphics.");
                    } else {
                        entity.stateMachine.changeState(UI_state.Graphics);
                    }
                }
            });

            Controls.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.stateMachine.changeState(UI_state.Controls);
                }
            });

            back.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (entity.inGame) {
                        entity.stateMachine.changeState(UI_state.InGameHome);
                    } else {
                        entity.stateMachine.changeState(UI_state.Home);
                    }
                }
            });
        }

        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    Audio() {

        private Table table;

        @Override
        public void enter(UIFSM entity) {
            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            final Label Master = new Label("Master Volume", entity.skin);
            final Slider MasterVolume = new Slider(0, 1, 0.01f, false, entity.skin);
            MasterVolume.setValue(AudioM.getMasterVolume());
            table.add(Master);
            table.row();
            table.add(MasterVolume).padBottom(12);
            table.row();

            final Label Music = new Label("Music Volume", entity.skin);
            final Slider MusicVolume = new Slider(0, 1, 0.01f, false, entity.skin);
            MusicVolume.setValue(AudioM.getMusicVolume());
            table.add(Music);
            table.row();
            table.add(MusicVolume).padBottom(12);
            table.row();

            final Label Sound = new Label("Sound Volume", entity.skin);
            final Slider SoundVolume = new Slider(0, 1, 0.01f, false, entity.skin);
            SoundVolume.setValue(AudioM.getSoundVolume());
            table.add(Sound);
            table.row();
            table.add(SoundVolume).padBottom(12);
            table.row();

            final TkTextButton back = new TkTextButton("Back", entity.skin);
            table.add(back).pad(2);
            table.row();

            MasterVolume.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    AudioM.setMasterVolume(MasterVolume.getValue());
                }
            });

            MusicVolume.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    AudioM.setMusicVolume(MusicVolume.getValue());
                }
            });

            SoundVolume.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    AudioM.setSoundVolume(SoundVolume.getValue());
                }
            });

            back.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.stateMachine.changeState(entity.stateMachine.getPreviousState());
                }
            });
        }

        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    Graphics() {

        private Table table;
        SelectBox ResolutionOptions;
        CheckBox FullScreen;

        @Override
        public void enter(UIFSM entity) {
            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            if (Gdx.app.getPreferences("properties").getString("FullScreen").equals("")) {
                Gdx.app.getPreferences("properties").putString("Resolution", "1280X720");
                Gdx.app.getPreferences("properties").flush();
            }

            FullScreen = new CheckBox("FullScreen", entity.skin);
            FullScreen.setChecked(Gdx.graphics.isFullscreen());
            FullScreen.getLabel().setColor(Color.BLACK);
            FullScreen.setChecked(Gdx.app.getPreferences("properties").getBoolean("FullScreen"));
            table.add(FullScreen).pad(2).row();

            FullScreen.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.getPreferences("properties").putBoolean("FullScreen", FullScreen.isChecked());
                    Gdx.app.getPreferences("properties").flush();

                    if (FullScreen.isChecked()) {
                        Gdx.graphics.setFullscreenMode(Gdx.graphics.getDisplayMode());
                    } else {
                        String[] temp = Gdx.app.getPreferences("properties").getString("Resolution").split("X");
                        Gdx.graphics.setWindowedMode(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
                        Gdx.graphics.setVSync(true);
                    }

                    String[] temp = Gdx.app.getPreferences("properties").getString("Resolution").split("X");
                    entity.reSize();
                }
            });

            ResolutionOptions = new SelectBox(entity.skin) {
                @Override
                protected void onShow(Actor selectBoxList, boolean below) {
                    //selectBoxList.getColor().a = 0;
                    //selectBoxList.addAction(fadeIn(0.3f, Interpolation.fade));
                }

                @Override
                protected void onHide(Actor selectBoxList) {
                    //selectBoxList.getColor().a = 1;
                    selectBoxList.addAction(removeActor());
                }
            };
            ResolutionOptions.setItems("1280X720", "1366X768", "1440X900", "1600X900", "1920X1080");
            ResolutionOptions.setSelected(Gdx.app.getPreferences("properties").getString("Resolution"));
            table.add(ResolutionOptions).pad(2).row();

            ResolutionOptions.addListener(new ChangeListener() {
                @Override
                public void changed(ChangeEvent event, Actor actor) {
                    Gdx.app.getPreferences("properties").putString("Resolution", ResolutionOptions.getSelected().toString());
                    Gdx.app.getPreferences("properties").flush();

                    String[] temp = ResolutionOptions.getSelected().toString().split("X");
                    FullScreen.setChecked(false);
                    Gdx.graphics.setWindowedMode(Integer.parseInt(temp[0]), Integer.parseInt(temp[1]));
                    entity.reSize();

                    Lwjgl3Window window = ((Lwjgl3Graphics) Gdx.graphics).getWindow();
                    window.setPosition(GetMonitorSizeW() / 2 - Gdx.graphics.getWidth() / 2, GetMonitorSizeH() / 2 - Gdx.graphics.getHeight() / 2);
                }

            });

            final TkTextButton back = new TkTextButton("Back", entity.skin);
            table.add(back).pad(2);
            table.row();

            back.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.stateMachine.changeState(entity.stateMachine.getPreviousState());
                }
            });
        }

        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    Controls() {

        private Table table;

        @Override
        public void enter(UIFSM entity) {
            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            final TkTextButton back = new TkTextButton("Back", entity.skin);
            table.add(back).pad(2);
            table.row();

            back.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.stateMachine.changeState(entity.stateMachine.getPreviousState());
                }
            });
        }

        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    INGAMEUI() {

        private Table table;
        private Label Health;
        private ProgressBar Healthbar;

        private Table Playing;

        @Override
        public void enter(UIFSM entity) {
            table = new Table();
            table.setFillParent(true);
            table.top().left();
            entity.stage.addActor(table);

            Playing = new Table();
            Playing.add(Health).top().left();
            Playing.add(Healthbar).top().left();
            table.add(Playing).top().left().padTop(2);

        }

        @Override
        public void update(UIFSM entity) {

            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    MULTIPLAYERUI() {


        private Table table;

        @Override
        public void enter(UIFSM entity) {

            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            Table Window = new Table(entity.skin);
            Window.setBackground("Window_red");

            Window.add(new Label("Username", entity.skin)).pad(2);
            final TextArea Username = new TextArea(entity.gsm.Username, entity.skin);
            Window.add(Username).pad(2).padTop(10).row();

            Window.add(new Label("IP Address", entity.skin)).pad(2);
            final TextArea IP = new TextArea(entity.gsm.IP, entity.skin);
            Window.add(IP).pad(2).row();

            final TkTextButton Connect = new TkTextButton("Connect", entity.skin) {
                @Override
                public void act(float delta) {
                    super.act(delta);
                    if (!entity.gsm.ErrorMessages.equals("")) {
                        this.setText(entity.gsm.ErrorMessages);
                    }
                }
            };

            Table Title = new Table(entity.skin);
            Title.add(new Label("Connect to a Server", entity.skin));
            Title.setBackground("Window_red");

            table.add(Window).row();
            table.pack();
            table.add(Title).padTop(-Window.getHeight()*2).row();

            Table tempTabl = new Table();

            final TkTextButton Back = new TkTextButton("Back", entity.skin);

            tempTabl.add(Back).pad(2);
            tempTabl.add(Connect).pad(2);

            table.add(tempTabl).pad(2);
            table.row();


            Connect.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.gsm.Username = Username.getText();
                    entity.gsm.setState(GameStateManager.State.MULTI);
                }
            });

            Back.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.stateMachine.changeState(entity.stateMachine.getPreviousState());
                }
            });
        }

        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    EditorChooser() {


        private Table table;

        @Override
        public void enter(UIFSM entity) {

            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            Table SavesList = new Table(entity.skin);
            Table Left = new Table(entity.skin);
            Table Right = new Table(entity.skin);

            SavesList.add(Left);
            SavesList.add(Right);

            ScrollPane RecipeScroll = new ScrollPane(SavesList, entity.skin);
            RecipeScroll.setupOverscroll(5, 50f, 100f);

            JsonParser jsonReaderthing = new JsonParser();

            //Finds all saves, and lists them
            if (Gdx.files.internal("Saves").isDirectory()) {
                for (int i = 0; i < Gdx.files.internal("Saves").list().length; i++) {
                    String[] saves = Gdx.files.internal("Saves").list()[i].toString().split("/");

                    if (!saves[1].split("[.]cube")[0].contains(".ecube")) {

                        JsonObject MapObject = jsonReaderthing.parse(Gdx.files.internal("Saves/" + saves[1]).readString()).getAsJsonObject();

                        String Created = MapObject.get("Created").getAsString();
                        String LastEdit = MapObject.get("LastEdit").getAsString();

                        Image Icon = new Image(new Texture(Gdx.files.internal("Sprites/Gunter.png")));
                        Table Data = new Table();
                        Label SaveName = new Label(saves[1].split(".cube")[0], entity.skin);
                        Label MetaData = new Label("[GREEN]Created:[WHITE] " + Created + " | [GREEN]Edited:[WHITE] " + LastEdit, entity.skin);
                        Data.add(SaveName).left().row();
                        Data.add(MetaData).left().row();
                        TkImageButton Delete = new TkImageButton(entity.skin, "Trash");
                        TkImageButton Dupe = new TkImageButton(entity.skin, "Plus");
                        TkImageButton Load = new TkImageButton(entity.skin, "Play");
                        Left.add(Icon).pad(2).left();
                        Left.add(Data).pad(2).left().row();
                        Right.add(Delete).pad(5).center();
                        Right.add(Dupe).pad(5).center();
                        Right.add(Load).pad(5).center().row();

                        Load.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                entity.gsm.SaveSelected = saves[1].split(".cube")[0];
                                entity.gsm.setState(GameStateManager.State.EDITOR);
                            }
                        });

                        Dupe.addListener(new ClickListener() {
                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);
                                try {
                                    Path path = Paths.get("Saves/" + saves[1]);

                                    int Copy = 0;
                                    while (Files.exists(Paths.get("Saves/" + saves[1].split(".cube")[0] + "_" + Copy + ".cube"))) {
                                        Copy++;
                                    }

                                    Files.copy(path, Paths.get("Saves/" + saves[1].split(".cube")[0] + "_" + Copy + ".cube"));
                                    System.out.println("Duped " + saves[1]);

                                    if (Gdx.files.internal("Saves/" + saves[1].split("[.]")[0] + ".ecube").exists()) {
                                        Path path2 = Paths.get("Saves/" + saves[1].split("[.]")[0] + ".ecube");

                                        Files.copy(path2, Paths.get("Saves/" + saves[1].split("[.]")[0] + "_" + Copy + ".ecube"));
                                        System.out.println("Duped " + saves[1].split("[.]")[0] + " Editor File");
                                    }
                                } catch (Exception e) {

                                }
                                entity.setState(EditorChooser);
                            }
                        });

                        Delete.addListener(new ClickListener() {
                            boolean temp = false;

                            @Override
                            public void clicked(InputEvent event, float x, float y) {
                                super.clicked(event, x, y);

                                if (!temp) {
                                    temp = true;
                                    System.out.println("Are you sure you want to delete (Saves/" + saves[1] + ")?");
                                    return;
                                }

                                if (temp) {
                                    try {
                                        Path path = Paths.get("Saves/" + saves[1]);
                                        Files.deleteIfExists(path);
                                        System.out.println("Deleted " + saves[1]);
                                        entity.setState(EditorChooser);
                                    } catch (Exception e) {

                                    }
                                }
                            }
                        });


                    }
                }
            }

            Table Title = new Table(entity.skin);
            Title.add(new Label("Levels", entity.skin));
            Title.setBackground("Window_red");

            table.add(RecipeScroll).row();
            table.pack();
            table.add(Title).padTop(-RecipeScroll.getHeight()*2).row();

            Table tempTabl = new Table();

            final TkTextButton Create = new TkTextButton("New", entity.skin);

            final TkTextButton Back = new TkTextButton("Back", entity.skin);

            tempTabl.add(Back).pad(2);
            tempTabl.add(Create).pad(2);
            table.add(tempTabl);
            table.row();

            Create.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.setState(NewLevel);
                }
            });

            Back.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.stateMachine.changeState(Home);
                }
            });
        }

        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    NewLevel() {


        private Table table;

        @Override
        public void enter(UIFSM entity) {

            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            Table SavesList = new Table(entity.skin);

            ScrollPane RecipeScroll = new ScrollPane(SavesList, entity.skin);
            RecipeScroll.setupOverscroll(5, 50f, 100f);

            Table Title = new Table(entity.skin);
            Title.add(new Label("Create New Level", entity.skin));
            Title.setBackground("Window_red");

            TextArea FileName = new TextArea("Untitled", entity.skin);
            TextArea Width = new TextArea("60", entity.skin);
            TextArea Height = new TextArea("60", entity.skin);
            TextArea Tileset = new TextArea("tileset", entity.skin);
            TextArea TileSize = new TextArea("16", entity.skin);
            TextArea TilePadding = new TextArea("2", entity.skin);
            TextArea TileSpeed = new TextArea("1", entity.skin);

            SavesList.add(new Label("Save name", entity.skin)).pad(2).padTop(10);
            SavesList.add(FileName).pad(2).padTop(10).row();
            SavesList.add(new Label("Map width", entity.skin)).pad(2).padTop(10);
            SavesList.add(Width).pad(2).row();
            SavesList.add(new Label("Map height", entity.skin)).pad(2).padTop(10);
            SavesList.add(Height).pad(2).row();
            SavesList.add(new Label("Tileset name", entity.skin)).pad(2).padTop(10);
            SavesList.add(Tileset).pad(2).row();
            SavesList.add(new Label("Tile size", entity.skin)).pad(2).padTop(10);
            SavesList.add(TileSize).pad(2).row();
            SavesList.add(new Label("Tile padding", entity.skin)).pad(2).padTop(10);
            SavesList.add(TilePadding).pad(2).row();
            SavesList.add(new Label("Animation Speed", entity.skin)).pad(2).padTop(10);
            SavesList.add(TileSpeed).pad(2).row();

            table.add(RecipeScroll).row();
            table.pack();
            table.add(Title).padTop(-RecipeScroll.getHeight()*2).row();

            Table tempTabl = new Table();

            final TkTextButton Back = new TkTextButton("Back", entity.skin);
            final TkTextButton Create = new TkTextButton("Create", entity.skin);

            tempTabl.add(Back).pad(2);
            tempTabl.add(Create).pad(2);
            table.add(tempTabl);
            table.row();

            Create.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    System.out.println("Open UI to create a new map");
                    TkMap temp = new TkMap("Saves/" + FileName.getText() + ".cube", Integer.parseInt(Width.getText()), Integer.parseInt(Height.getText()), Tileset.getText(), Integer.parseInt(TileSize.getText()), Integer.parseInt(TilePadding.getText()), Float.parseFloat(TileSpeed.getText()));

                    temp.SaveMap(null);
                    entity.gsm.SaveSelected = FileName.getText();
                    entity.gsm.setState(GameStateManager.State.EDITOR);

                }
            });

            Back.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.stateMachine.changeState(EditorChooser);
                }
            });
        }

        @Override
        public void update(UIFSM entity) {
            table.setVisible(entity.Visible);
            ControllerCheck(table);
            entity.stage.act(Gdx.graphics.getDeltaTime());
        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    Inventory() {

        Skin BackupSkin;

        int InventoryIdSelected;

        private Table Screen;
        private Table InventoryWindow;

        private Table InventoryTable;
        private Table EquipmentTable;

        ClickListener StageListener;

        @Override
        public void enter(UIFSM entity) {

            StageListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //drop out of inventory
                    if (entity.ClickedOutsideInventory) {
                        //Drop item in CursorItem
                        if (CursorItem != null) {

                            if (CursorItem.isStructure()) {

                                //GET CORRECT POSITION ON WORLD
                                Vector3 pos = new Vector3(Gdx.input.getX(), Gdx.input.getY(), 0);
                                PlayState.camera.unproject(pos);

                                Storage tempObj = new Storage((int) pos.x, (int) pos.y, (int) pos.z, new Vector3(11, 8, 8), WorldObject.type.Static, true);
                                tempObj.setTexLocation("Sprites/Map/Objects_04.png");
                                tempObj.Name = CursorItem.getName();
                                tempObj.Description = CursorItem.getDescription();

                                tempObj.setHitboxOffset(new Vector3(3, 0, 0));

                                PlayState.Entities.add(tempObj);
                                if (CursorItem.getQuantity() > 1)
                                    CursorItem.setQuantity(CursorItem.getQuantity() - 1);
                                else
                                    CursorItem = null;
                                Vector3 tempVec = tempObj.getPosition();
                                Vector3 tempVecOffset = tempObj.getHitboxOffset();
                                Vector3 tempVecSize = tempObj.getSize();
                                //PlayState.Collisions.add(new Cube((int) tempVec.x + (int) tempVecOffset.x, (int) tempVec.y + (int) tempVecOffset.y, (int) tempVec.z + (int) tempVecOffset.z, (int) tempVecSize.x, (int) tempVecSize.y, (int) tempVecSize.z));


                            } else {

                                WorldItem temp = new WorldItem(0, 0, (int) entity.player.getIntereactBox().max.z, CursorItem);
                                temp = new WorldItem((int) entity.player.getIntereactBox().min.x, (int) entity.player.getIntereactBox().min.y, (int) entity.player.getIntereactBox().max.z, CursorItem);

                                PlayState.Entities.add(temp);
                                CursorItem = null;
                            }
                        }
                    }

                    entity.ClickedOutsideInventory = true;
                }
            };

            BackupSkin = entity.skin;
            Screen = new Table(entity.skin);
            Screen.setFillParent(true);
            entity.stage.addActor(Screen);

            InventoryWindow = new Table(entity.skin);
            InventoryWindow.setBackground("Window_grey_back");
            InventoryWindow.pad(2);

            entity.stage.addListener(StageListener);

            InventoryWindow.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //Place
                    entity.ClickedOutsideInventory = false;
                    /*if (entity.CursorItem != null) {
                        WorldItem temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                        PlayState.Entities.add(temp);
                        entity.CursorItem = null;
                    }*/
                }
            });

            InventoryTable = new Table(entity.skin);

            for (int i = 1; i < entity.player.Inventory.length + 1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi - 1, entity.player);

                ItemBox.add(temp).size(32);

                InventoryTable.add(ItemBox).size(36).pad(0.5f);
                if (i % 6 == 0)
                    InventoryTable.row();
            }

            EquipmentTable = new Table(entity.skin);

            for (int i = 1; i < entity.player.Equipment.length + 1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi - 1, entity.player, true);

                ItemBox.add(temp).size(32);

                EquipmentTable.add(ItemBox).size(36).pad(0.5f).row();
            }

            InventoryWindow.add(InventoryTable);
            InventoryWindow.add(EquipmentTable).row();

            Screen.add(InventoryWindow);

            //__________________________________________________________

            final TkTextButton Close = new TkTextButton("Close", entity.skin);
            InventoryWindow.add(Close);
            InventoryWindow.row();

            Close.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.setState(UI_state.INGAMEUI);
                }
            });

        }

        @Override
        public void update(UIFSM entity) {
            Screen.setVisible(entity.Visible);
            ControllerCheck(Screen);
            entity.stage.act(Gdx.graphics.getDeltaTime());

        }

        @Override
        public void exit(UIFSM entity) {

            for (int j = 0; j < entity.player.Inventory.length; j++) {
                if (entity.player.Inventory[j] == null) {
                    entity.player.Inventory[j] = CursorItem;
                    CursorItem = null;
                    break;
                }
            }

            entity.stage.clear();
            entity.stage.removeListener(StageListener);
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    InventoryAndStorage() {

        Skin BackupSkin;

        private Table Screen;
        private Table InventoryWindow;

        private Table InventoryTable;
        private Table EquipmentTable;

        private Table StorageInventoryWindow;

        private Table StorageInventoryTable;

        ClickListener StageListener;

        @Override
        public void enter(UIFSM entity) {

            StageListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //drop out of inventory
                    if (entity.ClickedOutsideInventory) {
                        //Drop item in CursorItem
                        if (CursorItem != null) {
                            WorldItem temp = new WorldItem(0, 0, (int) entity.player.getIntereactBox().max.z, CursorItem);
                            temp = new WorldItem((int) entity.player.getIntereactBox().min.x, (int) entity.player.getIntereactBox().min.y, (int) entity.player.getIntereactBox().max.z, CursorItem);
                            PlayState.Entities.add(temp);
                            CursorItem = null;
                        }
                    }

                    entity.ClickedOutsideInventory = true;
                }
            };

            BackupSkin = entity.skin;
            Screen = new Table(entity.skin);
            Screen.setFillParent(true);
            entity.stage.addActor(Screen);

            InventoryWindow = new Table(entity.skin);
            InventoryWindow.setBackground("Window_grey_back");
            InventoryWindow.pad(2);

            entity.stage.addListener(StageListener);

            InventoryWindow.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //Place
                    entity.ClickedOutsideInventory = false;
                    /*if (entity.CursorItem != null) {
                        WorldItem temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                        PlayState.Entities.add(temp);
                        entity.CursorItem = null;
                    }*/
                }
            });

            InventoryTable = new Table(entity.skin);

            for (int i = 1; i < entity.player.Inventory.length + 1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi - 1, entity.player);

                ItemBox.add(temp).size(32);

                InventoryTable.add(ItemBox).size(36).pad(0.5f);
                if (i % 6 == 0)
                    InventoryTable.row();
            }

            EquipmentTable = new Table(entity.skin);

            for (int i = 1; i < entity.player.Equipment.length + 1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi - 1, entity.player, true);

                ItemBox.add(temp).size(32);

                EquipmentTable.add(ItemBox).size(36).pad(0.5f).row();
            }

            InventoryWindow.add(InventoryTable);
            InventoryWindow.add(EquipmentTable).row();

            Screen.add(InventoryWindow);

            StorageInventoryWindow = new Table(entity.skin);
            StorageInventoryWindow.setBackground("Window_grey_back");
            StorageInventoryWindow.pad(2);

            StorageInventoryWindow.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    //Place
                    entity.ClickedOutsideInventory = false;
                    /*if (entity.CursorItem != null) {
                        WorldItem temp = new WorldItem((int) player.getIntereactBox().max.x, (int) player.getIntereactBox().max.y, (int) player.getIntereactBox().max.z, entity.CursorItem);
                        PlayState.Entities.add(temp);
                        entity.CursorItem = null;
                    }*/
                }
            });

            StorageInventoryTable = new Table(entity.skin);

            for (int i = 1; i < entity.StorageOpen.getInventory().length + 1; i++) {
                int tempi = i;

                Table ItemBox = new Table(entity.skin);
                ItemBox.setBackground("Table_dialog");

                TkItem temp = new TkItem(entity.skin, tempi - 1, entity.player, entity.StorageOpen);

                ItemBox.add(temp).size(32);

                StorageInventoryTable.add(ItemBox).size(36).pad(0.5f);
                if (i % 6 == 0)
                    StorageInventoryTable.row();
            }

            StorageInventoryWindow.add(StorageInventoryTable);
            Screen.add(StorageInventoryWindow);

            //__________________________________________________________

            final TkTextButton Close = new TkTextButton("Close", entity.skin);
            InventoryWindow.add(Close);
            InventoryWindow.row();

            Close.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.setState(UI_state.INGAMEUI);
                }
            });

        }

        @Override
        public void update(UIFSM entity) {
            Screen.setVisible(entity.Visible);
            ControllerCheck(Screen);
            entity.stage.act(Gdx.graphics.getDeltaTime());

        }

        @Override
        public void exit(UIFSM entity) {

            for (int j = 0; j < entity.player.Inventory.length; j++) {
                if (entity.player.Inventory[j] == null) {
                    entity.player.Inventory[j] = CursorItem;
                    CursorItem = null;
                    break;
                }
            }

            entity.stage.clear();
            entity.stage.removeListener(StageListener);
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    SeedInventory() {

        Skin BackupSkin;

        private Table Screen;
        private Table UIWindow;
        private Table CraftingWindow;

        private Table RecipeList;
        private Table CraftingDescription;

        Table Container;

        int SelectedIndex = 0;
        int TotalRows = 1;

        TkTextButton Close;

        int ControllerDelay = 0;

        @Override
        public void enter(UIFSM entity) {

            BackupSkin = entity.skin;
            Screen = new Table(entity.skin);
            Screen.setFillParent(true);
            entity.stage.addActor(Screen);

            UIWindow = new Table(entity.skin);
            UIWindow.setBackground("Window_grey_back");

            CraftingWindow = new Table(entity.skin);
            UIWindow.add(CraftingWindow).fill();

            //--------------------

            CraftingDescription = new Table(entity.skin);
            CraftingDescription.setBackground("Window_red");

            Table CraftingDescTop = new Table(entity.skin);
            TypingLabel ItemName = new TypingLabel("", entity.skin);
            ItemName.setWidth(CraftingDescTop.getWidth());
            ItemName.setName("ItemTitle");
            if (InventorySlotSelected != -1) {
                ItemName.restart(ItemPresets.get(entity.player.Inventory[InventorySlotSelected].getID()).getName());
                ItemName.skipToTheEnd();
            } else {
                ItemName.restart("");
                ItemName.skipToTheEnd();
            }
            CraftingDescTop.add(ItemName).pad(5).row();

            TkItemIcon CrafintItemIcon = new TkItemIcon(entity.skin, -1);
            CrafintItemIcon.setName("CraftingIcon");
            CraftingDescTop.add(CrafintItemIcon).size(48).center().row();
            CraftingDescTop.row();

            CraftingDescription.add(CraftingDescTop).row();
            Table CraftingDescBottom = new Table(entity.skin);
            CraftingDescBottom.setBackground("Window_grey_TopOutline");
            CraftingDescription.add(CraftingDescBottom).padTop(5).expand().row();

            TypingLabel RescourLable = new TypingLabel("Description", entity.skin);
            RescourLable.skipToTheEnd();
            CraftingDescBottom.add(RescourLable).row();

            TypingLabel ItemDescription = new TypingLabel("Choose an item to view it's infomation!", entity.skin);
            ItemDescription.setWrap(true);
            ScrollPane DescPane = new ScrollPane(ItemDescription, entity.skin);
            DescPane.setScrollingDisabled(true, false);
            DescPane.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    entity.stage.setScrollFocus(DescPane);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    entity.stage.setScrollFocus(null);
                }
            });
            CraftingDescBottom.add(DescPane).width(130).expandY().pad(5).row();

            //--------------------
            RecipeList = new Table(entity.skin);
            ScrollPane RecipeScroll = new ScrollPane(RecipeList, entity.skin);
            RecipeScroll.setupOverscroll(5, 50f, 100f);
            RecipeScroll.addListener(new ClickListener() {
                @Override
                public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                    super.enter(event, x, y, pointer, fromActor);
                    entity.stage.setScrollFocus(RecipeScroll);
                }

                @Override
                public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                    super.exit(event, x, y, pointer, toActor);
                    entity.stage.setScrollFocus(null);
                }
            });

            CraftingWindow.add(RecipeScroll).width(150).height(200).padRight(1);

            Container = new Table(entity.skin);
            for (int i = 0; i < entity.player.Inventory.length; i++) {

                if (entity.player.Inventory[i] == null) {
                    continue;
                }

                int tempi = i;

                Table ItemGroup = new Table(entity.skin);
                ItemGroup.setBackground("Window_blank");

                TkItemIcon ItemIcon = new TkItemIcon(entity.skin, entity.player.Inventory[i].getID());
                ItemGroup.add(new Label(entity.player.Inventory[i].getQuantity() + "", entity.skin)).padRight(5).left();
                ItemGroup.add(ItemIcon).size(16).left();
                ItemGroup.add(new Label(entity.player.Inventory[i].getName(), entity.skin)).padLeft(5).left();
                Container.add(ItemGroup).fillX();
                ItemGroup.addListener(new ClickListener() {
                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        InventorySlotSelected = tempi;
                        TkItemIcon temp = CraftingDescTop.findActor("CraftingIcon");
                        temp.reload(entity.player.Inventory[InventorySlotSelected].getID());
                        ItemDescription.restart(ItemPresets.get(entity.player.Inventory[InventorySlotSelected].getID()).getDescription());
                        ItemDescription.skipToTheEnd();
                        ItemName.restart(ItemPresets.get(entity.player.Inventory[InventorySlotSelected].getID()).getName());
                        ItemName.skipToTheEnd();
                    }

                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        super.enter(event, x, y, pointer, fromActor);
                        ItemGroup.setBackground("Window_grey");
                        if (ctm.controllers.size() < 1)
                            SelectedIndex = tempi;
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        super.exit(event, x, y, pointer, toActor);
                        ItemGroup.setBackground("Window_blank");
                    }
                });
                ItemGroup.pack();

                Container.row();
                TotalRows++;
            }

            RecipeList.add(Container);

            RecipeList.row();


            //--------------------------------------

            CraftingWindow.add(CraftingDescription).top().width(150).height(200).padLeft(1);

            //--------------------------------------
            Screen.add(UIWindow);

            UIWindow.row();
            Close = new TkTextButton("Close", entity.skin);
            UIWindow.add(Close).padTop(2);

            Close.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.setState(UI_state.INGAMEUI);
                }
            });

        }

        @Override
        public void update(UIFSM entity) {
            ControllerDelay++;
            Screen.setVisible(entity.Visible);
            if (ControllerDelay >= 10) {
                SelectedIndex = ControllerCheckSeed(Container, SelectedIndex, TotalRows, Close, entity);
            }
            entity.stage.act(Gdx.graphics.getDeltaTime());

            for (int i = 0; i < Container.getCells().size; i++) {
                if (i == SelectedIndex) {
                    ((Table)Container.getCells().get(i).getActor()).setBackground("Window_grey");
                } else {
                    ((Table)Container.getCells().get(i).getActor()).setBackground("Window_blank");
                }
            }


        }

        @Override
        public void exit(UIFSM entity) {
            entity.stage.clear();
            SelectedIndex = 0;
            TotalRows = 1;
            ControllerDelay = 0;
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    },

    SeedSelection() {

        Skin BackupSkin;

        private Table Screen;
        private Table InventoryWindow;

        private Table InventoryTable;

        int SelectedIndex = 0;
        int TotalRows = 1;

        int ControllerDelay = 0;

        TkTextButton Close;

        ClickListener StageListener;

        @Override
        public void enter(UIFSM entity) {

            StageListener = new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    if (entity.ClickedOutsideInventory) {
                        //Close the window
                        entity.setState(UI_state.INGAMEUI);
                    }
                }
            };

            BackupSkin = entity.skin;
            Screen = new Table(entity.skin);
            Screen.setFillParent(true);
            entity.stage.addActor(Screen);

            InventoryWindow = new Table(entity.skin);
            InventoryWindow.setBackground("Window_grey_back");
            InventoryWindow.pad(2);

            entity.stage.addListener(StageListener);

            InventoryTable = new Table(entity.skin);

            for (int i = 0; i < entity.player.Inventory.length; i++) {
                int tempi = i;

                if (entity.player.Inventory[i] == null) {
                    continue;
                }

                Table ItemGroup = new Table(entity.skin);
                ItemGroup.setBackground("Window_blank");

                TkItemIcon ItemIcon = new TkItemIcon(entity.skin, entity.player.Inventory[i].getID());
                ItemGroup.add(new Label(entity.player.Inventory[i].getQuantity() + "", entity.skin)).padRight(5);
                ItemGroup.add(ItemIcon).size(16);
                ItemGroup.add(new Label(entity.player.Inventory[i].getName(), entity.skin)).padLeft(5);
                InventoryTable.add(ItemGroup).left();
                ItemGroup.addListener(new ClickListener() {

                    @Override
                    public void clicked(InputEvent event, float x, float y) {
                        //Plant the seed
                        if (entity.gsm.gameState instanceof PlayState) {
                            System.out.println("Clicked");
                            if (((PlayState) entity.gsm.gameState).TryToPlantSeed(entity.player.Inventory[tempi].getID())) {
                                entity.player.DeductFromInventory(entity.player.Inventory[tempi].getID(), 1);
                                entity.LastUsedSeedType = entity.player.Inventory[tempi];
                                entity.setState(UI_state.INGAMEUI);
                            }
                        }

                    }

                    @Override
                    public void enter(InputEvent event, float x, float y, int pointer, Actor fromActor) {
                        super.enter(event, x, y, pointer, fromActor);
                        ItemGroup.setBackground("Window_grey");
                        if (ctm.controllers.size() < 1)
                            SelectedIndex = tempi;
                    }

                    @Override
                    public void exit(InputEvent event, float x, float y, int pointer, Actor toActor) {
                        super.exit(event, x, y, pointer, toActor);
                        ItemGroup.setBackground("Window_blank");
                    }
                });
                ItemGroup.pack();

                InventoryTable.row();
                TotalRows++;
            }

            InventoryWindow.add(InventoryTable).row();

            Screen.add(InventoryWindow).height(160).row();

            //__________________________________________________________

            Close = new TkTextButton("Close", entity.skin);
            InventoryWindow.add(Close);

            Close.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    entity.setState(UI_state.INGAMEUI);
                }
            });

            InventoryWindow.pack();

        }

        @Override
        public void update(UIFSM entity) {
            ControllerDelay++;
            Screen.setVisible(entity.Visible);
            if (ControllerDelay >= 5) {
                SelectedIndex = ControllerCheckSeed(InventoryTable, SelectedIndex, TotalRows, Close, entity);
            }
            entity.stage.act(Gdx.graphics.getDeltaTime());

            for (int i = 0; i < InventoryTable.getCells().size; i++) {
                if (i == SelectedIndex) {
                    ((Table)InventoryTable.getCells().get(i).getActor()).setBackground("Window_grey");
                } else {
                    ((Table)InventoryTable.getCells().get(i).getActor()).setBackground("Window_blank");
                }
            }

        }

        @Override
        public void exit(UIFSM entity) {

            for (int j = 0; j < entity.player.Inventory.length; j++) {
                if (entity.player.Inventory[j] == null) {
                    entity.player.Inventory[j] = CursorItem;
                    CursorItem = null;
                    break;
                }
            }

            entity.stage.clear();
            entity.stage.removeListener(StageListener);
            ControllerDelay = 0;
            TotalRows = 1;
            SelectedIndex = 0;
        }

        @Override
        public boolean onMessage(UIFSM entity, Telegram telegram) {
            return false;
        }
    };

    public float ScrollingSpeed = 0;

    public void ControllerCheck(Table table) {
        if (ctm.controllers.size() > 0) {
            for (int i = 0; i < table.getCells().size; i++) {
                if (table.getCells().get(i).getActor() instanceof TkTextButton) {
                    int nextSelection = i;
                    if (((TkTextButton) table.getCells().get(i).getActor()).Selected) {
                        //Gdx.app.log("menu", "i is " + i);
                        if (ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                            ((TkTextButton) table.getCells().get(i).getActor()).Selected = false;
                            if (ScrollingSpeed % 5 == 0) {
                                nextSelection += 1;
                            }
                            ScrollingSpeed++;

                        } else if (ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                            ((TkTextButton) table.getCells().get(i).getActor()).Selected = false;
                            if (ScrollingSpeed % 5 == 0) {
                                nextSelection -= 1;
                            }
                            ScrollingSpeed++;
                        } else {
                            ScrollingSpeed = 0;
                        }

                        if (nextSelection < 0)
                            nextSelection = table.getCells().size - 1;
                        if (nextSelection >= table.getCells().size)
                            nextSelection = 0;

                        if (table.getCells().get(nextSelection).getActor() instanceof TkTextButton) {
                            ((TkTextButton) table.getCells().get(nextSelection).getActor()).Selected = true;
                        }

                        if (ctm.isButtonJustDown(0, ControlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                            //Gdx.app.debug("", "");
                            Array<EventListener> listeners = table.getCells().get(i).getActor().getListeners();
                            for (int b = 0; b < listeners.size; b++) {
                                if (listeners.get(b) instanceof ClickListener) {
                                    ((ClickListener) listeners.get(b)).clicked(null, 0, 0);
                                }
                            }
                        }

                        break;
                    } else if (i == table.getCells().size - 1) {
                        if (table.getCells().get(0).getActor() instanceof TkTextButton)
                            ((TkTextButton) table.getCells().get(0).getActor()).Selected = true;
                        else
                            ((TkTextButton) table.getCells().get(i).getActor()).Selected = true;
                    }
                }
            }

        }
    }

    public int ControllerCheckSeed(Table Inventorytable, int Index, int Size, TkTextButton Back, UIFSM entity) {
        if (ctm.controllers.size() > 0) {

            if (ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                if (ScrollingSpeed % 5 == 0) {
                    Index += 1;
                }
                ScrollingSpeed++;

            } else if (ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                if (ScrollingSpeed % 5 == 0) {
                    Index -= 1;
                }
                ScrollingSpeed++;
            } else {
                ScrollingSpeed = 0;
            }

        } else {

            if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                if (ScrollingSpeed % 5 == 0) {
                    Index += 1;
                }
                ScrollingSpeed++;

            } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                if (ScrollingSpeed % 5 == 0) {
                    Index -= 1;
                }
                ScrollingSpeed++;
            } else {
                ScrollingSpeed = 0;
            }

        }

        if (Index < 0)
            Index = Size - 1;
        if (Index >= Size)
            Index = 0;

        if (Index+1 != Size) {
            Back.Selected = false;
        } else {
            Back.Selected = true;
        }

        if (ctm.controllers.size() > 0) {
            if (ctm.isButtonJustDown(0, ControlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (Index+1 != Size) {
                    Array<EventListener> listeners = Inventorytable.getCells().get(Index).getActor().getListeners();
                    for (int b = 0; b < listeners.size; b++) {
                        if (listeners.get(b) instanceof ClickListener) {
                            ((ClickListener) listeners.get(b)).clicked(null, 0, 0);
                        }
                    }
                } else {
                    entity.setState(UI_state.INGAMEUI);

                }
            }
            if (ctm.isButtonJustDown(0, ControlerManager.buttons.BUTTON_B)) {
                entity.setState(UI_state.INGAMEUI);
            }
        } else {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                if (Index+1 != Size) {
                    Array<EventListener> listeners = Inventorytable.getCells().get(Index).getActor().getListeners();
                    for (int b = 0; b < listeners.size; b++) {
                        if (listeners.get(b) instanceof ClickListener) {
                            ((ClickListener) listeners.get(b)).clicked(null, 0, 0);
                        }
                    }
                } else {
                    entity.setState(UI_state.INGAMEUI);
                }
            }
        }


        return Index;
    }

}
