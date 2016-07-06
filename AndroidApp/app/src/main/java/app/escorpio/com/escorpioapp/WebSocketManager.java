package app.escorpio.com.escorpioapp;

import android.content.Context;
import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Timer;
import java.util.TimerTask;

public class WebSocketManager {

    private static final int ABNORMAL_CLOSE = 1006;
    private static final String WS_SERVER = "ws://telemetrywsserver-escorpioevo.rhcloud.com:8000";

    private Context context;

    private WebSocketClient wsClient;
    private URI serverURI;
    private boolean connected = false;

    public WebSocketManager(Context context){
        this.context = context;
        try {
            serverURI = new URI(WS_SERVER);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void connect(){
        try {
            wsClient = getNewSocket(serverURI);
            wsClient.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disconnect(){
        if(isConnected()){
            wsClient.close();
        }
    }

    private WebSocketClient getNewSocket(final URI serverURI){
        return new WebSocketClient(serverURI) {
            @Override
            public void onOpen(ServerHandshake handshakedata) {
                connected = true;

                Log.d("WS", "Connected to the server");
                //Toast.makeText(context, "Connected to the server", //Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onMessage(String message) {

            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                connected = false;

                //Server crash or not legal disconnect
                //if(code == ABNORMAL_CLOSE){
                WebSocketManager.this.connect();
                //}

                Log.d("WS", "Connection with the server closed " + code + " " + reason + " " + remote);
                //Toast.makeText(context, "Connection with the server closed", //Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onError(Exception ex) {
                connected = false;

                Timer t = new Timer();
                TimerTask task = new TimerTask() {
                    @Override
                    public void run() {
                        WebSocketManager.this.connect();
                    }
                };

                t.schedule(task, 5000);

                Log.d("WS", "Error with the server connection!");
                //Toast.makeText(context, "Error with the server connection", //Toast.LENGTH_SHORT).show();

                ex.printStackTrace();
        }
        };
    }

    public void sendData(String data){
        if(wsClient != null){
            wsClient.send(data);
        }
    }

    public void sendData(byte[] data){
        if(wsClient != null){
            wsClient.send(data);
        }
    }

    public boolean isConnected(){
        return wsClient != null && connected;
    }

}
