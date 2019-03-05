package com.thecubecast.ReEngine2.desktop;

import com.badlogic.gdx.Graphics;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Window;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3WindowListener;
import com.thecubecast.reengine.data.Common;
import com.thecubecast.reengine.MainClass;

public class DesktopLauncher {
    public static void main(String[] args) {

        DcpTexturePackerManager texturePackerManager = new DcpTexturePackerManager();
        texturePackerManager.checkWhetherToPackImages();

        //set variables from the settings file
        boolean Display_Fullscreen = false;
        boolean Display_WindowedFull = false;

        final Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();

        config.setWindowListener(new Lwjgl3WindowListener() {

            @Override
            public void created(Lwjgl3Window window) {

            }

            @Override
            public void iconified(boolean isIconified) {
                //Minimized
                Common.print("Minimized Window");
            }

            @Override
            public void maximized(boolean isMaximized) {

            }

            @Override
            public void focusLost() {

            }

            @Override
            public void focusGained() {

            }

            @Override
            public boolean closeRequested() {
                Common.print("clicked X");
                Common.ProperShutdown();
                return true;
            }

            @Override
            public void filesDropped(String[] files) {

            }

            @Override
            public void refreshRequested() {

            }
        });

        if (Display_Fullscreen) { //Fullscreen
            // get the current display mode of the monitor the window is on
            Graphics.DisplayMode primaryDesktopMode = Lwjgl3ApplicationConfiguration.getDisplayMode();
            // set the window to fullscreen mode
            config.setFullscreenMode(primaryDesktopMode);
            //Gdx.graphics.setFullscreenMode(mode);
        } else {
            if (Display_WindowedFull) { //Windowed Fullscreen
                config.setMaximized(true);
                config.setDecorated(false);
                Common.print("Windowed-fullscreen");
            } else { //Windowed
                Common.print("Windowed");
                config.setResizable(false);
                config.setWindowedMode(1920, 1080);
                //config.setMaximized(true);
            }
        }
        config.setWindowIcon("icon.png");
        config.setTitle("reengine");
        config.useVsync(true);
        new Lwjgl3Application(new MainClass(), config);
    }
}
