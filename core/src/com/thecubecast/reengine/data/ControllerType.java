package com.thecubecast.reengine.data;

public class ControllerType {

    public String Name = "";

    public int BUTTON_X;
    public int BUTTON_Y;
    public int BUTTON_A;
    public int BUTTON_B;
    public int BUTTON_BACK;
    public int BUTTON_START;
    public int BUTTON_LB;
    public int BUTTON_L3;
    public int BUTTON_RB;
    public int BUTTON_R3;

    public boolean INVERT_LEFT_AXIS = false;
    public boolean INVERT_RIGHT_AXIS = false;

    public int AXIS_LEFT_X; //-1 is left | +1 is right
    public int AXIS_LEFT_Y; //-1 is up | +1 is down
    public int AXIS_LEFT_TRIGGER; //value 0 to 1f
    public int AXIS_RIGHT_X; //-1 is left | +1 is right
    public int AXIS_RIGHT_Y; //-1 is up | +1 is down
    public int AXIS_RIGHT_TRIGGER; //value 0 to -1f

    public int DPAD;

}
