package com.thecubecast.reengine.worldobjects.ai.pawn;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.thecubecast.reengine.worldobjects.Bullet;
import com.thecubecast.reengine.worldobjects.entityprefabs.Pawn;

import java.util.Random;

import static com.thecubecast.reengine.data.GameStateManager.AudioM;

public enum EnemyState implements State<Smart> {

    IDLE() {
        @Override
        public void enter(Smart Student) { // INIT

        }

        @Override
        public void update(Smart Student) {
            //Check for changes, then update state
            BoundingBox AreaAround = new BoundingBox(new Vector3(Student.WorldObject.player.getPosition().x-75, Student.WorldObject.player.getPosition().y-50, 0), new Vector3(Student.WorldObject.player.getPosition().x+125, Student.WorldObject.player.getPosition().y+75, 32));
            if (Student.WorldObject.getHitbox().intersects(AreaAround)) {
                Student.getStateMachine().changeState(HUNTING);
            }
        }

        @Override
        public void exit(Smart Student) {

        }

        @Override
        public boolean onMessage(Smart Student, Telegram telegram) {
            return false;
        }
    },

    WANDER() {
        @Override
        public void enter(Smart Student) { // INIT
            ChooseRandomDestination(Student);
        }

        @Override
        public void update(Smart Student) {
            //Check for changes, then update state
            if (Student.getPath().nodes.size > 1) {

                Vector3 CenterOfEntity = new Vector3(Student.WorldObject.getPosition().x + Student.WorldObject.getSize().x/2, Student.WorldObject.getPosition().y + Student.WorldObject.getSize().y/2, Student.WorldObject.getPosition().z + Student.WorldObject.getSize().z/2);
                Vector3 PlayerCenter = new Vector3(Student.getPath().get(1).x * 16 + 8, Student.getPath().get(1).y * 16 + 8, Student.WorldObject.player.getPosition().z + Student.WorldObject.player.getSize().z/2);

                //Angle from CenterOfEntity to PlayerCenter for gun aiming
                Vector3 Angle = new Vector3(CenterOfEntity).sub(PlayerCenter);

                if (Angle.x < 0) {
                    ((Pawn)Student.WorldObject).Facing = false;
                } else {
                    ((Pawn)Student.WorldObject).Facing = true;
                }

                Vector2 Loc = new Vector2(Student.WorldObject.getPosition().x, Student.WorldObject.getPosition().y);
                Vector2 Dest = new Vector2(Student.getPath().get(1).x * 16, Student.getPath().get(1).y * 16);
                //temp.interpolate(new Vector2(Student.getPath().get(1).x * 16, Student.getPath().get(1).y * 16), 0.15f, Interpolation.linear);

                Loc.sub(Dest);
                Loc.clamp(-2, 2);

                Student.WorldObject.setVelocity(Student.WorldObject.getVelocity().x + (-1*Loc.x), Student.WorldObject.getVelocity().y + (-1*Loc.y), Student.WorldObject.getVelocity().z);
                if (Math.abs(Student.getPath().get(1).x * 16 - Student.WorldObject.getPosition().x) < 1) {
                    if (Math.abs(Student.getPath().get(1).y * 16 - Student.WorldObject.getPosition().y) < 1) {
                        //If the ai is within 150 pixels of the player. switch to attacking instead of tracking
                        BoundingBox AreaAround = new BoundingBox(new Vector3(Student.WorldObject.player.getPosition().x-75, Student.WorldObject.player.getPosition().y-50, 0), new Vector3(Student.WorldObject.player.getPosition().x+125, Student.WorldObject.player.getPosition().y+75, 32));
                        if (Student.WorldObject.getHitbox().intersects(AreaAround)) {
                            Student.getStateMachine().changeState(HUNTING);
                        } else {
                            Student.WorldObject.setPosition(Student.getPath().get(1).x * 16, Student.getPath().get(1).y * 16, 0);
                            Student.updatePath(true);
                        }
                    }
                }
            } else {
                ChooseRandomDestination(Student);
            }

        }

        @Override
        public void exit(Smart Student) {

        }

        private void ChooseRandomDestination(Smart Student) {
            Random temp = new Random();
            Vector2 Dest = new Vector2(Student.WorldObject.getPosition().x, Student.WorldObject.getPosition().y);
            if (temp.nextBoolean()) {
                Dest.x += temp.nextInt(5);
            } else {
                Dest.x += -temp.nextInt(5);
            }

            if (temp.nextBoolean()) {
                Dest.y += temp.nextInt(5);
            } else {
                Dest.y += -temp.nextInt(5);
            }

            Student.setDestination(new Vector3(Dest.x, Dest.y, 0));
        }

        @Override
        public boolean onMessage(Smart Student, Telegram telegram) {
            return false;
        }
    },

