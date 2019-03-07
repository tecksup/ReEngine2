package kryonetwork;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.kryonet.Client;
import com.esotericsoftware.kryonet.Connection;
import com.esotericsoftware.kryonet.Listener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Just make sure to do this in the init
 * network = new KryoClient("username", "Ip Address", int TCP, int UDP);
 * <p>
 * and this in the update
 * network.Update("");
 **/

/**
 * A great example of the render (Update) method
 *
 * float x = network.GetClient().x;
 float y = network.GetClient().y;

 if (Gdx.input.isKeyPressed(Keys.UP)) { //KeyHit
 y += 1;
 }
 if (Gdx.input.isKeyPressed(Keys.DOWN)) { //KeyHit
 y -= 1;
 }
 if (Gdx.input.isKeyPressed(Keys.LEFT)) { //KeyHit
 x -= 1;
 }
 if (Gdx.input.isKeyPressed(Keys.RIGHT)) { //KeyHit
 x += 1;
 }

 network.updateClientPos(x, y);

 Gdx.gl.glClearColor(1, 0, 0, 1);
 Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
 batch.begin();
 if (network.Users.size() != 0) {
 for(int l=0; l< network.Users.size(); l++){
 batch.draw(img, (network.Users.get(l).x*80),  (network.Users.get(l).y*80));
 }
 }
 batch.end();
 network.Update("");
 }
 *
 * **/

public class KryoClient {

    int CustomID;
    String Username;
    public List<User> Users;
    int MapID;

    public boolean established = false;
    ReentrantLock lock;

    public Client client;

    public KryoClient(String username, String IpAdress, int TCPPort, int UDPport) throws IOException {

        lock = new ReentrantLock();

        Users = new ArrayList<User>();

        client = new Client();

        Username = username;

        //Sets up the classes
        Network.register(client);

        client.start();
        //InetAddress address = client.discoverHost(TCPPort, 5000);
        //System.out.println(address);
        client.connect(5000, IpAdress, TCPPort, UDPport); // TCP THEN UDP PORTS

        while (client.isConnected() != true) { // Keep trying to connect
            client.connect(5000, IpAdress, TCPPort, UDPport); // TCP THEN UDP PORTS
        }

        Connect ConnectToken = new Connect();
        ConnectToken.username = Username;
        client.sendTCP(ConnectToken);

        client.addListener(new Listener() {
            public void received(Connection connection, Object object) {
                if (object instanceof User) {
                    User response = (User) object;
                    //updatePos(response.x, response.y);
                    System.out.println(response.text);
                }
                if (object instanceof ServerSide) {

                    ServerSide response = (ServerSide) object;
                    if (response.text.split(":")[0].equals("ID")) {
                        CustomID = Integer.parseInt(response.text.split(":")[1]);
                        System.out.println(response.text);
                    } else if (response.text.length() > 0) {
                        System.out.println(response.text);
                    }

                    //Handle the other shit

                    lock.lock();  // block until condition holds
                    try {
                        if (response.Users != null) {
                            //Common.print("Updating Users");
                            //User temp = GetClient();
                            Users = null;
                            Users = response.Users;
                            established = true;
                        }
                    } finally {
                        lock.unlock();
                    }

                }

            }
        });

    }

    public void Update() {
        User request = new User();
        request.username = Username;
        request.text = "";
        request.ID = CustomID;
        for (int i = 0; i < GetUsers().size(); i++) {
            if (GetUsers().get(i).ID == CustomID) {
                request.x = GetUsers().get(i).x;
                request.y = GetUsers().get(i).y;
                request.angle = GetUsers().get(i).angle;
            }
        }
        client.sendTCP(request);
    }

    public void Send(String text) {
        User request = new User();
        request.username = Username;
        request.text = text;
        request.ID = CustomID;
        client.sendTCP(request);
    }

    public User GetClient() {
        for (int i = 0; i < GetUsers().size(); i++) {
            if (GetUsers().get(i).ID == CustomID) {
                return GetUsers().get(i);
            }
        }
        return null;
    }

    public List<User> GetUsers() {
        while (lock.isLocked()) {
            //Waiting for it to unlock
        }
        return Users;
    }

    public void updateClientPos(Vector2 Location, float angle) {
        for (int i = 0; i < GetUsers().size(); i++) {
            if (GetUsers().get(i).ID == CustomID) {
                GetUsers().get(i).x = Location.x;
                GetUsers().get(i).y = Location.y;
                GetUsers().get(i).angle = angle;
            }
        }
    }

    public void Disconnect() {
        client.close();
        client.stop();
    }

}