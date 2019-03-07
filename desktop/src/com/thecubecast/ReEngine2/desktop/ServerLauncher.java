package com.thecubecast.ReEngine2.desktop;

import com.badlogic.gdx.backends.headless.HeadlessApplication;
import com.badlogic.gdx.backends.headless.HeadlessApplicationConfiguration;
import com.thecubecast.reengine.ServerClass;

public class ServerLauncher {
    public static void main(String[] args) {

        HeadlessApplicationConfiguration config = new HeadlessApplicationConfiguration();
        new HeadlessApplication(new ServerClass(), config);
    }
}
