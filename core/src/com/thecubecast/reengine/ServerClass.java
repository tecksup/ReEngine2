package com.thecubecast.reengine;

import com.badlogic.gdx.ApplicationAdapter;
import kryonetwork.KryoServer;

import java.io.IOException;

public class ServerClass extends ApplicationAdapter {

    public void create() { // INIT FUNCTION
        KryoServer server = new KryoServer();
        try {
            server.main();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}