package app.escorpio.com.escorpioapp;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

public class BluetoothManager {

    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private static final String EscorpioBlName = "EscorpioEvo";

    private Context context;

    private BluetoothAdapter blAdapter;
    private BroadcastReceiver blEvtReceiver;

    private BluetoothSocket blSocket;
    private DataInputStream blInput;
    private DataOutputStream blOutput;

    public BluetoothManager(Context context){
        this.context = context;
        checkEnable();
    }

    public void connect(){
        setupStateMachine();
        startDiscovery();
    }

    public synchronized boolean isConnected(){
        return blSocket != null && blOutput != null && blInput != null;
    }

    public boolean available() throws IOException {
        if(isConnected()){
            return blInput.available() > 0;
        }
        return false;
    }

    public String readLine() throws IOException {
        return blInput.readLine();
    }

    public int read() throws IOException{
        return blInput.read();
    }

    public void write(byte[] buffer) throws IOException {
        blOutput.write(buffer);
    }

    public void write(int i) throws IOException {
        blOutput.write(i);
    }

    public void writeBytes(String s) throws IOException {
        blOutput.writeBytes(s);
    }

    private boolean checkEnable(){
        blAdapter = BluetoothAdapter.getDefaultAdapter();
        blSocket = null;

        if(blAdapter != null){
            if(!blAdapter.isEnabled()){
                if(blAdapter.enable()) {
                    Toast.makeText(context, "Bluetooth enabled!", Toast.LENGTH_SHORT).show();
                    Log.d("BL", "Bluetooth enabled");
                    return true;
                }
                else {
                    Toast.makeText(context, "Can't turn on bluetooth!", Toast.LENGTH_SHORT).show();
                    Log.d("BL", "Can't turn on bluetooth!");
                    return false;
                }
            }
            else{
                return true;
            }
        }
        else{
            Toast.makeText(context, "Bluetooth not supported in this device!", Toast.LENGTH_SHORT).show();
            Log.d("BL", "Bluetooth not supported in this device");
        }
        return false;
    }

    private void setupStateMachine() {
        //Setup device scan receiver
        blEvtReceiver = new BroadcastReceiver() {
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                //Log.d("BL", "Action " + action);
                switch (action) {
                    case BluetoothAdapter.ACTION_STATE_CHANGED:
                        onStateChanged(context, intent);
                        break;
                    case BluetoothDevice.ACTION_FOUND:
                        onDeviceFound(context, intent);
                        break;
                    case BluetoothAdapter.ACTION_DISCOVERY_FINISHED:
                        onDiscoveryFinished(context, intent);
                        break;
                    case BluetoothDevice.ACTION_ACL_CONNECTED:
                        onConnected(context, intent);
                        break;
                    case BluetoothDevice.ACTION_ACL_DISCONNECTED:
                        onDisconnected(context, intent);
                        break;
                }
            }
        };

        //Start the devices scan
        IntentFilter turningOnIntent = new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED);
        IntentFilter foundIntent = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        IntentFilter connectedIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_CONNECTED);
        IntentFilter disconnectedIntent = new IntentFilter(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        IntentFilter discoveryFinishedIntent = new IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        context.registerReceiver(blEvtReceiver, turningOnIntent);
        context.registerReceiver(blEvtReceiver, foundIntent);
        context.registerReceiver(blEvtReceiver, connectedIntent);
        context.registerReceiver(blEvtReceiver, disconnectedIntent);
        context.registerReceiver(blEvtReceiver, discoveryFinishedIntent);
    }

    private void startDiscovery(){
        if (blAdapter.getState() != BluetoothAdapter.STATE_OFF) {
            blAdapter.startDiscovery();
        }
    }

    public BroadcastReceiver getEventReceiver(){
        return blEvtReceiver;
    }

    //Machine states
    private void onStateChanged(Context context, Intent intent){
        if (blAdapter.getState() == BluetoothAdapter.STATE_ON) {
            Log.d("BL", "Start discovery");
            blAdapter.startDiscovery();
        }
    }

    private void onDeviceFound(Context context, Intent intent){
        BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
        String name = device.getName();
        //Escorpio found
        if (device != null && name != null) {
            if (name.compareTo(EscorpioBlName) == 0) {
                Log.d("BL", "Escorpio found");
                Toast.makeText(context, "Escorpio found!", Toast.LENGTH_SHORT).show();
                connectToEscorpio(context, device);
            } else {
                Log.d("BL", "Found " + name);
            }
        }
    }

    private void onDiscoveryFinished(Context context, Intent intent){
       if(blSocket == null) {
           Toast.makeText(context, "Escorpio not found!", Toast.LENGTH_SHORT).show();
           Log.d("BL", "Escorpio not found");
           blAdapter.startDiscovery();
       }
    }

    private void onConnected(Context context, Intent intent){
        Toast.makeText(context, "Connected to Escorpio!", Toast.LENGTH_SHORT).show();
        Log.d("BL", "Connected to Escorpio");
        blAdapter.cancelDiscovery();
    }

    private void onDisconnected(Context context, Intent intent){
        Toast.makeText(context, "Escorpio connection lost!", Toast.LENGTH_SHORT).show();
        Log.d("BL", "Escorpio connection lost");
        blAdapter.startDiscovery();
        blSocket = null;
        blOutput = null;
        blInput = null;
    }

    //Connection
    private void connectToEscorpio(Context context, BluetoothDevice device){
        try {
            try {
                blAdapter.cancelDiscovery();
                blSocket = device.createInsecureRfcommSocketToServiceRecord(MY_UUID);
                blSocket.connect();

                blInput = new DataInputStream(blSocket.getInputStream());
                blOutput = new DataOutputStream(blSocket.getOutputStream());

            }catch (Exception e){
                blAdapter.startDiscovery();
                Log.d("BL", "Connection to Escorpio failed");
                Toast.makeText(context, "Connection to Escorpio failed!", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }

        }catch (Exception e){
            Log.d("BL", "Connection to Escorpio failed");
            Toast.makeText(context, "Connection to Escorpio failed!", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }
}
