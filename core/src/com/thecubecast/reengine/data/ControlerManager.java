package com.thecubecast.reengine.data;

import com.badlogic.gdx.controllers.Controller;
import com.badlogic.gdx.controllers.ControllerListener;
import com.badlogic.gdx.controllers.Controllers;
import com.badlogic.gdx.controllers.PovDirection;
import com.badlogic.gdx.math.Vector3;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.FileUtils;
import sun.security.ssl.Debug;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

public class ControlerManager implements ControllerListener {

    public List<eachController> controllers = new ArrayList<>();

    public enum buttons {
        BUTTON_X,
        BUTTON_Y,
        BUTTON_A,
        BUTTON_B,
        BUTTON_BACK,
        BUTTON_START,
        BUTTON_LB,
        BUTTON_L3,
        BUTTON_RB,
        BUTTON_R3
    }

    public enum axisies {
        AXIS_LEFT_X, //-1 is left | +1 is right
        AXIS_LEFT_Y, //-1 is up | +1 is down
        AXIS_LEFT_TRIGGER, //value 0 to 1f
        AXIS_RIGHT_X, //-1 is left | +1 is right
        AXIS_RIGHT_Y, //-1 is up | +1 is down
        AXIS_RIGHT_TRIGGER //value 0 to -1f
    }

    public enum POVs {
        DPAD
    }

    public List<ControllerType> Types = new ArrayList<>();

