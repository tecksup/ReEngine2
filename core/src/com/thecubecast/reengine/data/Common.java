package com.thecubecast.reengine.data;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;

import java.awt.*;

public class Common {

    public static void ProperShutdown(GameStateManager gsm) {
        Common.print("Proper Shutdown Commenced!");
        Common.print("Running all pre-shutdown protocalls...");
        //Then run all the code u want to do before it ends
        //Cleanup(); SaveAll();

        gsm.Shutdown();

        //Now Finish It!
        Common.print("Terminating Program!");
        Gdx.app.exit();
    }

    public static void ProperShutdown() {
        Common.print("Proper Shutdown Commenced!");
        Common.print("Running all pre-shutdown protocalls...");
        //Then run all the code u want to do before it ends
        //Cleanup(); SaveAll();
        //Now Finish It!
        Common.print("Terminating Program!");
        Gdx.app.exit();
    }

    public static int GetMonitorSizeW() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        return gd.getDisplayMode().getWidth();
    }

    public static int GetMonitorSizeH() {
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        return gd.getDisplayMode().getHeight();
    }

    public static void print(String Output) {
        System.out.println(Output);
    }

    public static void sleep(int Time) {
        try {
            // thread to sleep for 1000 milliseconds
            Thread.sleep(Time);
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static int roundDown(float x) {

        int output = (int) Math.floor(x);

        return output;
    }

    public static int roundUp(float x) {

        int output = (int) Math.ceil(x);

        return output;
    }

    public static float GetAngle(Vector2 center, Vector2 point) {
        double angleRadians = Math.atan2(point.y - center.y, point.x - center.x);
        float angleDegrees = (float) (angleRadians * MathUtils.radiansToDegrees) * -1;
        return angleDegrees;
    }

    public static void updategsmValues(GameStateManager gsm, Vector3 pos) {
		/*gsm.MouseX = (int) pos.x;
		gsm.MouseY = (int) pos.y;
		gsm.MouseClick[1] = (int) pos.x;
		gsm.MouseClick[2] = (int) pos.y;
		gsm.MouseDrag[1] = (int) pos.x;
		gsm.MouseDrag[2] = (int) pos.y;
		*/
    }
}