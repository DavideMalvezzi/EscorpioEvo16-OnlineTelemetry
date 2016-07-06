package app.escorpio.com.escorpioapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;


public class CallManager {

    private boolean callActive = false;
    private Context context;

    public CallManager(Context context){
        this.context = context;
        setupCallStatusListener();
    }

    //Call
    private void setupCallStatusListener(){
        final TelephonyManager telephonyManager =(TelephonyManager)context.getSystemService(context.TELEPHONY_SERVICE);
        PhoneStateListener phoneStateListener = new PhoneStateListener(){

            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);

                switch (state) {
                    case TelephonyManager.CALL_STATE_RINGING:
                        try {
                            Log.d("CALL", "Answering incoming call");
                            Runtime.getRuntime().exec("input keyevent " + Integer.toString(KeyEvent.KEYCODE_HEADSETHOOK));
                        }catch (Exception e){
                            Log.d("CALL", "Error on call answering");
                            callActive = false;
                            e.printStackTrace();
                        }
                        break;

                    case TelephonyManager.CALL_STATE_OFFHOOK:
                        Log.d("CALL", "In call");
                        Toast.makeText(context, "In call", Toast.LENGTH_SHORT).show();

                        /*
                        try {
                            if(blMan.isConnected()){
                                blMan.writeBytes("CALL");
                                blMan.write(1);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        */
                        callActive = true;
                        break;

                    case TelephonyManager.CALL_STATE_IDLE:
                        Log.d("CALL", "No call");
                        Toast.makeText(context, "No call", Toast.LENGTH_SHORT).show();

                        /*
                        try {
                            if(blMan.isConnected()){
                                blMan.writeBytes("CALL");
                                blMan.write(0);
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        */
                        callActive = false;
                        break;
                }
            }
        };

        telephonyManager.listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void startCall(String cmd){
        if(!callActive) {
            Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse(cmd.toLowerCase()));
            callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(callIntent);
        }
    }

    public boolean isCallActive(){
        return callActive;
    }

}
