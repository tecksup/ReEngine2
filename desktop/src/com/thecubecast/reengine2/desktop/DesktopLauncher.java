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

        MainClass TheBigclass = new MainClass();

        //TexturePacker.process("../../images/atlas-T", "../../../core/assets/", "Tiles");

        //The Image Packing that happens on startup
        DcpTexturePackerManager texturePackerManager = new DcpTexturePackerManager();
        texturePackerManager.checkWhetherToPackImages();

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
                //Common.print("clicked X");
                Common.ProperShutdown(TheBigclass.gsm);
                return true;
            }

            @Override
            public void filesDropped(String[] files) {

            }

            @Override
            public void refreshRequested() {

            }
        });

        config.setResizable(true);
        config.setWindowedMode(1920, 1080);
        config.setMaximized(true);

        config.setWindowIcon("icon.png");
        config.setTitle("ReEngine 2");
        config.useVsync(true);
        new Lwjgl3Application(TheBigclass, config);
    }
}
