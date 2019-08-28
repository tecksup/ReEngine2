package com.thecubecast.reengine.worldobjects.ai.aistates;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.gamestates.GameState;
import com.thecubecast.reengine.gamestates.PlayState;
import com.thecubecast.reengine.worldobjects.Bullet;
import com.thecubecast.reengine.worldobjects.ai.Smart;
import com.thecubecast.reengine.worldobjects.entityprefabs.Pawn;

import java.util.Random;

import static com.thecubecast.reengine.data.GameStateManager.AudioM;

public enum PawnStates implements State<Smart> {

    IDLE() {
        @Override
        public void enter(Smart ParrentAI) { // INIT

        }

        @Override
        public void update(Smart ParrentAI) {
            //Check for changes, then update state
            BoundingBox AreaAround = new BoundingBox(new Vector3(((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().x-75, ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().y-50, 0), new Vector3(((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().x+125, ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().y+75, 32));
            if (ParrentAI.WorldObject.getHitbox().intersects(AreaAround)) {
                ParrentAI.getStateMachine().changeState(HUNTING);
            }
        }

        @Override
        public void exit(Smart ParrentAI) {

        }

        @Override
        public boolean onMessage(Smart ParrentAI, Telegram telegram) {
            return false;
        }
    },

    HUNTING() {

        @Override
        public void enter(Smart ParrentAI) { // INIT
            ParrentAI.setDestination(((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition());
            ParrentAI.updatePath(true);
            //System.out.println(ParrentAI.getPath().nodes.size + ": Size");
        }

        @Override
        public void update(Smart ParrentAI) {

            ParrentAI.setDestination(((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition());

            //Check for changes, then update state
            if (ParrentAI.getPath().nodes.size > 20) {
                ParrentAI.getStateMachine().changeState(IDLE);
            } else if (ParrentAI.getPath().nodes.size > 2) {
                BoundingBox AreaAround = new BoundingBox(new Vector3(((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().x-50, ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().y-50, 0), new Vector3(((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().x+100, ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().y+100, 32));
                if (ParrentAI.WorldObject.getHitbox().intersects(AreaAround)) {
                    ParrentAI.getStateMachine().changeState(ATTACKING);
                    return;
                }
                Vector3 CenterOfEntity = new Vector3(ParrentAI.WorldObject.getPosition().x + ParrentAI.WorldObject.getSize().x/2, ParrentAI.WorldObject.getPosition().y + ParrentAI.WorldObject.getSize().y/2, ParrentAI.WorldObject.getPosition().z + ParrentAI.WorldObject.getSize().z/2);
                Vector3 PlayerCenter = new Vector3(ParrentAI.getPath().get(0).x * 16 + 8, ParrentAI.getPath().get(0).y * 16 + 8, ((PlayState) ParrentAI.WorldObject.getAI().GState).player.getPosition().z + ((PlayState) ParrentAI.WorldObject.getAI().GState).player.getSize().z/2);

                //Angle from CenterOfEntity to PlayerCenter for gun aiming
                Vector3 Angle = new Vector3(CenterOfEntity).sub(PlayerCenter);

                ((Pawn) ParrentAI.WorldObject).Facing = !(Angle.x < 0);

                Vector2 Loc = new Vector2(ParrentAI.WorldObject.getPosition().x, ParrentAI.WorldObject.getPosition().y);
                Vector2 Dest = new Vector2(ParrentAI.getPath().get(2).x * 16, ParrentAI.getPath().get(2).y * 16);
                //temp.interpolate(new Vector2(Student.getPath().get(2).x * 16, Student.getPath().get(2).y * 16), 0.15f, Interpolation.linear);

                Loc.sub(Dest);
                Loc.clamp(-10, 10);

                ParrentAI.WorldObject.setVelocity(ParrentAI.WorldObject.getVelocity().x + (-1*Loc.x), ParrentAI.WorldObject.getVelocity().y + (-1*Loc.y), ParrentAI.WorldObject.getVelocity().z);
                if (Math.abs(ParrentAI.getPath().get(2).x * 16 - ParrentAI.WorldObject.getPosition().x) < 1) {
                    if (Math.abs(ParrentAI.getPath().get(2).y * 16 - ParrentAI.WorldObject.getPosition().y) < 1) {
                        ParrentAI.WorldObject.setPosition(ParrentAI.getPath().get(2).x * 16, ParrentAI.getPath().get(2).y * 16, 0);
                        ParrentAI.updatePath(true);
                    }
                }
            } else {
                ParrentAI.getStateMachine().changeState(ATTACKING);
            }
        }

        @Override
        public void exit(Smart ParrentAI) { // Leave this state

        }

        @Override
        public boolean onMessage(Smart ParrentAI, Telegram telegram) {
            return false;
        }
    },

    ATTACKING() {

        float TimeSinceLastShot = 0;
        float ReloadTime = 0.6f;

        float ShotsFired;

        float MaxBulletSpeed = 2.5f;

        @Override
        public void enter(Smart ParrentAI) { // INIT
            ParrentAI.updatePath(true);
        }

        @Override
        public void update(Smart ParrentAI) {
            TimeSinceLastShot += Gdx.graphics.getDeltaTime();
            //Check for changes, then update state

            BoundingBox AreaAround = new BoundingBox(new Vector3(((PlayState)(ParrentAI.WorldObject).getAI().GState).player.getPosition().x-50, ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().y-50, 0), new Vector3(((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().x+100, ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().y+100, 32));
            if (!ParrentAI.WorldObject.getHitbox().intersects(AreaAround)) {
                ParrentAI.getStateMachine().changeState(HUNTING);
            }

            Vector3 CenterOfEntity = new Vector3(ParrentAI.WorldObject.getPosition().x + ParrentAI.WorldObject.getSize().x/2, ParrentAI.WorldObject.getPosition().y + ParrentAI.WorldObject.getSize().y/2, ParrentAI.WorldObject.getPosition().z + ParrentAI.WorldObject.getSize().z/2);
            Vector3 PlayerCenter = new Vector3(((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().x + ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getSize().x/2, ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().y + ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getSize().y/2, ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getPosition().z + ((PlayState)ParrentAI.WorldObject.getAI().GState).player.getSize().z/2);

            //Angle from CenterOfEntity to PlayerCenter for gun aiming
            Vector3 Angle = new Vector3(CenterOfEntity).sub(PlayerCenter);

            ((Pawn) ParrentAI.WorldObject).Facing = !(Angle.x < 0);

            Angle.clamp(0, MaxBulletSpeed);

            //If the x velocity is greater than 0, set it to the constant bullet speed, 2;
            if (Angle.x > 0) {
                Angle.x = -Angle.x;
            } else if (Angle.x < 0) {
                Angle.x = -1*Angle.x;
            } else {
                Angle.x = 0;
            }

            if (Angle.y > 0) {
                Angle.y = -Angle.y;
            } else if (Angle.y < 0) {
                Angle.y = -1*Angle.y;
            } else {
                Angle.y = 0;
            }

            if (Angle.z > 0) {
                Angle.z = -Angle.z;
            } else if (Angle.z < 0) {
                Angle.z = -1*Angle.z;
            } else {
                Angle.z = 0;
            }

            if (TimeSinceLastShot > ReloadTime) {

                if (ParrentAI.WorldObject.getHealth() > 30) {
                    if (ShotsFired < 1) {
                        ReloadTime = 0.1f;
                    } else {
                        ReloadTime = 0.5f;
                        ShotsFired = 0;
                    }
                    if (!((Pawn)ParrentAI.WorldObject).Facing) {
                        GameState.Entities.add(new Bullet((int) ParrentAI.WorldObject.getPosition().x + 14, (int) ParrentAI.WorldObject.getPosition().y, (int) ParrentAI.WorldObject.getPosition().z, Angle, ParrentAI.WorldObject));
                        AudioM.play("gun");
                    } else {
                        GameState.Entities.add(new Bullet((int) ParrentAI.WorldObject.getPosition().x - 4, (int) ParrentAI.WorldObject.getPosition().y, (int) ParrentAI.WorldObject.getPosition().z, Angle, ParrentAI.WorldObject));
                        AudioM.play("gun");
                    }
                    ShotsFired++;
                } else if (ParrentAI.WorldObject.getHealth() <= 30) {
                    if (ShotsFired < 2) {
                        ReloadTime = 0.08f;
                    } else {
                        ReloadTime = 0.7f;
                        ShotsFired = 0;
                    }
                    if (!((Pawn)ParrentAI.WorldObject).Facing) {
                        GameState.Entities.add(new Bullet((int) ParrentAI.WorldObject.getPosition().x + 14, (int) ParrentAI.WorldObject.getPosition().y, (int) ParrentAI.WorldObject.getPosition().z, Angle, ParrentAI.WorldObject));
                        AudioM.play("gun");
                    } else {
                        GameState.Entities.add(new Bullet((int) ParrentAI.WorldObject.getPosition().x - 4, (int) ParrentAI.WorldObject.getPosition().y, (int) ParrentAI.WorldObject.getPosition().z, Angle, ParrentAI.WorldObject));
                        AudioM.play("gun");
                    }
                    ShotsFired++;
                }
                TimeSinceLastShot = 0;
            }

        }

        @Override
        public void exit(Smart ParrentAI) { // Leave this state

        }

        @Override
        public boolean onMessage(Smart ParrentAI, Telegram telegram) {
            return false;
        }
    }
}
