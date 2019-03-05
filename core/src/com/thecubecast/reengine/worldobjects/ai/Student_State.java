package com.thecubecast.reengine.worldobjects.ai;

import com.badlogic.gdx.ai.fsm.State;
import com.badlogic.gdx.ai.msg.Telegram;

public enum Student_State implements State<Smart> {

    IDLE() {
        @Override
        public void enter(Smart Student) { // INIT

        }

        @Override
        public void update(Smart Student) {
            //Check for changes, then update state

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

        }

        @Override
        public void update(Smart Student) {
            //Check for changes, then update state
            //Student.WorldObject.setPosition(Student.WorldObject.getPosition().x + 1, Student.WorldObject.getPosition().y);

        }

        @Override
        public void exit(Smart Student) {

        }

        @Override
        public boolean onMessage(Smart Student, Telegram telegram) {
            return false;
        }
    },

    WALKING_TO_DESTINATION() {
        @Override
        public void enter(Smart Student) { // INIT
            //Student.updatePath(true);
        }

        @Override
        public void update(Smart Student) {
            //Check for changes, then update state

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
