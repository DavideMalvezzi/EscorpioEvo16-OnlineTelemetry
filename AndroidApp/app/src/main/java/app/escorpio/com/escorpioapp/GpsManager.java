package app.escorpio.com.escorpioapp;

import android.app.Dialog;
import android.content.Context;
import android.hardware.SensorEventListener;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class GpsManager implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    //Google APIs
    private Context context;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private double gpsLat, gpsLon, gpsAlt, gpsAcc, gpsSpeed;

    //Log file
    private BufferedWriter mOut;
    private DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    public GpsManager(Context context){
        this.context = context;
        createGpsLogFile();
        setupGoogleApiClient();
    }

    private void createGpsLogFile(){
        try {
            File root = Environment.getExternalStorageDirectory();
            if(root.canWrite() && mOut == null){
                Date date = new Date();

                File dir = new File(root.getAbsolutePath() + "/LOGS/");
                dir.mkdir();

                File LogFile = new File(dir, "LOG_" + dateFormat.format(date) + ".txt");
                FileWriter LogWriter = new FileWriter(LogFile, true);
                mOut = new BufferedWriter(LogWriter);
                mOut.write("TIME,LAT,LON,ALT,PRECISION,\n");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    //Google
    private synchronized void setupGoogleApiClient(){
        int available = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if(available != ConnectionResult.SUCCESS){
        //Toast.makeText(context, "Google play services not available error: " + GooglePlayServicesUtil.getErrorString(available), Toast.LENGTH_LONG);
            Log.d("GP", "Google play services not available error: " + GooglePlayServicesUtil.getErrorString(available));

        }
        googleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();

        Log.d("GP", "Building");

    }

    @Override
    public void onConnected(Bundle bundle) {
        Toast.makeText(context, "Connected to Google Play!", Toast.LENGTH_SHORT).show();
        Log.d("GP", "Connected to Google Play");

        //Setup GPS requests
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(100);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(context, "Connection to GooglePlay suspended!", Toast.LENGTH_SHORT).show();
        Log.d("GP", "Connection to GooglePlay suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(context, "Connection to GooglePlay failed!", Toast.LENGTH_SHORT).show();
        Log.d("GP", "Connection to GooglePlay failed");

        //Schedule new connession attempt
        Timer t = new Timer();
        t.schedule(new TimerTask() {
            @Override
            public void run() {
                setupGoogleApiClient();
            }
        }, 1000);
    }

    //GPS
    @Override
    public void onLocationChanged(Location location) {
        gpsLat = location.getLatitude();
        gpsLon = location.getLongitude();
        gpsAlt = location.getAltitude();
        gpsAcc = location.getAccuracy();
        gpsSpeed = location.getSpeed();
        //Log.d("GPS", "Location changed");

        /*
        Log.d("GPS", "Lat " + location.getLatitude());
        Log.d("GPS", "Lon " + location.getLongitude());
        Log.d("GPS", "Alt " + location.getAltitude());
        Log.d("GPS", "Acc " + location.getAccuracy());
        */
        try {
            Date date = new Date();

            mOut.write( dateFormat.format(date) + ";" +
                            String.format("%.10f", location.getLatitude()) + ";" +
                            String.format("%.10f", location.getLongitude()) + ";" +
                            location.getAltitude() + ";" +
                            location.getAccuracy() + ";\n"
            );
            mOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public double getGpsLat() {
        return gpsLat;
    }

    public double getGpsLon() {
        return gpsLon;
    }

    public double getGpsAlt() {
        return gpsAlt;
    }

    public double getGpsAcc() {
        return gpsAcc;
    }

    public double getGpsSpeed() {
        return gpsSpeed;
    }

    public boolean isConnected(){
        return googleApiClient.isConnected();
    }

}
