package kryonetwork;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;
import com.esotericsoftware.kryonet.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class KryoServer {

    static String ServerPassword = "locked";

    public void main() throws IOException {

        final List<User> Users = new ArrayList<User>();
        final List<Integer> Admins = new ArrayList<Integer>();

        //final TiledMap tiledMap = new ServerTmxMapLoader().load("Saves/Save1/map.tmx");

        final Server server = new Server();

        //Sets up the classes
        Network.register(server);

        server.start();
        System.out.println("Server Started!");
        server.bind(54555, 54777);

        server.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof User) {
                    User request = (User) object;
                    //System.out.println(request.username + ": " + request.text);
                    //System.out.println(request.username);

                    functions(Users, Admins, server, request.text, connection);

                    for (int i = 0; i < Users.size(); i++) {
                        if (Users.get(i).ID == connection.hashCode()) {
                            Users.get(i).x = request.x;
                            Users.get(i).y = request.y;
                            Users.get(i).angle = request.angle;
                        }
                    }

                    ServerSide response = new ServerSide();
                    response.Users = Users;
                    response.text = "";

                    //response.text = request.username + " is at " + (response.x) + " " + (response.y);
                    connection.sendTCP(response);
                    //connection.sendUDP(response);

                }
                if (object instanceof Connect) {
                    Join(Users, (Connect)object, connection);
                    updateClient(Users, server);

                }
            }

            public void disconnected(Connection connection) {
                Leave(Users, server, connection);

            }
        });

        while (true) {

            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

    }


    public static void updateClient(List<User> Users, Server server) {
        ServerSide send = new ServerSide();
        send.Users = Users;
        send.text = "lol";
        server.sendToAllTCP(send);
    }

    public static void functions(List<User> Users, List<Integer> Admins, Server server, String text, Connection connection) {

        boolean Administrator = false;
        for (int i = 0; i < Admins.size(); i++) {
            if (Admins.get(i) == connection.hashCode()) {
                Administrator = true;
                break;
            }
        }

        if (text.equals("/users")) {
            ServerSide send = new ServerSide();
            send.text = "Online users: ";
            for (int i = 0; i < Users.size(); i++) {
                send.text += Users.get(i).username;
                send.text += ", ";
            }
            connection.sendTCP(send);
        }

        if (text.split(" ")[0].equals("/login")) {
            if (text.split(" ")[1].equals(ServerPassword)) {
                Admins.add(connection.hashCode());
                System.out.println(connection.hashCode() + " has logged in as admin");
            }
        }

        if (text.equals("/test")) {
            System.out.println("sent from: " + connection.hashCode());
            for (int i = 0; i < Users.size(); i++) {
                System.out.println(Users.get(i).username + ": " + Users.get(i).ID);
            }
        }

        if (text.equals("/help")) {
            ServerSide send = new ServerSide();
            send.text = "type 'users' to see who is online. ";
            connection.sendTCP(send);
        }


        if (Administrator) {
            if (text.equals("/spawn")) {
                TpToSpawn(Users, server);
            }

            if (text.equals("/admins")) {
                ServerSide send = new ServerSide();
                send.text = "Online Administrators: ";
                for (int i = 0; i < Admins.size(); i++) {
                    for (int l = 0; l < Users.size(); l++) {
                        if (Users.get(l).ID == Admins.get(i)) {
                            send.text += Users.get(i).username;
                            send.text += ", ";
                        }
                    }
                }
                connection.sendTCP(send);
            }
        } else if (text.length() >= 1) {
            for (int i = 0; i < Users.size(); i++) {
                if (Users.get(i).ID == connection.hashCode()) {
                    ServerSide send = new ServerSide();
                    send.text = Users.get(i).username + ": " + text;
                    server.sendToAllTCP(connection.getID());
                    break;
                }
            }
        }
    }

    public static void Join(List<User> Users, Connect connectToken, Connection connet) {
        User temp = new User();
        temp.ID = connet.hashCode();
        temp.text = "";
        temp.x = 0;
        temp.y = 0;
        temp.angle = 0;
        temp.username = connectToken.username;
        Users.add(temp);
        ServerSide send = new ServerSide();
        send.text = "ID:" + connet.hashCode();
        send.Users = Users;
        System.out.println("User " + temp.username + " has joined!");
        connet.sendTCP(send);

    }

    public static void Leave(List<User> Users, Server server, Connection connet) {
        for (int i = 0; i < Users.size(); i++) {
            if (Users.get(i).ID == connet.hashCode()) {
                ServerSide send = new ServerSide();
                send.text = Users.get(i).username + " has left the game!";
                System.out.println(send.text);
                Users.remove(i);
                send.Users = Users;
                server.sendToAllTCP(send);
            }
        }
    }

    public static void TpToSpawn(List<User> Users, Server server) {
        for (int i = 0; i < Users.size(); i++) {
            Users.get(i).x = 0;
            Users.get(i).y = 0;
        }
        ServerSide send = new ServerSide();
        send.text = "Everyone has been brought to spawn!!";
        System.out.println(send.text);
        send.Users = Users;
        server.sendToAllTCP(send);
    }

}