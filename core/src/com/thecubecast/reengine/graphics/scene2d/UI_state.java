package com.thecubecast.reengine.graphics.scene2d;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Array;
import com.rafaskoberg.gdx.typinglabel.TypingLabel;
import com.thecubecast.reengine.data.Common;
import com.thecubecast.reengine.data.GameStateManager;
import com.thecubecast.reengine.data.ControlerManager;

import static com.badlogic.gdx.scenes.scene2d.actions.Actions.removeActor;
import static com.thecubecast.reengine.data.Common.GetMonitorSizeH;
import static com.thecubecast.reengine.data.Common.GetMonitorSizeW;
import static com.thecubecast.reengine.data.GameStateManager.AudioM;
import static com.thecubecast.reengine.data.GameStateManager.ctm;

public enum UI_state implements State<UIFSM> {

    Home() {

        private Table table;

        @Override
        public void enter(UIFSM entity) {

            table = new Table();
            table.setFillParent(true);
            entity.stage.addActor(table);

            TypingLabel temptemptemp = new TypingLabel("{COLOR=yellow}Grav-fusion", entity.skin);
            temptemptemp.skipToTheEnd();
            table.add(temptemptemp);
            table.row();

            final TkTextButton PlayState = new TkTextButton("Start", entity.skin);
            table.add(PlayState).pad(2);
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
                    //gsm.Audio.stopMusic("8-bit-Digger");
                    //GetLogin("", "");
                    Gdx.app.getPreferences("properties").putString("Username", "");
                    Gdx.app.getPreferences("properties").flush();
                    entity.gsm.setState(GameStateManager.State.PLAY);
                    PlayState.setText("Loading");
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

            final TkTextButton Graphics = new TkTextButton("graphics", entity.skin);
            table.add(Graphics).pad(2);
            table.row();

            //final TkTextButton Controls = new TkTextButton("Controls", entity.skin);
            //table.add(Controls).pad(2);
            //table.row();

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

            //Controls.addListener(new ClickListener() {
            //    @Override
            //    public void clicked(InputEvent event, float x, float y) {
            //        entity.stateMachine.changeState(UI_state.Controls);
            //    }
            //});

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

            Health = new Label("HEALTH ", entity.skin);
            Healthbar = new ProgressBar(0, 10, 1, false,entity.skin);

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
    };

    public void ControllerCheck(Table table) {
        if (ctm.controllers.size() > 0) {
            for (int i = 0; i < table.getCells().size; i++) {
                if (table.getCells().get(i).getActor() instanceof TkTextButton) {
                    int nextSelection = i;
                    if (((TkTextButton) table.getCells().get(i).getActor()).Selected) {
                        //Gdx.app.log("menu", "i is " + i);
                        if (ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y) < -0.2f || Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
                            ((TkTextButton) table.getCells().get(i).getActor()).Selected = false;
                            nextSelection += 1;

                        } else if (ctm.getAxis(0, ControlerManager.axisies.AXIS_LEFT_Y) > 0.2f || Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
                            ((TkTextButton) table.getCells().get(i).getActor()).Selected = false;
                            nextSelection -= 1;
                        }

                        if (nextSelection < 0)
                            nextSelection = table.getCells().size - 1;
                        if (nextSelection >= table.getCells().size)
                            nextSelection = 0;

                        if (table.getCells().get(nextSelection).getActor() instanceof TkTextButton) {
                            ((TkTextButton) table.getCells().get(nextSelection).getActor()).Selected = true;
                        }

                        if (ctm.isButtonJustDown(0, ControlerManager.buttons.BUTTON_A) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                            Gdx.app.debug("", "");
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


}