    HUNTING() {

        @Override
        public void enter(Smart Student) { // INIT
            Student.updatePath(true);
            Student.setDestination(Student.WorldObject.player.getPosition());
        }

        @Override
        public void update(Smart Student) {

            Student.setDestination(Student.WorldObject.player.getPosition());

            //Check for changes, then update state
            if (Student.getPath().nodes.size > 20) {
                Student.getStateMachine().changeState(IDLE);
            } else if (Student.getPath().nodes.size > 2) {

                BoundingBox AreaAround = new BoundingBox(new Vector3(Student.WorldObject.player.getPosition().x-50, Student.WorldObject.player.getPosition().y-50, 0), new Vector3(Student.WorldObject.player.getPosition().x+100, Student.WorldObject.player.getPosition().y+100, 32));
                if (Student.WorldObject.getHitbox().intersects(AreaAround)) {
                    Student.getStateMachine().changeState(ATTACKING);
                }

                Vector3 CenterOfEntity = new Vector3(Student.WorldObject.getPosition().x + Student.WorldObject.getSize().x/2, Student.WorldObject.getPosition().y + Student.WorldObject.getSize().y/2, Student.WorldObject.getPosition().z + Student.WorldObject.getSize().z/2);
                Vector3 PlayerCenter = new Vector3(Student.getPath().get(2).x * 16 + 8, Student.getPath().get(2).y * 16 + 8, Student.WorldObject.player.getPosition().z + Student.WorldObject.player.getSize().z/2);

                //Angle from CenterOfEntity to PlayerCenter for gun aiming
                Vector3 Angle = new Vector3(CenterOfEntity).sub(PlayerCenter);

                if (Angle.x < 0) {
                    ((Pawn)Student.WorldObject).Facing = false;
                } else {
                    ((Pawn)Student.WorldObject).Facing = true;
                }

                Vector2 Loc = new Vector2(Student.WorldObject.getPosition().x, Student.WorldObject.getPosition().y);
                Vector2 Dest = new Vector2(Student.getPath().get(2).x * 16, Student.getPath().get(2).y * 16);
                //temp.interpolate(new Vector2(Student.getPath().get(2).x * 16, Student.getPath().get(2).y * 16), 0.15f, Interpolation.linear);

                Loc.sub(Dest);
                Loc.clamp(-10, 10);

                Student.WorldObject.setVelocity(Student.WorldObject.getVelocity().x + (-1*Loc.x), Student.WorldObject.getVelocity().y + (-1*Loc.y), Student.WorldObject.getVelocity().z);
                if (Math.abs(Student.getPath().get(2).x * 16 - Student.WorldObject.getPosition().x) < 1) {
                    if (Math.abs(Student.getPath().get(2).y * 16 - Student.WorldObject.getPosition().y) < 1) {
                        Student.WorldObject.setPosition(Student.getPath().get(2).x * 16, Student.getPath().get(2).y * 16, 0);
                        Student.updatePath(true);
                    }
                }
            } else {
                Student.getStateMachine().changeState(ATTACKING);
            }
        }

        @Override
        public void exit(Smart Student) { // Leave this state

        }

        @Override
        public boolean onMessage(Smart Student, Telegram telegram) {
            return false;
        }
    },

