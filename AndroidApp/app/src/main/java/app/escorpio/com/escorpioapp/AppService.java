package app.escorpio.com.escorpioapp;


import android.app.IntentService;
import android.content.Intent;

import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AppService extends IntentService {

    //Bluetooth cmds;
    private static final int LOG_INTERVAL = 500;
    private static final String TEL_CMD = "TEL:";
    private static final String TLM_CDM = "TLM";
    private byte[] blBuffer = null;

    //Managers
    private BluetoothManager blMan;
    private GpsManager gpsMan;
    private CallManager callMan;
    private SensorManager sensorMan;
    //private WebSocketManager wsMan;
    private ioWebSocketManager wsMan;
    private Packet packet;

    public AppService(){
        super("AppService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        blMan = new BluetoothManager(this);
        gpsMan = new GpsManager(this);
        callMan = new CallManager(this);
        sensorMan = new SensorManager(this);
        //wsMan = new WebSocketManager(this);
        wsMan = new ioWebSocketManager();
        packet = new Packet();

        //createGpsLogFile();

        wsMan.connect();
        blMan.connect();

        Thread blLogThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    try {
                        updatePacketInfo();
                        logInfo();
                        Thread.sleep(LOG_INTERVAL);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        blLogThread.start();


        while (true) {
            if(blMan.isConnected()){
                updateBlConnection();
            }

            checkServicesStatus();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        wsMan.disconnect();
        unregisterReceiver(blMan.getEventReceiver());
    }

    private void updatePacketInfo(){
        packet.gpsLat = gpsMan.getGpsLat();
        packet.gpsLon = gpsMan.getGpsLon();
        packet.gpsAlt = gpsMan.getGpsAlt();
        packet.gpsAcc = gpsMan.getGpsAcc();
        packet.gpsSpeed = gpsMan.getGpsSpeed();

        packet.accX = sensorMan.getAccX();
        packet.accY = sensorMan.getAccY();
        packet.accZ = sensorMan.getAccZ();
        packet.accStatus = sensorMan.getAccStatus();

        packet.callStatus = callMan.isCallActive() ? (byte)1 : (byte)0;
    }

    private void logInfo(){
        //Send packet via bl
        try {
            if(blMan.isConnected()) {
                blMan.write(packet.getInfoDataPacket());
                blMan.write(packet.getGPSDataPacket());
                blMan.write(packet.getAccDataPacket());
                blMan.write(packet.getCallDataPacket());
                Log.d("Service", "Logged");
                //Log.d("Service", "GPS Data  " + bytesToHex(packet.getGPSDataPacket()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateBlConnection(){
        try {
            //If data incoming from bl
            if (blMan.available()) {
                Log.d("Service", "Bl data available");

                //Read cmd line
                String cmd = blMan.readLine();

                if (cmd.startsWith(TEL_CMD)) {
                    callMan.startCall(cmd);
                }
                else if(cmd.startsWith(TLM_CDM)){
                    //Remove header
                    cmd = cmd.substring(TLM_CDM.length());
                    //Get data size
                    int packetSize = Integer.valueOf(cmd);

                    Log.d("Service", "Received data packet size " + packetSize);

                    //If parsed well
                    if(packetSize > 0) {
                        int memIndex = 0;
                        //Resize buffer if necessary
                        if(blBuffer == null || blBuffer.length != packetSize){
                            blBuffer = new byte[packetSize];
                        }
                        //Read all
                        while(memIndex < packetSize){
                            blBuffer[memIndex++] = (byte) (blMan.read() & 0xFF);
                        }
                        //Log.d("DAT", bytesToHex(blBuffer));

                        //Send to server if connected and readed all
                        if (wsMan.isConnected()) {
                            wsMan.sendData(blBuffer);
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkServicesStatus(){
        if (blMan.isConnected()) {
            sendMsgToActivity(MainActivity.BL_ON);
        }
        else{
            sendMsgToActivity(MainActivity.BL_OFF);
        }

        if (gpsMan.isConnected()) {
            sendMsgToActivity(MainActivity.GPS_ON);
        }
        else{
            sendMsgToActivity(MainActivity.GPS_OFF);
        }

        if (callMan.isCallActive()) {
            sendMsgToActivity(MainActivity.CALL_ON);
        }
        else{
            sendMsgToActivity(MainActivity.CALL_OFF);
        }

        if (wsMan.isConnected()) {
            sendMsgToActivity(MainActivity.WS_ON);
        }
        else{
            sendMsgToActivity(MainActivity.WS_OFF);
        }
    }

    void sendMsgToActivity(String msg){
        Intent intent = new Intent(msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    //Utils
    public static String bytesToHex(byte[] in) {
        final StringBuilder builder = new StringBuilder();
        for(byte b : in) {
            builder.append(String.format("%02x", b) + " ");
        }
        return builder.toString();
    }

}
