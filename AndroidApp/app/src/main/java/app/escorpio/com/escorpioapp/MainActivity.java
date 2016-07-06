package app.escorpio.com.escorpioapp;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity{

    //Icon
    private static final float ALPHA_ON = 1;
    private static final float ALPHA_OFF = 0.3f;
    private ImageView blIcon, gpsIcon, callIcon, wsIcon;

    public static final String CALL_ON =    "CALL_ON";
    public static final String CALL_OFF =   "CALL_OFF";
    public static final String BL_ON =      "BL_ON";
    public static final String BL_OFF =     "BL_OFF";
    public static final String GPS_ON =     "GPS_ON";
    public static final String GPS_OFF =    "GPS_OFF";
    public static final String WS_ON =      "WS_ON";
    public static final String WS_OFF =     "WS_OFF";

    private BroadcastReceiver bReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()){
                case CALL_ON:
                    callIcon.setAlpha(ALPHA_ON);
                    break;
                case CALL_OFF:
                    callIcon.setAlpha(ALPHA_OFF);
                    break;
                case BL_ON:
                    blIcon.setAlpha(ALPHA_ON);
                    break;
                case BL_OFF:
                    blIcon.setAlpha(ALPHA_OFF);
                    break;
                case GPS_ON:
                    gpsIcon.setAlpha(ALPHA_ON);
                    break;
                case GPS_OFF:
                    gpsIcon.setAlpha(ALPHA_OFF);
                    break;
                case WS_ON:
                    wsIcon.setAlpha(ALPHA_ON);
                    break;
                case WS_OFF:
                    wsIcon.setAlpha(ALPHA_OFF);
                    break;
            }

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        //Don't go in stanby
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        //Setup icons
        blIcon =    (ImageView)findViewById(R.id.blIcon);
        gpsIcon =   (ImageView)findViewById(R.id.gpsIcon);
        callIcon =  (ImageView)findViewById(R.id.callIcon);
        wsIcon =    (ImageView)findViewById(R.id.wsIcon);


        //Crash test
        /*
        callIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = null;
                s.indexOf('c');
            }
        });
        */

        //Autorestart on crash
        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
            @Override
            public void uncaughtException(Thread thread, Throwable ex) {

                try {
                    File root = Environment.getExternalStorageDirectory();
                    if(root.canWrite()){
                        PrintWriter pw = new PrintWriter(new FileWriter(root + "/APP_EXCEPTION.txt", true));
                        SimpleDateFormat s = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");


                        pw.write("=============================================================\n");
                        pw.write(s.format(new Date()) + "\n");
                        ex.printStackTrace(pw);
                        pw.write("=============================================================\n");

                        pw.flush();
                        pw.close();
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                ex.printStackTrace();

                Intent intent = new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);

                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this.getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);

                AlarmManager mgr = (AlarmManager) MainActivity.this.getBaseContext().getSystemService(Context.ALARM_SERVICE);
                mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 2000, pendingIntent);

                MainActivity.this.finish();

                System.exit(2);
            }
        });


        //Communication with the service
        LocalBroadcastManager bManager = LocalBroadcastManager.getInstance(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(CALL_ON);
        intentFilter.addAction(CALL_OFF);
        intentFilter.addAction(BL_ON);
        intentFilter.addAction(BL_OFF);
        intentFilter.addAction(GPS_ON);
        intentFilter.addAction(GPS_OFF);
        intentFilter.addAction(WS_ON);
        intentFilter.addAction(WS_OFF);
        bManager.registerReceiver(bReceiver, intentFilter);

        //Start the service
        Intent intent = new Intent(this, AppService.class);
        startService(intent);

        //Check google play updates
        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(available != ConnectionResult.SUCCESS){
            Integer resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(resultCode, this, 0);
            if (dialog != null) {
                //This dialog will help the user update to the latest GooglePlayServices
                dialog.show();
            }
        }

    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        Intent intent = new Intent(this, AppService.class);
        stopService(intent);
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch( event.getKeyCode() ) {

            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_HOME:
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                return true;
        }
        return false;
    }


}