    ATTACKING() {

        float TimeSinceLastShot = 0;
        float ReloadTime = 0.6f;

        float ShotsFired;

        float MaxBulletSpeed = 2.5f;

        @Override
        public void enter(Smart Student) { // INIT
            Student.updatePath(true);
        }

        @Override
        public void update(Smart Student) {
            TimeSinceLastShot += Gdx.graphics.getDeltaTime();
            //Check for changes, then update state

            BoundingBox AreaAround = new BoundingBox(new Vector3(Student.WorldObject.player.getPosition().x-50, Student.WorldObject.player.getPosition().y-50, 0), new Vector3(Student.WorldObject.player.getPosition().x+100, Student.WorldObject.player.getPosition().y+100, 32));
            if (!Student.WorldObject.getHitbox().intersects(AreaAround)) {
                Student.getStateMachine().changeState(HUNTING);
            }

            Vector3 CenterOfEntity = new Vector3(Student.WorldObject.getPosition().x + Student.WorldObject.getSize().x/2, Student.WorldObject.getPosition().y + Student.WorldObject.getSize().y/2, Student.WorldObject.getPosition().z + Student.WorldObject.getSize().z/2);
            Vector3 PlayerCenter = new Vector3(Student.WorldObject.player.getPosition().x + Student.WorldObject.player.getSize().x/2, Student.WorldObject.player.getPosition().y + Student.WorldObject.player.getSize().y/2, Student.WorldObject.player.getPosition().z + Student.WorldObject.player.getSize().z/2);

            //Angle from CenterOfEntity to PlayerCenter for gun aiming
            Vector3 Angle = new Vector3(CenterOfEntity).sub(PlayerCenter);

            if (Angle.x < 0) {
                ((Pawn)Student.WorldObject).Facing = false;
            } else {
                ((Pawn)Student.WorldObject).Facing = true;
            }

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

                if (Student.WorldObject.getHealth() > 30) {
                    if (ShotsFired < 1) {
                        ReloadTime = 0.1f;
                    } else {
                        ReloadTime = 0.5f;
                        ShotsFired = 0;
                    }
                    if (!((Pawn)Student.WorldObject).Facing) {
                        Student.GState.Entities.add(new Bullet((int) Student.WorldObject.getPosition().x + 14, (int) Student.WorldObject.getPosition().y, (int) Student.WorldObject.getPosition().z, Angle, Student.WorldObject));
                        AudioM.play("gun");
                    } else {
                        Student.GState.Entities.add(new Bullet((int) Student.WorldObject.getPosition().x - 4, (int) Student.WorldObject.getPosition().y, (int) Student.WorldObject.getPosition().z, Angle, Student.WorldObject));
                        AudioM.play("gun");
                    }
                    ShotsFired++;
                } else if (Student.WorldObject.getHealth() <= 30) {
                    if (ShotsFired < 2) {
                        ReloadTime = 0.08f;
                    } else {
                        ReloadTime = 0.7f;
                        ShotsFired = 0;
                    }
                    if (!((Pawn)Student.WorldObject).Facing) {
                        Student.GState.Entities.add(new Bullet((int) Student.WorldObject.getPosition().x + 14, (int) Student.WorldObject.getPosition().y, (int) Student.WorldObject.getPosition().z, Angle, Student.WorldObject));
                        AudioM.play("gun");
                    } else {
                        Student.GState.Entities.add(new Bullet((int) Student.WorldObject.getPosition().x - 4, (int) Student.WorldObject.getPosition().y, (int) Student.WorldObject.getPosition().z, Angle, Student.WorldObject));
                        AudioM.play("gun");
                    }
                    ShotsFired++;
                }
                TimeSinceLastShot = 0;
            }

        }

        @Override
        public void exit(Smart Student) { // Leave this state

        }

        @Override
        public boolean onMessage(Smart Student, Telegram telegram) {
            return false;
        }
    }

}