    public ControlerManager() {
        Gson gson = new Gson();
        Controllers.addListener(this);

        //Search for config files in controllers folder
        File Directory = new File("Controllers");
        String[] extensions = new String[]{"json"};

        if (!Files.isDirectory(Directory.toPath())) {
            try {
                Files.createDirectory(Directory.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        List<File> files = (List<File>) FileUtils.listFiles(Directory, extensions, true);
        for (File file : files) {
            ControllerType typetemp = new ControllerType();

            try {
                typetemp = gson.fromJson(FileUtils.readFileToString(file, "UTF-8"), ControllerType.class);
            } catch (IOException e) {
                e.printStackTrace();
            }

            Types.add(typetemp);
        }

        for (int test2 = 0; test2 < Controllers.getControllers().size; test2++) {
            String Output = Controllers.getControllers().get(test2).getName() + " has connected!!";
            Debug.println("CTM", Output);
            eachController temp = new eachController(Controllers.getControllers().get(test2));
            controllers.add(temp);
        }


    }

    public void update() {
        for (int x = 0; x < controllers.size(); x++) {
            for (int i = 0; i < controllers.get(x).justPressedbuttons.length; i++) {
                controllers.get(x).justPressedbuttons[i] = false;
            }
        }
    }

    private Controller getController(int player) {
        if (player < controllers.size()) {
            return Controllers.getControllers().get(player);
        } else {
            return Controllers.getControllers().first();
        }

        //return Controllers.getControllers().first();
    }

    public void newController(String name) {
        ControllerType typetemp = new ControllerType();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Search for config files in controllers folder
        File Directory = new File("Controllers/" + name + ".json");

        String json = gson.toJson(typetemp);

        try {
            FileUtils.writeStringToFile(Directory, json, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Types.add(typetemp);

    }

    public void newController(String name, ControllerType ContraType) {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();

        //Search for config files in controllers folder
        File Directory = new File("Controllers/" + name + ".json");

        String json = gson.toJson(ContraType);

        try {
            FileUtils.writeStringToFile(Directory, json, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }

        Types.add(ContraType);

    }



    public void testButtons() {
        for (int test = 0; test < 20; test++) {
            for (int test2 = 0; test2 < controllers.size(); test2++) {
                if (Controllers.getControllers().get(test2).getButton(test)) {
                    String Output = Controllers.getControllers().get(test2).getName() + " button " + test + " is down";
                    Debug.println("CTM", Output);
                }
            }
        }
    }

    public void testInput() {
        for (int test = 0; test < 10; test++) {
            for (int test2 = 0; test2 < controllers.size(); test2++) {
                if (Math.abs(Controllers.getControllers().get(test2).getAxis(test)) > 0.2) {
                    String Output = Controllers.getControllers().get(test2).getName() + " axis " + test + " is " + Controllers.getControllers().get(test2).getAxis(test);
                    Debug.println("CTM", Output);
                }
            }
        }
        for (int test = 0; test < 10; test++) {
            for (int test2 = 0; test2 < controllers.size(); test2++) {
                if (Controllers.getControllers().get(test2).getPov(test) != PovDirection.center) {
                    String Output = Controllers.getControllers().get(test2).getName() + " Pov " + test + " is " + Controllers.getControllers().get(test2).getPov(test);
                    Debug.println("CTM", Output);
                }
            }
        }
    }

    public boolean isButtonDown(int player, buttons butt) {
        if (player < controllers.size()) {
            return Controllers.getControllers().get(player).getButton(getButtonToId(butt, player));
        } else {
            return false;
        }
    }

    public boolean isButtonJustDown(int player, buttons butt) {
        if (player < controllers.size()) {
            return controllers.get(player).justPressedbuttons[getButtonToId(butt, player)];
        } else {
            return false;
        }
    }


    public float getAxis(int player, axisies axises) {
        if (player < controllers.size()) {
            for (int controllerTypes = 0; controllerTypes < Types.size(); controllerTypes++) {
                if (Types.get(controllerTypes).Name.equals(controllers.get(player).controller.getName())) {
                    if (axises == axisies.AXIS_LEFT_Y) {
                        if (Types.get(controllerTypes).INVERT_LEFT_AXIS) {
                            return Controllers.getControllers().get(player).getAxis(getAxisToId(axises, player)) * -1;
                        } else {
                            return Controllers.getControllers().get(player).getAxis(getAxisToId(axises, player));
                        }
                    } else if (axises == axisies.AXIS_RIGHT_Y) {
                        if (Types.get(controllerTypes).INVERT_RIGHT_AXIS) {
                            return Controllers.getControllers().get(player).getAxis(getAxisToId(axises, player)) * -1;
                        } else {
                            return Controllers.getControllers().get(player).getAxis(getAxisToId(axises, player));
                        }
                    } else {
                        return Controllers.getControllers().get(player).getAxis(getAxisToId(axises, player));
                    }
                }
            }
        }
        return 0;
    }

    public PovDirection getPov(int player, POVs povies) {
        if (player < controllers.size()) {
            return Controllers.getControllers().get(player).getPov(getPOVToId(povies, player));
        } else {
            return PovDirection.center;
        }
    }

    private int getPOVToId(POVs POVies, int player) {
        String type = controllers.get(player).controller.getName();

        for (int controllerTypes = 0; controllerTypes < Types.size(); controllerTypes++) {
            if (Types.get(controllerTypes).Name.equals(type)) {
                switch (POVies) {
                    case DPAD:
                        return Types.get(controllerTypes).DPAD;
                }
            }
        }

        return -1;
    }

    private int getAxisToId(axisies axises, int player) {
        String type = controllers.get(player).controller.getName();

        for (int controllerTypes = 0; controllerTypes < Types.size(); controllerTypes++) {
            if (Types.get(controllerTypes).Name.equals(type)) {
                switch (axises) {
                    case AXIS_LEFT_X:
                        return Types.get(controllerTypes).AXIS_LEFT_X;
                    case AXIS_LEFT_Y:
                        return Types.get(controllerTypes).AXIS_LEFT_Y;
                    case AXIS_LEFT_TRIGGER:
                        return Types.get(controllerTypes).AXIS_LEFT_TRIGGER;
                    case AXIS_RIGHT_X:
                        return Types.get(controllerTypes).AXIS_RIGHT_X;
                    case AXIS_RIGHT_Y:
                        return Types.get(controllerTypes).AXIS_RIGHT_Y;
                    case AXIS_RIGHT_TRIGGER:
                        return Types.get(controllerTypes).AXIS_RIGHT_TRIGGER;
                }
            }
        }

        return -1;
    }

    private int getButtonToId(buttons butt, int player) {

        String type = controllers.get(player).controller.getName();

        for (int controllerTypes = 0; controllerTypes < Types.size(); controllerTypes++) {
            if (Types.get(controllerTypes).Name.equals(type)) {
                switch (butt) {
                    case BUTTON_X:
                        return Types.get(controllerTypes).BUTTON_X;
                    case BUTTON_Y:
                        return Types.get(controllerTypes).BUTTON_Y;
                    case BUTTON_A:
                        return Types.get(controllerTypes).BUTTON_A;
                    case BUTTON_B:
                        return Types.get(controllerTypes).BUTTON_B;
                    case BUTTON_BACK:
                        return Types.get(controllerTypes).BUTTON_BACK;
                    case BUTTON_START:
                        return Types.get(controllerTypes).BUTTON_START;
                    case BUTTON_LB:
                        return Types.get(controllerTypes).BUTTON_LB;
                    case BUTTON_L3:
                        return Types.get(controllerTypes).BUTTON_L3;
                    case BUTTON_RB:
                        return Types.get(controllerTypes).BUTTON_RB;
                    case BUTTON_R3:
                        return Types.get(controllerTypes).BUTTON_R3;
                }
            }
        }

        return -1;
    }

    @Override
    public void connected(Controller controller) {
        String Output = controller.getName() + " has connected!!";
        Debug.println("CTM", Output);
        eachController temp = new eachController(controller);
        controllers.add(temp);
    }

    @Override
    public void disconnected(Controller controller) {
        String Output = controller.getName() + " has disconnected!!";
        Debug.println("CTM", Output);
        for (int i = 0; i < controllers.size(); i++) {
            if (controllers.get(i).controller.equals(controller)) {
                controllers.remove(i);
            }
        }
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean buttonUp(Controller controller, int buttonCode) {
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        return false;
    }

    @Override
    public boolean povMoved(Controller controller, int povCode, PovDirection value) {
        return false;
    }

    @Override
    public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
        return false;
    }

    @Override
    public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
        return false;
    }

    public class eachController implements ControllerListener {
        private Controller controller;

        private boolean[] justPressedbuttons = new boolean[256];

        public eachController(Controller contr) {
            controller = contr;
            controller.addListener(this);
        }

        @Override
        public void connected(Controller controller) {

        }

        @Override
        public void disconnected(Controller controller) {

        }

        @Override
        public boolean buttonDown(Controller controller, int buttonCode) {
            justPressedbuttons[buttonCode] = true;
            return true;
        }

        @Override
        public boolean buttonUp(Controller controller, int buttonCode) {
            justPressedbuttons[buttonCode] = false;
            return true;
        }

        @Override
        public boolean axisMoved(Controller controller, int axisCode, float value) {
            return false;
        }

        @Override
        public boolean povMoved(Controller controller, int povCode, PovDirection value) {
            return false;
        }

        @Override
        public boolean xSliderMoved(Controller controller, int sliderCode, boolean value) {
            return false;
        }

        @Override
        public boolean ySliderMoved(Controller controller, int sliderCode, boolean value) {
            return false;
        }

        @Override
        public boolean accelerometerMoved(Controller controller, int accelerometerCode, Vector3 value) {
            return false;
        }
    }

}
