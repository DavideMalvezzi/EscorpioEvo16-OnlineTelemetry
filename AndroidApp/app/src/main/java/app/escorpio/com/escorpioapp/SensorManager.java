package app.escorpio.com.escorpioapp;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;


public class SensorManager implements SensorEventListener {

    private byte accStatus;
    private double accX, accY, accZ;
    private Context context;

    public SensorManager(Context context){
        this.context = context;
        setupSensors();
    }

    //Sensor
    private void setupSensors(){
        android.hardware.SensorManager sm = (android.hardware.SensorManager)context.getSystemService(Context.SENSOR_SERVICE);
        Sensor acc = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sm.registerListener(this, acc, android.hardware.SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Sensor s = event.sensor;
        if(s.getType() == Sensor.TYPE_ACCELEROMETER){
            accX = event.values[0];
            accY = event.values[1];
            accZ = event.values[2];

            //Log.d("ACC", "X: " + accX);
            //Log.d("ACC", "Y: " + accY);
            //Log.d("ACC", "Z: " + accZ);

        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        if(sensor.getType() == Sensor.TYPE_ACCELEROMETER){
            accStatus = (byte)accuracy;
        }
    }

    public double getAccX(){
        return accX;
    }

    public double getAccY(){
        return accY;
    }

    public double getAccZ(){
        return accZ;
    }

    public byte getAccStatus(){
        return accStatus;
    }


}
