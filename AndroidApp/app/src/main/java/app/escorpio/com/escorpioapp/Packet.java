package app.escorpio.com.escorpioapp;

import java.io.Serializable;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Packet implements Serializable {

    public static final String INF_HEADER = "INF";
    public static final String GPS_HEADER = "GPS";
    public static final String ACC_HEADER = "ACC";
    public static final String CALL_HEADER = "CALL";

    public static final int INF_PACKET_SIZE = 19;
    public static final int GPS_PACKET_SIZE = 43;
    public static final int ACC_PACKET_SIZE = 28;
    public static final int CALL_PACKET_SIZE = 5;

    public String date, time;
    private DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
    private DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");

    public double gpsLat, gpsLon, gpsAlt, gpsAcc, gpsSpeed;

    public byte accStatus;
    public double accX, accY, accZ;

    public byte callStatus;


    byte[] getInfoDataPacket(){
        Date systemDate = new Date();

        date = dateFormat.format(systemDate);
        time = hourFormat.format(systemDate);

        byte[] buffer = ByteBuffer.allocate(INF_PACKET_SIZE + 1)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(INF_HEADER.getBytes(), 0, INF_HEADER.length())
                .put(date.getBytes(), 0, 8)
                .put(time.getBytes(), 0, 8)
                .array();
        buffer[INF_PACKET_SIZE] = getAck(buffer, INF_HEADER.length(), INF_PACKET_SIZE);
        return buffer;
    }

    byte[] getGPSDataPacket(){
        byte[] buffer = ByteBuffer.allocate(GPS_PACKET_SIZE + 1)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(GPS_HEADER.getBytes(), 0, GPS_HEADER.length())
                .putDouble(gpsLat)
                .putDouble(gpsLon)
                .putDouble(gpsAlt)
                .putDouble(gpsAcc)
                .putDouble(gpsSpeed)
                .array();
        buffer[GPS_PACKET_SIZE] = getAck(buffer, GPS_HEADER.length(), GPS_PACKET_SIZE);
        return buffer;
    }

    byte[] getAccDataPacket(){
        byte[] buffer = ByteBuffer.allocate(ACC_PACKET_SIZE + 1)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(ACC_HEADER.getBytes(), 0, ACC_HEADER.length())
                .put(accStatus)
                .putDouble(accX)
                .putDouble(accY)
                .putDouble(accZ)
                .array();
        buffer[ACC_PACKET_SIZE] = getAck(buffer, ACC_HEADER.length(), ACC_PACKET_SIZE);
        return buffer;
    }

    byte[] getCallDataPacket(){
        byte[] buffer = ByteBuffer.allocate(CALL_PACKET_SIZE + 1)
                .order(ByteOrder.LITTLE_ENDIAN)
                .put(CALL_HEADER.getBytes(), 0, CALL_HEADER.length())
                .put(callStatus)
                .array();
        buffer[CALL_PACKET_SIZE] = getAck(buffer, CALL_HEADER.length(), CALL_PACKET_SIZE);
        return buffer;
    }

    byte getAck(byte[] buffer, int offset, int size){
        byte ack = 0;
        for(int i = offset; i < size; i++){
            ack ^= buffer[i];
        }
        return ack;
    }
}
