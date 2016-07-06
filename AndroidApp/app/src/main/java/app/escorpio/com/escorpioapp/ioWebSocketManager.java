package app.escorpio.com.escorpioapp;


import android.util.Log;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

/**
 * Created by Davide on 22/06/2016.
 */
public class ioWebSocketManager {

    private static final String WS_SERVER = "http://escorpiotelemetryws-67106.onmodulus.net";//"http://telemetrywsserver-escorpioevo.rhcloud.com";
    private Socket socket = null;

    public ioWebSocketManager(){

    }

    void connect(){
        try {
            socket = IO.socket(WS_SERVER);
            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener(){

                @Override
                public void call(Object... args) {
                    Log.d("WS", "Connected to the server");
                }
            }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener(){

                @Override
                public void call(Object... args) {
                    Log.d("WS", "Connection with the server closed");
                }
            });

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    void disconnect(){
        if(isConnected()){
            socket.disconnect();
        }
    }


    public void sendData(String data){
        if(socket != null){
            socket.emit("escorpio", data);
        }
    }

    public void sendData(byte[] data){
        if(socket != null){
            socket.emit("escorpio", data);
        }
    }

    public boolean isConnected(){
        return socket != null && socket.connected();
    }
}
