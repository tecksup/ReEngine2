package kryonetwork;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryonet.EndPoint;
import com.thecubecast.reengine.data.tkmap.TkMap;

public class Network {

    static public void register(EndPoint endPoint) {
        Kryo kryo = endPoint.getKryo();
        kryo.register(Connect.class);
        kryo.register(User.class);
        kryo.register(ServerSide.class);
        kryo.register(java.util.ArrayList.class);
    }

}