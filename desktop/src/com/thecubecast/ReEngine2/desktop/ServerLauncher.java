package com.thecubecast.ReEngine2.desktop;

import kryonetwork.KryoServer;

import java.io.IOException;

public class ServerLauncher {
    public static void main(String[] args) {

        KryoServer server = new KryoServer();
        try {
            server.main();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
